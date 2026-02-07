package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WelcomeController {
    private Stage primaryStage;
    private Scene scene;
    
    //Welcome.fxml uses these two methods to switch to either Login scene or Signup scene depending on the button clicked in the GUI

    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        //get current window, create and set new scene for Login, then display it
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchToSignup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Signup.fxml"));
        //get current window, create and set new scene for Signup, then display it
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
