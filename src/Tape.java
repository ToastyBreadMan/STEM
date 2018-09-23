import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectExpression;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tape{
    private GridPane tapeDisplay;
    private GridPane headDisplay;
    private final Lock l = new ReentrantLock();
    private Integer tapeDisplayOffset = 0;
    private Integer tapeHead = 0;
    ObservableIntegerValue tapeWidth;

    private LinkedList<Character> tape = new LinkedList<>();

    @Override
    public String toString() {
        String s = new String();
        for(char c : tape) {
            s += c;
        }
        return s;
    }

    public void incrementDisplayOffset() {
        tapeDisplayOffset++;
    }
    public void decrementDisplayOffset() {
        tapeDisplayOffset--;
    }

    public void centerTapeDisplay() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tapeDisplayOffset = tapeHead - tapeWidth.get() / 2;
            }
        });
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
                                    ((Label) b).setText("↓");
                                    ((Label) b).setFont(Font.font(20));
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

    public void setDisplay(GridPane tape, GridPane head, BorderPane tapeArea) {
        tapeDisplayOffset = 0;
        tapeDisplay = tape;
        headDisplay = head;


        tapeWidth = Bindings.createIntegerBinding(
                () -> ((int)(tapeArea.getWidth() - 130)) / 31, tapeArea.widthProperty());

        tapeWidth.addListener((obs, oldCount, newCount) -> {
            Character[] tapeChars;
            try {
                tapeChars = getTapeAsArray();
            }
            catch (NullPointerException e) {
                tapeChars = new Character[] {};
            }
            int index = tapeDisplayOffset;
            int size = tapeChars.length;
            tapeArea.getChildren().remove(tapeDisplay);
            tapeArea.getChildren().remove(headDisplay);
            tapeDisplay = new GridPane();
            tapeDisplay.setAlignment(Pos.CENTER);
            headDisplay = new GridPane();
            headDisplay.setAlignment(Pos.CENTER);
            // FIXME Add a right click listener to choose the head by right clicking the rectangle desired?
            for (Integer i = 0; i < newCount.intValue(); i++) {
                StackPane box = new StackPane();
                StackPane headBox = new StackPane();
                Rectangle tapeBox = new Rectangle(30, 30, Paint.valueOf("#ffffff"));
                Rectangle headTapeBox = new Rectangle(30, 30, Paint.valueOf("#ffffff"));
                Label tapeChar;
                Label headTapeChar;

                if (index == tapeHead) {
                    headTapeChar = new Label("↓");
                    headTapeChar.setFont(Font.font(20));
                    //tapeBox.setFill(Paint.valueOf("#CAE1F9"));
                }
                else {
                    headTapeChar = new Label(" ");
                }
                if (index < size && index >= 0) {
                    tapeChar = new Label(tapeChars[index].toString());
                }
                else {
                    tapeChar = new Label(" ");
                }

                tapeChar.setId(Integer.toString(i + tapeDisplayOffset));

                tapeBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    tapeHead = Integer.parseInt(tapeChar.getId()) + tapeDisplayOffset;

                    refreshTapeDisplay();
                });

                tapeChar.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    tapeHead = Integer.parseInt(tapeChar.getId()) + tapeDisplayOffset;

                    refreshTapeDisplay();
                });

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
            tapeArea.setCenter(headDisplay);
            tapeArea.setBottom(tapeDisplay);
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
        this.tapeDisplayOffset = 0;
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
