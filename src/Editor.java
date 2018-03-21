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

public class Editor {
	private Scene editor;
	private ToolBar bar;
	private Pane editorSpace;
	private Machine currentMachine;
	private EventHandler<MouseEvent> currentHandler;
	
	public Editor(Stage window, Scene prev){
		BorderPane pane = new BorderPane();
		
		editorSpace = new Pane();
		//editorSpace.getChildren().addAll();
		
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
	
	/* Add state upon clicking. */
	private void addStateSelected(ToggleButton button, Stage window, Machine machine){
		window.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
			int val = 0;
			int savedVal;
			ArrayList<Integer> deletedValues;
			@Override
			public void handle(javafx.scene.input.MouseEvent event) {
				if (button.isSelected()) {
					State newState = new State();
					newState.setX(event.getX());
					newState.setY(event.getY());
					newState.setStart(false);
					newState.setAccept(false);
					
					/*
					if (deletedValues.isEmpty()) {
						newState.setName(Integer.toString(val));
						val++;
						System.out.println(val);
					}
					else {
						savedVal = deletedValues.get(0);
						deletedValues.remove(0);
						newState.setName(Integer.toString(savedVal));
						System.out.println(savedVal);
					}
					*/

					newState.setName(Integer.toString(val));
					System.out.println(val);
					val++;

					Circle circle = new Circle();
					circle.setCenterX(event.getX());
					circle.setCenterY(event.getY() - bar.getHeight()); //toolbar messes this up
					circle.setRadius(20);
					editorSpace.getChildren().add(circle);
				}
			}
		});
	}
	
	public void newMachine(Stage window, Scene prev){
		currentMachine = new Machine();
		
		ToggleGroup group = ((ToggleButton) bar.getItems().get(0)).getToggleGroup();
		
		//add listener for toggle button selections
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			int val = 0;
			int savedVal;
			ArrayList<Integer> deletedValues;
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
								
								/*
								if (deletedValues.isEmpty()) {
									newState.setName(Integer.toString(val));
									val++;
									System.out.println(val);
								}
								else {
									savedVal = deletedValues.get(0);
									deletedValues.remove(0);
									newState.setName(Integer.toString(savedVal));
									System.out.println(savedVal);
								}
								*/
								//System.out.println(deletedValues.isEmpty());
								
								newState.setName(Integer.toString(val));
								System.out.println(val);
								val++;
								
								Circle circle = new Circle();
								circle.setCenterX(event.getX());
								circle.setCenterY(event.getY() - bar.getHeight()); //toolbar messes this up
								circle.setRadius(20);
								editorSpace.getChildren().add(circle);
							}
						};
						window.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
					}
					else if (newValue == (ToggleButton) bar.getItems().get(1)){
						currentHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								System.out.println("Delete");
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
}
