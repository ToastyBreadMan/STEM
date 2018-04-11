import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
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

import java.util.ArrayList;
import java.util.Collections;

class Editor {
	private Scene editor;
	private ToolBar bar;
	private Pane editorSpace;
	private Machine currentMachine;
	private EventHandler<MouseEvent> currentHandler;
	private Input inputWindow;
	
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

		// open the tester dialog
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
	
	/* Called whenever a new machine is setup */
	void newMachine(Stage window, Scene prev){
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
					if(newValue == bar.getItems().get(0)) {
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
									
									EventHandler<MouseEvent> circleHandler = new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											/* FIXME Debugging prints. */
											/*
											System.out.println("This is " + newState.getName() + ":");
											System.out.println("\t X: " + newState.getX());
											System.out.println("\t Y: " + newState.getY());
											System.out.println("\t Start: " + newState.isStart());
											System.out.println("\t Accept: " + newState.isAccept());
											*/
											
											deletedValues = deleteState(group, newState, deletedValues, this);
										}
									};
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
					
					// FIXME this section was replaced with a listener on the object itself
					else if (newValue == (ToggleButton) bar.getItems().get(1)){
						currentHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								//System.out.println("Delete");
								
							}
						};
						window.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
					}
					else if (newValue == (ToggleButton) bar.getItems().get(2)){

						currentHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								State currentTarget = null;
								BooleanProperty drag = new SimpleBooleanProperty();
								if(event.getTarget() instanceof Circle || event.getTarget() instanceof Text) {
									
									ArrayList<State> states = currentMachine.getStates();
									for(State state : states){
										//System.out.println(state.getName());
										if(state.getName().equals(((Node)event.getTarget()).getId())){
											//System.out.println("Found " + state);
											currentTarget = state;
										}
									}
									
									Line line = new Line();
									line.setMouseTransparent(true);
									
									if(currentTarget != null) {
										
										double centX = currentTarget.getCircle().getCenterX();
										double centY = currentTarget.getCircle().getCenterY();
										
										/*
										currentTarget.getCircle().setOnMouseClicked(e->{
											double testx = event.getX();
											double testy = event.getY();
											line.setStartX(testx);
											line.setStartY(testy);
											line.setEndX(testx);
											line.setEndY(testy);
											drag.set(true);
											editorSpace.getChildren().add(line);
										});
										*/
										
										currentTarget.getCircle().setOnDragDetected(e -> {
											double startX = event.getX();
											double startY = event.getY() - bar.getHeight();
											System.out.println("Drag starts now");
											
											/* FIXME problem if already has a line & single click to add one. */
											
											/*  TODO:
												JFLAP handles this by showing a grey line on drag centered
												in the middle of the circle.
												Upon drop, the line falls behind the state.
											 */
											
											line.setStartX(centX);
											line.setStartY(centY);
											line.setEndX(startX);
											line.setEndY(startY);
											editorSpace.getChildren().add(line);
											drag.set(true);
										});
										
										editorSpace.setOnMouseDragged(e -> {
											if (drag.get()) {
												line.setEndX(e.getX());
												line.setEndY(e.getY());
												line.toBack();
											}
										});
										
										currentTarget.getCircle().setOnMouseReleased(e -> {
											drag.set(false);
											if (e.getTarget() instanceof Circle || e.getTarget() instanceof Text) {
												System.out.println("End");
												//System.out.printf("Landed on %s", ((State)event.getTarget()).getName());
											}
											//editorSpace.getChildren().remove(line);
										});
									}
								}
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

	double distForm(double x1, double x2, double y1, double y2){
		return Math.hypot(x2-x1, y2-y1);
	}
	
	void getInput(Stage window, Scene prev){
		inputWindow = new Input(window, prev);
		inputWindow.launch();
	}
}
