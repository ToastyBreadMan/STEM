import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
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
		window.setMinWidth(200);
		window.setMinHeight(200);

		setUpMenu();
		setUpHelp();
		setUpEditor();
		
		window.setScene(menu);
		window.setTitle("Definitely NOT JFlap");
		window.show();
	}
	
	private void setUpMenu(){
		Menu fileMenu = new Menu("File");
		fileMenu.setStyle("-fx-text-fill: #ffffff");
		fileMenu.setStyle("-fx-font-size: 10px;");
		fileMenu.getItems().add(new MenuItem("Close"));
		
		Menu helpMenu = new Menu("Help");
		helpMenu.setStyle("-fx-text-fill: #839496");
		helpMenu.setStyle("-fx-font-size: 10px;");
		helpMenu.getItems().add(new MenuItem("About"));
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		//menuBar.setStyle("-fx-background-color: #002b36");
		
		Label label = new Label("Welcome to umm... JFLAP?");
		label.setStyle("-fx-text-fill: #839496");
		
		Button tmButton = new Button("Start");
		tmButton.requestFocus();
		tmButton.setOnAction(e-> window.setScene(editor));
		
		Button helpButton = new Button("Help");
		helpButton.setOnAction(e-> window.setScene(help));
		
		setCloseButton();
		
		BorderPane menuLayout = new BorderPane();
		menuLayout.setTop(menuBar);
		menuLayout.setStyle("-fx-background-color: #002b36");
		VBox buttonLayout = new VBox(20);
		buttonLayout.getChildren().addAll(label, tmButton, helpButton, closebutton);
		buttonLayout.setStyle("-fx-background-color: #002b36");
		menuLayout.setCenter(buttonLayout);
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
		helpLayout.setStyle("-fx-background-color: #002b36");
		help = new Scene(helpLayout, 200, 200);
	}
	
	private void setUpEditor(){
		setCloseButton();
		
		Button editorBackButton = new Button("Back");
		editorBackButton.setOnAction(e->window.setScene(menu));
		
		VBox editorLayout = new VBox(30);
		editorLayout.getChildren().addAll(editorBackButton, closebutton);
		editorLayout.setStyle("-fx-background-color: #002b36");
		editor = new Scene(editorLayout, 500, 500);
	}
	
	private void setCloseButton(){
		closebutton = new Button("Close");
		closebutton.setOnAction(e->window.close());
		closebutton.setAlignment(Pos.BOTTOM_RIGHT);
	}
}
