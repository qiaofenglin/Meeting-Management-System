package Models;



import Dao.DBConnect;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @ClassName: BaseModel
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/4/30 10:18
 * @Version: v1.0
 */
public class BaseModel {
    private DBConnect db;
    Statement stmt = null;

    public BaseModel() {
        db = DBConnect.getInstance();
    }

    //This will tell use whether the connection was established or not
    public boolean isConnected(){
        try{
            Connection con = db.getConnection();
            if(con != null){
                return true;
            }else{
                return false;
            }

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
