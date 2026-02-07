package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import controller.LoginController;

public class MainApp extends Application {
    //extending JavaFX's Application class to create a JavaFX GUI application

    @Override
    public void start(Stage primaryStage) {
        try{
            //load welcome screen, use it to create scene object, and set up JavaFX stage
            Parent root = FXMLLoader.load(getClass().getResource("/view/Welcome.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            //if user clicks close button
            primaryStage.setOnCloseRequest(event -> {
            event.consume();
            exit(primaryStage);	
            });
	} catch(Exception e) {
            e.printStackTrace();
        }	
    }
	//method to show dialog box to confirm exiting application, prevent accidental app close
	public void exit(Stage stage){	
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            //check if user is logged in
            boolean isLoggedIn = !LoginController.loggedInUsername.isEmpty() && LoginController.loggedInUsername != null;

            if (isLoggedIn) {
                //for logged in user
                alert.setTitle("Logout");
                alert.setHeaderText("You're about to logout and exit the application!");
                alert.setContentText("Are you sure you want to logout and exit?");
            } else {
                //for user who hasn't logged in yet
                alert.setTitle("Exit Application");
                alert.setHeaderText("Exit KodeLib?");
                alert.setContentText("Are you sure you want to exit?");
            }
		
            if (alert.showAndWait().get() == ButtonType.OK){ stage.close(); }
	}

	public static void main(String[] args) {
		launch(args);
        //javafx method to initialise javafx runtime, create an instance of this MainApp class, and to call the start() method
	}
}