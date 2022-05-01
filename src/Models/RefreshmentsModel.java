package Models;




import Dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class RefreshmentsModel {

    private long roomId;
    private String date;
    private String time;
    private String refreshment;

    private DBConnect db;
    Statement stmt = null;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getRefreshment() {
        return refreshment;
    }

    public void setRefreshment(String refreshment) {
        this.refreshment = refreshment;
    }

    public RefreshmentsModel() {
        db = DBConnect.getInstance();
    }

    //This will take the following as parameters to add their details into the database. The ID column will be automatically incremented
    public boolean registerLogic(String Username, String Firstname, String Lastname, String Password, String Email) throws SQLException {
        String sql = "INSERT INTO users_lqf(Username, Firstname, Lastname, Password, Email, Account) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = db.getConnection();
            assert con != null;
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setString(1, Username);
            statement.setString(2, Firstname);
            statement.setString(3, Lastname);
            statement.setString(4, Password);
            statement.setString(5, Email);
            statement.setString(6, "Admin");

            statement.execute();
            con.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error!\n" + e);
            return false;
        }
    }

    public ObservableList<CatererModel> loadTable(String date) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ObservableList<CatererModel> data = FXCollections.observableArrayList();
        try {

            String sql = "SELECT * FROM refreshments_lqf WHERE Date = ?";

            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setString(1, date);
            rs = ps.executeQuery();

            while (rs.next()) {
                data.add(new CatererModel(rs.getString(1), rs.getString(3), rs.getString(4)));
            }

            //Here i was going to make sure i close the connection by calling the method ps and rs .close() but for now it doesn't really matter

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
