import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;

class Editor {
	private Scene editor;
	private ToolBar bar;
	private Pane editorSpace;
	private Machine currentMachine;
	private EventHandler<MouseEvent> currentHandler;
	private Input inputWindow;
	private ToggleGroup toggleGroup;
	//private ArrayList<Integer> deletedValues;
	
	Editor(Stage window, Scene prev){
		BorderPane pane = new BorderPane();

		editorSpace = new Pane();
		
		pane.setTop(initMenuBar(window, prev));
		pane.setCenter(editorSpace);

		editor = new Scene(pane, 500, 500);

		pane.prefHeightProperty().bind(editor.heightProperty());
		pane.prefWidthProperty().bind(editor.widthProperty());
	}
	
	
	void setMenu(Stage window){
		window.setTitle("Editor");
		window.setMinWidth(500);
		window.setMinHeight(500);
		window.setScene(editor);
	}
	
	/* This sets up the menu bar, but does NOT set the button actions */
	ToolBar initMenuBar(Stage window, Scene prev){
		bar = new ToolBar();
		toggleGroup = new ToggleGroup();

		ToggleButton addState = new ToggleButton("Add State");
		ObjectProperty<Font> addStateTrack = new SimpleObjectProperty<>(Font.getDefault());
		addState.fontProperty().bind(addStateTrack);
		addState.setUserData("Add State");
		addState.setToggleGroup(toggleGroup);
		
		ToggleButton deleteState = new ToggleButton("Delete State");
		ObjectProperty<Font> deleteStateTrack = new SimpleObjectProperty<>(Font.getDefault());
		deleteState.fontProperty().bind(deleteStateTrack);
		deleteState.setUserData("Delete State");
		deleteState.setToggleGroup(toggleGroup);
		
		ToggleButton addTransition = new ToggleButton("Add Transition");
		ObjectProperty<Font> addTransitionTrack = new SimpleObjectProperty<>(Font.getDefault());
		addTransition.fontProperty().bind(addTransitionTrack);
		addTransition.setUserData("Add Transition");
		addTransition.setToggleGroup(toggleGroup);

		/* Open the tester dialog. */
		Button testButton = new Button("Test Machine");
		ObjectProperty<Font> testButtonTrack = new SimpleObjectProperty<>(Font.getDefault());
		testButton.fontProperty().bind(testButtonTrack);
		testButton.setOnAction(e-> getInput(window, prev));
		
		Button backButton = new Button("Back");
		ObjectProperty<Font> backButtonTrack = new SimpleObjectProperty<>(Font.getDefault());
		backButton.fontProperty().bind(backButtonTrack);
		backButton.setOnAction(e->deleteEditor(window, prev));

		bar.getItems().addAll(addState, deleteState, addTransition, testButton, backButton);
		bar.setStyle("-fx-background-color: #dae4e3");

		bar.widthProperty().addListener((observable, oldWidth, newWidth) -> {
			addStateTrack.set(Font.font(Math.min(newWidth.doubleValue() / 45, 20)));
			deleteStateTrack.set(Font.font(Math.min(newWidth.doubleValue() / 45, 20)));
			addTransitionTrack.set(Font.font(Math.min(newWidth.doubleValue() / 45, 20)));
			testButtonTrack.set(Font.font(Math.min(newWidth.doubleValue() / 45, 20)));
			backButtonTrack.set(Font.font(Math.min(newWidth.doubleValue() / 45, 20)));
		});

		return bar;
	}
	
