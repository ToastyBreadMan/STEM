import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class TransitionEditor {
    private ToggleGroup col3Toggle;

    public TransitionEditor(Stage window, State from, State to){
        Stage transitionEditor = new Stage();
        transitionEditor.initModality(Modality.APPLICATION_MODAL);
        transitionEditor.initOwner(window);

        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox();
        VBox col1 = new VBox();
        VBox col2 = new VBox();
        VBox col3 = new VBox();


        //    ____      _    _
        //   / ___|___ | |  / |
        //  | |   / _ \| |  | |
        //  | |___ (_) | |  | |
        //   \____\___/|_|  |_|
        //
        Text col1Text = new Text("Read");

        TextField col1TextArea = new TextField();
        col1TextArea.setPrefColumnCount(1);
        col1TextArea.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            if(newText.length() > 1 || containsIllegalCharacters(newText))
                return null;
            else
                return change;
        }));
        col1TextArea.setAlignment(Pos.CENTER);
        col1TextArea.setStyle("-fx-padding: 5px; -fx-border-insets: 5px; -fx-background-insets:5px;");

        col1.getChildren().addAll(col1Text,col1TextArea);

        //    ____      _    ____
        //   / ___|___ | |  |___ \
        //  | |   / _ \| |    __) |
        //  | |___ (_) | |   / __/
        //   \____\___/|_|  |_____|
        //
        Text col2Text= new Text("Write");

        TextField col2TextArea = new TextField();
        col2TextArea.setPrefColumnCount(1);
        col2TextArea.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            if(newText.length() > 1 || containsIllegalCharacters(newText))
                return null;
            else
                return change;
        }));
        col2TextArea.setAlignment(Pos.CENTER);
        col2TextArea.setStyle("-fx-padding: 5px; -fx-border-insets: 5px; -fx-background-insets: 5px;");

        col2.getChildren().addAll(col2Text, col2TextArea);

        //    ____      _    _____
        //   / ___|___ | |  |___ /
        //  | |   / _ \| |    |_ \
        //  | |___ (_) | |   ___) |
        //   \____\___/|_|  |____/
        //
        Text col3Text = new Text("Move");

        VBox col3VBox = new VBox();

        col3Toggle = new ToggleGroup();
        ToggleButton left = new ToggleButton("<-");
        ToggleButton right = new ToggleButton("->");
        ToggleButton none = new ToggleButton("N");

        left.setUserData("left");
        left.setToggleGroup(col3Toggle);
        right.setUserData("right");
        right.setToggleGroup(col3Toggle);
        none.setUserData("none");
        none.setToggleGroup(col3Toggle);

        col3VBox.getChildren().addAll(left, right, none);

        col3.getChildren().addAll(col3Text, col3VBox);

        //   ____        _               _ _
        //  / ___| _   _| |__  _ __ ___ (_) |_
        //  \___ \| | | | '_ \| '_ ` _ \| | __|
        //   ___) | |_| | |_) | | | | | | | |_
        //  |____/ \__,_|_.__/|_| |_| |_|_|\__|
        //

        Button submit = new Button("Confirm");
        borderPane.setAlignment(submit, Pos.CENTER);

        //   ____  _           _ _
        //  | __ )(_)_ __   __| (_)_ __   __ _ ___
        //  |  _ \| | '_ \ / _` | | '_ \ / _` / __|
        //  | |_) | | | | | (_| | | | | | (_| \__ \
        //  |____/|_|_| |_|\__,_|_|_| |_|\__, |___/
        //                               |___/
        col1.setAlignment(Pos.TOP_CENTER);
        col2.setAlignment(Pos.TOP_CENTER);
        col3.setAlignment(Pos.TOP_CENTER);

        hBox.getChildren().addAll(col1,col2,col3);

        ObjectExpression<Font> col1TextAreaFontTrack = Bindings.createObjectBinding(
                () -> Font.font(col1TextArea.getWidth() / 2), col1TextArea.widthProperty());
        ObjectExpression<Font> col2TextAreaFontTrack = Bindings.createObjectBinding(
                () -> Font.font(col2TextArea.getWidth() / 2), col2TextArea.widthProperty());
        ObjectExpression<Font> toggleButtonFontTrack = Bindings.createObjectBinding(
                () -> Font.font(left.getWidth() / 8), left.widthProperty());

        col1TextArea.prefHeightProperty().bind(col1TextArea.widthProperty());
        col1TextArea.fontProperty().bind(col1TextAreaFontTrack);

        col2TextArea.prefHeightProperty().bind(col2TextArea.widthProperty());
        col2TextArea.fontProperty().bind(col2TextAreaFontTrack);

        left.prefWidthProperty().bind(col3.widthProperty());
        left.prefHeightProperty().bind(col2TextArea.widthProperty().divide(3.2));
        left.fontProperty().bind(toggleButtonFontTrack);
        right.prefWidthProperty().bind(col3.widthProperty());
        right.prefHeightProperty().bind(col2TextArea.widthProperty().divide(3.2));
        right.fontProperty().bind(toggleButtonFontTrack);
        none.prefWidthProperty().bind(col3.widthProperty());
        none.prefHeightProperty().bind(col2TextArea.widthProperty().divide(3.2));
        none.fontProperty().bind(toggleButtonFontTrack);

        col1.prefWidthProperty().bind(hBox.widthProperty().divide(3));
        col2.prefWidthProperty().bind(hBox.widthProperty().divide(3));
        col3.prefWidthProperty().bind(hBox.widthProperty().divide(3));


        borderPane.setCenter(hBox);
        borderPane.setBottom(submit);
        Scene popUp = new Scene(borderPane, 300,200);
        transitionEditor.setScene(popUp);

        transitionEditor.minHeightProperty().bind(borderPane.widthProperty().divide(2));

        transitionEditor.show();
    }

    private boolean containsIllegalCharacters(String s){
        char s_arr[] = s.trim().toCharArray();

        for(char c : s_arr){
            if(c < 32 || c >= 127)
                return true;
        }

        return false;
    }
}
