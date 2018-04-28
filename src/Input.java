import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class Input {

	private Stage popUp;
	private Scene inputScene;
	private Button rightButton;
	private Button leftButton;
	private Button closeButton;

	
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

		popUp.setTitle("Test Machine");
		popUp.show();
	}
	
	void launch(){
		/* Launch new window here. */

	}
}
