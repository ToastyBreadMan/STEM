import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Editor {
	private Scene editor;
	private ToolBar bar;
	private Pane editorSpace;
	
	public Editor(Stage window, Scene prev){
		BorderPane pane = new BorderPane();
		
		editorSpace = new Pane();
		editorSpace.getChildren().addAll();
		
		pane.setTop(initMenuBar(window, prev));
		pane.setCenter(editorSpace);
		editor = new Scene(pane, 500, 500);
	}
	
	
	public void setMenu(Stage window){
		window.setTitle("Editor");
		window.setMinWidth(500);
		window.setMinHeight(500);
		window.setScene(editor);
	}
	
	private ToolBar initMenuBar(Stage window, Scene prev){
		bar = new ToolBar();
		
		ToggleGroup buttonGroup = new ToggleGroup();
		ToggleButton addState = new ToggleButton("Add State");
		addState.setStyle("-fx-font-size: 10px;");
		addState.setToggleGroup(buttonGroup);
		//addState.selectedProperty().addListener(e-> addStateSelected(addState, window));
		
		ToggleButton deleteState = new ToggleButton("Delete State");
		deleteState.setStyle("-fx-font-size: 10px;");
		deleteState.setToggleGroup(buttonGroup);
		
		Button backButton = new Button("Back");
		backButton.setOnAction(e->window.setScene(prev));
		backButton.setStyle("-fx-font-size: 10px;");
		bar.getItems().addAll(addState, deleteState, backButton);
		bar.setStyle("-fx-background-color: #dae4e3");
		return bar;
	}
	
	/* Add state upon clicking. */
	private void addStateSelected(ToggleButton button, Stage window, Machine machine){
		window.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
			int val = 0;
			@Override
			public void handle (javafx.scene.input.MouseEvent event){
				if(button.isSelected()) {
					State newState = new State();
					newState.setX(event.getX());
					newState.setY(event.getY());
					newState.setStart(false);
					newState.setAccept(false);
					
					/* Note to self:
					to implement auto numbering,
					you need to make an array
					of deleted numbers.
					Check that array before doing val.
					if empty, use val.
					otherwise, delete that number and use it.
					 */
					
					newState.setName(Integer.toString(val));
					System.out.println(val);
					val++;
					
					Circle circle = new Circle();
					circle.setCenterX(event.getX());
					circle.setCenterY(event.getY() - 33.0); //toolbar messes this up by 33
					circle.setRadius(20);
					editorSpace.getChildren().add(circle);
				}
			}
		});
	}
	
	public void newMachine(Stage window, Scene prev){
		Machine currentMachine = new Machine();
		
		int val = 0;
		ToggleButton addState = (ToggleButton) bar.getItems().get(0);
		addState.selectedProperty().addListener(e-> addStateSelected(addState, window, currentMachine));
		
		
		
	}
}
