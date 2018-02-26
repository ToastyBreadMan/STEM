import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	
	Stage window;
	Scene menu, help, editor;
	Button closebutton;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		window = primaryStage;

		setUpMenu();
		setUpHelp();
		setUpEditor();
		
		window.setScene(menu);
		window.setTitle("Definitely NOT JFlap hello git");
		window.show();
	}
	
	private void setUpMenu(){
		Button toolFile = new Button("File");
		toolFile.setStyle("-fx-font-size: 10px;");
		//toolFile.setStyle("-fx-text-fill: #839496");
		Button toolHelp = new Button("Help");
		toolHelp.setStyle("-fx-font-size: 10px;");
		//toolHelp.setStyle("-fx-text-fill: #839496");
		ToolBar toolbar = new ToolBar(toolFile, toolHelp);
		toolbar.setStyle("-fx-background-color: #002b36");
		
		Label label = new Label("Welcome to umm... JFLAP?");
		label.setStyle("-fx-text-fill: #839496");
		
		
		Button tmButton = new Button("Start");
		tmButton.requestFocus();
		tmButton.setOnAction(e-> window.setScene(editor));
		
		Button helpButton = new Button("Help");
		helpButton.setOnAction(e-> window.setScene(help));
		
		setCloseButton();
		
		VBox menuLayout = new VBox(10);
		menuLayout.getChildren().addAll(toolbar, label, tmButton, helpButton, closebutton);
		menuLayout.setStyle("-fx-background-color: #002b36");
		menu = new Scene(menuLayout, 300, 300);
	}
	
	private void setUpHelp(){
		setCloseButton();
		
		Button helpBackButton = new Button("Back");
		helpBackButton.setOnAction(e->window.setScene(menu));
		
		Button displayHelpButton = new Button("Settings");
		displayHelpButton.setOnAction(e-> AlertBox.displayAlert("Title", "Wow"));
		
		VBox helpLayout = new VBox(30);
		helpLayout.getChildren().addAll(helpBackButton, displayHelpButton, closebutton);
		help = new Scene(helpLayout, 200, 200);
	}
	
	private void setUpEditor(){
		setCloseButton();
		
		VBox editorLayout = new VBox(30);
		editorLayout.getChildren().addAll(closebutton);
		editorLayout.setStyle("-fx-background-color: #002b36");
		editor = new Scene(editorLayout, 500, 500);
	}
	
	private void setCloseButton(){
		closebutton = new Button("Close");
		closebutton.setOnAction(e->window.close());
		closebutton.setAlignment(Pos.BOTTOM_RIGHT);
	}
}
