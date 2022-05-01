package Models;



import Controllers.Admin.userBookings;

import Controllers.Login.LoginController;
import Controllers.Login.User;
import Dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class UsersModel extends BaseModel {

    private long id;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;
    private String account;
    private DBConnect db;
    Statement stmt = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public UsersModel() {
        db = DBConnect.getInstance();
    }


    //This will take the following parameters and will verify it with the database.
    // It will reject you if you try and login as a customer from and admin account and vice versa
    public boolean isLogin(String username, String password, String AccountType) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "{call UserLogin(?,?,?)}";
        try {
            ps = db.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, AccountType);

            rs = ps.executeQuery();
            if (rs.next()) {
                LoginController.currentUser = new User(rs.getInt(1), rs.getString(3), rs.getString(4), rs.getString(6), rs.getString(2));
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        } finally {
            assert ps != null;
            assert rs != null;
            ps.close();
            rs.close();
        }
    }

    @FXML
    public ObservableList<AccountData> loadCustomerData() {
        ObservableList<AccountData> data= FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM users_lqf";
            Connection con = db.getConnection();

            assert con != null;
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {

                data.add(new AccountData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return data;

    }

    //If there are no booking for the room in the day, then we will just get whenever the cleaner is next free
    private LocalTime getNextCleanerFree(String startDate){
        String sql = "SELECT * FROM bookings_lqf WHERE UserID = ? AND StartDate = ? ORDER BY EndTime DESC";
        try {
            Connection con = db.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ArrayList<userBookings> ub = new ArrayList<>();

            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, 2);
            ps.setString(2, startDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                ub.add(new userBookings(rs.getInt(1), rs.getInt(2), LocalTime.parse(rs.getString(4)), LocalDate.parse(rs.getString(6))));
            }
            //Now we need to get the end time of the last cleaner slot.
            for(userBookings x: ub){
                return x.getStartTime().plus(30, ChronoUnit.MINUTES);
            }



        }catch(Exception e){
            System.out.println("Error: " + e);
        }
        return null;
    }

    //This will take the following as parameters to add their details into the database. The ID column will be automatically incremented
    public boolean registerLogic(String Username, String Firstname, String Lastname, String Password, String Email) throws SQLException {
        String sql = "INSERT INTO room_lqf(Username, Firstname, Lastname, Password, Email, Account) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = db.getConnection();
        try {
            con.setAutoCommit(false);
            assert con != null;
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setString(1, Username);
            statement.setString(2, Firstname);
            statement.setString(3, Lastname);
            statement.setString(4, Password);
            statement.setString(5, Email);
            statement.setString(6, "Customer");

            statement.execute();
            con.commit();
            con.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error!\n" + e);

            try {
                if(con !=null&& !con.isClosed()) { //
                    con.rollback();//rollback
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

}
