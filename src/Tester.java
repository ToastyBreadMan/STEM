import java.util.ArrayList;

public class Tester {

    public boolean runMachine(Machine m, int tapeLoc, int waitTime) throws Exception{
        boolean loop = true;
        State currentState;
        ArrayList<State> states = m.getStates();
        Tape tape = m.tape;

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

            // Find the transition where the current tape char is
            // equal to the first transition's readChar
            for(Transition t : currentState.getPaths()){
                if(t.getReadChar() == curChar.charValue()){
                    curTransition = t;
                    break;
                }
            }

            // If no Transition is found, search for a transition
            // with no read char. I.E. catchall transition
            if(curTransition == null){
                for(Transition t : currentState.getPaths()){
                    if(t.getReadChar() == '\u0000'){
                        curTransition = t;
                        break;
                    }
                }
            }

            // If no transition is found still, exit the Machine
            if(curTransition == null){
                if(currentState.isAccept())
                    return true;
                else
                    return false;
            }

            // If the writeChar is the null character do not write anything
            if(curTransition.getWriteChar() != '\u0000'){
                try{
                    tape.setTape(curTransition.getWriteChar());
                } catch (Exception e){
                    System.out.println(e.getMessage());
                    return false;
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

            currentState = curTransition.getToState();

            System.out.printf("|");
            for(Character c : tape.getTapeAsArray()){
                System.out.printf("%c|", c.charValue());
            }
            System.out.printf("\n");

            // TODO: prompt user if loop goes over X iterations
        }

        return false;
    }
}