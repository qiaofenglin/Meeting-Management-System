package Models;



import Controllers.Admin.userBookings;
import Controllers.Cleaners.CleaningSlot;
import Controllers.Login.LoginController;
import Controllers.RoomBooker.TimeSlot;
import Dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class BookingsModel {

    private long roomId;
    private long userId;
    private String username;
    private String startTime;
    private String endTime;
    private String startDate;
    private String endDate;
    private String resources;
    private String refreshments;
    private String refreshmentsTime;
    private ArrayList<userBookings> bookedTimes = new ArrayList<>();

    private DBConnect db;
    Statement stmt = null;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }


    public String getRefreshments() {
        return refreshments;
    }

    public void setRefreshments(String refreshments) {
        this.refreshments = refreshments;
    }


    public String getRefreshmentsTime() {
        return refreshmentsTime;
    }

    public void setRefreshmentsTime(String refreshmentsTime) {
        this.refreshmentsTime = refreshmentsTime;
    }

    public BookingsModel() {
        db = DBConnect.getInstance();
    }

    //This method loads the data onto the pi chart
    public ArrayList<userBookings> setPieData() {
        ArrayList<userBookings> ubList = new ArrayList<>();
        try {

            String sql = "SELECT * FROM bookings_lqf";
            Connection con = db.getConnection();

            ResultSet rs = con.createStatement().executeQuery(sql);

            //Creates a new userBookings object while going through the table Bookings
            while (rs.next()) {
                userBookings ub = new userBookings(rs.getInt(1), rs.getInt(2), rs.getString(3), LocalTime.parse(rs.getString(4)), LocalTime.parse(rs.getString(5)),
                        LocalDate.parse(rs.getString(6)), LocalDate.parse(rs.getString(7)), rs.getString(8));
                ubList.add(ub);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return ubList;
    }


    public void clearUpDB(){
        try {
            //removes old data from bookings_lqf table
            String sql = "DELETE FROM bookings_lqf WHERE EndDate <=  DATE_SUB(CURDATE(),INTERVAL -1 DAY)";
            Connection con = db.getConnection();
            assert con != null;
            con.createStatement().execute(sql);
        }catch(SQLException e){
            System.out.println("Error: " + e);
        }

    }



    public ObservableList<CleaningSlot>  loadTable(String date) throws SQLException {
        ObservableList<CleaningSlot> data=FXCollections.observableArrayList();;
        try{

            String sql = "SELECT * FROM bookings_lqf WHERE UserID = ? AND StartDate = ?";
            Connection con = db.getConnection();

            assert con != null;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, 2);
            System.out.println(date);
            ps.setString(2, date);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                data.add(new CleaningSlot(rs.getString(1), rs.getString(4), rs.getString(5)));
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return data;

    }

    //This will look at the database to see when the cleaner has cleaning tasks/bookings
    public void getCleanerTimes(String dtime) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            Connection con = db.getConnection();
            //Here user ID is sufficient, and need to make sure that the cleaner's account always has the ID 2
            String sql = "SELECT * FROM bookings_lqf WHERE UserID = ? AND StartDate = ?";
            assert con != null;

            ps = con.prepareStatement(sql);
            ps.setInt(1, 2);
            ps.setString(2, dtime);
            rs = ps.executeQuery();

            while(rs.next()){
                bookedTimes.add(new userBookings(rs.getInt(1), rs.getInt(2), LocalTime.parse(rs.getString(4)), LocalDate.parse(rs.getString(6))));
            }

        }catch(Exception e){
            System.out.println("Error: " + e);

        }
    }

    //This method will see if the requested start time is appropriate for the cleaners
    public boolean isBooked(String et ){
        for(userBookings x: bookedTimes){
            boolean condition1 = et.equals(x.getStartTime());
            if(condition1){
                return true;
            }
        }
        //Will return false if the cleaner is not booked for the slotting starting at st (variable st)
        return false;
    }


    //This will add the cleaner timeslot of 30 minutes with startTime as we passed
    public boolean addCleanerBooking(int roomID, LocalTime st, LocalDate date){
        LocalTime et = st.plus(30, ChronoUnit.MINUTES);
        String sql = "INSERT INTO bookings_lqf(RoomID, UserID, Username, StartTime, EndTime, StartDate, EndDate, Resources, Refreshments, RefreshmentsTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement ps = null;
            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, roomID);
            ps.setInt(2, 2);
            ps.setString(3, "CLEANER");
            ps.setString(4, st.toString());
            ps.setString(5, et.toString());
            ps.setString(6, date.toString());
            ps.setString(7, date.toString());
            ps.setString(8, "");
            ps.setString(9, "");
            ps.setString(10, "");
            ps.execute();
            return true;
        }catch(Exception e){
            System.out.println("Error: "+e);
            return false;
        }

    }

    //This method will return the next closest booking of the room to see if the room can be cleaned before then.
    public LocalTime getNextBooking(String sd,LocalTime startTime){
        String sql = "SELECT * FROM bookings_lqf WHERE StartDate = ? ORDER BY StartTime ASC";
        try{
            Connection con = db.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ArrayList<userBookings> ub = new ArrayList<>();

            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setString(1, sd);
            rs = ps.executeQuery();
            while(rs.next()){
                ub.add(new userBookings(rs.getInt(1), rs.getInt(2), LocalTime.parse(rs.getString(4)), LocalDate.parse(rs.getString(6))));
            }

            //Since the result set is ordered we will get the immediate booking and not just a booking with a later time.
            if(ub.size()>1){
                return getNextCleanerFree(sd);
            }else{
                for(userBookings x: ub){
                    if(x.getStartTime().isAfter(startTime)){
                        return x.getStartTime();
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Error: " + e);
        }
        return null;

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


    //Sets tableView
    public ObservableList<userBookings> loadBookingData() {
        ObservableList<userBookings> data=null;
        try {
            PreparedStatement ps;
            ResultSet rs;
            String sql = "SELECT * FROM bookings_lqf WHERE UserID = ?";
            Connection con = db.getConnection();
            data = FXCollections.observableArrayList();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setString(1, String.valueOf(LoginController.currentUser.getUserID()));

            rs = ps.executeQuery();
            while (rs.next()) {
                //To make things complicated I've decided to use userBookings objects which contains 100% of the details and then use the data i get from it to then add the data to the observableList data i created as a userBooking object.
                userBookings ub  = new userBookings(rs.getInt(1), rs.getInt(2), rs.getString(3), LocalTime.parse(rs.getString(4)), LocalTime.parse(rs.getString(5)),
                        LocalDate.parse(rs.getString(6)), LocalDate.parse(rs.getString(7)), rs.getString(8), rs.getString(9), rs.getString(10));
                data.add(ub);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return data;


    }


    //This will get the selected item from the table view and delete it
    public void deleteBooking(int roomId,String startTime,String startDate){
        try {
            PreparedStatement ps;
            String sql = "DELETE FROM bookings_lqf WHERE RoomID = ? AND UserID = ? AND StartTime = ? AND StartDate = ?";
            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, roomId);
            ps.setInt(2, LoginController.currentUser.getUserID());
            ps.setString(3, startTime);
            ps.setString(4, startDate);
            ps.execute();
        }catch(SQLException e){
            System.out.println("Error: " + e);
        }
    }

    public boolean checkDouble(String date,LocalTime startTime,LocalTime endTime) throws SQLException {
        PreparedStatement ps =null;
        ResultSet rs=null;
        String sql = "SELECT * FROM bookings_lqf WHERE UserID = ? and StartDate = ?";
        ArrayList<TimeSlot> results = new ArrayList<>();
        try{
            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, LoginController.currentUser.getUserID());
            ps.setString(2, date);

            ArrayList<TimeSlot> userBookings = new ArrayList<>();
            ArrayList<TimeSlot> selectedTimeSlots = new ArrayList<>(TimeSlot.returnTimeSlots(startTime, TimeSlot.getSlotNumber(startTime, endTime)));


            rs = ps.executeQuery();
            while(rs.next()){
                results.add(new TimeSlot(rs.getString(4), rs.getString(5)));
            }

            //Now we are going to split all objects in the userBookings arraylist into 30 min timeslots from the results we got from the database
            for(TimeSlot x: results){
                userBookings.addAll(TimeSlot.returnTimeSlots(x.getStartTime(), TimeSlot.getSlotNumber(x.getStartTime(), x.getEndTime())));
            }

            //rq as in requesting slot
            for(TimeSlot ub: userBookings){
                for(TimeSlot rq: selectedTimeSlots){
                    if(rq.exists(ub)){
                        return false;
                    }
                }

//                System.out.println(x.toString());
//                boolean condition1 = startTime.isAfter(x.getStartTime());
//                boolean condition2 = startTime.isBefore(x.getEndTime());
//                boolean condition3 = endTime.isBefore(x.getEndTime());
//                boolean condition4 = endTime.isAfter(x.getStartTime());
//                System.out.println(startTime.toString());
//                System.out.println(endTime.toString());
//                if((condition1 && condition2) || (condition3 && condition4)){
//                    return false;
//                }
//                if(startTime.equals(x.getStartTime()) || endTime.equals(x.getEndTime())){
//                    return false;
//                }
            }

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            assert ps != null;
            assert rs != null;
            ps.close();
            rs.close();
        }
        return false;
    }



    //This code does nothing but display the free times for the date selected in 30 minute intervals and adds them to the list view
    public ArrayList<TimeSlot> setTableView(LocalDate selectedDate,int selectedRoom) throws SQLException {
        PreparedStatement ps = null;
        ArrayList<TimeSlot> out = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM bookings_lqf WHERE RoomID = ? and StartDate = ?";
        try {
            if (selectedDate == null) {
                return null;
            }
            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);



            ps.setInt(1, selectedRoom);
            ps.setString(2, selectedDate.toString());


            ArrayList<LocalTime> startTimes = new ArrayList<>();
            ArrayList<LocalTime> endTimes = new ArrayList<>();

            rs = ps.executeQuery();
            while (rs.next()) {
                startTimes.add(LocalTime.parse(rs.getString(4)));
                endTimes.add(LocalTime.parse(rs.getString(5)));
            }


            ArrayList<TimeSlot> template = TimeSlot.createTimeSlots();
            ArrayList<TimeSlot> bookedTimeSlots = new ArrayList<>();

            //The methods below me could've easily been made into methods of the timeslot class, but TIME :/
            for (int x = 0; x < startTimes.size(); x++) {
                int length = TimeSlot.getSlotNumber(startTimes.get(x), endTimes.get(x));
                bookedTimeSlots.addAll(TimeSlot.returnTimeSlots(startTimes.get(x), length));

            }

            /*
            The method list contains returns true if the object ts is already existing in bookedTimeSlots. If it does not exist
            then we can add it to the out arrayList which will be displayed to the user
            */
            for(TimeSlot ts: template){
                if(!TimeSlot.listContains(ts, bookedTimeSlots)){
                    out.add(ts);
                }
            }


        } catch (Exception e) {
            System.out.println(e);

        } finally {
            ps.close();
            rs.close();
        }
        return out;
    }



    //This will take our details and add it to the bookings_lqf table in our database
    public boolean addBooking(String st,String et,int roomID,String dtime,String resources,String refresh,String refreshTime) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO bookings_lqf(RoomID, UserID, Username, StartTime, EndTime, StartDate, EndDate, Resources, Refreshments, RefreshmentsTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, roomID);
            ps.setInt(2, LoginController.currentUser.getUserID());
            ps.setString(3, LoginController.currentUser.getUsername());
            ps.setString(4, st);
            ps.setString(5, et);
            ps.setString(6, dtime);
            ps.setString(7, dtime);
            ps.setString(8, resources);
            ps.setString(9, refresh);
            ps.setString(10, refreshTime);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }

    //This method will make sure that the caterers are free to deliver the refreshments for the selected time.
    public ArrayList<LocalTime> checkRefreshments(int roomId,String dtime) throws SQLException {


        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM refreshments_lqf WHERE RoomID = ? AND Date = ?";
        ArrayList<LocalTime> results = new ArrayList<>();

        try {
            Connection con = db.getConnection();
            assert con != null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, roomId);
            ps.setString(2, dtime);

            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(LocalTime.parse(rs.getString(3)));
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            //To close the connection
            assert ps != null;
            ps.close();
            assert rs != null;
            rs.close();
        }
    }

    public boolean addRefreshment(String[] refreshments,String[] refreshmentTimes,int roomId,String dtime) throws SQLException {
        PreparedStatement ps = null;
        try {
            Connection con = db.getConnection();
            String sql = "INSERT INTO refreshments_lqf(RoomID, Date, Time, Refreshment) VALUES (?, ?, ?, ?)";
            assert con != null;
            for (int x = 0; x < refreshments.length; x++) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, roomId);
                ps.setString(2, dtime);
                ps.setString(3, refreshmentTimes[x]);
                ps.setString(4, refreshments[x]);
                ps.execute();
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        } finally {
            assert ps != null;
            ps.close();
        }


    }


}
