public class Employee {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String job;
    private float salary;
    private String phone;
    private String email;

    public void setEmployee(String id, String name, int age, String gender, String job, float salary, String phone, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.job = job;
        this.salary = salary;
        this.phone = phone;
        this.email = email;
    }

    public String getID(){ return id; }
    public String getName(){ return name; }
    public int getAge(){ return age; }
    public String getGender(){ return gender; }
    public String getJob(){ return job; }
    public float getSalary(){ return salary; }
    public String getPhone(){ return phone; }
    public String getEmail(){ return email; }
}