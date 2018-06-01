/*
	Upon runtime, generates a window and a menu.
	FIXME: LINE 84
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	private Stage window;
	private Scene menu;
	private HelpMenu help;
	private Editor editor;
	
	/* Launch the app */
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage){
		window = primaryStage;
		window.setMinWidth(200);
		window.setMinHeight(200);
		initMenu();
		window.setScene(menu);
		window.setTitle("Definitely NOT JFlap");
		window.show();
	}
	
	private void initMenu(){
		/* File menu option. */
		Menu fileMenu = new Menu("File");
		fileMenu.setStyle("-fx-font-size: 10px;");
		fileMenu.getItems().add(new MenuItem("Close"));
		
		/* Help menu option */
		Menu helpMenu = new Menu("Help");
		helpMenu.setStyle("-fx-font-size: 10px;");
		helpMenu.getItems().add(new MenuItem("About"));
		MenuBar menuBar = new MenuBar();
		
		/* Add menu options */
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		menuBar.setStyle("-fx-background-color: #dae4e3");
		
		/* Contents of page. */
		Label label0 = new Label("Welcome to the Turing Machine Simulator!");
		Button newMachineButton = new Button("New Machine");
		Label label1 = new Label("To begin, create a new machine.");
		
		newMachineButton.requestFocus();
		Button helpButton = new Button("Help");
		Button closeButton = new Button("Close");
		(closeButton).setOnAction(e-> window.close());
		
		/* Set Border Pane for window. */
		BorderPane menuLayout = new BorderPane();
		menuLayout.setTop(menuBar);
		
		/* Create Vertical Box to hold Labels, Buttons. */
		VBox buttonLayout = new VBox(20);
		buttonLayout.setPadding(new Insets(0, 0, 0, 20));
		buttonLayout.getChildren().addAll(label0, label1, newMachineButton, helpButton, closeButton);
		
		/* Populate center of Border Pane with VBox. */
		menuLayout.setCenter(buttonLayout);
		menu = new Scene(menuLayout, 300, 300);
		
		/* After menu is set up, create other scenes. */
		help = new HelpMenu(window, menu);
		helpButton.setOnAction(e-> help.setMenu(window));
		
		/* Create new machine on button press. */
		newMachineButton.setOnAction(e-> {
			editor = new Editor(window, menu);
			editor.setMenu(window);
			editor.newMachine(window, menu);
			//editor = null; /* FIXME why did I do this? */
		});
		
	}
}
