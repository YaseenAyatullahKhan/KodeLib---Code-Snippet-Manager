package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HomeController {
    private Stage primaryStage;
    private Scene scene;
    //private Parent root;
    
    @FXML
    private Label usernameLabel;
    /**
    // getting username from LoginController's static variable set while logging in
    @FXML
    public void initialize() {
        if (usernameLabel != null) {
            String currentUser = LoginController.loggedInUsername;
            if (currentUser != null && !currentUser.isEmpty()) {
                usernameLabel.setText("Welcome, " + currentUser);
            }
        }
    }
    */
    @FXML
    public void switchToAdd(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Editor.fxml"));
        primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    @FXML
    public void switchToSearch(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Search.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @FXML
    public void switchToCollections(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Collections.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}