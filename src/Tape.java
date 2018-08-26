import java.util.ArrayList;
import java.util.LinkedList;

public class Tape{
    private int tapeHead;
    private LinkedList<Character> tape = new LinkedList<>();

    public boolean setTapeHead(int tapeHead) {
        if(tapeHead > this.tape.size()-1 || tapeHead < 0)
            return false;

        this.tapeHead = tapeHead;
        return true;
    }

    public int getTapeHead(){
        return this.tapeHead;
    }

    // Initialize Tape to t and set Tapehead to the start
    public void initTape(ArrayList<Character> t){
        this.tape.clear();
        this.tape.addAll(t);
        this.tapeHead = 0;
    }

    public int getSize(){ return tape.size(); }

    public void appendTape(Character c){
        tape.addLast(c);
    }

    public void prependTape(Character c){
        tape.addFirst(c);
    }

    public void setTape(Character c) throws Exception{
        tape.set(tapeHead, c);
    }

    public Character currentTapeVal(){
        if(tape.isEmpty())
            appendTape(' ');
        return tape.get(tapeHead);
    }

    public Character left(){
        tapeHead--;

        if(tapeHead == -1){
            prependTape(' ');
            tapeHead = 0;
        }
        return tape.get(tapeHead);
    }

    public Character right(){
        tapeHead++;

        if(tapeHead > tape.size()-1){
            appendTape(' ');
            tapeHead = tape.size()-1;
        }
        return tape.get(tapeHead);
    }

    public Character[] getTapeAsArray(){
        return tape.toArray(new Character[0]);
    }
}
