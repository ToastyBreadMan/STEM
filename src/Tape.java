import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class Tape{
    private int tapeHead;
    private LinkedList<Character> tape = new LinkedList<>();

    public boolean setTapeHead(int tapeHead) {
        if(tapeHead > this.tape.size()-1 || tapeHead < 0)
            return false;

        this.tapeHead = tapeHead;
        return true;
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
        return tape.get(tapeHead);
    }

    public Character left(){
        tapeHead--;

        if(tapeHead == -1){
            prependTape(new Character(' '));
            tapeHead = 0;
        }
        return tape.get(tapeHead);
    }

    public Character right(){
        tapeHead++;

        if(tapeHead > tape.size()-1){
            appendTape(new Character(' '));
            tapeHead = tape.size()-1;
        }
        return tape.get(tapeHead);
    }

    public Character[] getTapeAsArray(){
        return tape.toArray(new Character[0]);
    }
}
