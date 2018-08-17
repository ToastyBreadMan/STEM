import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

class Editor {
	private Stage window;
	private Scene editor;
	private ToolBar bar;
	private Pane editorSpace;
	private ToggleGroup toggleGroup;
	private Machine currentMachine;
	private EventHandler<MouseEvent> currentHandler;
	private ArrayList<Integer> deletedValues = new ArrayList<>();
	private State transitionFromState;
	private int stateNextVal = 0;
	private int circleRadius;

	Editor(Stage window, Scene prev){
		this.window = window;

		BorderPane pane = new BorderPane();

		editorSpace = new Pane();
		
		pane.setTop(initMenuBar(window, prev));
		pane.setCenter(editorSpace);

		editor = new Scene(pane, 500, 500);

		pane.prefHeightProperty().bind(editor.heightProperty());
		pane.prefWidthProperty().bind(editor.widthProperty());

		circleRadius = 20;
	}

	void setCircleRadius(int size){
		circleRadius = size;
	}
	
	void setMenu(Stage window){
		window.setTitle("Editor");
		window.setMinWidth(500);
		window.setMinHeight(500);
		window.setScene(editor);
	}
	
	/* This sets up the menu bar, but does NOT set the button actions */
	private ToolBar initMenuBar(Stage window, Scene prev){
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

		// END TOGGLE BUTTONS

		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);

		// Begin NON-Toggle buttons

		/* Open the tester dialog. */
		Button testButton = new Button("Test Machine");
		ObjectProperty<Font> testButtonTrack = new SimpleObjectProperty<>(Font.getDefault());
		testButton.fontProperty().bind(testButtonTrack);
		testButton.setOnAction(e-> getInput(window, prev));
		
		Button backButton = new Button("Back");
		ObjectProperty<Font> backButtonTrack = new SimpleObjectProperty<>(Font.getDefault());
		backButton.fontProperty().bind(backButtonTrack);
		backButton.setOnAction(e->deleteEditor(window, prev));

		// Add toggle buttons
		bar.getItems().addAll(addState, deleteState, addTransition);

		// Add separator
		bar.getItems().add(separator);

		// Add non-toggle buttons
		bar.getItems().addAll(testButton, backButton);

		bar.setStyle("-fx-background-color: #dae4e3");

		// Cursor when over the bar will always be default cursor
		bar.addEventFilter(MouseEvent.MOUSE_MOVED, event -> editor.setCursor(Cursor.DEFAULT));

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


		EventHandler<MouseEvent> MoveEvent = this::setCursor;

		toggleGroup.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {

			//   ____       _
			//  / ___|  ___| |_ _   _ _ __
			//  \___ \ / _ \ __| | | | '_ \
			//   ___) |  __/ |_| |_| | |_) |
			//  |____/ \___|\__|\__,_| .__/
			//                       |_|
			editorSpace.removeEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);
			if(currentHandler != null)
				editorSpace.removeEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
			if(transitionFromState != null){
				transitionFromState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
				transitionFromState = null;
			}

			//   _   _
			//  | \ | | ___  _ __   ___
			//  |  \| |/ _ \| '_ \ / _ \
			//  | |\  | (_) | | | |  __/
			//  |_| \_|\___/|_| |_|\___|
			//
			if(new_toggle == null){
				System.out.println("No toggle selected");
			}

			//      _       _     _   ____  _        _
			//     / \   __| | __| | / ___|| |_ __ _| |_ ___
			//    / _ \ / _` |/ _` | \___ \| __/ _` | __/ _ \
			//   / ___ \ (_| | (_| |  ___) | |_ (_| | |_  __/
			//  /_/   \_\__,_|\__,_| |____/ \__\__,_|\__\___|
			//
			else if (new_toggle.getUserData() == "Add State"){
				System.out.println(new_toggle.getUserData());

				editorSpace.addEventFilter(MouseEvent.MOUSE_MOVED, MoveEvent);

				// Define our new click handler
				currentHandler = event -> {

				    // If click is left click and not too close to another circle
					double minDist = calcDist(event, currentMachine);
					if (event.getButton() == MouseButton.PRIMARY
							&& !(event.getTarget() instanceof Circle)
							&& !(event.getTarget() instanceof Text)
							&& (minDist / 3) >= circleRadius
							&& event.getY() > circleRadius) {

						// Figure out what the name of the state should be;
						String name;
						if (deletedValues.isEmpty()) {
							name = Integer.toString(stateNextVal);
							System.out.println(stateNextVal);
							stateNextVal++;
						} else {
							int minIndex = deletedValues.indexOf(Collections.min(deletedValues));
							int savedVal = deletedValues.get(minIndex);
							deletedValues.remove(minIndex);
							System.out.println(savedVal);
							name = Integer.toString(savedVal);
						}

						Circle c = new Circle(event.getX(), event.getY(), circleRadius, Color.LIGHTGOLDENRODYELLOW);
						c.setId(name);
						c.setStrokeWidth(2);
						c.setStroke(Color.BLACK);

						Text t = new Text(name);
						t.setId(name);
						t.setX(c.getCenterX() - (t.getLayoutBounds().getWidth() / 2));
						t.setY(c.getCenterY() + (t.getLayoutBounds().getHeight() / 4));

						// Set Create State and add it to the Node's user data
						// so it is easy to find if clicked on
						State s = new State(name, event.getX(), event.getY(), t, c);
						c.setUserData(s);
						t.setUserData(s);

						currentMachine.addState(s);
						editorSpace.getChildren().addAll(s.getCircle(), s.getLabel());
					}
				};

				// Add the new event handler to the editorSpace
				editorSpace.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
			}

