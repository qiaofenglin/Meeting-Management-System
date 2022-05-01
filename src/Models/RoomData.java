package Models;


public class RoomData {

  private long id;
  private String name;
  private String number;
  private String picture;
  private String description;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }


  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RoomData(long id, String name, String number, String picture, String description) {
    this.id = id;
    this.name = name;
    this.number = number;
    this.picture = picture;
    this.description = description;
  }
  public RoomData() {

  }

}
