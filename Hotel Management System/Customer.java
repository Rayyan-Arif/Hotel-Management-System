public class Customer{
    private String id;
    private String name;
    private int age;
    private String gender;
    private String country;
    private int room_no;
    private float deposit;
    private String checkIN;

    public void setCustomer(String id , String name , int age , String gender , String country , int room_no , float deposit , String checkIN){
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.country = country;
        this.room_no = room_no;
        this.deposit = deposit;
        this.checkIN = checkIN;
    }

    public String getID(){ return id; }
    public String getName(){ return name; }
    public int getAge(){ return age; }
    public String getGender(){ return gender; }
    public String getCountry(){ return country; }
    public int getRoomNO(){ return room_no; }
    public float getDeposit(){ return deposit; }
    public String getCheckIN(){ return checkIN; }
}