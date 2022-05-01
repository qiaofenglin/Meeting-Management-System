package Controllers.Customer;



import Controllers.Admin.userBookings;
import Controllers.Login.LoginController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Models.BookingsModel;

import java.io.IOException;

/**
 * @ClassName: CustomerController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class CustomerController {

    //<editor-fold desc="variables">
    @FXML
    private Label currentBookingsLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button newBookingButton;
    @FXML
    private TableView<userBookings> myTable;
    @FXML
    private TableColumn<userBookings, Integer> roomIDColumn;
    @FXML
    private TableColumn<userBookings, String> startTimeColumn;
    @FXML
    private TableColumn<userBookings, String> endTimeColumn;
    @FXML
    private TableColumn<userBookings, String> startDateColumn;
//    @FXML for multi day bookings
//    private TableColumn<userBookings, String> endDateColumn;
    @FXML
    private TableColumn<userBookings, String> resourcesColumn;
    @FXML
    private TableColumn<userBookings, String> refreshmentsColumn;
    @FXML
    private TableColumn<userBookings, String> refreshmentsTimeColumn;

    private ObservableList<userBookings> data;
    private final BookingsModel bookingsModel = new BookingsModel();
    //</editor-fold>

    public void initialize(){
        welcomeLabel.setText("Welcome back " + LoginController.currentUser.getFirstname());
        loadBookingData();
    }

    //Sets tableView
    public void loadBookingData() {
        data=bookingsModel.loadBookingData();

        this.roomIDColumn.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        this.startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        this.endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        this.startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        //to be used if multi day bookings are implemented
//        this.endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        this.resourcesColumn.setCellValueFactory(new PropertyValueFactory<>("resources"));
        this.refreshmentsColumn.setCellValueFactory(new PropertyValueFactory<>("refreshments"));
        this.refreshmentsTimeColumn.setCellValueFactory(new PropertyValueFactory<>("refreshmentsTime"));
        //so that this doubles as a refresh function, i made it so that it removes existing data from the table and then adds them again.
        this.myTable.setItems(null);
        this.myTable.setItems(data);

    }

    //This will get the selected item from the table view and delete it
    @FXML
    public void deleteBooking(){
        bookingsModel.deleteBooking(myTable.getSelectionModel().getSelectedItem().getRoomID(),String.valueOf(myTable.getSelectionModel().getSelectedItem().getStartTime()),String.valueOf(myTable.getSelectionModel().getSelectedItem().getStartDate()));
        myTable.getItems().remove(myTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void newBooking(){
        try {
            Stage old = (Stage)newBookingButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/RoomBooker/RoomBooker.fxml").openStream());
            Scene scene = new Scene(root, 720, 720);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/RoomBooker.css").toExternalForm());
            stage.getIcons().add(new Image("/images/booking.png"));
            stage.setScene(scene);
            stage.setTitle("Add Booking");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToLogin(){
        try {
            Stage old = (Stage) backButton.getScene().getWindow();
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
}
