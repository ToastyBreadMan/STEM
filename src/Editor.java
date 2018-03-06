import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Editor {
	private Scene editor;
	private CloseButton closeButton;
	
	public Editor(Stage window, Scene prev){
		//add close button
		closeButton = new CloseButton();
		closeButton.setCloseButton(window);
		
		Label label = new Label("Editor goes here");
		label.setStyle("-fx-text-fill: #839496");
		
		Button backButton = new Button("Back");
		backButton.setOnAction(e->window.setScene(prev));
		
		VBox layout = new VBox(20);
		layout.getChildren().addAll(label, backButton, closeButton.getCloseButton());
		layout.setStyle("-fx-background-color: #002b36");
		editor = new Scene(layout, 300, 300);
	}
	
	public void setMenu(Stage window){
		window.setTitle("Editor");
		window.setMinWidth(200);
		window.setMinHeight(200);
		window.setScene(editor);
	}
}
