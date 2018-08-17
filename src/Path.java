import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Path {
    private State state1;
    private State state2;
    private Line line;
    private ArrayList<Text> texts;

    public Path (State state1, State state2){
        this.state1 = state1;
        this.state2 = state2;
        texts = new ArrayList<>();
    }

    public ArrayList<Node> addTransition(Transition t){
        ArrayList<Node> ret = new ArrayList<>();



        return ret;
    }

    public ArrayList<Node> addTransitions(ArrayList<Transition> tl){
        ArrayList<Node> ret = new ArrayList<>();

        for(Transition t : tl)
            ret.addAll(this.addTransition(t));

        return ret;
    }

    public ArrayList<Node> removeTransition(Transition t){
        ArrayList<Node> ret = new ArrayList<>();


        return ret;
    }

    public ArrayList<Node> removeTransitions(ArrayList<Transition> tl){
        ArrayList<Node> ret = new ArrayList<>();

        for(Transition t : tl)
            ret.addAll(removeTransition(t));

        return ret;
    }

    public ArrayList<Node> getAllNodes(){
        ArrayList<Node> ret = new ArrayList<>();
        ret.add(line);
        ret.addAll(texts);

        return ret;
    }

}
