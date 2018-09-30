public class MachineStep {
    private Transition t;
    private char c;


    MachineStep(Transition t, char c){
        this.t = t;
        this.c = c;
    }

    public char getChar(){
        return this.c;
    }

    public Transition getTransition(){
        return this.t;
    }
}
