package unitbv.devops.meniudigitalui.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingDialog {
    private final Stage dialog;
    private final Label messageLabel;

    public LoadingDialog(Stage owner, String initialMessage) {
        dialog = new Stage();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(60, 60);

        messageLabel = new Label(initialMessage);
        messageLabel.setStyle("-fx-font-size: 14px;");

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 30; -fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 2;");
        vbox.getChildren().addAll(progress, messageLabel);

        Scene scene = new Scene(vbox, 300, 150);
        dialog.setScene(scene);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.close();
    }
}

