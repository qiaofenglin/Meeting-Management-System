package Controllers.Register;

import Utils.MD5Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Models.UsersModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @ClassName: RegisterController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class RegisterController {

    //<editor-fold desc="variables">
    @FXML
    private Label Heading;
    @FXML
    private Label errorLabel;
    @FXML
    private Label backgroundLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField verifyPasswordField;
    @FXML
    private TextField emailField;
    private final UsersModel usersModel = new UsersModel();
    //</editor-fold>

    //This will take the following as parameters to add their details into the database. The ID column will be automatically incremented
    public boolean registerLogic(String Username, String Firstname, String Lastname, String Password, String Email) throws SQLException {
        Password=MD5Utils.stringToMD5(Password);
        return usersModel.registerLogic( Username,  Firstname,  Lastname,  Password,  Email);
    }

    //This will make sure that the required format is entered returning true/false. For this project only verification of email is required.
    public boolean checkFormat() {
        String regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailField.getText());
        return matcher.matches();
    }

    //This will take you back to the login page.
    public void backToLogin() {
        try {
            //We need to get the old stage, so we can close it before we go back to the login page
            Stage old = (Stage) registerButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Login/LoginFXML.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Login.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Register Page");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void registerUser() throws SQLException {
        try {

            if (!usernameField.getText().isEmpty()
                    && !firstNameField.getText().isEmpty()
                    && !lastNameField.getText().isEmpty()
                    && !emailField.getText().isEmpty()
                    && !passwordField.getText().isEmpty()
                    && !verifyPasswordField.getText().isEmpty()) {
                if (checkFormat()) {
                    if (passwordField.getText().equals(verifyPasswordField.getText())) {
                        if (registerLogic(usernameField.getText(), firstNameField.getText(), lastNameField.getText(), passwordField.getText(), emailField.getText())) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Message");
                            alert.setHeaderText(null);
                            alert.setContentText("Account created!");

                            alert.showAndWait().ifPresent((btnType) -> {
                                if (btnType == ButtonType.OK) {
                                    backToLogin();
                                }
                            });
                        }else{
                            errorLabel.setText("Account cannot be created with current details");
                        }
                    } else {
                        errorLabel.setText("Passwords do not match!");
                    }

                } else {
                    errorLabel.setText("Invalid email address!");
                }
            }else{
                errorLabel.setText("Missing information!");

            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }
}
