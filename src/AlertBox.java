import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
	public static void displayAlert(String title, String message){
		Stage popup = new Stage();
		
		//handle this window before moving on
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.setTitle("Help");
		popup.setMinWidth(250);
		
		Label label = new Label("Help menu here");
		
		Button closeButton = new Button("Close");
		closeButton.setOnAction(e-> popup.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		popup.setScene(scene);
		popup.showAndWait();
	}
}
