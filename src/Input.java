import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Input {

	private Stage popUp;
	private Scene inputScene;
	private Button rightButton;
	private Button leftButton;
	private Button closeButton;

	private Button testButton;

	Input(Stage window){
		/* Constructor */
		/* NOTE: I've included prev here,
				 but you may not have to use it.
				 In the past, I've used it for a back button,
				 but if this is a pop up window, it
				 may be unnecessary.
		*/

		// build the new window
		popUp = new Stage();
		popUp.initModality(Modality.APPLICATION_MODAL);
		popUp.initOwner(window);

		// set the layout of the new window and set the size constraints
		BorderPane layout = new BorderPane();
		inputScene = new Scene(layout, 400, 300);
		popUp.setScene(inputScene);
		System.out.println("Opening Tester");
		popUp.setMinWidth(582);
		popUp.setMinHeight(350);
		popUp.setMaxWidth(582);
		popUp.setMaxHeight(350);

		// add the buttons for the tape to the top of the window
		rightButton = new Button(">>>");
		leftButton = new Button("<<<");
		BorderPane buttonLayout = new BorderPane();
		//buttonLayout.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		buttonLayout.setLeft(leftButton);
		buttonLayout.setRight(rightButton);
		leftButton.setAlignment(Pos.CENTER);
		leftButton.setPadding(new Insets(18, 10, 18, 10));
		rightButton.setPadding(new Insets(18, 10, 18, 10));
		rightButton.setAlignment(Pos.CENTER);

		// create the tape GUI for the TM
		GridPane tape = new GridPane();
		tape.setHgap(0);
		tape.setVgap(0);
		tape.setPadding(new Insets(11, 5, 10, 5));
		//tape.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		for (int i = 0; i < 15; i++) {
			Rectangle thing = new Rectangle(30, 30, Paint.valueOf("#ffffff"));
			thing.setStroke(Paint.valueOf("#000000"));
			tape.setConstraints(thing, i, 0);
			tape.getChildren().add(thing);
		}
		buttonLayout.setCenter(tape);
		buttonLayout.setStyle("-fx-background-color: #dae4e3");
		layout.setTop(buttonLayout);

		// This button is used as a shitty test for the Tester class
		testButton = new Button("Test Machine");
		testButton.setOnAction(e -> shittyTest());

		layout.setCenter(testButton);

		popUp.setTitle("Test Machine");
		popUp.show();
	}
	
	void launch(){
		/* Launch new window here. */

	}

	private boolean shittyTest(){
		Tester t = new Tester();
		Machine m = new Machine();
		Tape tape = m.tape;
		ArrayList<State> states = new ArrayList<>();
		ArrayList<Transition> transitions = new ArrayList<>();

		ArrayList<Character> tmp = new ArrayList<>();
		String s = "Send Nudes";
		for(int i = 0; i < s.length(); i++)
			tmp.add(s.charAt(i));

		tape.initTape(tmp);

		for(int i = 0; i < 11; i++){
			State st = new State();
			st.setName(Integer.toString(i));
			st.setPaths(new ArrayList<>());
			states.add(st);
		}

		for(int i = 0; i < 10; i++){
			Transition tr = new Transition();
			tr.setFromState(states.get(i));
			tr.setToState(states.get(i+1));
			tr.setReadChar('~');
			tr.setWriteChar('-');
			tr.setMoveDirection(Transition.Direction.RIGHT);
			transitions.add(tr);
			states.get(i).getPaths().add(tr);
		}

		states.get(0).setStart(true);
		states.get(9).setAccept(true);

		m.setStates(states);
		m.setTransitions(transitions);
		m.setStartState(states.get(0));

		System.out.print(m.toString());

		System.out.print("\nTape Before Running Machine\n|");
		for(Character c : tape.getTapeAsArray()){
			System.out.printf("%c|", c);
		}
		System.out.print("\n\n");

		try{
			return t.runMachine(m, 0, 0);
		} catch (Exception e){
			return false;
		}
	}
}
