package Controllers.Admin;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Models.AccountData;
import Models.BookingsModel;
import Models.UsersModel;

import java.io.IOException;
import java.util.ArrayList;
/**
 * @ClassName: AdminController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:37
 * @Version: v1.0
*/
public class AdminController {

    //<editor-fold desc="variables">
    @FXML
    private Button cleanersDashboardButton;
    @FXML
    private Button caterersDashboardButton;
    @FXML
    private Button addAdminButton;
    @FXML
    private Button addRoomButton;
    @FXML
    private AnchorPane fullPane;
    @FXML
    private Label backgroundLabel;
    @FXML
    private Label dbSizeLabel;
    @FXML
    private Label DBOnlineLabel;
    @FXML
    private Label accountLabel;
    @FXML
    private Label headingLabel;
    @FXML
    private Button clearDB;
    @FXML
    private Button reloadButton;
    @FXML
    private PieChart chart;
    @FXML
    private TableView<AccountData> myTable;
    @FXML
    private TableColumn<AccountData, String> IDColumn;
    @FXML
    private TableColumn<AccountData, String> usernameColumn;
    @FXML
    private TableColumn<AccountData, String> firstnameColumn;
    @FXML
    private TableColumn<AccountData, String> lastnameColumn;
    @FXML
    private TableColumn<AccountData, String> passwordColumn;
    @FXML
    private TableColumn<AccountData, String> emailColumn;
    @FXML
    private TableColumn<AccountData, String> accountTypeColumn;
    private ObservableList<AccountData> data;
    private ObservableList pieData;
    int duration = 0;
    //</editor-fold>
    private final BookingsModel bookingsModel = new BookingsModel();
    private final UsersModel usersModel = new UsersModel();

    //Initialises the window, so it will open with the table and pie chart populated
    public void initialize() {
        DBOnlineLabel.setText("DB online");
        setPieData();
        loadCustomerData();
    }

    //This method loads the data onto the pi chart
    public void setPieData() {
            duration = 0;
            pieData = FXCollections.observableArrayList();
            ArrayList<userBookings> ubList = bookingsModel.setPieData();


            //Goes through every single booking we have.
            for (userBookings x : ubList) {
                //To get the bookings we have for today.
                duration = duration + x.getTodayDuration();
                }

            //To get the duration when there are no bookings.

            int free = 7200 - duration;
            pieData = FXCollections.observableArrayList(new PieChart.Data("Bookings", duration),
                    new PieChart.Data("Unoccupied", free));
            //Sets it to null first and then adds the data so that this method can be used when the page is refreshed
            chart.setData(pieData);

    }

    //This method will read the Users table from the database and transfer all the details including their password on to the Tableview
    @FXML
    public void loadCustomerData() {

        this.data = usersModel.loadCustomerData();
        this.IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        this.usernameColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
        this.firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
        this.lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("Lastname"));
        this.passwordColumn.setCellValueFactory(new PropertyValueFactory<>("Password"));
        this.emailColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        this.accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("AccountType"));
        //so that this doubles as a refresh function, i made it so that it removes existing data from the table and then adds them again.
        this.myTable.setItems(null);
        this.myTable.setItems(data);


    }

    //ability to add an admin
    @FXML
    public void addAdmin() {
        try {
            Stage old = (Stage) addAdminButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Admin/adminCreation.fxml").openStream());
            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Register.css").toExternalForm());
            stage.getIcons().add(new Image("/images/add.png"));
            stage.setScene(scene);
            stage.setTitle("Register admin");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reloads the page using data from database. calls the initialise method.
    @FXML
    public void refreshPage() {
        initialize();
    }


    //Delete all booking details before today.
    @FXML
    private void clearUpDB(){
        bookingsModel.clearUpDB();

    }

    //<editor-fold desc="methods that open different windows">

    //redirects you back to login
    @FXML
    private void backToLogin() {
        try {
            Stage old = (Stage) fullPane.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Login/LoginFXML.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Login.css").toExternalForm());
            stage.getIcons().add(new Image("/images/mainIcon.jpeg"));
            stage.setScene(scene);
            stage.setTitle("Meeting booker login");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Closes the current window and opens the cleaners dashboard
    @FXML
    private void openCleanersDashboard() {
        try {
            Stage old = (Stage) cleanersDashboardButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Cleaners/Cleaners.fxml").openStream());
            Scene scene = new Scene(root, 640, 720);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Cleaners.css").toExternalForm());
            stage.getIcons().add(new Image("/images/cleaners.png"));
            stage.setScene(scene);
            stage.setTitle("Cleaners Dashboard");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Closes the current window and opens the caterers dashboard
    @FXML
    private void openCaterersDashboard() {
        try {
            Stage old = (Stage) caterersDashboardButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Caterers/Caterers.fxml").openStream());
            Scene scene = new Scene(root, 640, 720);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Caterers.css").toExternalForm());
            stage.getIcons().add(new Image("/images/caterers.png"));
            stage.setScene(scene);
            stage.setTitle("Caterers Dashboard");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //</editor-fold>
    //Closes the current window and opens the caterers dashboard
    @FXML
    private void addRoom() {
        try {
            Stage old = (Stage)fullPane.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Room/Room.fxml").openStream());
            Scene scene = new Scene(root, 1100, 600);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Room.css").toExternalForm());
            stage.getIcons().add(new Image("/images/booking.png"));
            stage.setScene(scene);
            stage.setTitle("Add Rooming");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //</editor-fold>

}
