package Controllers.Caterers;

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
import Models.CatererModel;
import Models.RefreshmentsModel;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
/**
 * @ClassName: CaterersController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class CaterersController {

    @FXML
    private Label headingLabel;
    @FXML
    private Button backButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<CatererModel> myTable;
    @FXML
    private TableColumn<CatererModel, String> roomIDColumn;
    @FXML
    private TableColumn<CatererModel, String> timeColumn;
    @FXML
    private TableColumn<CatererModel, String> refreshmentColumn;
    private final RefreshmentsModel refreshmentsModel = new RefreshmentsModel();

    public void initialize() throws SQLException {
        initializeDatePicker();
        loadTable();

    }

    private void initializeDatePicker(){
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);

        datePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            try {
                loadTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadTable() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ObservableList<CatererModel> data = FXCollections.observableArrayList();
            String date = datePicker.getValue().toString();
            data=refreshmentsModel.loadTable(date);

            roomIDColumn.setCellValueFactory(new PropertyValueFactory<>("roomID"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            refreshmentColumn.setCellValueFactory(new PropertyValueFactory<>("refreshment"));

            myTable.setItems(null);
            myTable.setItems(data);

            //Here i was going to make sure i close the connection by calling the method ps and rs .close() but for now it doesn't really matter

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //This will take us back to the admin dashboard
    @FXML
    public void returnToAdmin() {
        try {
            Stage old = (Stage) this.backButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../../Views/Admin/Admin.fxml").openStream());
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Admin.css").toExternalForm());
            stage.getIcons().add(new Image("/images/admin.png"));
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
