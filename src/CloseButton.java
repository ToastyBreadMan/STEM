import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/*
	NOTE: This class is probably useless.
		  We will probably get rid of this during cleanup.
 */

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
