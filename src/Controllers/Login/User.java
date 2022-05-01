package Controllers.Login;

/**
 * @ClassName: User
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
//This objected has been created to pass the user info into different windows.
public class User {

    private final int userID;
    private final String firstname;
    private final String lastname;
    private final String email;
    private String username;




    public User(int userID, String firstname, String lastname, String email,String username) {
        this.userID = userID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }



    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername(){
        return username;
    }
}
