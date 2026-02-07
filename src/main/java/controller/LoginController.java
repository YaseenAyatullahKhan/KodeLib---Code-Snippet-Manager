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
import model.User;
import java.util.Optional;

public class LoginController {
    //for scene navigation
    private Stage primaryStage;
    private Scene scene;
    private Parent root;

    //FXML annotations to bind them to UI components in Login.fxml file
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;
    //for showing if login successful or error of invalid username or password entered
    @FXML
    private Label statusLabel;

    private UserService userService = new UserService();
    public static String loggedInUsername = ""; //store current logged-in user

    @FXML
    public void handleSignIn(ActionEvent event) {
        String inputUser = userField.getText();
        String inputPass = passField.getText();

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: RED;");
            statusLabel.setText("Please enter username and password.");
            return;
        }

        //authenticate using Optional class and UserService method
        Optional<User> user = userService.authenticate(inputUser, inputPass);
        
        if (user.isPresent()) {
            loggedInUsername = inputUser; //store logged-in user
            statusLabel.setStyle("-fx-text-fill: #4CAF50;");
            statusLabel.setText("Login Successful! Welcome " + inputUser);
            
            //delay navigation by 2 seconds to let user see message of successful login
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> {
                try {
                    switchToHome(event);
                } catch (IOException ioException) {
                    statusLabel.setText("Error loading Home screen.");
                }
            });
            delay.play();
            
        } else {
            statusLabel.setStyle("-fx-text-fill: RED;");
            statusLabel.setText("Invalid username or password.");
        }
    }

    //helper methods for switching to either Welcome or Home scenes
    public void switchToWelcome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Welcome.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void switchToHome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Home.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
