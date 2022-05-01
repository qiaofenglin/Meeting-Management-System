package Controllers.Login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 * @ClassName: Login
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class Login extends Application {

    public void start(Stage primaryStage)throws Exception{
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("../../Views/Login/LoginFXML.fxml"));
        Scene scene =  new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/Stylesheets/Login.css").toExternalForm());
        primaryStage.getIcons().add(new Image("/images/mainIcon.jpeg"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Conference reservation system By:qiaofenglin");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }

}
