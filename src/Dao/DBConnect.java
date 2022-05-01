package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @ClassName: DBConnect
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/4/17 9:25
 * @Version: v1.0
 */
public class DBConnect  {
    private static String url = "jdbc:mysql://127.0.0.1:3306/510project";
    private static String username = "root";
    private static String password = "Kk1314258";
    private static DBConnect instance = new DBConnect(url,username,password);
    protected Connection connection;





    public static DBConnect getInstance() {
        return instance;
    }
/**
 * @Description: DBConnect
 * @param: []
 * @return: 
 */
    private  DBConnect(String url, String username, String password) {

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Error creating connection to database: " + e);
            System.exit(-1);
        }

    }
/**
 * @Description: getConnection
 * @param: []
 * @return: java.sql.Connection
 */
    public Connection getConnection() {
        try {
            if (connection.isClosed()) {
                try {
                    connection = DriverManager.getConnection(url, username, password);
                } catch (SQLException e) {
                    System.out.println("Error creating connection to database: " + e);
                    System.exit(-1);
                }
            }
        }catch (SQLException e){
            System.out.println("Error creating connection to database: " + e);
            System.exit(-1);
        }
        return connection;
    }

}
