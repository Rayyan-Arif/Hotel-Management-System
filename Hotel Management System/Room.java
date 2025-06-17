public class Room {
    private int room_no;
    private String availability;
    private String status;
    private float price;
    private String bed_type;

    public void setRoom(int room_no , String availability , String status , float price , String bed_type){
        this.room_no = room_no;
        this.availability = availability;
        this.status = status;
        this.price = price;
        this.bed_type = bed_type;
    }

    public int getRoomNO(){ return room_no; }
    public String getAvailability(){ return availability; }
    public String getStatus(){ return status; }
    public float getPrice(){ return price; }
    public String getBedType(){ return bed_type; }
}
