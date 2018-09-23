import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Tester {
    private String failReason;
    private boolean succeeded;
    private int loops;




    public String getFailReason() {
        return failReason;
    }

    public boolean didSucceed() {
        return succeeded;
    }

    public void runMachine(Machine m) throws Exception{
        State currentState;
        ArrayList<State> states = m.getStates();
        Tape tape = m.getTape();

        // Fail if there is no start state
        currentState = m.getStartState();
        if(currentState == null){
            failReason = "Machine has no start state!";
            succeeded = false;
            return;
        }

        int waitTime = m.getSpeed();

        loops = 0;
        // Main body
        while(true) {
            Character curChar = tape.currentTapeVal();
            Transition curTransition = null;

            // Set the color of the selected State
            if(currentState.getCircle() != null){
                currentState.getCircle().setFill(Color.GREENYELLOW);
            }

            // Find the transition where the current tape char is
            // equal to the first transition's readChar
            for(Transition t : currentState.getTransition()){
                if(t.getReadChar() == curChar && t.getFromState() == currentState){
                    curTransition = t;
                    break;
                }
            }

            // If no Transition is found, search for a transition
            // with no read char. I.E. catchall transition
            if(curTransition == null){
                for(Transition t : currentState.getTransition()){
                    if(t.getFromState() == currentState && t.getReadChar() == '~'){
                        curTransition = t;
                        break;
                    }
                }
            }

            // If no transition is found still, exit the Machine
            if(curTransition == null){

                TimeUnit.MILLISECONDS.sleep(waitTime);
                if(currentState.getCircle() != null) {
                    currentState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
                }

                failReason = String.format("State %s is not an accept state", currentState.getName());
                succeeded = currentState.isAccept();
                return;
            }

            // Set color of the selected Transition
            /*
            if(curTransition.getLine() != null){
                curTransition.getLine().setFill(Color.YELLOWGREEN);
            }
            */

            // If the writeChar is the null character do not write anything
            if(curTransition.getWriteChar() != '~'){
                try{
                    tape.setTape(curTransition.getWriteChar());
                } catch (Exception e){
                    failReason = String.format("Cannot set %c to tape location %d", curTransition.getWriteChar(), tape.getTapeHead());
                    throw e;
                }
            }

            switch(curTransition.getMoveDirection()){
                case LEFT:
                    tape.left();
                    break;
                case RIGHT:
                    tape.right();
                    break;
                case STAY:
                    break;
            }
            m.getTape().centerTapeDisplay();
            m.getTape().refreshTapeDisplay();
            System.out.printf("Going from State %s to %s along Transition %c ; %c ; %c\n",
                    currentState.getName(), curTransition.getToState().getName(),
                    curTransition.getReadChar(), curTransition.getWriteChar(), curTransition.getMoveDirection().toString().charAt(0));
            TimeUnit.MILLISECONDS.sleep(waitTime);

            // Reset Colors
            if(currentState.getCircle() != null) {
                currentState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
            }

            currentState = curTransition.getToState();

            System.out.print("|");
            for(Character c : tape.getTapeAsArray()){
                System.out.printf("%c|", c);
            }
            System.out.print("\n");

            loops++;
            // TODO: prompt user if loop goes over X iterations
            if(loops % 1000 == 0){
            }
        }
    }
}