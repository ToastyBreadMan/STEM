import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	private Stage window;
	private Scene menu;
	private CloseButton closebutton;
	private HelpMenu help;
	private Editor editor;
	
	/* Launch the app */
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		window = primaryStage;
		window.setMinWidth(300);
		window.setMinHeight(400);
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
		VBox buttonLayout = new VBox(20); 				//inner VBox to hold buttons
		buttonLayout.setPadding(new Insets(0, 20, 20, 20));

		ObjectExpression<Font> fontTrack = Bindings.createObjectBinding(
				() -> Font.font(buttonLayout.getWidth() / 25), buttonLayout.widthProperty());

		Label label0 = new Label("Welcome to the Turing Machine Simulator!");
		Label label1 = new Label("To begin, create a new machine.");

		label0.fontProperty().bind(fontTrack);
		label1.fontProperty().bind(fontTrack);

		Button newMachineButton = new Button("New Machine");
		newMachineButton.prefWidthProperty().bind(buttonLayout.widthProperty());
		newMachineButton.prefHeightProperty().bind(buttonLayout.heightProperty());
		newMachineButton.fontProperty().bind(fontTrack);
		newMachineButton.requestFocus();

		Button loadMachineButton = new Button("Load Machine");
		loadMachineButton.prefWidthProperty().bind(buttonLayout.widthProperty());
		loadMachineButton.prefHeightProperty().bind(buttonLayout.heightProperty());
		loadMachineButton.fontProperty().bind(fontTrack);

		Button helpButton = new Button("Help");
		helpButton.prefWidthProperty().bind(buttonLayout.widthProperty());
		helpButton.prefHeightProperty().bind(buttonLayout.heightProperty());
		helpButton.fontProperty().bind(fontTrack);

		/* Set layout. */
		BorderPane menuLayout = new BorderPane(); 				//outer Borderpane to hold menubar
		menuLayout.setTop(menuBar);

		buttonLayout.getChildren().addAll(label0, label1, newMachineButton, loadMachineButton, helpButton); //, closebutton.getCloseButton());

		menuLayout.setCenter(buttonLayout);
		menu = new Scene(menuLayout, 300, 400);
		
		/* After menu is set up, create other scenes. */
		help = new HelpMenu(window, menu);
		helpButton.setOnAction(e-> help.setMenu(window));
		
		newMachineButton.setOnAction(e-> {
			editor = new Editor(window, menu);
			editor.setMenu(window);
			editor.newMachine(window, menu);
			editor = null;
		});

		loadMachineButton.setOnAction(e-> {
			editor = new Editor(window, menu);
			editor.setMenu(window);
			editor.loadMachine(window, menu);
			editor = null;
		});
	}
}
