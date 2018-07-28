import java.util.ArrayList;

public class Tester {

    public boolean runMachine(Machine m, int tapeLoc, int waitTime){
        boolean loop = true;
        State currentState;
        ArrayList<State> states = m.getStates();
        Tape tape = m.getTape();

        // Fail if tapeLoc is out of scope
        if(!tape.setTapeHead(tapeLoc)){
            System.out.printf("Invalid TapeHead value: %d /t TapeHead must be between 0 and %d\n", tapeLoc, tape.getSize()-1);
            return false;
        }

        // Fail if there is no start state
        currentState = m.getStartState();
        if(currentState == null){
            System.out.println("Machine has no start state!");
            return false;
        }

        // Main body
        while(loop) {
            Character curChar = tape.currentTapeVal();
            Transition curTransition = null;

            for(Transition t : currentState.getPaths()){
                if(t.getReadChar() == curChar.charValue())
                    curTransition = t;
            }

            if(curTransition == null){
                if(currentState.isAccept())
                    return true;
                else
                    return false;
            }

            try{
                tape.setTape(curTransition.getWriteChar());
            } catch (Exception e){
                System.out.println(e.getMessage());
                return false;
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


            // TODO: prompt user if loop goes over X iterations
        }

        return false;
    }
}