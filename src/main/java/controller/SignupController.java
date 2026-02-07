package controller;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import service.UserService;
import util.ValidationUtil;

public class SignupController {
    //for scene navigation
    private Stage primaryStage;
    private Scene scene;
    private Parent root;
    
    //FXML annotations to bind them to UI components in Signup.fxml file
    @FXML
    private TextField newUser;
    @FXML
    private PasswordField newPass;
    //for showing if signup successful or error like already registered username or wrong password format used
    @FXML
    private Label statusLabel;

    private UserService userService = new UserService();

    public void handleRegistration(ActionEvent event) {
        String username = newUser.getText();
        String password = newPass.getText();

        //validate password using ValidationUtil
        if (!ValidationUtil.isValidPassword(password)) {
            statusLabel.setStyle("-fx-text-fill: RED;");
            statusLabel.setText("Password must be 8+ characters with at least 1 digit, upper, lower, and special.");
            return;
        }

        //register user using UserService method
        if (userService.registerUser(username, password)) {
            statusLabel.setStyle("-fx-text-fill: #4CAF50;"); 
            statusLabel.setText("Success! Redirecting to login...");

            //delay navigation by 2 seconds to let user see message of successful signup
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> {
                try {
                    //switch to login page upon successful signup
                    switchToLogin(event);
                } catch (IOException ioException) {
                    statusLabel.setText("Error loading Login screen.");
                }
            });
            delay.play();
        } else {
            //as registerUser method will return false if user entered a username that already exists
            statusLabel.setStyle("-fx-text-fill: RED;");
            statusLabel.setText("Username already exists or registration failed.");
        }
    }

    //helper methods for switching to either Login or Welcome scenes
    public void switchToLogin(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchToWelcome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Welcome.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}