	/*
	FIXME
	If someone can clean up this next function, please do so.


	I gotchu fam
	 */
	
	
	/* Called whenever a new machine is setup */
	void newMachine(Stage window, Scene prev){
		currentMachine = new Machine();

		EventHandler<MouseEvent> MoveEvent = event -> setCursor(event);

		toggleGroup.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
			if(new_toggle == null){
				System.out.println("No toggle selected");
				editor.removeEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
			}
			else if (new_toggle.getUserData() == "Add State"){
				System.out.println(new_toggle.getUserData());
				editor.addEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
			}
			else if (new_toggle.getUserData() == "Delete State"){
				System.out.println(new_toggle.getUserData());
				editor.removeEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
			}
			else if (new_toggle.getUserData() == "Add Transition"){
				System.out.println(new_toggle.getUserData());
				editor.removeEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
			}
		});



	}
	
	void setCursor(MouseEvent event){
		if(event.getY() > bar.getHeight() + 20) {
			Circle circle = new Circle(20, null);
			circle.setStroke(Color.BLACK);
			
			SnapshotParameters sp = new SnapshotParameters();
			sp.setFill(Color.TRANSPARENT);
			
			Image img = circle.snapshot(sp, null);
			ImageCursor cursor = new ImageCursor(img, img.getHeight() / 2, img.getWidth() / 2);
			
			editor.setCursor(cursor);
		}
		else{
			editor.setCursor(Cursor.DEFAULT);
		}
	}
	
	void deleteEditor(Stage window, Scene prev){
		System.out.println("If you see this you should be saving your machine");
		window.setScene(prev);

		window.setMinWidth(300);
		window.setMinHeight(400);
		window.setHeight(400);
		window.setWidth(300);

		/* garbage collection to the rescue */
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
	ArrayList<Integer> deleteState(ToggleGroup group, State state, ArrayList<Integer> deletedValues,
								   EventHandler<MouseEvent> eventHandler){
		if(group.getSelectedToggle() == (ToggleButton) bar.getItems().get(1)){
			state.getCircle().removeEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
			state.getLabel().removeEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
			editorSpace.getChildren().removeAll(state.getCircle(), state.getLabel());
			state.setCircle(null);
			state.setLabel(null);
			currentMachine.deleteState(state);
			deletedValues.add(Integer.parseInt(state.getName()));
			state = null;
		}

		return deletedValues;
	}
	
	void addTransition(MouseEvent event) {
		State currentTarget = null;
		BooleanProperty drag = new SimpleBooleanProperty();
		BooleanProperty valid = new SimpleBooleanProperty();
		valid.set(true);
		
		Line line = new Line();
		line.setMouseTransparent(true);
		Transition transition = new Transition();
		transition.setLine(line);
		ArrayList<Transition> transitions = currentMachine.getTransitions();
		
		if (event.getTarget() instanceof Circle || event.getTarget() instanceof Text) {
			ArrayList<State> states = currentMachine.getStates();
			for(State state : states){
				//System.out.println(state.getName());
				if(state.getName().equals(((Node)event.getTarget()).getId())){
					//System.out.println("Found " + state);
					currentTarget = state;
				}
			}
			transition.setFromState(currentTarget);
			
			Node tar = null;
			if(event.getTarget() instanceof Circle){
				tar = (Node)event.getTarget();
			}
			else if(event.getTarget() instanceof Text){
				tar = (Node)event.getTarget();
			}
			
			if(tar != null && currentTarget != null) {
				tar.startFullDrag();
				
				double centX = currentTarget.getCircle().getCenterX();
				double centY = currentTarget.getCircle().getCenterY();
				
				tar.setOnDragDetected(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						double startX = event.getX();
						double startY = event.getY() - bar.getHeight();
						
						line.setStartX(centX);
						line.setStartY(centY);
						line.setEndX(startX);
						line.setEndY(startY);
						editorSpace.getChildren().add(line);
						drag.set(true);
					}
				});
					/* FIXME problem if already has a line & single click to add one. */
					
					/*  TODO:
						JFLAP handles this by showing a grey line on drag centered
						in the middle of the circle.
						Upon drop, the line falls behind the state.
					 */
				
				editorSpace.setOnMouseDragged(e -> {
					if (drag.get()) {
						line.setEndX(e.getX());
						line.setEndY(e.getY());
						line.toBack();
					}
				});
				
				//tar.setOnMouseReleased(e -> {
				tar.setOnMouseReleased(e -> {
					drag.set(false);
					/* Ugh more distance formula... */
					Pair closeStatePair = calcCloseState(e, currentMachine);
					if(((double)closeStatePair.getKey()) < 20){
						line.setEndX(((State) closeStatePair.getValue()).getX());
						line.setEndY(((State) closeStatePair.getValue()).getY());
						transition.setLine(line);
						//System.out.println();
						transition.setToState((State)closeStatePair.getValue());
						System.out.println("From: " + transition.getFromState().getName());
						System.out.println("To: " + transition.getToState().getName());
						transitions.add(transition);
					}
					else{
						valid.set(false);
						System.out.println("Invalid");
						line.setVisible(false);
						editorSpace.getChildren().remove(line);
						//line = null;
						//transition = null;
					}
				});
			}
		}
	}
	
	double calcDist(MouseEvent event, Machine currentMachine){
		double min = Double.MAX_VALUE;
		if(!(currentMachine.getStates().isEmpty())) {
			State minState = null;
			for (State state : currentMachine.getStates()) {
				double dist = distForm(event.getX(), state.getCircle().getCenterX(),
						event.getY() - bar.getHeight(), state.getCircle().getCenterY());
				if(min > dist){
					min = dist;
					minState = state;
				}
			}
		}
		return min;
	}
	
	Pair calcCloseState(MouseEvent event, Machine currentMachine){
		double min = Double.MAX_VALUE;
		State minState = null;
		Pair returnVal = null;
		if(!(currentMachine.getStates().isEmpty())) {
			minState = null;
			for (State state : currentMachine.getStates()) {
				//System.out.printf("Using (%f, %f) and (%f, %f)", event.getX(), event.getY(), state.getCircle().getCenterX(), state.getCircle().getCenterY());
				double dist = distForm(event.getX(), state.getCircle().getCenterX(),
						event.getY(), state.getCircle().getCenterY());
				if(min > dist){
					min = dist;
					minState = state;
					returnVal = new Pair(min, minState);
				}
			}
		}
		return returnVal;
	}

	double distForm(double x1, double x2, double y1, double y2){
		return Math.hypot(x2-x1, y2-y1);
	}
	
	void getInput(Stage window, Scene prev){
		inputWindow = new Input(window);
		inputWindow.launch();
	}
}
