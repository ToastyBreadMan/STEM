/*
 *     Simple Turing machine EMulator (STEM)
 *     Copyright (C) 2018  Sam MacLean,  Joel Kovalcson, Dakota Sanders, Matt Matto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

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
	private Polygon startTriangle;
	private ContextMenu contextMenu = initContextMenu();
	private String machineFile;
	private BorderPane tapeArea;
	//private Integer tapeDisplayOffset;

	void setCircleRadius(int size){
		circleRadius = size;
	}

	Editor(Stage window, Scene prev){
		this.window = window;
		//setMenu(window);
		//newMachine(window, prev);
		BorderPane pane = new BorderPane();
		BorderPane tapeArea = new BorderPane();
		this.tapeArea = tapeArea;
		editorSpace = new Pane();

		pane.setCenter(editorSpace);
		pane.setBottom(tapeArea);
		pane.setTop(initMenuBar(window, prev));

		editor = new Scene(pane, 500, 500);

		pane.prefHeightProperty().bind(editor.heightProperty());
		pane.prefWidthProperty().bind(editor.widthProperty());

		circleRadius = 20;
		startTriangle = new Polygon();
	}

	// Call when exiting the Editor
	private boolean deleteEditor(Stage window, Scene prev, Machine m){
		System.out.println("If you see this you should be saving your machine");
		if(machineFile.compareTo(currentMachine.toString()) != 0){
			ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
			ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

			Alert saveAlert = new Alert(Alert.AlertType.WARNING, "You have not saved your machine, would you like to?");
			saveAlert.initOwner(window);
			saveAlert.initModality(Modality.APPLICATION_MODAL);
			saveAlert.setTitle("Warning!");
			saveAlert.getButtonTypes().setAll(yes, no, cancel);

			Optional<ButtonType> buttonData = saveAlert.showAndWait();

			if(buttonData.isPresent() && buttonData.get().getButtonData() == ButtonBar.ButtonData.YES){
				saveMachine(window, currentMachine);
			}
			else if(buttonData.isPresent() && buttonData.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
				return false;
			}
			else if(!buttonData.isPresent())
				return false;
		}

		window.setOnCloseRequest(null);

		window.setScene(prev);

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
		return true;
	}

	void setMenu(Stage window){
		window.setTitle("Editor");
		window.setMinWidth(550);
		window.setMinHeight(550);
		window.setScene(editor);
	}

	//   ___ _   _ ___ _____ ____
	//  |_ _| \ | |_ _|_   _/ ___|
	//   | ||  \| || |  | | \___ \
	//   | || |\  || |  | |  ___) |
	//  |___|_| \_|___| |_| |____/
	//
	private BorderPane initTapeDisplay(BorderPane tapeArea) {
		// StackPane to overlay elements
		// GridPane to display the boxes and the characters BorderPane for buttons
		GridPane tapeDisplay = new GridPane();
		tapeDisplay.setAlignment(Pos.CENTER);

		GridPane headDisplay = new GridPane();
		headDisplay.setAlignment(Pos.CENTER);

		// Move tape view right button
		Button shiftRight = new Button(">>>");
		shiftRight.setPrefWidth(50);
		shiftRight.setPrefHeight(30);

		tapeArea.setPrefHeight(0);
		// Move tape view left button
		Button shiftLeft = new Button("<<<");
		shiftLeft.setPrefWidth(50);
		shiftLeft.setPrefHeight(30);

		tapeArea.setTop(headDisplay);
		tapeArea.setCenter(tapeDisplay);
		tapeArea.setLeft(shiftLeft);
		tapeArea.setRight(shiftRight);
		//tapeArea.setPrefHeight(headDisplay.getHeight() + tapeDisplay.getHeight());

		shiftLeft.setOnMouseClicked((button) -> {
			currentMachine.getTape().decrementDisplayOffset();
			currentMachine.getTape().refreshTapeDisplay();
		});

		shiftRight.setOnMouseClicked((button) -> {
			currentMachine.getTape().incrementDisplayOffset();
			currentMachine.getTape().refreshTapeDisplay();
		});

		currentMachine.getTape().setDisplay(tapeDisplay, headDisplay, tapeArea);
		System.out.println("I'm in here!");
		return tapeArea;
	}


	/* This sets up the menu bar, but does NOT set the button actions */
	private ToolBar initMenuBar(Stage window, Scene prev){
		bar = new ToolBar();
		toggleGroup = new ToggleGroup();
		ObjectExpression<Font> barTextTrack = Bindings.createObjectBinding(
				() -> Font.font(Math.min(bar.getWidth() / 55, 20)), bar.widthProperty());

		ToggleButton addState = new ToggleButton("Add State");
		addState.fontProperty().bind(barTextTrack);
		addState.prefWidthProperty().bind(bar.widthProperty().divide(8));
		addState.setUserData("Add State");
		addState.setToggleGroup(toggleGroup);
		
		ToggleButton deleteState = new ToggleButton("Delete Value");
		deleteState.fontProperty().bind(barTextTrack);
		deleteState.prefWidthProperty().bind(bar.widthProperty().divide(7));
		deleteState.setUserData("Delete Value");
		deleteState.setToggleGroup(toggleGroup);
		
		ToggleButton addTransition = new ToggleButton("Add Transition");
		addTransition.fontProperty().bind(barTextTrack);
		addTransition.prefWidthProperty().bind(bar.widthProperty().divide(6));
		addTransition.setUserData("Add Transition");
		addTransition.setToggleGroup(toggleGroup);
		// END TOGGLE BUTTONS

		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);

		// Begin NON-Toggle buttons
		Button tapeButton = new Button("Edit Tape");
		tapeButton.fontProperty().bind(barTextTrack);
		tapeButton.prefWidthProperty().bind(bar.widthProperty().divide(8));
		tapeButton.setOnAction(e->editTape(window, currentMachine));

		// Run Machine with options for speed
		MenuItem manualControl = new MenuItem("Manual");
		manualControl.setOnAction(e -> currentMachine.setSpeed(-1));
		MenuItem slow = new MenuItem("Slow");
		slow.setOnAction(e -> currentMachine.setSpeed(500));
		MenuItem normal = new MenuItem("Normal");
		normal.setOnAction(e -> currentMachine.setSpeed(250));
		MenuItem fast = new MenuItem("Fast");
		fast.setOnAction(e -> currentMachine.setSpeed(75));
		MenuItem noDelay = new MenuItem("No Delay");
		noDelay.setOnAction(e -> currentMachine.setSpeed(0));

		SplitMenuButton runMachine = new SplitMenuButton(manualControl, slow, normal, fast, noDelay);
		runMachine.setText("Run Machine");
		runMachine.fontProperty().bind(barTextTrack);
		runMachine.prefWidthProperty().bind(bar.widthProperty().divide(5));
		runMachine.setOnAction(e-> runMachine(runMachine, addState, deleteState, addTransition, tapeButton));


		Button saveButton = new Button("Save");
		saveButton.fontProperty().bind(barTextTrack);
		saveButton.prefWidthProperty().bind(bar.widthProperty().divide(14));
		saveButton.setOnAction(event -> saveMachine(window, currentMachine));

		Button backButton = new Button("Back");
		backButton.fontProperty().bind(barTextTrack);
		backButton.prefWidthProperty().bind(bar.widthProperty().divide(14));
		backButton.setOnAction(e->deleteEditor(window, prev, currentMachine));

		// Add toggle buttons
		bar.getItems().addAll(addState, addTransition, deleteState);

		// Add separator
		bar.getItems().add(separator);

		// Add non-toggle buttons
		bar.getItems().addAll(tapeButton, runMachine, saveButton, backButton);

		bar.setStyle("-fx-background-color: #dae4e3");

		// Cursor when over the bar will always be default cursor
		bar.addEventFilter(MouseEvent.MOUSE_MOVED, event -> editor.setCursor(Cursor.DEFAULT));

		return bar;
	}

	private ContextMenu initContextMenu(){
		ContextMenu contextMenu = new ContextMenu();

		MenuItem setStart = new MenuItem("Set Start");
		setStart.setOnAction(event -> {
			State s = (State) contextMenu.getOwnerNode().getUserData();

			editorSpace.getChildren().remove(startTriangle);
			startTriangle.getPoints().clear();

			startTriangle.getPoints().addAll(
					s.getCircle().getCenterX()-circleRadius - 1, s.getCircle().getCenterY(),
					s.getCircle().getCenterX()-2*circleRadius, s.getCircle().getCenterY()-circleRadius,
					s.getCircle().getCenterX()-2*circleRadius, s.getCircle().getCenterY()+circleRadius
			);

			startTriangle.setFill(null);
			startTriangle.setStroke(Color.BLACK);

			editorSpace.getChildren().addAll(startTriangle);
			currentMachine.setStartState(s);
			System.out.printf("State %s is now start\n", currentMachine.getStartState().getName());
		});

		MenuItem toggleAccept = new MenuItem("Toggle Accept");
		toggleAccept.setOnAction(event -> {
			State s = (State) contextMenu.getOwnerNode().getUserData();

			if(s.getAcceptCircle() == null){
				s.setAccept(true);

				Circle c = new Circle(s.getCircle().getCenterX(), s.getCircle().getCenterY()
						, circleRadius * 1.25, null);
				c.setStrokeWidth(2);
				c.setStroke(Color.BLACK);

				s.setAcceptCircle(c);
				editorSpace.getChildren().add(c);
				c.toBack();

				System.out.printf("State %s is accept = %s\n", s.getName(), s.isAccept());
			}
			else {
				s.setAccept(false);

				editorSpace.getChildren().remove(s.getAcceptCircle());

				s.setAcceptCircle(null);
				System.out.printf("State %s is accept = %s\n", s.getName(), s.isAccept());
			}
		});

		MenuItem moveState = new MenuItem("Move State");
		moveState.setOnAction(event -> {
			State s = (State) contextMenu.getOwnerNode().getUserData();
			Double initialX = s.getX();
			Double initialY = s.getY();
			ArrayList<Transition> tl = new ArrayList<>();

			tl.addAll(s.getTransition());
			for(Transition t : currentMachine.getTransitions()){
				if(t.getToState() == s && t.getToState() != t.getFromState()){
					System.out.printf("Adding Transiton %s -> %s, %c ; %c ; %c\n", t.getFromState().getName(), t.getToState().getName(),
							t.getReadChar(), t.getWriteChar(), t.getMoveDirection().toString().charAt(0));
					tl.add(t);
				}
			}

			toggleGroup.selectToggle(null);
			editorSpace.setCursor(Cursor.DEFAULT);

			for (Node n : bar.getItems()) {
				if (n instanceof ToggleButton || n instanceof Button || n instanceof SplitMenuButton)
					n.setDisable(true);
			}

			EventHandler<MouseEvent> move = event1 -> {

				if ((Math.abs(event1.getX() - s.getX()) > 5 || Math.abs(event1.getY() - s.getY()) > 5)
						&& event1.getY() > circleRadius) {

					s.setX(event1.getX());
					s.setY(event1.getY());
					redrawState(s);
					redrawPaths(tl);
				}
			};

			EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					editorSpace.removeEventHandler(MouseEvent.MOUSE_MOVED, move);

					if(event.getButton() == MouseButton.PRIMARY){
						double min = Double.MAX_VALUE;
						for (State state : currentMachine.getStates()) {
							if(state == s)
								continue;
							double dist = distForm(s.getX(), state.getX(),
									s.getY(), state.getY());
							if(min > dist)
								min = dist;
						}

						if(min / 3 < circleRadius){
							s.setX(initialX);
							s.setY(initialY);
							redrawState(s);
							redrawPaths(tl);
						}
					}
					if(event.getButton() == MouseButton.SECONDARY){
						s.setX(initialX);
						s.setY(initialY);
						redrawState(s);
						redrawPaths(tl);
					}

					for (Node n : bar.getItems()) {
						if (n instanceof ToggleButton || n instanceof Button || n instanceof SplitMenuButton)
							n.setDisable(false);
					}
					editorSpace.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
				}
			};

			editorSpace.addEventHandler(MouseEvent.MOUSE_MOVED, move);
			editorSpace.addEventHandler(MouseEvent.MOUSE_CLICKED, click);
		});

		MenuItem setBreak = new MenuItem("Set Breakpoint");
		setBreak.setOnAction(event -> {
			State s = (State) contextMenu.getOwnerNode().getUserData();
			if(!s.isDebug()){
				s.setDebug(true);
				s.getCircle().setStroke(Color.RED);
				System.out.printf("State %s is breakpoint = %s\n", s.getName(), s.isDebug());
			}
			else {
				s.setDebug(false);
				s.getCircle().setStroke(Color.BLACK);
				System.out.printf("State %s is breakpoint = %s\n", s.getName(), s.isDebug());
			}


		});

		contextMenu.getItems().addAll(setStart, toggleAccept, moveState, setBreak);

		return contextMenu;
	}

	//   __  __            _     _              ___       _ _
	//  |  \/  | __ _  ___| |__ (_)_ __   ___  |_ _|_ __ (_) |_ ___
	//  | |\/| |/ _` |/ __| '_ \| | '_ \ / _ \  | || '_ \| | __/ __|
	//  | |  | | (_| | (__| | | | | | | |  __/  | || | | | | |_\__ \
	//  |_|  |_|\__,_|\___|_| |_|_|_| |_|\___| |___|_| |_|_|\__|___/
	//
	public void newMachine(Stage window, Scene prev){
		currentMachine = new Machine();
		startMachine(window, prev);
	}

	public boolean saveMachine(Stage window, Machine m) {
	    SaveLoad saveLoad = new SaveLoad();

		machineFile = m.toString();
	    return saveLoad.saveMachine(window, m);
	}

	public void loadMachine(Stage window, Scene prev){
	    SaveLoad saveLoad = new SaveLoad();

	    currentMachine = saveLoad.loadMachine(window);
	    stateNextVal = saveLoad.getStateNextVal();

		//currentMachine = currentMachine;
		redrawAllStates();
		redrawAllPaths();

		//currentMachine.getTape().refreshTapeDisplay();
		startMachine(window, prev);
	}
	
	/* Called whenever a new machine is setup */
	private void startMachine(Stage window, Scene prev){
		initTapeDisplay(tapeArea);
		machineFile = currentMachine.toString();

		window.setOnCloseRequest(we -> {
			if(!deleteEditor(window, prev, currentMachine))
				we.consume();
		});

		Circle circle = new Circle(circleRadius, null);
		circle.setStroke(Color.BLACK);

		SnapshotParameters sp = new SnapshotParameters();
		sp.setFill(Color.TRANSPARENT);

		Image img = circle.snapshot(sp, null);
		ImageCursor cursor = new ImageCursor(img, img.getHeight() / 2, img.getWidth() / 2);

		EventHandler<MouseEvent> MoveEvent = event -> {
			if(event.getY() > circleRadius)
				editorSpace.setCursor(cursor);
			else
				editorSpace.setCursor(Cursor.DEFAULT);
		};

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
			for (Path p : currentMachine.getPaths())
				p.setTextFillColor(Color.BLACK);

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

						c.setOnContextMenuRequested(event1 -> {
							contextMenu.show(c,event1.getScreenX(), event1.getScreenY());
						});
						t.setOnContextMenuRequested(event2 -> {
							contextMenu.show(t,event2.getScreenX(),event2.getScreenY());
						});

						currentMachine.addState(s);
						editorSpace.getChildren().addAll(s.getCircle(), s.getLabel());
					}
				};

				// Add the new event handler to the editorSpace
				editorSpace.addEventHandler(MouseEvent.MOUSE_CLICKED, currentHandler);
			}


			//   ____       _      _        __     __    _
			//  |  _ \  ___| | ___| |_ ___  \ \   / /_ _| |_   _  ___
			//  | | | |/ _ \ |/ _ \ __/ _ \  \ \ / / _` | | | | |/ _ \
			//  | |_| |  __/ |  __/ |_  __/   \ V / (_| | | |_| |  __/
			//  |____/ \___|_|\___|\__\___|    \_/ \__,_|_|\__,_|\___|
			//
			else if (new_toggle.getUserData() == "Delete Value"){
				System.out.println(new_toggle.getUserData());

				for (Path p : currentMachine.getPaths())
					p.setTextFillColor(Color.DARKRED);

				currentHandler = event -> {
					if(event.getButton() == MouseButton.PRIMARY
							&& (event.getTarget() instanceof Circle
							|| event.getTarget() instanceof Text)){

						Object Target = ((Node) event.getTarget()).getUserData();

						if(Target instanceof State) {
							State targetState;
							ArrayList<Transition> deleteTransitions = new ArrayList<>();
							ArrayList<Path> deletePaths = new ArrayList<>();

							targetState = (State) Target;

							for (Transition t : currentMachine.getTransitions()) {
								if (t.getToState() == targetState) {

									ArrayList<Node> nodes = t.getPath().getAllNodes();
									if (!nodes.isEmpty())
										editorSpace.getChildren().removeAll(t.getPath().getAllNodes());
									t.getFromState().getTransition().remove(t);

									deletePaths.add(t.getPath());
									deleteTransitions.add(t);
								}
							}
							currentMachine.getPaths().removeAll(deletePaths);
							currentMachine.getTransitions().removeAll(deleteTransitions);
							deleteTransitions.clear();
							deletePaths.clear();

							deleteState(targetState);
						}
						else if(Target instanceof Transition){
						    System.out.println("Test");
							ArrayList<Node> nodes;
							Transition targetTransition;

							targetTransition = (Transition) Target;
							nodes = targetTransition.getPath().removeTransition(targetTransition);

							if(!nodes.isEmpty())
								editorSpace.getChildren().removeAll(nodes);

							if(targetTransition.getPath().getAllNodes().isEmpty())
								currentMachine.getPaths().remove(targetTransition.getPath());

							targetTransition.getFromState().getTransition().remove(targetTransition);
							currentMachine.getTransitions().remove(targetTransition);
						}

						for(Transition t : currentMachine.getTransitions())
							System.out.printf("%c ; %c ; %c\n", t.getReadChar(), t.getWriteChar(), t.getMoveDirection().toString().charAt(0));

						for(Path p : currentMachine.getPaths())
							System.out.println(p.toString());
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

									s.getCircle().setFill(Color.AQUA);
									Transition t = addTransition(transitionFromState, s);

									if(t == null){
										transitionFromState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
										s.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
										transitionFromState = null;

										return;
									}

									for (Transition temp : currentMachine.getTransitions()){
										if(temp.compareTo(t))
											return;
									}

									currentMachine.getTransitions().add(t);
									transitionFromState.getTransition().add(t);

									Path path = null;
									for(Path p : currentMachine.getPaths()){
										if(p.compareTo(transitionFromState, s)) {
										    path = p;
										    System.out.println("Found Path");
										    break;
										}
									}

									if (path == null){
										path = new Path(transitionFromState, s);
										System.out.println("New Path");
										currentMachine.getPaths().add(path);
									}

									t.setPath(path);
									ArrayList<Node> nodes = path.addTransition(t);
									editorSpace.getChildren().addAll(nodes);

									for(Node n : nodes)
										if(n instanceof Line || n instanceof CubicCurve)
											n.toBack();

									s.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
									transitionFromState.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
									transitionFromState = null;
								}
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

	//   __  __            _     _              __  __                               _       _   _
	//  |  \/  | __ _  ___| |__ (_)_ __   ___  |  \/  | __ _ _ __  _   _ _ __  _   _| | __ _| |_(_) ___  _ __
	//  | |\/| |/ _` |/ __| '_ \| | '_ \ / _ \ | |\/| |/ _` | '_ \| | | | '_ \| | | | |/ _` | __| |/ _ \| '_ \
	//  | |  | | (_| | (__| | | | | | | |  __/ | |  | | (_| | | | | |_| | |_) | |_| | | (_| | |_| | (_) | | | |
	//  |_|  |_|\__,_|\___|_| |_|_|_| |_|\___| |_|  |_|\__,_|_| |_|\__,_| .__/ \__,_|_|\__,_|\__|_|\___/|_| |_|
	//                                                                  |_|
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

	private void editTape(Stage window, Machine currentMachine) {
		TextInputDialog tapeEdit = new TextInputDialog( currentMachine.getTape().toString());
		tapeEdit.setTitle("Edit Tape");
		tapeEdit.setHeaderText("Valid characters are Ascii values 32-125\nThis includes all alpha-numeric values.");

		tapeEdit.setContentText("Enter a string for the tape (spaces for blank):");
		tapeEdit.initOwner(window);
		tapeEdit.initModality(Modality.APPLICATION_MODAL);

		Optional<String> result = tapeEdit.showAndWait();
		result.ifPresent(tapeString -> {
			ArrayList<Character> characters = new ArrayList<>();
			for(Character c: tapeString.toCharArray()) {
				if (c >= 32 && c < 126) {
					characters.add(c);
				}
				else {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Invalid character(s)");
					alert.setContentText("You input invalid character(s) in your tape.");
					alert.initOwner(window);
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.showAndWait();
					editTape(window, currentMachine);
					return;
				}
			}

			currentMachine.getTape().initTape(characters);
			currentMachine.getTape().refreshTapeDisplay();

		});
	}

	// Function to delete state
	private void deleteState(State state){
		editorSpace.getChildren().removeAll(state.getCircle(), state.getLabel());

		currentMachine.getTransitions().removeAll(state.getTransition());

		for(Transition t : state.getTransition()){
			editorSpace.getChildren().removeAll(t.getPath().getAllNodes());
			currentMachine.getPaths().remove(t.getPath());
			t.setPath(null);
		}
		state.getTransition().clear();

		state.setCircle(null);
		state.setLabel(null);

		if (currentMachine.getStartState() == state){
		    System.out.printf("State %s is start removing...", state.getName());
			currentMachine.setStartState(null);
			editorSpace.getChildren().remove(startTriangle);
		}

		if (state.isAccept())
			editorSpace.getChildren().remove(state.getAcceptCircle());

		currentMachine.deleteState(state);
		deletedValues.add(Integer.parseInt(state.getName()));
		state = null;
	}

	private void runMachine(SplitMenuButton thisButton , Node... args){
		toggleGroup.selectToggle(null);
		for(Node b : args)
			b.setDisable(true);

		Tester tester = new Tester();

		if(currentMachine.getTape().getSize() < 0){
			currentMachine.getTape().initTape(new ArrayList<>(' '));
		}

		if(currentMachine.getSpeed() == -1){
			ObjectExpression<Font> textTrack = Bindings.createObjectBinding(
					() -> Font.font(Math.min(editorSpace.getWidth() / 55, 20)), editorSpace.widthProperty());

			Text t = new Text( "<Right Arrow> Advance one state  <Left Arrow> Back one state  <Esc> Stop Machine");
			t.xProperty().bind(editorSpace.widthProperty().divide(10));
			t.yProperty().bind(editorSpace.heightProperty());
			t.fontProperty().bind(textTrack);
			editorSpace.getChildren().add(t);

			ArrayList<MachineStep> machineSteps = new ArrayList<>();

			currentMachine.getTape().centerTapeDisplay();
			currentMachine.getTape().refreshTapeDisplay();

			EventHandler<KeyEvent> keyPress = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ESCAPE) {
						thisButton.fire();
						System.out.println("ESC");

						keyEvent.consume();
					}
					else if(keyEvent.getCode() == KeyCode.RIGHT) {
						State currentState;

						if(machineSteps.size() == 0){
							currentState = currentMachine.getStartState();
						}
						else{
							System.out.println(machineSteps.get(machineSteps.size()-1).getTransition().toString());
							currentState = machineSteps.get(machineSteps.size()-1).getTransition().getToState();
						}

						System.out.printf("Current State = %s\n", currentState.getName());
						Transition next = tester.nextTransition(currentState, currentMachine.getTape());

						if(next == null) {
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.initOwner(window);
							alert.initModality(Modality.APPLICATION_MODAL);
							alert.setTitle("The machine has finished");

							if (currentState.isAccept()) {
								alert.setGraphic(new ImageView(this.getClass().getResource("checkmark.png").toString()));
								alert.setHeaderText("The machine has finished successfully");
							} else {
								alert.setHeaderText("The machine has finished unsuccessfully");
								alert.setContentText(tester.getFailReason());
							}

							currentMachine.getTape().centerTapeDisplay();
							currentMachine.getTape().refreshTapeDisplay();

							alert.showAndWait();
							keyEvent.consume();
							return;
						}

						System.out.printf("Next = %c %c %s\n", next.getReadChar(), next.getWriteChar(), next.getMoveDirection().toString());
						machineSteps.add(new MachineStep(next, currentMachine.getTape().currentTapeVal()));

						next.getFromState().getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);
						next.getToState().getCircle().setFill(Color.GREENYELLOW);

						if(next.getWriteChar() != '~'){
							try{
								currentMachine.getTape().setTape(next.getWriteChar());
							} catch (Exception e){
								showException(e);
							}
						}

						switch(machineSteps.get(machineSteps.size()-1).getTransition().getMoveDirection()) {
							case LEFT:
								currentMachine.getTape().left();
								break;
							case RIGHT:
								currentMachine.getTape().right();
								break;
							case STAY:
								break;
						}

						currentMachine.getTape().centerTapeDisplay();
						currentMachine.getTape().refreshTapeDisplay();

						keyEvent.consume();
					}
					else if(keyEvent.getCode() == KeyCode.LEFT){
						System.out.println("Left");
						if(machineSteps.size() == 0){
							return;
						}

						MachineStep lastStep = machineSteps.get(machineSteps.size()-1);
						System.out.printf("Next = %c %c %s\n", lastStep.getTransition().getReadChar(), lastStep.getTransition().getWriteChar(), lastStep.getTransition().getMoveDirection().toString());


						lastStep.getTransition().getToState().getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);

						switch(lastStep.getTransition().getMoveDirection()){
							case LEFT:
								currentMachine.getTape().right();
								break;
							case RIGHT:
								currentMachine.getTape().left();
								break;
							case STAY:
								break;
						}

						try {
							currentMachine.getTape().setTape(lastStep.getChar());
						} catch (Exception e){
							showException(e);
						}

						lastStep.getTransition().getFromState().getCircle().setFill(Color.GREENYELLOW);

						currentMachine.getTape().centerTapeDisplay();
						currentMachine.getTape().refreshTapeDisplay();

						machineSteps.remove(machineSteps.size()-1);

						keyEvent.consume();
					}
					t.requestFocus();
				}
			};

			thisButton.setText("Stop Machine");
			thisButton.setOnAction(event -> {
				editorSpace.getChildren().remove(t);

				currentMachine.getTape().refreshTapeDisplay();

				for (State s : currentMachine.getStates())
					s.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);

				for (Node b : args)
					b.setDisable(false);

				machineSteps.clear();
				window.removeEventHandler(KeyEvent.KEY_RELEASED, keyPress);

				System.out.println(machineSteps.size());
				thisButton.setText("Run Machine");
				thisButton.setOnAction(event1 -> runMachine(thisButton, args));
			});

			window.addEventHandler(KeyEvent.KEY_RELEASED, keyPress);
			currentMachine.getStartState().getCircle().setFill(Color.GREENYELLOW);
			t.requestFocus();
		}
		else {
			Task<Void> task = new Task<Void>() {
				@Override
				public Void call() {
					try {
						tester.runMachine(currentMachine);
					} catch (Exception e) {
						showException(e);
					}
					return null;
				}
			};
			task.setOnSucceeded(event -> {
				currentMachine.getTape().refreshTapeDisplay();

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.initOwner(window);
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("The machine has finished");

				if (tester.didSucceed()) {
					alert.setGraphic(new ImageView(this.getClass().getResource("checkmark.png").toString()));
					alert.setHeaderText("The machine has finished successfully");
				} else {
					alert.setHeaderText("The machine has finished unsuccessfully");
					alert.setContentText(tester.getFailReason());
				}

				alert.showAndWait();

				thisButton.setText("Run Machine");
				thisButton.setOnAction(event1 -> runMachine(thisButton, args));

				for (Node b : args)
					b.setDisable(false);
			});
			task.setOnCancelled(event -> {
				currentMachine.getTape().refreshTapeDisplay();

				for (State s : currentMachine.getStates())
					s.getCircle().setFill(Color.LIGHTGOLDENRODYELLOW);

				for (Node b : args)
					b.setDisable(false);

				thisButton.setText("Run Machine");
				thisButton.setOnAction(event1 -> runMachine(thisButton, args));
			});

			thisButton.setText("Stop Machine");
			thisButton.setOnAction(event -> task.cancel());

			new Thread(task).start();
		}
	}

	//   ____          _                      __  __      _   _               _
	//  |  _ \ ___  __| |_ __ __ ___      __ |  \/  | ___| |_| |__   ___   __| |___
	//  | |_) / _ \/ _` | '__/ _` \ \ /\ / / | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
	//  |  _ <  __/ (_| | | | (_| |\ V  V /  | |  | |  __/ |_| | | | (_) | (_| \__ \
	//  |_| \_\___|\__,_|_|  \__,_| \_/\_/   |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
	//
	private void redrawAllStates(){
		for(State s : currentMachine.getStates())
			redrawState(s);
	}

	private void redrawState(State s) {
		editorSpace.getChildren().removeAll(s.getCircle(), s.getLabel());
		if(s.getAcceptCircle()!= null)
			editorSpace.getChildren().remove(s.getAcceptCircle());


		Circle c = new Circle(s.getX(), s.getY(), circleRadius, Color.LIGHTGOLDENRODYELLOW);
		c.setId(s.getName());
		c.setStrokeWidth(2);
		c.setStroke(Color.BLACK);
		s.setCircle(c);
		Text t = new Text(s.getName());
		t.setId(s.getName());
		t.setX(c.getCenterX() - (t.getLayoutBounds().getWidth() / 2));
		t.setY(c.getCenterY() + (t.getLayoutBounds().getHeight() / 4));
		s.setLabel(t);
		// Set Create State and add it to the Node's user data
		// so it is easy to find if clicked on
		c.setUserData(s);
		t.setUserData(s);

		c.setOnContextMenuRequested(event1 -> contextMenu.show(c, event1.getScreenX(), event1.getScreenY()));
		t.setOnContextMenuRequested(event2 -> contextMenu.show(t, event2.getScreenX(), event2.getScreenY()));

		editorSpace.getChildren().addAll(s.getCircle(), s.getLabel());

		if (s.isAccept()) {
			Circle ca = new Circle(s.getCircle().getCenterX(), s.getCircle().getCenterY()
					, circleRadius * 1.25, null);
			ca.setStrokeWidth(2);
			ca.setStroke(Color.BLACK);

			s.setAcceptCircle(ca);
			editorSpace.getChildren().add(s.getAcceptCircle());
		}

		if (s.isStart() || currentMachine.getStartState() == s) {
			editorSpace.getChildren().remove(startTriangle);
			startTriangle.getPoints().clear();

			startTriangle.getPoints().addAll(
					s.getCircle().getCenterX() - circleRadius - 1, s.getCircle().getCenterY(),
					s.getCircle().getCenterX() - 2 * circleRadius, s.getCircle().getCenterY() - circleRadius,
					s.getCircle().getCenterX() - 2 * circleRadius, s.getCircle().getCenterY() + circleRadius
			);

			startTriangle.setFill(null);
			startTriangle.setStroke(Color.BLACK);

			editorSpace.getChildren().addAll(startTriangle);
		}

	}
	
	// TODO: Create better redraw method in path class so we don't have to delete it
	private void redrawPaths(ArrayList<Transition> tl){
	    for(Transition t : tl){
			//System.out.printf("Removing Transition %s -> %s, %c ; %c ; %c\n", t.getFromState().getName(), t.getToState().getName(),
			//		t.getReadChar(), t.getWriteChar(), t.getMoveDirection().toString().charAt(0));
	        if(t.getPath() == null)
	        	continue;

			currentMachine.getPaths().remove(t.getPath());
			System.out.println("Delete" + t.getPath().toString());
			editorSpace.getChildren().removeAll(t.getPath().getAllNodes());

			for(Transition t2 : tl){
			    if(t2 == t)
			    	continue;
				if(t2.getPath() == t.getPath())
					t2.setPath(null);
			}

			t.setPath(null);
		}

	    for(Transition t : tl){
			Path path = null;
			for(Path p : currentMachine.getPaths()){
				if(p.compareTo(t.getFromState(), t.getToState())) {
					path = p;
					break;
				}
			}
			if (path == null){
				path = new Path(t.getFromState(), t.getToState());
				currentMachine.getPaths().add(path);
			}

			t.setPath(path);
			ArrayList<Node> nodes = path.addTransition(t);
			editorSpace.getChildren().addAll(nodes);

			for(Node n : nodes)
				if(n instanceof Line || n instanceof CubicCurve)
					n.toBack();
		}
	}

	private void redrawAllPaths(){
		for(Path p : currentMachine.getPaths())
			editorSpace.getChildren().removeAll(p.getAllNodes());

		currentMachine.getPaths().clear();

		for (Transition t : currentMachine.getTransitions()){
			Path path = null;
			for(Path p : currentMachine.getPaths()){
				if(p.compareTo(t.getFromState(), t.getToState())) {
					path = p;
					System.out.println("Found Path");
					break;
				}
			}

			if (path == null){
				path = new Path(t.getFromState(), t.getToState());
				System.out.println("New Path");
				currentMachine.getPaths().add(path);
			}

			t.setPath(path);
			ArrayList<Node> nodes = path.addTransition(t);
			editorSpace.getChildren().addAll(nodes);

			for(Node n : nodes)
				if(n instanceof Line || n instanceof CubicCurve)
					n.toBack();
		}


	}


	public void showException(Exception e){
		System.out.println(e);
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initOwner(window);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("An Exception has Occurred!");
		alert.setHeaderText(e.toString());
		// TODO: Better send us message
		alert.setContentText("Oops...");

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		String exceptionText = stringWriter.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
	//   __  __       _   _       _____                 _   _
	//  |  \/  | __ _| |_| |__   |  ___|   _ _ __   ___| |_(_) ___  _ __  ___
	//  | |\/| |/ _` | __| '_ \  | |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
	//  | |  | | (_| | |_| | | | |  _|| |_| | | | | (__| |_| | (_) | | | \__ \
	//  |_|  |_|\__,_|\__|_| |_| |_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/
	//
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

	private double distForm(double x1, double x2, double y1, double y2){
		return Math.hypot(x2-x1, y2-y1);
	}
	
}