			//   ____       _      _         ____  _        _
			//  |  _ \  ___| | ___| |_ ___  / ___|| |_ __ _| |_ ___
			//  | | | |/ _ \ |/ _ \ __/ _ \ \___ \| __/ _` | __/ _ \
			//  | |_| |  __/ |  __/ |_  __/  ___) | |_ (_| | |_  __/
			//  |____/ \___|_|\___|\__\___| |____/ \__\__,_|\__\___|
			//
			else if (new_toggle.getUserData() == "Delete State"){
				System.out.println(new_toggle.getUserData());

				currentHandler = event -> {
					if(event.getButton() == MouseButton.PRIMARY
							&& (event.getTarget() instanceof Circle
							|| event.getTarget() instanceof Text)){

						Object Target;
						State targetState;
						ArrayList<Transition> deleteTransitions = new ArrayList<>();

						Target = ((Node) event.getTarget()).getUserData();

						if(Target instanceof State)
							targetState = (State) Target;
						else
							return;

						for(Transition t : currentMachine.getTransitions()){
							if(t.getToState() == targetState) {
								editorSpace.getChildren().removeAll(t.getLine(), t.getLabel());
								t.getFromState().getPaths().remove(t);

								deleteTransitions.add(t);
							}
						}
						currentMachine.getTransitions().removeAll(deleteTransitions);
						deleteTransitions.clear();

						deleteState(targetState);
					}
				};
				editorSpace.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
			}

			//      _       _     _   _____                    _ _   _
			//     / \   __| | __| | |_   _| __ __ _ _ __  ___(_) |_(_) ___  _ __
			//    / _ \ / _` |/ _` |   | || '__/ _` | '_ \/ __| | __| |/ _ \| '_ \
			//   / ___ \ (_| | (_| |   | || | | (_| | | | \__ \ | |_| | (_) | | | |
			//  /_/   \_\__,_|\__,_|   |_||_|  \__,_|_| |_|___/_|\__|_|\___/|_| |_|
			//
			else if (new_toggle.getUserData() == "Add Transition"){
				System.out.println(new_toggle.getUserData());

				currentHandler = event -> {
					if(event.getButton() == MouseButton.PRIMARY){
						if(event.getTarget() instanceof Circle || event.getTarget() instanceof Text){
							Node Target = (Node) event.getTarget();

							if(Target.getUserData() instanceof State){
								State s = (State) Target.getUserData();
								System.out.printf("State: %s\n", s.getName());

								if(transitionFromState == null){
									transitionFromState = s;
									transitionFromState.getCircle().setFill(Color.AQUA);
								}
								else{
									System.out.printf("Create Transition from %s to %s\n", transitionFromState.getName(), s.getName());

									// TODO: Implement add Transition
									addTransition(transitionFromState, s);

									transitionFromState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
									transitionFromState = null;
								}
							}
							else if (Target.getUserData() instanceof Transition){
							}
						}
						else{
							if(transitionFromState != null)
								transitionFromState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
							transitionFromState = null;
						}

					}

				};
				editorSpace.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
			}
		});
	}

	State findState(String id){
		for(State s : currentMachine.getStates())
			if(s.getName().equals(id))
				return s;
		return null;
	}

	// Function to delete state
	private void deleteState(State state){
		editorSpace.getChildren().removeAll(state.getCircle(), state.getLabel());

		currentMachine.getTransitions().removeAll(state.getPaths());

		for(Transition t : state.getPaths())
			editorSpace.getChildren().removeAll(t.getLine(), t.getLabel());
		state.getPaths().clear();

		state.setCircle(null);
		state.setLabel(null);

		currentMachine.deleteState(state);
		deletedValues.add(Integer.parseInt(state.getName()));
		state = null;
	}

	private void setCursor(MouseEvent event){
		if(event.getY() > circleRadius) {
			Circle circle = new Circle(circleRadius, null);
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
	
	private void deleteEditor(Stage window, Scene prev){
		System.out.println("If you see this you should be saving your machine");
		window.setScene(prev);

		//TODO: Save machine before this

		deletedValues.clear();

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
	

	private Transition addTransition(State from, State to) {
		// This window suspends until Transition editor is done.
		TransitionEditor t = new TransitionEditor(window ,from, to);

		// Check if transition is valid is done.
		if(t.createdTransition == null)
			System.out.println("null");
		else
			System.out.printf("Transition: %s -> %s %c %c %s\n", t.createdTransition.getFromState().getName(), t.createdTransition.getToState().getName(),
					t.createdTransition.getReadChar(), t.createdTransition.getWriteChar(), t.createdTransition.getMoveDirection().toString());

		return t.createdTransition;
	}
	
	private double calcDist(MouseEvent event, Machine currentMachine){
		double min = Double.MAX_VALUE;
		if(!(currentMachine.getStates().isEmpty())) {
			State minState = null;
			for (State state : currentMachine.getStates()) {
				double dist = distForm(event.getX(), state.getCircle().getCenterX(),
						event.getY() , state.getCircle().getCenterY());
				if(min > dist){
					min = dist;
					minState = state;
				}
			}
		}
		return min;
	}
	
	private Pair calcCloseState(MouseEvent event, Machine currentMachine){
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

	private double distForm(double x1, double x2, double y1, double y2){
		return Math.hypot(x2-x1, y2-y1);
	}
	
	private void getInput(Stage window, Scene prev){
		Input inputWindow = new Input(window);
		inputWindow.launch();
	}
}
