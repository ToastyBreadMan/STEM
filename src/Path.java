import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class Path {
    private State state1;
    private State state2;
    private Line line;
    private ArrayList<Text> aboveTexts;
    private ArrayList<Text> belowTexts;

    public Path (State state1, State state2){
        this.state1 = state1;
        this.state2 = state2;
        aboveTexts = new ArrayList<>();
        belowTexts = new ArrayList<>();
    }

    public ArrayList<Node> addTransition(Transition t){
        ArrayList<Node> ret = new ArrayList<>();

        State fromState = t.getFromState();
        State toState = t.getToState();

        if(line == null){
            line = new Line(fromState.getX(), fromState.getY(),
                    toState.getX(), toState.getY());
            line.toBack();

            System.out.println("New Line");
            ret.add(line);
        }

        if(fromState.getX() != toState.getX()){
            double degree = computeDegree(fromState.getX(), toState.getX(),
                    fromState.getY(), toState.getY());

            if(fromState.getX() < toState.getX()){
                Text newText = new Text(String.format("%c ; %c ; %c -->",
                        t.getReadChar(), t.getWriteChar(), t.getMoveDirection().toString().charAt(0)));

                newText.setRotate(degree);
                newText.setTextAlignment(TextAlignment.CENTER);
                newText.setX((fromState.getX() + toState.getY())/2);
                newText.setY(((fromState.getY() + toState.getY())/2) + 5 + 5 * aboveTexts.size());
                newText.setUserData(t);

                aboveTexts.add(newText);

                ret.add(newText);
            }
            else {
                Text newText = new Text(String.format("<-- %c ; %c ; %c",
                        t.getReadChar(), t.getWriteChar(), t.getMoveDirection().toString().charAt(0)));

                newText.setRotate(degree);
                newText.setTextAlignment(TextAlignment.CENTER);
                newText.setX((fromState.getX() + toState.getY()) / 2);
                newText.setY(((fromState.getY() + toState.getY()) / 2) - 5 - 5 * belowTexts.size());
                newText.setUserData(t);

                belowTexts.add(newText);

                ret.add(newText);
            }
        }

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
        ret.addAll(aboveTexts);
        ret.addAll(belowTexts);

        return ret;
    }

    public boolean compareTo(State a, State b){
        return (a == state1 || a == state2) && (b == state1 || b == state2);
    }

    public ArrayList<State> getStates(){
        ArrayList<State> ret = new ArrayList<>();

        ret.add(state1);
        ret.add(state2);

        return ret;
    }

    private static double computeDegree(double x1, double x2, double y1, double y2){
        if (x1 != x2)
            return Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
        else
            return 0;
    }

}
