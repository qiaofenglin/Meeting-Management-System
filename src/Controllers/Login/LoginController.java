package Controllers.Login;

import Utils.MD5Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Models.UsersModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * @ClassName: LoginController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class LoginController implements Initializable {

    //<editor-fold desc="variables">
    //This stores the information of the current user
    public static User currentUser;
    //creates a LoginModel
    private final UsersModel usersModel = new UsersModel();
    //If any variable are not used in the methods, then are probably are used for styling in the stylesheet (Login.css) which is in the stylesheet folder
    @FXML
    private Label connectionLabel;
    @FXML
    private Label credentialsLabel;
    @FXML
    private Label meetingBookerLabel;
    @FXML
    private Label backgroundLabel;
    @FXML
    private ImageView background;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> accountType;
    //</editor-fold>


//Initialize method the tells us whether if successfully connected to the database or not
    public void initialize(URL url, ResourceBundle rb) {
        if (this.usersModel.isConnected()) {
            connectionLabel.setText("Connected to DB");
        } else{
            connectionLabel.setText("DB offline");
        }
        accountType.getItems().addAll("Admin", "Customer");
        Image image = new Image("/images/login.png");
        background = new ImageView(image);
        background.setFitHeight(200);
        background.setFitWidth(600);
        //background.setPreserveRatio(true);
        backgroundLabel.setGraphic(background);
    }



    //This method will take the inputs from the fields, verify that they are correct and will take them to their respective window, based on their account type
    @FXML
    public void login() {
        try {
            //this if statement uses the loginModel object created in the beginning of this class and uses the isLogin method.
            if (this.usersModel.isLogin(this.usernameField.getText(), MD5Utils.stringToMD5(this.passwordField.getText()), accountType.getValue())) {
                //This deletes the current stage (the current stage needs to be reached from the button) and opens the new stage
                Stage stage = (Stage) this.loginButton.getScene().getWindow();
                stage.close();
                switch (accountType.getValue()) {
                    case "Admin":
                        adminLogin(currentUser);
                        break;
                    case "Customer":
                        customerLogin(currentUser);
                        break;
                }
            } else {
                credentialsLabel.setText("Invalid log in!");
            }
        } catch (Exception ignored) {
        }
    }

    //<editor-fold desc="methods to open other windows">
    //Launches the admin dashboard, the related files are in the Admin package
    public void adminLogin(User currentUser) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Admin/Admin.fxml").openStream());
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Admin.css").toExternalForm());
            stage.getIcons().add(new Image("/images/admin.png"));
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Launches the customer dashboard, the related files are in the Customer package
    public void customerLogin(User currentUser) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Customer/Customer.fxml").openStream());
            Scene scene = new Scene(root, 900, 720);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Customer.css").toExternalForm());
            stage.getIcons().add(new Image("/images/customer.png"));
            stage.setScene(scene);
            stage.setTitle("Customer Dashboard");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Launches the registration page to create a new User
    public void registerUser(){
        try {
            //We need to get the old stage, so we can close it before we open the Register page
            Stage old = (Stage)registerButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Register/Register.fxml").openStream());
            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Register.css").toExternalForm());
            stage.getIcons().add(new Image("/images/add.png"));
            stage.setScene(scene);
            stage.setTitle("Register Page");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //</editor-fold>

}
