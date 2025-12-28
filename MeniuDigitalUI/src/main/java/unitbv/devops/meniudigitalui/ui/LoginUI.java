package unitbv.devops.meniudigitalui.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import unitbv.devops.meniudigitalui.entity.User;
import unitbv.devops.meniudigitalui.entity.UserRole;
import unitbv.devops.meniudigitalui.service.AuthService;

public class LoginUI {
    private final SessionFactory sessionFactory;
    private final AuthService authService = new AuthService();
    private Stage stage;

    public LoginUI(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void show(Stage primaryStage) {
        this.stage = primaryStage;

        VBox loginPane = createLoginPane();

        Scene scene = new Scene(loginPane, 400, 300);
        primaryStage.setTitle("Restaurant Management - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLoginPane() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font-size: 12;");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(300);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(300);
        loginButton.setStyle("-fx-font-size: 14;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and password are required");
                return;
            }

            var user = authService.authenticate(username, password);
            if (user.isPresent()) {
                redirectToRole(user.get());
            } else {
                errorLabel.setText("Invalid credentials");
                passwordField.clear();
            }
        });

        root.getChildren().addAll(
                titleLabel,
                new Label("Username:"),
                usernameField,
                new Label("Password:"),
                passwordField,
                errorLabel,
                loginButton
        );

        return root;
    }

    private void redirectToRole(User user) {
        if (user.getRole() == UserRole.GUEST) {
            GuestUI guestUI = new GuestUI(sessionFactory);
            guestUI.show(stage);
        } else if (user.getRole() == UserRole.STAFF) {
            StaffUI staffUI = new StaffUI(sessionFactory, user);
            staffUI.show(stage);
        } else if (user.getRole() == UserRole.ADMIN) {
            AdminUI adminUI = new AdminUI(sessionFactory, user);
            adminUI.show(stage);
        }
    }
}

