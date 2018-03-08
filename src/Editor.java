import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Editor {
	private Scene editor;
	
	public Editor(Stage window, Scene prev){
		BorderPane pane = new BorderPane();
		
		Label label = new Label("Editor goes here");
		
		Pane editorSpace = new Pane();
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
		ToolBar bar = new ToolBar();
		
		ToggleGroup buttonGroup = new ToggleGroup();
		ToggleButton addState = new ToggleButton("Add State");
		addState.setStyle("-fx-font-size: 10px;");
		addState.setToggleGroup(buttonGroup);
		addState.selectedProperty().addListener(e-> addStateSelected(addState, window));
		
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
	private void addStateSelected(ToggleButton button, Stage window){
		window.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle (javafx.scene.input.MouseEvent event){
				if(button.isSelected()) {
					System.out.printf("X: " + event.getX() + "\tY: " + event.getY() + "\n");
				}
			}
		});
	}
}
