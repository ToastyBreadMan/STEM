import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CloseButton {
	private Button button;
	
	public CloseButton(){
		button = new Button("Close");
	}
	
	public Button getCloseButton(){
		return button;
	}
	
	public void setCloseButton(Stage window){
		button.setOnAction(e->window.close());
	}
}
