import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmAlert {
	public static void confirmAlert(String title, String message){
		Stage confirmMenu = new Stage();
		
		//handle this window before moving on
		confirmMenu.initModality(Modality.APPLICATION_MODAL);
		confirmMenu.setTitle("Confirmation");
		confirmMenu.setMinWidth(100);
		
		Label label = new Label("Are you sure you want to do this?");
		
		Button yes = new Button("Yes");
		
		Button no = new Button("No");
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, yes, no);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		confirmMenu.setScene(scene);
		confirmMenu.showAndWait();
	}
}
