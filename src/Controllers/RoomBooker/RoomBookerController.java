package Controllers.RoomBooker;



import Models.RoomData;
import Models.RoomModel;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Models.BookingsModel;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
/**
 * @ClassName: RoomBookerController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class RoomBookerController {

    //<editor-fold desc="variables">
    @FXML
    private Label backgroundLabel;
    @FXML
    private Label tableLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private Button refreshButton;
    @FXML
    private Button bookButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField resourcesTextField;
    @FXML
    private ComboBox startTimeHour;
    @FXML
    private ComboBox startTimeMin;
    @FXML
    private ComboBox endTimeHour;
    @FXML
    private ComboBox endTimeMin;
    @FXML
    private ComboBox numberBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField refreshmentsTimeBox;
    @FXML
    private TextArea roomDescriptionBox;
    @FXML
    private TextArea refreshmentsArea;
    @FXML
    private TableView<TimeSlot> myTable;
    @FXML
    private TableColumn<TimeSlot, String> startTimeColumn;
    @FXML
    private TableColumn<TimeSlot, String> endTimeColumn;
    private final RoomModel roomModel = new RoomModel();
    private int roomId;

    private ArrayList<TimeSlot> bookedTimeSlots = new ArrayList<>();
    private final BookingsModel bookingsModel = new BookingsModel();
    //</editor-fold>

    public void initialize() throws SQLException {
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        infoLabel.setWrapText(true);
        initializeComboBox();
        initializeSpinner();
        setDateBounds();
        setTableView();

    }

    //This will adds a listener to the spinner that will be used to change the description of the room they have selected.
    public void initializeSpinner() {
        ObservableList<String> rooms = FXCollections.observableArrayList();

        ObservableList<RoomData> roomData=roomModel.loadCustomerData();
        for (RoomData data:roomData){
            rooms.add(String.valueOf(data.getId()));
        }

        numberBox.getItems().addAll(rooms);
        numberBox.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            errorLabel.setText("");
            infoLabel.setText("");
            this.roomId=Integer.valueOf(newValue);
            for (RoomData data:roomData){
                System.out.println(String.valueOf(data.getId()));
               if( String.valueOf(data.getId()).equals(newValue)){
                   roomDescriptionBox.setText(data.getDescription());
               };
            }


            setTableView();

        });
    }

    //This will add the time options into the respective combo boxes
    public void initializeComboBox() {
        ObservableList<String> startHourOptions = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21");
        ObservableList<String> endHourOptions = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22");
        startTimeHour.getItems().addAll(startHourOptions);
        endTimeHour.getItems().addAll(endHourOptions);
        ObservableList<String> minuteOptions = FXCollections.observableArrayList("00", "30");
        startTimeMin.getItems().addAll(minuteOptions);
        endTimeMin.getItems().addAll(minuteOptions);

    }

    //This sets it so that you can't book on days that have already passed
    public void setDateBounds() {
        LocalDate minDate = LocalDate.now();
        datePicker.setDayCellFactory(d ->
                new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isBefore(minDate));
                    }
                });
    }

    //This code does nothing but display the free times for the date selected in 30 minute intervals and adds them to the list view
    @FXML
    public void setTableView()  {
        try {
            ArrayList<TimeSlot> out = bookingsModel.setTableView(datePicker.getValue(), this.roomId);

            this.bookedTimeSlots = bookedTimeSlots; // This list will later be used to verify if the user has booked when the room is free or not

            ObservableList<TimeSlot> data = FXCollections.observableArrayList(out);

            startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

            myTable.setItems(null);
            myTable.setItems(data);

        } catch (Exception e) {
            errorLabel.setText(e.toString());

        }

    }

    //This will validate all details, compare them for any conflicts before finally booking the room
    @FXML
    private void bookRoom() throws SQLException {
        if (verifyFields()) {
            if (checkBookings()) {
                //here
                if (checkDouble()) {
                    if (checkRefreshments()) {
                        //Even after checking for overlapping bookings, we need to make sure that the cleaners are free at this time and can clean the room before the next booking
                        if (roomCleaned()) {
                            if (addBooking()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Message");
                                alert.setHeaderText(null);
                                alert.setContentText("Booking created!");

                                alert.showAndWait().ifPresent((btnType) -> {
                                    if (btnType == ButtonType.OK) {
                                        backToDashboard();
                                    }
                                });
                            } else {
                                infoLabel.setText("Note that we need to clean the rooms once you are done\n" +
                                        "Therefore some booking me be unavailable depending on\n" +
                                        "our cleaners!");
                            }
                        } else {
                            errorLabel.setText("Error with cleaners");
                        }
                    } else {
                        errorLabel.setText("Time selected for refreshments is unfortunately busy");
                        infoLabel.setText("Please make sure that the refreshments times are within selected time frame!");
                    }
                    //here
                } else {
                    errorLabel.setText("Error");
                    infoLabel.setText("User cannot make bookings in 2 rooms at the same time!");
                }
            } else {
                errorLabel.setText("Room busy at given time!");
            }

        } else {
            errorLabel.setText("Field verification error!");
        }

    }

    //This will take our details and add it to the Bookings table in our database
    private boolean addBooking()  {

        String st = startTimeHour.getValue() + ":" + startTimeMin.getValue();
        String et = endTimeHour.getValue() + ":" + endTimeMin.getValue();
        try {
            bookingsModel.addBooking(st, et, this.roomId, datePicker.getValue().toString(), resourcesTextField.getText(), refreshmentsArea.getText(), refreshmentsTimeBox.getText());
            errorLabel.setText("");
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }

    //This method will check for any overlapping bookings from the already booked slots to the time slots we requested
    public boolean checkBookings() {
        LocalTime selectedST = LocalTime.parse(startTimeHour.getValue() + ":" + startTimeMin.getValue());
        LocalTime selectedET = LocalTime.parse(endTimeHour.getValue() + ":" + endTimeMin.getValue());

        ArrayList<TimeSlot> requestingTS = TimeSlot.returnTimeSlots(selectedST, TimeSlot.getSlotNumber(selectedST, selectedET));

        //this.bookedTimeSlots is set data in the table view method
        for (TimeSlot bs : this.bookedTimeSlots) {
            for (TimeSlot rs : requestingTS) {
                if (rs.exists(bs)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean verifyFields() {
        if (verifyFieldsNull()) {
            return verifyBookTimes();
        }
        return false;
    }

    //This will make sure that the user has entered valid values into the fields, this is different from checking for double bookings.(Where a room is booked for 2 people at the same time.)
    private boolean verifyFieldsNull() {
        try {

            /*
            The next block of code has multiple nested if statements, and its confusing to read so I've explained it here
            It checks whether all the fields are null, except resources field, refreshments and refreshments time field. That is the reason
            you can see "else return true" if refreshments area is null; If it is not null I need to make sure they enter a time that i need to deliver it at.
            They need to mention the time for each refreshments, e.g if they ask for 2 refreshments i require 2 refreshment time values.
            All the other else statements just add text to the error label the user know where they have gone wrong.
             */

            //Checks datePicker
            if (datePicker.getValue() != null) {
                //This will check if something has been selected in the time combo boxes
                if (startTimeHour.getValue() != null && startTimeMin.getValue() != null && endTimeHour.getValue() != null && endTimeMin.getValue() != null) {

                    if (!refreshmentsArea.getText().isEmpty() && !refreshmentsTimeBox.getText().isEmpty()) {
                        String[] refreshments = refreshmentsArea.getText().split("[,] ", 0);
                        String[] refreshmentTimes = refreshmentsTimeBox.getText().split("[,] ", 0);
                        for (String x : refreshmentTimes) {
                            LocalTime.parse(x);
                        }
                        if (refreshments.length == refreshmentTimes.length) {
                            errorLabel.setText("");
                            return true;
                        } else {
                            errorLabel.setText("Specify time for each refreshment");
                        }
                    } else if (refreshmentsArea.getText().isEmpty() && refreshmentsTimeBox.getText().isEmpty()) {
                        errorLabel.setText("");
                        return true;
                    } else {
                        errorLabel.setText("Enter both refreshment fields");
                    }
                } else {
                    errorLabel.setText("Select time");
                }
            } else {
                errorLabel.setText("Date cannot be empty!");
            }

            return false;

        } catch (Exception e) {
            errorLabel.setText("Invalid details");
            return false;
        }
    }

    //This will take the values entered by the user and compares to the database to see if there are any clashes with other bookings.
    private boolean verifyBookTimes() {
        try {
            String x = startTimeHour.getValue() + ":" + startTimeMin.getValue();
            LocalTime st = LocalTime.parse(x);
            x = endTimeHour.getValue() + ":" + endTimeMin.getValue();
            LocalTime et = LocalTime.parse(x);

            int timeCompare = st.compareTo(et);

            if (timeCompare == 0) {
                errorLabel.setText("End time cannot be start time");
            } else if (timeCompare == 1) {
                errorLabel.setText("End time cannot be before start time");
            } else if (endTimeHour.getValue().equals("22") && endTimeMin.getValue().equals("30")) {
                errorLabel.setText("End time out of bounds!");
            } else if (timeCompare == -1) {
                errorLabel.setText("");
                return true;
            }
            return false;

        } catch (Exception e) {
            errorLabel.setText("Error entering time");
            return false;
        }
    }

    //This method will make sure that the caterers are free to deliver the refreshments for the selected time.
    private boolean checkRefreshments() throws SQLException {

        if (refreshmentsTimeBox.getText().isEmpty()) {
            errorLabel.setText("");
            return true;
        }

        String[] refreshmentsTimes = refreshmentsTimeBox.getText().split("[,] ", 0);


        ArrayList<LocalTime> results = bookingsModel.checkRefreshments(this.roomId, datePicker.getValue().toString());


        for (String x : refreshmentsTimes) {
            for (LocalTime existingRTS : results) {
                LocalTime rts = LocalTime.parse(x);
                if (rts.compareTo(existingRTS) == 0) {
                    return false;
                }
            }
        }

        //To make sure that the time they give is actually withing the booked time;
        for (String x : refreshmentsTimes) {
            LocalTime st = LocalTime.parse(startTimeHour.getValue() + ":" + startTimeMin.getValue());
            LocalTime et = LocalTime.parse(endTimeHour.getValue() + ":" + endTimeMin.getValue());
            LocalTime rt = LocalTime.parse(x);

            boolean condition1 = rt.isAfter(st);
            boolean condition2 = rt.isBefore(et);

            if (condition1 && condition2) {
                errorLabel.setText("");
                return addRefreshment();
            }
        }

        return false;

    }

    public boolean addRefreshment() throws SQLException {
        String[] refreshments = refreshmentsArea.getText().split("[,] ", 0);
        String[] refreshmentTimes = refreshmentsTimeBox.getText().split("[,] ", 0);
        if(bookingsModel.addRefreshment(refreshments,refreshmentTimes,this.roomId,datePicker.getValue().toString())){
            errorLabel.setText("");
            return true;
        }else{
            errorLabel.setText("Error adding refreshment details");
            return false;
        }

    }

    //This will return use to the customer dashboard.
    @FXML
    private void backToDashboard() {
        try {
            //We need to get the old stage, so we can close it before we go back to the login page
            Stage old = (Stage) backButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Customer/Customer.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Customer.css").toExternalForm());
            stage.getIcons().add(new Image("/images/customer.png"));
            stage.setScene(scene);
            stage.setTitle("Customer Dashboard");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkDouble() throws SQLException {
        String date = datePicker.getValue().toString();
        LocalTime startTime = LocalTime.parse(startTimeHour.getValue() + ":" + startTimeMin.getValue());
        LocalTime endTime = LocalTime.parse(endTimeHour.getValue() + ":" + endTimeMin.getValue());
        return bookingsModel.checkDouble(date, startTime, endTime);

    }
    /*
    To make sure that the rooms are always cleaned before someone books them we can do 2 things :
    1. Make sure the room is cleaned before someone books the room
    2.Clean the room after the end time and before someone else books the room
    For this project I'm going to implement a simple system where it checks whether the room can be cleaned
    either right after the endTime or right before the next booking of that room.
     */

    public boolean roomCleaned() throws SQLException {
        String et = endTimeHour.getValue() + ":" + endTimeMin.getValue();
        //CleaningModel cleaner = new CleaningModel(et, datePicker.getValue());
        String date = datePicker.getValue().toString();
        bookingsModel.getCleanerTimes(date);

        if (bookingsModel.isBooked(et)) {
            int roomID = this.roomId;

            //Cleaner is booked, and we will try to see if the cleaner is busy even before the next room booking
            if (bookingsModel.getNextBooking(date,LocalTime.parse(et)) != null) {
                bookingsModel.addCleanerBooking(roomID, (bookingsModel.getNextBooking(date,LocalTime.parse(et))), datePicker.getValue());
                return true;
            } else {
                return false;
            }
        } else {
            //Cleaner is not booked, so we can add a cleaner booking for this time.
            return bookingsModel.addCleanerBooking(this.roomId, LocalTime.parse(et), datePicker.getValue());
        }
    }

    //bad coding as its over 500 lines :/
}
