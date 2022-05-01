package Models;


import Dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * @ClassName: DaoModel
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/4/17 9:24
 * @Version: v1.0
 */
public class RoomModel {
    private DBConnect db;
    Statement stmt = null;
    private ResultSet rs;

    public RoomModel() {
        db = DBConnect.getInstance();
    }


    // INSERT INTO METHOD

    /**
     * @Description: insertRecords
     * @param: [robjs]
     * @return: void
     */
    public void insert(RoomData roomData) {
        try {

            if (null == findOneByNunber(roomData.getNumber())) {
                stmt = db.getConnection().createStatement();
                PreparedStatement ppst = null;
                ppst = db.getConnection().prepareStatement("insert into Room(name,number,picture,description) values(?, ?, ?,?)");
                ppst.setString(1, roomData.getName());
                ppst.setString(2, roomData.getNumber());
                ppst.setString(3, roomData.getPicture());
                ppst.setString(4, roomData.getDescription());
                ppst.execute();
                ppst.close();
                System.out.println("212");
                db.getConnection().close();
            }else{
                update(roomData);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * @Description: insertRecords
     * @param: [robjs]
     * @return: void
     */
    public boolean update(RoomData roomData) {
        try {
                stmt = db.getConnection().createStatement();
                PreparedStatement ppst = null;
                ppst = db.getConnection().prepareStatement("update Room set name=?,picture=?,description=? where number=?");
                ppst.setString(1, roomData.getName());
                ppst.setString(2, roomData.getPicture());
                ppst.setString(3, roomData.getDescription());
                ppst.setString(4, roomData.getNumber());
            System.out.println("564");
                ppst.execute();
                ppst.close();
                db.getConnection().close();
                return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public ObservableList<RoomData> loadCustomerData() {
        ObservableList<RoomData> data = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM Room";
            Connection con = db.getConnection();

            assert con != null;
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                data.add(new RoomData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return data;

    }

    public RoomData findOneByNunber(String number) {
        RoomData roomData=null;
        try {
            stmt = db.getConnection().createStatement();
            PreparedStatement ppst = null;
            ppst = db.getConnection().prepareStatement("SELECT * FROM Room  where number=? limit 1");
            ppst.setString(1, number);
            ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                return (new RoomData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return roomData;

    }

    public void delete(RoomData roomData) {
        try {
            stmt = db.getConnection().createStatement();
            PreparedStatement ppst = null;
            ppst = db.getConnection().prepareStatement("delete FROM Room where id=?");
            ppst.setLong(1, roomData.getId());
            ppst.execute();
            ppst.close();
            db.getConnection().close();

        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }

    }
}
