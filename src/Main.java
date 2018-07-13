import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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

		Label label0 = new Label("Welcome to the Turing Machine Simulator!");
		ObjectProperty<Font> label0track = new SimpleObjectProperty<Font>(Font.getDefault());
		Label label1 = new Label("To begin, create a new machine.");
		ObjectProperty<Font> label1track = new SimpleObjectProperty<Font>(Font.getDefault());

		label0.fontProperty().bind(label0track);
		label1.fontProperty().bind(label1track);

		Button newMachineButton = new Button("New Machine");
		newMachineButton.prefWidthProperty().bind(buttonLayout.widthProperty());
		newMachineButton.prefHeightProperty().bind(buttonLayout.heightProperty());
		ObjectProperty<Font> newMachineLabelTrack = new SimpleObjectProperty<Font>(Font.getDefault());
		newMachineButton.fontProperty().bind(newMachineLabelTrack);
		newMachineButton.requestFocus();

		Button loadMachineButton = new Button("Load Machine");
		loadMachineButton.prefWidthProperty().bind(buttonLayout.widthProperty());
		loadMachineButton.prefHeightProperty().bind(buttonLayout.heightProperty());
		ObjectProperty<Font> loadMachineLabelTrack = new SimpleObjectProperty<Font>(Font.getDefault());
		loadMachineButton.fontProperty().bind(loadMachineLabelTrack);

		Button helpButton = new Button("Help");
		helpButton.prefWidthProperty().bind(buttonLayout.widthProperty());
		helpButton.prefHeightProperty().bind(buttonLayout.heightProperty());
		ObjectProperty<Font> helpButtonTrack= new SimpleObjectProperty<Font>(Font.getDefault());
		helpButton.fontProperty().bind(helpButtonTrack);

		//closebutton = new CloseButton();
		//closebutton.setCloseButton(window);
		
		/* Set layout. */
		BorderPane menuLayout = new BorderPane(); 				//outer Borderpane to hold menubar
		menuLayout.setTop(menuBar);
		//menuLayout.setStyle("-fx-background-color: #002b36");

		buttonLayout.getChildren().addAll(label0, label1, newMachineButton, loadMachineButton, helpButton); //, closebutton.getCloseButton());
		//buttonLayout.setStyle("-fx-background-color: #002b36");

		// Set set the label size to be buttonLayout's width / 25
		buttonLayout.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldWidth, Number newWidth) {
				label0track.set(Font.font(newWidth.doubleValue() / 25));
				label1track.set(Font.font(newWidth.doubleValue() / 25));
				newMachineLabelTrack.set(Font.font(newWidth.doubleValue() / 25));
				loadMachineLabelTrack.set(Font.font(newWidth.doubleValue() / 25));
				helpButtonTrack.set(Font.font(newWidth.doubleValue() / 25));
			}
		});

		menuLayout.setCenter(buttonLayout);
		menu = new Scene(menuLayout, 400, 300);
		
		/* After menu is set up, create other scenes. */
		help = new HelpMenu(window, menu);
		helpButton.setOnAction(e-> help.setMenu(window));
		
		newMachineButton.setOnAction(e-> {
			editor = new Editor(window, menu);
			editor.setMenu(window);
			editor.newMachine(window, menu);
			editor = null;
		});
		
	}
}
