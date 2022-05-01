package Controllers.Room;




import Models.RoomData;
import Models.RoomModel;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.Random;

/**
 * @ClassName: RoomController
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 9:03
 * @Version: v1.0
*/
public class RoomController {

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
    private DatePicker datePicker;
    @FXML
    private TextField nameText;
    @FXML
    private TextField numberText;
    @FXML
    private TextArea discribeText;
    @FXML
    private ImageView roomImg;

    @FXML
    private AnchorPane fullPane;


    @FXML
    private Spinner<Integer> roomSelector;
    private String img;
    private final Desktop desktop = Desktop.getDesktop();
    private final RoomModel roomModel = new RoomModel();
    //</editor-fold>
    @FXML
    private TableView<RoomData> myTable;
    @FXML
    private TableColumn<RoomData, String> IDColumn;
    @FXML
    private TableColumn<RoomData, String> nameColumn;
    @FXML
    private TableColumn<RoomData, String> numberColumn;
    @FXML
    private TableColumn<RoomData, String> pictureColumn;
    @FXML
    private TableColumn<RoomData, String> descriptionColumn;
    private ObservableList<RoomData> data;


    public void initialize() throws SQLException {
        roomImg.setImage(new Image("/images/img.png"));
        loadCustomerData();

    }
/**
 * @Description: loadCustomerData
 * @param: []
 * @return: void
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 8:55
 */
    public void loadCustomerData(){
        this.data = roomModel.loadCustomerData();
        if( null !=this.data) {
            this.IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            this.nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            this.numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
            this.pictureColumn.setCellValueFactory(new PropertyValueFactory<>("picture"));
            this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            //so that this doubles as a refresh function, i made it so that it removes existing data from the table and then adds them again.
            this.myTable.setItems(null);
            this.myTable.setItems(data);
        }

    }

    //This will return use to the customer dashboard.
    @FXML
    private void backToDashboard() {
        try {
            Stage old = (Stage)fullPane.getScene().getWindow();
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


    @FXML
    private void addRoom() {
        final FileChooser fileChooser = new FileChooser();//FileChooser
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All FILE", "*.png")
        );

        Stage stage=(Stage)fullPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                String basePath=getClass().getResource("../../").getPath().replaceFirst("/","");
                File file1=new File(basePath+"Upload/");
                if (!file1.exists()){
                    file1.mkdirs();
                }
                Random r = new Random();
                String  s=String.valueOf(System.currentTimeMillis());
                String fpath=basePath+"Upload/"+s+".png";
                Files.copy(file.toPath(), new File(fpath).toPath());
                img="/Upload/"+s+".png";
                roomImg.setImage(new Image(img));

            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
    public void doAddRoom(){
        RoomData roomData=new RoomData();
        roomData.setName(nameText.getText());
        roomData.setDescription(discribeText.getText());
        roomData.setNumber(numberText.getText());
        roomData.setPicture(img);
        roomModel.insert(roomData);
        errorLabel.setText("commit success");
        loadCustomerData();

    }
    /**
     * @Description: deleteRoom
     * @param: []
     * @return: void
     */
    public void deleteRoom(){
        RoomData roomData=new RoomData();
        roomData.setId(myTable.getSelectionModel().getSelectedItem().getId());
        roomModel.delete(roomData);
        loadCustomerData();

    }
    public void onclick(){
        if( null!= myTable.getSelectionModel().getSelectedItem()) {
            nameText.setText(myTable.getSelectionModel().getSelectedItem().getName());
            numberText.setText(myTable.getSelectionModel().getSelectedItem().getNumber());
            discribeText.setText(myTable.getSelectionModel().getSelectedItem().getDescription());
            System.out.println(myTable.getSelectionModel().getSelectedItem().getPicture());
            roomImg.setImage(new Image(myTable.getSelectionModel().getSelectedItem().getPicture()));
        }

    }

    
}
