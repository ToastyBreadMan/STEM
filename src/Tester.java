import javafx.application.Platform;
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

    public Transition nextTransition(State currentState, Tape tape){
        Character curChar = tape.currentTapeVal();
        Transition curTransition = null;


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

        return curTransition;
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

        Transition next = this.nextTransition(currentState, m.getTape());
        while(next != null) {

            // Set the color of the selected State
            if(currentState.getCircle() != null){
                currentState.getCircle().setFill(Color.GREENYELLOW);
            }

            // If the writeChar is the null character do not write anything
            if(next.getWriteChar() != '~'){
                try{
                    tape.setTape(next.getWriteChar());
                } catch (Exception e){
                    failReason = String.format("Cannot set %c to tape location %d", next.getWriteChar(), tape.getTapeHead());
                    throw e;
                }
            }

            System.out.printf("Going from State %s to %s along Transition %c ; %c ; %c\n",
                    currentState.getName(), next.getToState().getName(),
                    next.getReadChar(), next.getWriteChar(), next.getMoveDirection().toString().charAt(0));

            TimeUnit.MILLISECONDS.sleep(waitTime);
            switch(next.getMoveDirection()){
                case LEFT:
                    tape.left();
                    break;
                case RIGHT:
                    tape.right();
                    break;
                case STAY:
                    break;
            }

            Platform.runLater(() -> {
                m.getTape().centerTapeDisplay();
                m.getTape().refreshTapeDisplay();
            });

            // Reset Colors
            if(currentState.getCircle() != null) {
                currentState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
            }

            currentState = next.getToState();
            next = this.nextTransition(currentState, m.getTape());

            loops++;
            // TODO: prompt user if loop goes over X iterations
            if(loops % 1000 == 0){
            }
        }

        if(currentState != null)
            currentState.getCircle().setFill(Color.GREENYELLOW);

        Platform.runLater(() -> {
            m.getTape().centerTapeDisplay();
            m.getTape().refreshTapeDisplay();
        });

        TimeUnit.MILLISECONDS.sleep(waitTime);
        if(currentState.getCircle() != null) {
            currentState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
        }
        this.succeeded = currentState.isAccept();
    }
}