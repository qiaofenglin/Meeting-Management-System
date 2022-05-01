package Controllers.Cleaners;

//I wanted to quickly make a class for the table view in CleanersController
/**
 * @ClassName: CleaningSlot
 * @Description: TODO
 * @Author: Qiaofeng Lin
 * @Date: 2022/5/1 10:38
 * @Version: v1.0
*/
public class CleaningSlot {
    private final String roomID;
    private final String startTime;
    private final String endTime;

    public CleaningSlot(String roomID, String startTime, String endTime) {
        this.roomID = (roomID);
        this.startTime = (startTime);
        this.endTime = (endTime);
    }

    @Override
    public String toString(){
        return this.roomID + " "+ this.startTime + " " + this.endTime;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
