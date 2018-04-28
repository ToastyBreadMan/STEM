import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	//private ArrayList<Integer> deletedValues;
	
	Editor(Stage window, Scene prev){
		BorderPane pane = new BorderPane();
		
		editorSpace = new Pane();
		
		pane.setTop(initMenuBar(window, prev));
		pane.setCenter(editorSpace);
		editor = new Scene(pane, 500, 500);
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
		
		ToggleGroup buttonGroup = new ToggleGroup();
		ToggleButton addState = new ToggleButton("Add State");
		addState.setStyle("-fx-font-size: 10px;");
		addState.setToggleGroup(buttonGroup);
		
		ToggleButton deleteState = new ToggleButton("Delete State");
		deleteState.setStyle("-fx-font-size: 10px;");
		deleteState.setToggleGroup(buttonGroup);
		
		ToggleButton addTransition = new ToggleButton("Add Transition");
		addTransition.setStyle("-fx-font-size: 10px;");
		addTransition.setToggleGroup(buttonGroup);

		/* Open the tester dialog. */
		Button testButton = new Button("Test Machine");
		testButton.setStyle("-fx-font-size: 10px");
		testButton.setOnAction(e-> getInput(window, prev));
		
		Button backButton = new Button("Back");
		backButton.setOnAction(e->deleteEditor(window, prev));
		backButton.setStyle("-fx-font-size: 10px;");
		bar.getItems().addAll(addState, deleteState, addTransition, testButton, backButton);
		bar.setStyle("-fx-background-color: #dae4e3");
		return bar;
	}
	
	/*
	FIXME
	If someone can clean up this next function, please do so.
	 */
	
	
	/* Called whenever a new machine is setup */
	void newMachine(Stage window, Scene prev){
		currentMachine = new Machine();
		ToggleGroup group = ((ToggleButton) bar.getItems().get(0)).getToggleGroup();
		
		/* Add listener for toggle button selections. */
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			int val = 0;
			int savedVal;
			int minIndex;
			ArrayList<Integer> deletedValues = new ArrayList<Integer>();
			
			/* Handler to change cursor on drag. */
			EventHandler<MouseEvent> MoveEvent = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setCursor(event);
				}
			};
			
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(currentHandler != null) {
					window.removeEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
				}
				if(newValue != null){
					if(newValue == bar.getItems().get(0)) {
						/* This needs to be a filter so it can run before other event stuff. */
						editor.addEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
						
						currentHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								double minDist= calcDist(event, currentMachine);
								if (!(event.getTarget() instanceof Circle) && !(event.getTarget() instanceof Text)
										&& (minDist / 2) >= 20 && event.getY() - bar.getHeight() > 20) {
									
									State newState = new State();
									newState.setX(event.getX());
									newState.setY(event.getY() - bar.getHeight());
									newState.setStart(false);
									newState.setAccept(false);
									
									/* check for deleted values */
								
									if (deletedValues.isEmpty()) {
										newState.setName(Integer.toString(val));
										System.out.println(val);
										val++;
									} else {
										minIndex = deletedValues.indexOf(Collections.min(deletedValues));
										savedVal = deletedValues.get(minIndex);
										deletedValues.remove(minIndex);
										System.out.println(savedVal);
										newState.setName(Integer.toString(savedVal));
									}
									
									
									/* add circle and name centered on click */
									Circle circle = new Circle();
									circle.setFill(Color.LIGHTGOLDENRODYELLOW);
									circle.setStrokeWidth(3);
									circle.setStroke(Color.BLACK);
									circle.setId(newState.getName());
									Text label = new Text(newState.getName());
									label.setId(newState.getName());
									circle.setCenterX(event.getX());
									circle.setCenterY(event.getY() - (bar.getHeight())); /* toolbar messes this up */
								
									circle.setRadius(20);
									label.setX(circle.getCenterX() - (label.getLayoutBounds().getWidth() / 2));
									label.setY(circle.getCenterY() + (label.getLayoutBounds().getHeight() / 4));
									newState.setCircle(circle);
									newState.setLabel(label);
									
									EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											addTransition(event);
										}
									};
									
									EventHandler<MouseEvent> circleHandler = new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											if(group.getSelectedToggle() == group.getToggles().get(1)){
												deletedValues = deleteState(group, newState, deletedValues, this);
											}
										}
									};
									label.addEventFilter(MouseEvent.DRAG_DETECTED, dragHandler);
									circle.addEventFilter(MouseEvent.DRAG_DETECTED, dragHandler);
									label.addEventHandler(MouseEvent.MOUSE_CLICKED, circleHandler);
									circle.addEventHandler(MouseEvent.MOUSE_CLICKED, circleHandler);
									
									newState.setClickListener(circleHandler);
									currentMachine.addState(newState);
									editorSpace.getChildren().addAll(newState.getCircle(), label);
								}
							}
						};
						window.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
					}
					else{
						editor.removeEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
						if(currentHandler != null) {
							window.removeEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
						}
					}
				}
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
