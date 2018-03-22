import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
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
import java.util.Collections;

public class Editor {
	private Scene editor;
	private ToolBar bar;
	private Pane editorSpace;
	private Machine currentMachine;
	private EventHandler<MouseEvent> currentHandler;
	
	public Editor(Stage window, Scene prev){
		BorderPane pane = new BorderPane();
		
		editorSpace = new Pane();
		
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
		backButton.setOnAction(e->deleteEditor(window, prev));
		backButton.setStyle("-fx-font-size: 10px;");
		bar.getItems().addAll(addState, deleteState, backButton);
		bar.setStyle("-fx-background-color: #dae4e3");
		return bar;
	}
	
	public void newMachine(Stage window, Scene prev){
		currentMachine = new Machine();
		
		ToggleGroup group = ((ToggleButton) bar.getItems().get(0)).getToggleGroup();
		
		//add listener for toggle button selections
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			int val = 0;
			int savedVal;
			int minIndex;
			ArrayList<Integer> deletedValues = new ArrayList<Integer>();
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(currentHandler != null) {
					window.removeEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
				}
				if(newValue != null){
					System.out.println("Changed to " + newValue);
					if(newValue == (ToggleButton) bar.getItems().get(0)) {
						currentHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								State newState = new State();
								newState.setX(event.getX());
								newState.setY(event.getY());
								newState.setStart(false);
								newState.setAccept(false);
								
								//check for deleted values
								if (deletedValues.isEmpty()) {
									newState.setName(Integer.toString(val));
									System.out.println(val);
									val++;
								}
								else {
									minIndex = deletedValues.indexOf(Collections.min(deletedValues));
									savedVal = deletedValues.get(minIndex);
									deletedValues.remove(minIndex);
									System.out.println(savedVal);
									newState.setName(Integer.toString(savedVal));
								}
								
								/* FIXME Keep this only for a backup.
								newState.setName(Integer.toString(val));
								System.out.println(val);
								val++;
								*/
								
								Circle circle = new Circle();
								circle.setCenterX(event.getX());
								circle.setCenterY(event.getY() - bar.getHeight()); //toolbar messes this up
								circle.setRadius(20);
								newState.setCircle(circle);
								
								EventHandler<MouseEvent> circleHandler = new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent event) {
										/* FIXME Debugging prints. */
										System.out.println("This is " + newState.getName() + ":");
										System.out.println("\t X: " + newState.getX());
										System.out.println("\t Y: " + newState.getY());
										System.out.println("\t Start: " + newState.isStart());
										System.out.println("\t Accept: " + newState.isAccept());
										
										deletedValues = deleteState(group, newState, deletedValues);
									}
								};
								circle.addEventHandler(MouseEvent.MOUSE_CLICKED, circleHandler);
								newState.setClickListener(circleHandler);
								editorSpace.getChildren().add(circle);
							}
						};
						window.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
					}
					else if (newValue == (ToggleButton) bar.getItems().get(1)){
						currentHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								//System.out.println("Delete");
								
							}
						};
						window.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
					}
					else{
						if(currentHandler != null) {
							window.removeEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
						}
					}
				}
			}
		});
	}
	
	public void deleteEditor(Stage window, Scene prev){
		System.out.println("If you see this you should be saving your machine");
		window.setScene(prev);
		//garbage collection to the rescue
		editor = null;
		bar = null;
		editorSpace = null;
		currentMachine = null;
		if(currentHandler != null) {
			window.removeEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
		}
		currentHandler = null;
	}
	
	/* Function to check state of toggle and delete. */
	public ArrayList<Integer> deleteState(ToggleGroup group, State state, ArrayList<Integer> deletedValues){
		if(group.getSelectedToggle() == null){
			//do nothing
		}
		else if(group.getSelectedToggle() == (ToggleButton) bar.getItems().get(0)){
			//do nothing
		}
		else if(group.getSelectedToggle() == (ToggleButton) bar.getItems().get(1)){
			state.getCircle().removeEventHandler(MouseEvent.MOUSE_CLICKED, state.getClickListener());
			editorSpace.getChildren().remove(state.getCircle());
			deletedValues.add(Integer.parseInt(state.getName()));
			state = null;
		}
		else{
			System.out.println("Error?");
		}
		return deletedValues;
	}
}
