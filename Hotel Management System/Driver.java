public class Driver {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String company;
    private String brand;
    private String availability;
    private String location;

    public void setDriver(String id , String name , int age , String gender , String company , String brand , String availability , String location){
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.company = company;
        this.brand = brand;
        this.availability = availability;
        this.location = location;
    }

    public String getID(){ return id; }
    public String getName(){ return name; }
    public int getAge(){ return age; }
    public String getGender(){ return gender; }
    public String getCompany(){ return company; }
    public String getBrand(){ return brand; }
    public String getAvailability(){ return availability; }
    public String getLocation(){ return location; }
}
