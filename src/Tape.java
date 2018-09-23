import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.LinkedList;

public class Tape{
    private GridPane tapeDisplay;
    private GridPane headDisplay;
    private Integer tapeDisplayOffset;
    private int tapeHead;
    private LinkedList<Character> tape = new LinkedList<>();

    public void incrementDisplayOffset() {
        tapeDisplayOffset++;
    }
    public void decrementDisplayOffset() {
        tapeDisplayOffset--;
    }

    public void refreshTapeDisplay() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int index = tapeDisplayOffset;
                Character[] tapeChars = getTapeAsArray();
                int size = tapeChars.length;
                for(Node n : tapeDisplay.getChildren()) {
                    if (n instanceof StackPane) {
                        for(Node b : ((StackPane) n).getChildren()) {
                            if (b instanceof Label) {
                                if(index < size && index >= 0) {
                                    ((Label) b).setText(tapeChars[index].toString());
                                }
                                else {
                                    ((Label) b).setText(" ");
                                }
                            }
                        }
                        index++;
                    }
                }
                index = tapeDisplayOffset;
                for(Node n: headDisplay.getChildren()) {
                    if (n instanceof StackPane) {
                        for (Node b: ((StackPane) n).getChildren()) {
                            if (b instanceof Label) {
                                if(index == getTapeHead()) {
                                    ((Label) b).setText("v");
                                }
                                else {
                                    ((Label) b).setText(" ");
                                }
                            }
                        }
                    }
                    index++;
                }
            }
        });

    }

    public void setDisplay(GridPane tape, GridPane head) {
        tapeDisplayOffset = 0;
        tapeDisplay = tape;
        headDisplay = head;

        tapeDisplay.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue() - 130;
            double oldWidth = oldVal.doubleValue() - 130;
            int newCount = (int)newWidth / 30;
            int oldCount = (int)oldWidth / 30;
            if (newCount == oldCount) return;
            Character[] tapeChars;
            try {
                tapeChars = getTapeAsArray();
            }
            catch (NullPointerException e) {
                tapeChars = new Character[] {};
            }
            int index = 0;
            int size = tapeChars.length;

            tapeDisplay.getChildren().clear();
            headDisplay.getChildren().clear();
            // FIXME Add a right click listener to choose the head by right clicking the rectangle desired?
            for (int i = 0; i < newCount; i++) {
                StackPane box = new StackPane();
                StackPane headBox = new StackPane();
                Rectangle tapeBox = new Rectangle(30, 30, Paint.valueOf("#ffffff"));
                Rectangle headTapeBox = new Rectangle(30, 30, Paint.valueOf("#ffffff"));
                Label tapeChar;
                Label headTapeChar;

                if (index == tapeHead) headTapeChar = new Label("v");
                else headTapeChar = new Label(" ");
                if (index < size) {
                    tapeChar = new Label(tapeChars[index].toString());
                }
                else {
                    tapeChar = new Label(" ");
                }

                headTapeBox.setStroke(Paint.valueOf("#ffffff"));
                tapeBox.setStroke(Paint.valueOf("#000000"));
                GridPane.setConstraints(box, i, 0);
                GridPane.setConstraints(headBox, i, 0);
                headBox.getChildren().add(headTapeBox);
                headBox.getChildren().add(headTapeChar);
                headDisplay.getChildren().add(headBox);
                box.getChildren().add(tapeBox);
                box.getChildren().add(tapeChar);
                tapeDisplay.getChildren().add(box);
                index++;
            }


        });

    }


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
