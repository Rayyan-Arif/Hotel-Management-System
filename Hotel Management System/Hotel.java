import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Hotel implements ActionListener{
    //creating all necessary variables for the class
    private JFrame mainWindow , loginWindow , customerWindow , roomWindow , driverWindow , employeeWindow , optionsWindow , windowForAllOptions;
    private Image mainHotelImage;
    private JLabel mainLabel , loginLabel , usernameLabel , passLabel , loginImageLabel , receptionLabel ,addImageLabel;
    private JLabel showCustomerDataLabels[] , customerFormTitle , roomImageLabel , showRoomDataLabels[] , roomFormTitle , driverImageLabel , employeeImageLabel , optionsImageLabel;
    private JLabel showDriverDataLabels[] , driverFormTitle , showEmployeeDataLabels[] , employeeFormTitle , showCheckOutLabels[] , blinkingLabel;
    private JLabel allthingslabel , roomStatusLabels[] , customerStatusLabels[] , noteLabel;
    private JTextField inputCustomerDataFields[] , inputDriverDataFields[] , inputEmployeeDataFields[] , inputCheckOutFields[] , price , inputCustomerStatusFields[];
    private JButton loginButton , submitCredentials , submitCustomerDetails , submitRoomDetails , submitDriverDetails , submitEmployeeDetails , allOptions[] , submitCheckOutDetails;
    private JButton updateRoom , updateCustomer;
    private int count = 0 , totalrooms = 0 , totalcustomers = 0 , totalemployees = 0 , totalmanagers = 0 , room_no , countImages = 0 , index = 0;
    private Dimension screenSize;
    private JTextField username , inputRoomNo , inputRoomPrice;
    private JPasswordField password;
    private JPanel overlayPanel , overlayPanel2 , overlayPanel3 , first5 , second5;
    private JMenuBar mainBar;
    private JMenu management , more;
    private JMenuItem options , add_driver , add_room , add_customer, add_employee;
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private javax.swing.Timer timer;
    private Customer customer;
    private Room room;
    private Driver driver;
    private Employee employee;
    private JComboBox<String> gender , bed_type , availability , status , job , ids , brands;
    private JComboBox<Integer> room_number;
    private String blinkingLabelText = "" , text = "   Limited Rooms Available. Book Now!!               " , empty = "";
    private JScrollPane scrollForAll;
    private JTable data;
    private DefaultTableModel model;
    private JTableHeader tableHeader;
    private JCheckBox isAvailable , isUnavailable;
    private ImageIcon images[];

    //Constructor initializes necessary components (especially those which are used in the timer)
    public Hotel() throws SQLException{
        employeeFormTitle = new JLabel("Fill details to add a employee....");
        driverFormTitle = new JLabel("Fill details to add a driver....");
        customerFormTitle = new JLabel("Fill details to add a customer....");
        roomFormTitle = new JLabel("Fill details to add a room....");
        blinkingLabel = new JLabel();
        blinkingLabel.setOpaque(false);
        blinkingLabel.setForeground(Color.white);
        blinkingLabel.setFont(new Font("Arial",Font.BOLD,20));
        allOptions = new JButton[10];
        accessDatabase();
        setMainWindow();
    }

    //The function that controls all the event listening (almost all)
    public void actionPerformed(ActionEvent e){
        if(count == 0){
            blinkingLabel.setText("");
            driverFormTitle.setText("");
            customerFormTitle.setText("");
            roomFormTitle.setText("");
            employeeFormTitle.setText("");
        } else if(count == 1){
            blinkingLabel.setText(blinkingLabelText);
            customerFormTitle.setText("Fill details to add a customer....");
            roomFormTitle.setText("Fill details to add a room....");
            driverFormTitle.setText("Fill details to add a driver....");
            employeeFormTitle.setText("Fill details to add a employee....");
        }
        count++;
        if(count==2) count = 0;

        if(e.getSource()==loginButton){
            setLoginWindow();
        } else if(e.getSource()==submitCredentials){
            char[] pass = {'r','e','c','e','p','t','i','o','n','1','2','3'};
            char[] corr = password.getPassword();

            if(username.getText().equals("Reception") && Arrays.equals(pass , corr)){
                loginWindow.dispose();
                mainWindow.remove(loginButton);
                mainWindow.revalidate();
                mainWindow.repaint();
                receptionLabel.setText("Successfully Logged In!!");
                mainWindow.setJMenuBar(mainBar);
            } else{
                JOptionPane.showMessageDialog(null,"Invalid Credentials!" ,"Failure To Login", JOptionPane.ERROR_MESSAGE);
            }
        } else if(e.getSource()==add_customer){
            try {
                setCustomerForm();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==submitCustomerDetails){
            if(gender.getSelectedItem()==null || room_number.getSelectedItem()==null
            || inputCustomerDataFields[0].getText().isEmpty() || inputCustomerDataFields[1].getText().isEmpty()
            || inputCustomerDataFields[2].getText().isEmpty() || inputCustomerDataFields[3].getText().isEmpty()
            || inputCustomerDataFields[4].getText().isEmpty() || inputCustomerDataFields[5].getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill all the details!" ,"Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else{
                customer = new Customer();
                customer.setCustomer(inputCustomerDataFields[0].getText(), inputCustomerDataFields[1].getText(), Integer.parseInt(inputCustomerDataFields[2].getText()), (String)gender.getSelectedItem() , inputCustomerDataFields[3].getText(), (Integer)room_number.getSelectedItem(), Float.parseFloat(inputCustomerDataFields[4].getText()), inputCustomerDataFields[5].getText());
                try {
                    if(checkUniqueID("customer", customer.getID())){
                        JOptionPane.showMessageDialog(null, "ID already present","Failure",JOptionPane.ERROR_MESSAGE);
                    } else
                    try {
                        st.executeUpdate("insert into customer values ('"+customer.getID()+"','"+customer.getName()+"',"+customer.getAge()+",'"+customer.getGender()+"','"+customer.getCountry()+"',"+customer.getRoomNO()+","+customer.getDeposit()+",'"+customer.getCheckIN()+"');");
                        customerWindow.dispose();
                        JOptionPane.showMessageDialog(null,"Customer Added Succesfully!" ,"Success", JOptionPane.INFORMATION_MESSAGE);
                        totalcustomers++;
                        st.executeUpdate("update room set availability = 'Unavailable' where room_no = "+customer.getRoomNO()+";");
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } catch (HeadlessException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource()==add_room){
            setRoomForm();
        } else if(e.getSource()==submitRoomDetails){
            if(inputRoomPrice.getText().isEmpty() || bed_type.getSelectedItem()==null
            || availability.getSelectedItem()==null || status.getSelectedItem()==null){
                JOptionPane.showMessageDialog(null,"Please fill all the details!" ,"Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else{
                room = new Room();
                room.setRoom(Integer.parseInt(inputRoomNo.getText()),(String)availability.getSelectedItem(),(String)status.getSelectedItem(),Float.parseFloat(inputRoomPrice.getText()),(String)bed_type.getSelectedItem());
                try {
                    st.executeUpdate("insert into room values ("+room.getRoomNO()+",'"+room.getAvailability()+"','"+room.getStatus()+"',"+room.getPrice()+",'"+room.getBedType()+"');");
                    JOptionPane.showMessageDialog(null,"Room Added Succesfully!" ,"Success", JOptionPane.INFORMATION_MESSAGE);
                    roomWindow.dispose();
                    totalrooms++;
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource()==add_driver){
            setDriverForm();
        } else if(e.getSource()==submitDriverDetails){
            if(inputDriverDataFields[0].getText().isEmpty() || inputDriverDataFields[1].getText().isEmpty()
            || inputDriverDataFields[2].getText().isEmpty() || inputDriverDataFields[3].getText().isEmpty()
            || inputDriverDataFields[4].getText().isEmpty() || inputDriverDataFields[5].getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill all the details!" ,"Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else{
                driver = new Driver();
                driver.setDriver(inputDriverDataFields[0].getText(),inputDriverDataFields[1].getText(),Integer.parseInt(inputDriverDataFields[2].getText()),(String)gender.getSelectedItem(),inputDriverDataFields[3].getText(),inputDriverDataFields[4].getText(),(String)availability.getSelectedItem(),inputDriverDataFields[5].getText());
                try {
                    if(checkUniqueID("driver", driver.getID())){
                        JOptionPane.showMessageDialog(null, "ID already present","Failure",JOptionPane.ERROR_MESSAGE);
                    } else{
                        driverWindow.dispose();
                        JOptionPane.showMessageDialog(null,"Driver Added Succesfully!" ,"Success", JOptionPane.INFORMATION_MESSAGE);
                        try {
                            st.executeUpdate("insert into driver values ('"+driver.getID()+"','"+driver.getName()+"',"+driver.getAge()+",'"+driver.getGender()+"','"+driver.getCompany()+"','"+driver.getBrand()+"','"+driver.getAvailability()+"','"+driver.getLocation()+"');");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (HeadlessException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource()==add_employee){
            setEmployeeForm();
        } else if(e.getSource()==submitEmployeeDetails){
            if(inputEmployeeDataFields[0].getText().isEmpty() || inputEmployeeDataFields[1].getText().isEmpty()
            || inputEmployeeDataFields[2].getText().isEmpty() || inputEmployeeDataFields[3].getText().isEmpty()
            || inputEmployeeDataFields[4].getText().isEmpty() || inputEmployeeDataFields[5].getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill all the details!" ,"Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else{
                employee = new Employee();
                employee.setEmployee(inputEmployeeDataFields[0].getText(),inputEmployeeDataFields[1].getText(),Integer.parseInt(inputEmployeeDataFields[2].getText()),(String)gender.getSelectedItem(),(String)job.getSelectedItem(),Float.parseFloat(inputEmployeeDataFields[3].getText()),inputEmployeeDataFields[4].getText(),inputEmployeeDataFields[5].getText());
                try {
                    if(checkUniqueID("employee", employee.getID()) || checkUniqueID("manager", employee.getID())){
                        JOptionPane.showMessageDialog(null, "ID already present","Failure",JOptionPane.ERROR_MESSAGE);
                    } else{
                        employeeWindow.dispose();
                        JOptionPane.showMessageDialog(null,"Employee Added Succesfully!" ,"Success", JOptionPane.INFORMATION_MESSAGE);
                        if("Manager".equals(job.getSelectedItem())){
                            try {
                                st.executeUpdate("insert into manager values ('"+employee.getID()+"','"+employee.getName()+"',"+employee.getAge()+",'"+employee.getGender()+"',"+employee.getSalary()+",'"+employee.getPhone()+"','"+employee.getEmail()+"');");
                                totalmanagers++;
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        } else{
                            try {
                                st.executeUpdate("insert into employee values ('"+employee.getID()+"','"+employee.getName()+"',"+employee.getAge()+",'"+employee.getGender()+"','"+employee.getJob()+"',"+employee.getSalary()+",'"+employee.getPhone()+"','"+employee.getEmail()+"');");
                                totalemployees++;
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } catch (HeadlessException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource()==options){
            showOptions();
        } else if(e.getSource()==allOptions[9]){
            optionsWindow.setVisible(false);;
            mainWindow.setJMenuBar(null);
            receptionLabel.setText("");
            mainWindow.add(loginButton);
            mainWindow.revalidate();
            mainWindow.repaint();
        } else if(e.getSource()==allOptions[5]){
            optionsWindow.setVisible(false);
            try {
                doCheckOut();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==submitCheckOutDetails){
            try {
                st.executeUpdate("insert into checkinout values ('"+(String)ids.getSelectedItem()+"',"+Integer.parseInt(inputCheckOutFields[0].getText())+",'"+inputCheckOutFields[1].getText()+"','"+inputCheckOutFields[2].getText()+"');");
                st.executeUpdate("update room set availability = 'Available' where room_no = "+Integer.parseInt(inputCheckOutFields[0].getText()));
                st.executeUpdate("delete from customer where room_no = "+Integer.parseInt(inputCheckOutFields[0].getText()));
                totalcustomers--;
                windowForAllOptions.dispose();
                optionsWindow.setVisible(true);
                JOptionPane.showMessageDialog(null,"Check Out Successfull","Success",JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==allOptions[1]){
            optionsWindow.setVisible(false);
            try {
                showRoomList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==allOptions[2]){
            optionsWindow.setVisible(false);
            try {
                showEmployeeList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==allOptions[3]){
            optionsWindow.setVisible(false);
            try {
                showCustomerList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==allOptions[4]){
            optionsWindow.setVisible(false);
            try {
                showManagerList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==allOptions[8]){
            optionsWindow.setVisible(false);
            try {
                pickupService();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==allOptions[7]){
            optionsWindow.setVisible(false);
            try {
                updateRoomStatus();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==updateRoom){
            if(room_number.getSelectedItem()==null || price.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill all the details!" ,"Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else{
                room = new Room();
                room.setRoom((Integer)room_number.getSelectedItem(),(String)availability.getSelectedItem(),(String)status.getSelectedItem(),Float.parseFloat(price.getText()),(String)bed_type.getSelectedItem());
                try {
                    st.executeUpdate("update room set availability = '"+room.getAvailability()+"',status = '"+room.getStatus()+"',price = "+room.getPrice()+",bed_type = '"+room.getBedType()+"' where room_no = "+room.getRoomNO()+";");
                    JOptionPane.showMessageDialog(null, "Room Updated Successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                    windowForAllOptions.dispose();
                    optionsWindow.setVisible(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource()==allOptions[6]){
            optionsWindow.setVisible(false);
            try {
                updateCustomerStatus();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()==updateCustomer){
            if(ids.getSelectedItem()==null || inputCustomerStatusFields[0].getText().isEmpty() || inputCustomerStatusFields[1].getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill all the details!" ,"Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else{
                try {
                    st.executeUpdate("update customer set name = '"+inputCustomerStatusFields[0].getText()+"',room_no = "+(Integer)room_number.getSelectedItem()+",deposit = "+Float.parseFloat(inputCustomerStatusFields[1].getText())+" where id = '"+ids.getSelectedItem()+"';");
                    st.executeUpdate("update room set availability = 'Unavailable' where room_no = "+(Integer)room_number.getSelectedItem()+";");
                    if(room_no!=(Integer)room_number.getSelectedItem()){
                        st.executeUpdate("update room set availability = 'Available' where room_no = "+room_no+";");
                    }
                    optionsWindow.setVisible(true);
                    JOptionPane.showMessageDialog(null, "Customer Updated Successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                    windowForAllOptions.dispose();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource()==allOptions[0]){
            optionsWindow.setVisible(false);
            try {
                searchRoom();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    //This function connects with the database and creates necessary tables
    public void accessDatabase() throws SQLException{
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306","root","yourpassword");
        st = con.createStatement();
        st.executeUpdate("create database if not exists hotel");
        st.executeUpdate("use hotel");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel","root","yourpassword");
        st.executeUpdate("create table if not exists room (room_no int primary key, availability varchar(20) , status varchar(10) , price float , bed_type varchar(20));");
        st.executeUpdate("create table if not exists manager (id varchar(13) primary key , name varchar(100) , age int , gender varchar(10) , salary float , phone varchar(11) , email varchar(255));");
        st.executeUpdate("create table if not exists customer (id varchar(13) primary key , name varchar(100) , age int , gender varchar(10) , country varchar(30) , room_no int , deposit float , checkIN_Status DATETIME , foreign key (room_no) references room(room_no));");
        st.executeUpdate("create table if not exists driver (id varchar(13) primary key , name varchar(100) , age int , gender varchar(10) , company varchar(25) , brand varchar(25) , availability varchar(20) , location varchar(100));");
        st.executeUpdate("create table if not exists checkINOUT (customer_id varchar(13) , room_no int , checkIN datetime , checkOUT datetime ,foreign key(room_no) references room(room_no));");
        st.executeUpdate("create table if not exists employee (id varchar(13) primary key , name varchar(100) , age int , gender varchar(10) , job varchar(30) , salary float , phone varchar(11) , email varchar(100));");
        rs = st.executeQuery("Select count(*) from room;");
        if(rs.next()) totalrooms = rs.getInt(1);
        rs = st.executeQuery("Select count(*) from customer;");
        if(rs.next()) totalcustomers = rs.getInt(1);
        rs = st.executeQuery("Select count(*) from employee;");
        if(rs.next()) totalemployees = rs.getInt(1);
        rs = st.executeQuery("Select count(*) from manager;");
        if(rs.next()) totalmanagers = rs.getInt(1);
    }

    //function that sets up the main window
    public void setMainWindow(){
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainWindow = new JFrame("R'Hotel");
        mainWindow.setIconImage(Toolkit.getDefaultToolkit().getImage("hotelicon.jpeg"));

        loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(200,70));
        loginButton.setBounds(660,600,200,70);
        loginButton.setFont(new Font("Arial",Font.ROMAN_BASELINE,30));
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.white);
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);

        images = new ImageIcon[5];
        
        images[0] = setImage("mainHotelImage.png");
        images[1] = setImage("mainHotelImage.jpg");
        images[2] = setImage("mainhotelimage2.jpg");
        images[3] = setImage("mainhotelimage3.jpg");
        images[4] = setImage("mainhotelimage4.jpg");

        mainLabel = new JLabel(images[0]);
        mainLabel.setBounds(0,0,screenSize.width , screenSize.height-45);

        overlayPanel = new JPanel();
        overlayPanel.setPreferredSize(new Dimension(screenSize.width,screenSize.height-45));
        overlayPanel.setBounds(0,0,screenSize.width,screenSize.height-45);
        overlayPanel.setBackground(new Color(0, 0, 0, 90));

        timer = new javax.swing.Timer(700,this);
        timer.start();

        receptionLabel = new JLabel();
        receptionLabel.setPreferredSize(new Dimension(500,70));
        receptionLabel.setBounds(610,600,500,70);
        receptionLabel.setForeground(Color.WHITE);
        receptionLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,30));
        receptionLabel.setOpaque(false);

        noteLabel = new JLabel("hello");
        noteLabel.setForeground(Color.white);
        noteLabel.setBounds(380,10,1000,40);
        noteLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,40));

        //this is for text occuring letter by letter animation
        javax.swing.Timer onebyone = new javax.swing.Timer(100,new ActionListener() {
            public void actionPerformed(ActionEvent e){
                empty += text.charAt(index);
                noteLabel.setText(empty);
                index++;
                if(index==text.length()){
                    index = 0;
                    empty = "";
                }
            }
        });
        onebyone.start();

        mainBar = new JMenuBar();
        management = new JMenu("MANAGEMENT");
        more = new JMenu("MORE");
        options = new JMenuItem("OPTIONS");
        add_driver = new JMenuItem("ADD DRIVER");
        add_customer = new JMenuItem("ADD CUSTOMER");
        add_room = new JMenuItem("ADD ROOM");
        add_employee = new JMenuItem("ADD EMPLOYEE");
        management.setMnemonic(KeyEvent.VK_M);
        more.setMnemonic(KeyEvent.VK_M);
        options.setMnemonic(KeyEvent.VK_O);
        add_customer.setMnemonic(KeyEvent.VK_A);
        add_driver.setMnemonic(KeyEvent.VK_A);
        add_room.setMnemonic(KeyEvent.VK_A);
        add_employee.setMnemonic(KeyEvent.VK_A);
        management.add(options);
        more.add(add_customer);
        more.add(add_driver);
        more.add(add_room);
        more.add(add_employee);
        more.setForeground(Color.blue);
        management.setForeground(Color.red);
        mainBar.add(management);
        mainBar.add(more);
        add_customer.addActionListener(this);
        add_driver.addActionListener(this);
        add_room.addActionListener(this);
        add_employee.addActionListener(this);
        options.addActionListener(this);

        mainWindow.add(noteLabel);
        mainWindow.add(receptionLabel);
        mainWindow.add(loginButton);
        mainBar.setOpaque(true);
        mainWindow.setLayout(null);
        mainWindow.setSize(screenSize.width , screenSize.height-45);
        mainWindow.setBounds(0,0,screenSize.width,screenSize.height-45);
        mainWindow.add(overlayPanel);
        mainWindow.add(mainLabel);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);
        mainWindow.setVisible(true);

        //this timer is for image changing
        javax.swing.Timer temp = new javax.swing.Timer(2000,new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(countImages==0){
                    mainLabel.setIcon(images[1]);
                } else if(countImages==1){
                    mainLabel.setIcon(images[2]);
                } else if(countImages==2){
                    mainLabel.setIcon(images[3]);
                } else if(countImages==3){ 
                    mainLabel.setIcon(images[4]);
                } else{
                    mainLabel.setIcon(images[0]);
                }
                countImages = (countImages + 1) % 5;
    
                mainWindow.revalidate();
                mainWindow.repaint();
            }
        });
        temp.start();
    }

    //this function sets up the login window
    public void setLoginWindow(){
        loginWindow = new JFrame("Login");
        loginWindow.setIconImage(Toolkit.getDefaultToolkit().getImage("login_icon.png"));

        loginLabel = new JLabel("Enter Login Credentials");
        loginLabel.setPreferredSize(new Dimension(300,70));
        loginLabel.setBounds(45,10,300,70);
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
        loginLabel.setOpaque(false);

        usernameLabel = new JLabel("Username: ");
        usernameLabel.setPreferredSize(new Dimension(150,70));
        usernameLabel.setBounds(20,70,150,70);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,15));
        usernameLabel.setOpaque(false);

        passLabel = new JLabel("Password: ");
        passLabel.setPreferredSize(new Dimension(150,70));
        passLabel.setBounds(20,120,150,70);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,15));
        passLabel.setOpaque(false);

        submitCredentials = setButton("SUBMIT", 70, 200 , 150 , 40);

        ImageIcon tempIcon = new ImageIcon("LoginImage.jpg");
        Image loginImage2 = tempIcon.getImage();
        loginImage2 = loginImage2.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage2);
        loginImageLabel = new JLabel(tempIcon);
        loginImageLabel.setBounds(0,0,800 , 500);

        username = new JTextField();
        username.setPreferredSize(new Dimension(300,30));
        username.setBounds(155,80,300,50);
        username.setForeground(Color.white);
        username.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
        username.setCaretColor(Color.WHITE);
        username.setOpaque(false);
        username.setBorder(null);

        password = new JPasswordField();
        password.setPreferredSize(new Dimension(300,30));
        password.setBounds(155,130,300,50);
        password.setForeground(Color.white);
        password.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
        password.setCaretColor(Color.WHITE);
        password.setOpaque(false);
        password.setBorder(null);
            
        overlayPanel2 = new JPanel();
        overlayPanel2.setPreferredSize(new Dimension(800,500));
        overlayPanel2.setBounds(0,0,800,500);
        overlayPanel2.setBackground(new Color(0, 0, 0, 120));

        loginWindow.setLayout(null);
        loginWindow.setSize(800,500);
        loginWindow.setLocationRelativeTo(null);
        loginWindow.add(username);
        loginWindow.add(password);
        loginWindow.add(loginLabel);
        loginWindow.add(usernameLabel);
        loginWindow.add(passLabel);
        loginWindow.add(submitCredentials);
        loginWindow.add(overlayPanel2);
        loginWindow.add(loginImageLabel);
        loginWindow.setResizable(false);
        loginWindow.setVisible(true);
    }

    //this function sets up the customer form , used for adding customer
    public void setCustomerForm() throws SQLException{
        customerWindow = new JFrame("Add Customer");

        ImageIcon tempIcon = new ImageIcon("addimage.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        addImageLabel = new JLabel(tempIcon);
        addImageLabel.setBounds(0,0,800 , 500);

        customerFormTitle.setPreferredSize(new Dimension(300,70));
        customerFormTitle.setBounds(20,5,300,70);
        customerFormTitle.setForeground(Color.WHITE);
        customerFormTitle.setFont(new Font("Arial",Font.BOLD,20));
        customerFormTitle.setOpaque(false);

        showCustomerDataLabels = new JLabel[8];
        showCustomerDataLabels[0] = setDataLabels("ID:", 50);
        showCustomerDataLabels[1] = setDataLabels("NAME:", 95);
        showCustomerDataLabels[2] = setDataLabels("AGE:", 140);
        showCustomerDataLabels[3] = setDataLabels("GENDER:", 185);
        showCustomerDataLabels[4] = setDataLabels("COUNTRY:", 230);
        showCustomerDataLabels[5] = setDataLabels("ROOM NUMBER:", 275);
        showCustomerDataLabels[6] = setDataLabels("DEPOSIT:", 320);
        showCustomerDataLabels[7] = setDataLabels("CHECK IN:", 365);

        inputCustomerDataFields = new JTextField[6];
        inputCustomerDataFields[0] = inputData(68);
        inputCustomerDataFields[1] = inputData(113);
        inputCustomerDataFields[2] = inputData(158);
        inputCustomerDataFields[3] = inputData(248);
        inputCustomerDataFields[4] = inputData(338);
        inputCustomerDataFields[5] = inputData(383);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        inputCustomerDataFields[5].setText(formattedDateTime);
        inputCustomerDataFields[5].setEditable(false);

        ArrayList<Integer> rooms = new ArrayList<>();
        rs = st.executeQuery("select room_no from room where availability = 'Available';");
        while(rs.next()){
            rooms.add(rs.getInt(1));
        }

        room_number = new JComboBox<>(rooms.toArray(new Integer[0]));
        room_number.setPreferredSize(new Dimension(300,30));
        room_number.setBounds(200 , 293 , 300 , 30);
        room_number.setBackground(Color.black);
        room_number.setForeground(Color.white);
        gender = new JComboBox<>(new String[]{"Male" , "Female"});
        gender.setPreferredSize(new Dimension(300,30));
        gender.setBounds(200 , 203 , 300 , 30);
        gender.setBackground(Color.black);
        gender.setForeground(Color.white);
        
        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(800,500));
        overlayPanel3.setBounds(0,0,800,500);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        submitCustomerDetails = setButton("SUBMIT", 622, 10 , 150 , 40);
        
        customerWindow.setLayout(null);
        customerWindow.setSize(800,500);
        customerWindow.setLocationRelativeTo(null);

        customerWindow.add(customerFormTitle);
        customerWindow.add(showCustomerDataLabels[0]);
        customerWindow.add(showCustomerDataLabels[1]);
        customerWindow.add(showCustomerDataLabels[2]);
        customerWindow.add(showCustomerDataLabels[3]);
        customerWindow.add(showCustomerDataLabels[4]);
        customerWindow.add(showCustomerDataLabels[5]);
        customerWindow.add(showCustomerDataLabels[6]);
        customerWindow.add(showCustomerDataLabels[7]);

        customerWindow.add(inputCustomerDataFields[0]);
        customerWindow.add(inputCustomerDataFields[1]);
        customerWindow.add(inputCustomerDataFields[2]);
        customerWindow.add(inputCustomerDataFields[3]);
        customerWindow.add(inputCustomerDataFields[4]);
        customerWindow.add(inputCustomerDataFields[5]);

        customerWindow.add(room_number);
        customerWindow.add(gender);

        customerWindow.add(submitCustomerDetails);
        customerWindow.add(overlayPanel3);
        customerWindow.add(addImageLabel);
        customerWindow.setResizable(false);
        customerWindow.setVisible(true);
    }

    //this function sets up the room form , used for adding room
    public void setRoomForm(){
        roomWindow = new JFrame("Add Room");

        ImageIcon tempIcon = new ImageIcon("roomimage.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        roomImageLabel = new JLabel(tempIcon);
        roomImageLabel.setBounds(0,0,800 , 500);

        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(800,500));
        overlayPanel3.setBounds(0,0,800,500);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        roomFormTitle.setPreferredSize(new Dimension(300,70));
        roomFormTitle.setBounds(20,5,300,70);
        roomFormTitle.setForeground(Color.WHITE);
        roomFormTitle.setFont(new Font("Arial",Font.BOLD,20));
        roomFormTitle.setOpaque(false);

        showRoomDataLabels = new JLabel[5];
        showRoomDataLabels[0] = setDataLabels("ROOM NUMBER:", 50);
        showRoomDataLabels[1] = setDataLabels("AVAILABILITY:", 95);
        showRoomDataLabels[2] = setDataLabels("STATUS:", 140);
        showRoomDataLabels[3] = setDataLabels("PRICE:", 185);
        showRoomDataLabels[4] = setDataLabels("BED TYPE:", 230);

        inputRoomNo = inputData(68);
        inputRoomPrice = inputData(203);
        inputRoomNo.setOpaque(false);
        inputRoomPrice.setOpaque(false);
        inputRoomNo.setText(String.valueOf(totalrooms+1));
        inputRoomNo.setEditable(false);
        inputRoomNo.setCaretColor(new Color(0, 0, 0, 0));

        bed_type = new JComboBox<>(new String[]{"Single Bed ", "Double Bed"});
        availability = new JComboBox<>(new String[]{"Available" , "Unavailable"});
        status = new JComboBox<>(new String[]{"Clean" , "Dirty"});
        bed_type.setBounds(200,248,300,30);
        availability.setBounds(200,113,300,30);
        status.setBounds(200,158,300,30);

        submitRoomDetails = setButton("SUBMIT",622, 10 , 150 , 40);

        roomWindow.setLayout(null);
        roomWindow.setSize(800,500);
        roomWindow.setLocationRelativeTo(null);
        roomWindow.add(roomFormTitle);
        roomWindow.add(showRoomDataLabels[0]);
        roomWindow.add(showRoomDataLabels[1]);
        roomWindow.add(showRoomDataLabels[2]);
        roomWindow.add(showRoomDataLabels[3]);
        roomWindow.add(showRoomDataLabels[4]);
        roomWindow.add(submitRoomDetails);
        roomWindow.add(bed_type);
        roomWindow.add(availability);
        roomWindow.add(status);
        roomWindow.add(inputRoomNo);
        roomWindow.add(inputRoomPrice);
        roomWindow.add(overlayPanel3);
        roomWindow.add(roomImageLabel);
        roomWindow.setResizable(false);
        roomWindow.setVisible(true);
    }

    //this function sets up the driver form , used for adding driver
    public void setDriverForm(){
        driverWindow = new JFrame("Add Driver");

        ImageIcon tempIcon = new ImageIcon("driver.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        driverImageLabel = new JLabel(tempIcon);
        driverImageLabel.setBounds(0,0,800 , 500);

        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(800,500));
        overlayPanel3.setBounds(0,0,800,500);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        driverFormTitle.setPreferredSize(new Dimension(300,70));
        driverFormTitle.setBounds(20,5,300,70);
        driverFormTitle.setForeground(Color.WHITE);
        driverFormTitle.setFont(new Font("Arial",Font.BOLD,20));
        driverFormTitle.setOpaque(false);

        showDriverDataLabels = new JLabel[8];
        showDriverDataLabels[0] = setDataLabels("ID:", 50);
        showDriverDataLabels[1] = setDataLabels("NAME:", 95);
        showDriverDataLabels[2] = setDataLabels("AGE:", 140);
        showDriverDataLabels[3] = setDataLabels("GENDER:", 185);
        showDriverDataLabels[4] = setDataLabels("COMPANY:", 230);
        showDriverDataLabels[5] = setDataLabels("BRAND:", 275);
        showDriverDataLabels[6] = setDataLabels("AVAILABILITY:", 320);
        showDriverDataLabels[7] = setDataLabels("LOCATION:", 365);

        inputDriverDataFields = new JTextField[6];
        inputDriverDataFields[0] = inputData(68);
        inputDriverDataFields[1] = inputData(113);
        inputDriverDataFields[2] = inputData(158);
        gender = new JComboBox<>(new String[]{"Male","Female"});
        gender.setBackground(Color.BLACK);
        gender.setForeground(Color.white);
        gender.setBounds(200,203,300,30);
        inputDriverDataFields[3] = inputData(248);
        inputDriverDataFields[4] = inputData(293);
        availability = new JComboBox<>(new String[]{"Available","Unavailable"});
        availability.setBackground(Color.BLACK);
        availability.setForeground(Color.white);
        availability.setBounds(200,338,300,30);
        inputDriverDataFields[5] = inputData(383);

        submitDriverDetails = setButton("SUBMIT",622,10,150,40);

        driverWindow.setLayout(null);
        driverWindow.setSize(800,500);
        driverWindow.setLocationRelativeTo(null);
        driverWindow.add(submitDriverDetails);
        driverWindow.add(driverFormTitle);
        driverWindow.add(showDriverDataLabels[0]);
        driverWindow.add(showDriverDataLabels[1]);
        driverWindow.add(showDriverDataLabels[2]);
        driverWindow.add(showDriverDataLabels[3]);
        driverWindow.add(showDriverDataLabels[4]);
        driverWindow.add(showDriverDataLabels[5]);
        driverWindow.add(showDriverDataLabels[6]);
        driverWindow.add(showDriverDataLabels[7]);
        driverWindow.add(inputDriverDataFields[0]);
        driverWindow.add(inputDriverDataFields[1]);
        driverWindow.add(inputDriverDataFields[2]);
        driverWindow.add(inputDriverDataFields[3]);
        driverWindow.add(inputDriverDataFields[4]);
        driverWindow.add(inputDriverDataFields[5]);
        driverWindow.add(gender);
        driverWindow.add(availability);
        driverWindow.add(overlayPanel3);
        driverWindow.add(driverImageLabel);
        driverWindow.setResizable(false);
        driverWindow.setVisible(true);
    }

    //this function sets up the employee form , used for adding employee
    public void setEmployeeForm(){
        employeeWindow = new JFrame("Add Employee");

        ImageIcon tempIcon = new ImageIcon("employee.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        employeeImageLabel = new JLabel(tempIcon);
        employeeImageLabel.setBounds(0,0,800 , 500);

        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(800,500));
        overlayPanel3.setBounds(0,0,800,500);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        employeeFormTitle.setPreferredSize(new Dimension(300,70));
        employeeFormTitle.setBounds(20,5,300,70);
        employeeFormTitle.setForeground(Color.WHITE);
        employeeFormTitle.setFont(new Font("Arial",Font.BOLD,20));
        employeeFormTitle.setOpaque(false);

        showEmployeeDataLabels = new JLabel[8];
        showEmployeeDataLabels[0] = setDataLabels("ID:", 50);
        showEmployeeDataLabels[1] = setDataLabels("NAME:", 95);
        showEmployeeDataLabels[2] = setDataLabels("AGE:", 140);
        showEmployeeDataLabels[3] = setDataLabels("GENDER:", 185);
        showEmployeeDataLabels[4] = setDataLabels("JOB:", 230);
        showEmployeeDataLabels[5] = setDataLabels("SALARY:", 275);
        showEmployeeDataLabels[6] = setDataLabels("PHONE:", 320);
        showEmployeeDataLabels[7] = setDataLabels("EMAIL:", 365);

        submitEmployeeDetails = setButton("SUBMIT",622,10,150,40);

        inputEmployeeDataFields = new JTextField[6];
        inputEmployeeDataFields[0] = inputData(68);
        inputEmployeeDataFields[1] = inputData(113);
        inputEmployeeDataFields[2] = inputData(158);
        inputEmployeeDataFields[3] = inputData(293);
        inputEmployeeDataFields[4] = inputData(338);
        inputEmployeeDataFields[5] = inputData(383);

        gender = new JComboBox<>(new String[]{"Male","Female"});
        gender.setBackground(Color.BLACK);
        gender.setForeground(Color.white);
        gender.setBounds(200,203,300,30);

        job = new JComboBox<>(new String[]{"Waiter","Manager","Chef","Guard","Porter","Plumber","Technician","Electrician"});
        job.setBackground(Color.BLACK);
        job.setForeground(Color.white);
        job.setBounds(200,248,300,30);
        
        employeeWindow.setLayout(null);
        employeeWindow.setSize(800,500);
        employeeWindow.setLocationRelativeTo(null);
        employeeWindow.add(submitEmployeeDetails);
        employeeWindow.add(employeeFormTitle);
        employeeWindow.add(showEmployeeDataLabels[0]);
        employeeWindow.add(showEmployeeDataLabels[1]);
        employeeWindow.add(showEmployeeDataLabels[2]);
        employeeWindow.add(showEmployeeDataLabels[3]);
        employeeWindow.add(showEmployeeDataLabels[4]);
        employeeWindow.add(showEmployeeDataLabels[5]);
        employeeWindow.add(showEmployeeDataLabels[6]);
        employeeWindow.add(showEmployeeDataLabels[7]);
        employeeWindow.add(inputEmployeeDataFields[0]);
        employeeWindow.add(inputEmployeeDataFields[1]);
        employeeWindow.add(inputEmployeeDataFields[2]);
        employeeWindow.add(inputEmployeeDataFields[3]);
        employeeWindow.add(inputEmployeeDataFields[4]);
        employeeWindow.add(inputEmployeeDataFields[5]);
        employeeWindow.add(gender);
        employeeWindow.add(job);
        employeeWindow.add(overlayPanel3);
        employeeWindow.add(employeeImageLabel);
        employeeWindow.setResizable(false);
        employeeWindow.setVisible(true);
    }

    //this function sets up the option window from where we can perform different operations
    public void showOptions(){
        optionsWindow = new JFrame("OPTIONS");

        ImageIcon tempIcon = new ImageIcon("options.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        optionsImageLabel = new JLabel(tempIcon);
        optionsImageLabel.setBounds(0,0,800 , 500);

        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(800,500));
        overlayPanel3.setBounds(0,0,800,500);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        first5 = new JPanel();
        first5.setLayout(new BoxLayout(first5 , BoxLayout.Y_AXIS));
        first5.setBounds(0,0,400,500);
        first5.setOpaque(false);

        second5 = new JPanel();
        second5.setLayout(new BoxLayout(second5 , BoxLayout.Y_AXIS));
        second5.setBounds(400,0,400,500);
        second5.setOpaque(false);
        
        String optionslist[] = {"SEARCH ROOM","ROOM LIST","EMPLOYEE INFO","CUSTOMER INFO","MANAGER INFO","CHECK OUT","UPDATE CUSTOMER","UPDATE ROOM STATUS","PICKUP SERVICE","LOG OUT"};
        for(int i=0 ; i<10 ; i++){
            allOptions[i] = new JButton(optionslist[i]);
            allOptions[i].setPreferredSize(new Dimension(300,50));
            allOptions[i].setMaximumSize(new Dimension(300,50));
            allOptions[i].setMinimumSize(new Dimension(300,50));
            allOptions[i].setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
            allOptions[i].setBackground(Color.black);
            allOptions[i].setForeground(Color.white);
            allOptions[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            allOptions[i].setFocusable(false);
            allOptions[i].addActionListener(this);
            if(i<5){
                first5.add(Box.createVerticalStrut(35));
                first5.add(allOptions[i]);
            } else{
                second5.add(Box.createVerticalStrut(35));
                second5.add(allOptions[i]);
            }
        }

        optionsWindow.setLayout(null);
        optionsWindow.setSize(800,500);
        optionsWindow.setLocationRelativeTo(null);
        optionsWindow.add(first5);
        optionsWindow.add(second5);
        optionsWindow.add(overlayPanel3);
        optionsWindow.add(optionsImageLabel);
        optionsWindow.setResizable(false);
        optionsWindow.setVisible(true);
    }

    //check out function that sets up check out window 
    public void doCheckOut() throws SQLException{
        windowForAllOptions = new JFrame("CHECK OUT");

        ImageIcon tempIcon = new ImageIcon("options.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(800 , 500, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        optionsImageLabel = new JLabel(tempIcon);
        optionsImageLabel.setBounds(0,0,800 , 500);

        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(800,500));
        overlayPanel3.setBounds(0,0,800,500);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        showCheckOutLabels = new JLabel[4];
        showCheckOutLabels[0] = setDataLabels("CUSTOMER ID:",50);
        showCheckOutLabels[1] = setDataLabels("ROOM NUMBER:",105);
        showCheckOutLabels[2] = setDataLabels("CHECK IN:",160);
        showCheckOutLabels[3] = setDataLabels("CHECK OUT:",215);

        blinkingLabelText = "Fill details to check out....";
        blinkingLabel.setBounds(20,5,300,70);

        rs = st.executeQuery("select id from customer;");
        ids = new JComboBox<>();
        while(rs.next()){
            ids.addItem(rs.getString(1));
        }
        ids.setBounds(200,68,300,30);
        ids.setBackground(Color.BLACK);
        ids.setForeground(Color.white);
        ids.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        ids.setSelectedItem(null);
        
        //this will control what happens after user select any option from the combobox
        ids.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                try {
                    rs = st.executeQuery("select room_no , checkIN_status from customer where id = '"+(String)ids.getSelectedItem()+"';");
                    rs.next();
                    int r = rs.getInt(1);
                    String dt = rs.getString(2);
                    inputCheckOutFields[0].setText(String.valueOf(r));
                    inputCheckOutFields[1].setText(dt);
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = now.format(formatter);
                    inputCheckOutFields[2].setText(formattedDateTime);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        inputCheckOutFields = new JTextField[3];
        inputCheckOutFields[0] = inputData(125);
        inputCheckOutFields[1] = inputData(180);
        inputCheckOutFields[2] = inputData(235);
        inputCheckOutFields[0].setEditable(false);
        inputCheckOutFields[1].setEditable(false);
        inputCheckOutFields[2].setEditable(false);

        submitCheckOutDetails = setButton("SUBMIT",200,290,300,30);

        windowForAllOptions.setLayout(null);
        windowForAllOptions.setSize(800,500);
        windowForAllOptions.setLocationRelativeTo(null);
        windowForAllOptions.add(blinkingLabel);
        windowForAllOptions.add(ids);
        windowForAllOptions.add(submitCheckOutDetails);
        windowForAllOptions.add(showCheckOutLabels[0]);
        windowForAllOptions.add(showCheckOutLabels[1]);
        windowForAllOptions.add(showCheckOutLabels[2]);
        windowForAllOptions.add(showCheckOutLabels[3]);
        windowForAllOptions.add(inputCheckOutFields[0]);
        windowForAllOptions.add(inputCheckOutFields[1]);
        windowForAllOptions.add(inputCheckOutFields[2]);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setResizable(false);
        windowForAllOptions.setVisible(true);

        //this controls what happens if the user closes the window by X button
        windowForAllOptions.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                optionsWindow.setVisible(true);
            }
        });
    }

    //this function shows the list of all rooms , it uses a JTable for storing data from database
    public void showRoomList() throws SQLException{
        windowForAllOptions = setWindow("ROOM LIST",1000,500);

        allthingslabel = new JLabel("All Rooms Shown Here....");
        allthingslabel.setPreferredSize(new Dimension(300,70));
        allthingslabel.setBounds(370,5,300,70);
        allthingslabel.setForeground(Color.WHITE);
        allthingslabel.setFont(new Font("Arial",Font.BOLD,25));
        allthingslabel.setOpaque(false);

        rs = st.executeQuery("Select * from room;");
        String columns[] = {"ROOM NUMBER","AVAILABILITY","STATUS","PRICE","BED TYPE"};

        model = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        while(rs.next()){
            Object[] rowdata = {
                rs.getInt(1),rs.getString(2),rs.getString(3),rs.getFloat(4),rs.getString(5)
            };
            model.addRow(rowdata);
        }
        
        data = new JTable(model);
        data.setBackground(Color.BLACK);
        data.setForeground(Color.white);
        data.setRowHeight(40); 
        data.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));

        tableHeader = data.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.white);
        tableHeader.setFont(new Font("Arial",Font.BOLD,17));

        colorCell(data, 1);
        
        scrollForAll = new JScrollPane(data);
        scrollForAll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollForAll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        int height = (totalrooms+1)*40;
        if(height > 380) height = 380;
        scrollForAll.setBounds(0,75,985,height);

        windowForAllOptions.add(allthingslabel);
        windowForAllOptions.add(scrollForAll);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);
    }

    //this function shows the list of all employees , it uses a JTable for storing data from database
    public void showEmployeeList() throws SQLException{
        windowForAllOptions = setWindow("EMPLOYEE LIST",1500,500);

        allthingslabel = new JLabel("All Employees Shown Here....");
        allthingslabel.setBounds(550,5,400,70);
        allthingslabel.setForeground(Color.WHITE);
        allthingslabel.setFont(new Font("Arial",Font.BOLD,25));
        allthingslabel.setOpaque(false);

        rs = st.executeQuery("Select * from employee;");
        String columns[] = {"ID","NAME","AGE","GENDER","JOB","SALARY","PHONE","EMAIL"};

        model = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        while(rs.next()){
            Object[] rowdata = {
                rs.getString(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getFloat(6),rs.getString(7),rs.getString(8)
            };
            model.addRow(rowdata);
        }
        
        data = new JTable(model);
        data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i=0 ; i<8 ; i++){
            data.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        data.setBackground(Color.BLACK);
        data.setForeground(Color.white);
        data.setRowHeight(40); 
        data.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));

        tableHeader = data.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.white);
        tableHeader.setFont(new Font("Arial",Font.BOLD,17));
        
        scrollForAll = new JScrollPane(data);
        scrollForAll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollForAll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        int height = (totalemployees+1)*40;
        if(height > 380) height = 380;
        scrollForAll.setBounds(0,75,1485,height);

        windowForAllOptions.add(allthingslabel);
        windowForAllOptions.add(scrollForAll);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);
    }

    //this function shows the list of all customers , it uses a JTable for storing data from database
    public void showCustomerList() throws SQLException{
        windowForAllOptions = setWindow("CUSTOMER LIST",1500,500);

        allthingslabel = new JLabel("All Customers Shown Here....");
        allthingslabel.setBounds(550,5,400,70);
        allthingslabel.setForeground(Color.WHITE);
        allthingslabel.setFont(new Font("Arial",Font.BOLD,25));
        allthingslabel.setOpaque(false);

        rs = st.executeQuery("Select * from customer;");
        String columns[] = {"ID","NAME","AGE","GENDER","COUNTRY","ROOM NUMBER","DEPOSIT","CHECK IN"};
        
        model = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        while(rs.next()){
            Object[] rowdata = {
                rs.getString(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getInt(6),rs.getFloat(7),rs.getString(8)
            };
            model.addRow(rowdata);
        }
        
        data = new JTable(model);
        data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i=0 ; i<8 ; i++){
            data.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        data.setBackground(Color.BLACK);
        data.setForeground(Color.white);
        data.setRowHeight(40); 
        data.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));

        tableHeader = data.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.white);
        tableHeader.setFont(new Font("Arial",Font.BOLD,17));
        
        scrollForAll = new JScrollPane(data);
        scrollForAll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollForAll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        int height = (totalcustomers+1)*40;   //this is to set maximum height of the scrollPane
        if(height > 380) height = 380;
        scrollForAll.setBounds(0,75,1485,height);

        windowForAllOptions.add(allthingslabel);
        windowForAllOptions.add(scrollForAll);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);
    }

    //this function shows the list of all managers , it uses a JTable for storing data from database
    public void showManagerList() throws SQLException{
        windowForAllOptions = setWindow("MANAGER LIST",1500,500);

        allthingslabel = new JLabel("All Managers Shown Here....");
        allthingslabel.setBounds(550,5,400,70);
        allthingslabel.setForeground(Color.WHITE);
        allthingslabel.setFont(new Font("Arial",Font.BOLD,25));
        allthingslabel.setOpaque(false);

        rs = st.executeQuery("Select * from manager;");
        String columns[] = {"ID","NAME","AGE","GENDER","SALARY","PHONE","EMAIL"};

        model = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        while(rs.next()){
            Object[] rowdata = {
                rs.getString(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getFloat(5),rs.getString(6),rs.getString(7)
            };
            model.addRow(rowdata);
        }
        
        data = new JTable(model);
        data.setBackground(Color.BLACK);
        data.setForeground(Color.white);
        data.setRowHeight(40); 
        data.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));

        tableHeader = data.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.white);
        tableHeader.setFont(new Font("Arial",Font.BOLD,17));
        
        scrollForAll = new JScrollPane(data);
        scrollForAll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollForAll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        int height = (totalmanagers+1)*40;
        if(height > 380) height = 380;
        scrollForAll.setBounds(0,75,1485,height);

        windowForAllOptions.add(allthingslabel);
        windowForAllOptions.add(scrollForAll);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);
    }

    //this function shows all the drivers based on the brand user select from the JCombobox
    public void pickupService() throws SQLException{
        windowForAllOptions = setWindow("PICKUP SERVICE",1500,500);

        allthingslabel = new JLabel("PickUp Service");
        allthingslabel.setBounds(600,5,400,70);
        allthingslabel.setForeground(Color.WHITE);
        allthingslabel.setFont(new Font("Arial",Font.BOLD,25));
        allthingslabel.setOpaque(false);

        rs = st.executeQuery("select brand from driver;");

        JLabel carType = setDataLabels("Choose Brand:",62);
        carType.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
        brands = new JComboBox<>();
        while(rs.next()){
            brands.addItem(rs.getString(1));
        }
        brands.setSelectedItem(null);
        brands.setBackground(Color.black);
        brands.setForeground(Color.white);
        brands.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        brands.setBounds(240,82,150,30);

        String columns[] = {"ID","NAME","AGE","GENDER","COMPANY","BRAND","AVAILABILITY","LOCATION"};

        model = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        data = new JTable(model);
        data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i=0 ; i<8 ; i++){
            data.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        data.setBackground(Color.BLACK);
        data.setForeground(Color.white);
        data.setRowHeight(40); 
        data.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        tableHeader = data.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.white);
        tableHeader.setFont(new Font("Arial",Font.BOLD,17));

        scrollForAll = new JScrollPane(data);
        scrollForAll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollForAll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollForAll.setPreferredSize(new Dimension(1485,40));
        scrollForAll.setBounds(0,140,1485,40);

        windowForAllOptions.add(scrollForAll);
        windowForAllOptions.add(allthingslabel);
        windowForAllOptions.add(carType);
        windowForAllOptions.add(brands);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);

        //this is the event listener which controls what happens after user select an option from the JCombobox
        brands.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                int C = 0;
                try {
                    rs = st.executeQuery("select count(*) from driver where brand = '"+(String)brands.getSelectedItem()+"';");
                    rs.next();
                    C = rs.getInt(1);
                    rs = st.executeQuery("Select * from driver where brand = '"+(String)brands.getSelectedItem()+"';");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                model.setRowCount(0);

                try {
                    while(rs.next()){
                        Object[] rowdata = {
                            rs.getString(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)
                        };
                        model.addRow(rowdata);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                colorCell(data, 6);
                
                int height = (C+1)*40;
                if(height > 300) height = 300;
                scrollForAll.setPreferredSize(new Dimension(1485,height));
                scrollForAll.setBounds(0,140,1485,height);
                windowForAllOptions.revalidate();
                windowForAllOptions.repaint();
            }
        });
    }

    //this function allows to update room status depending on the room selected by user
    public void updateRoomStatus() throws SQLException{
        windowForAllOptions = setWindow("Update Room Status",800,500);

        blinkingLabelText = "Update Room Status....";
        blinkingLabel.setBounds(20,5,300,70);

        roomStatusLabels = new JLabel[5];
        roomStatusLabels[0] = setDataLabels("ROOM NUMBER:", 50);
        roomStatusLabels[1] = setDataLabels("AVAILABILITY:", 95);
        roomStatusLabels[2] = setDataLabels("STATUS:", 140);
        roomStatusLabels[3] = setDataLabels("PRICE:", 185);
        roomStatusLabels[4] = setDataLabels("BED TYPE:", 230);

        price = inputData(203);

        availability = new JComboBox<>(new String[]{"Available","Unavailable"});
        availability.setBackground(Color.black);
        availability.setForeground(Color.white);
        availability.setBounds(200,113,300,30);
        availability.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        availability.setSelectedItem(null);

        status = new JComboBox<>(new String[]{"Clean","Dirty"});
        status.setBackground(Color.black);
        status.setForeground(Color.white);
        status.setBounds(200,158,300,30);
        status.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        status.setSelectedItem(null);

        bed_type = new JComboBox<>(new String[]{"Single Bed","Double Bed"});
        bed_type.setBackground(Color.black);
        bed_type.setForeground(Color.white);
        bed_type.setBounds(200,248,300,30);
        bed_type.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        bed_type.setSelectedItem(null);

        room_number = new JComboBox<>();
        room_number.setBackground(Color.black);
        room_number.setForeground(Color.white);
        rs = st.executeQuery("select room_no from room;");
        while(rs.next()){
            room_number.addItem(rs.getInt(1));
        }
        room_number.setBounds(200,68,300,30);
        room_number.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        room_number.setSelectedItem(null);

        updateRoom = setButton("UPDATE",200,300,300,30);

        windowForAllOptions.add(blinkingLabel);
        windowForAllOptions.add(roomStatusLabels[0]);
        windowForAllOptions.add(roomStatusLabels[1]);
        windowForAllOptions.add(roomStatusLabels[2]);
        windowForAllOptions.add(roomStatusLabels[3]);
        windowForAllOptions.add(roomStatusLabels[4]);
        windowForAllOptions.add(availability);
        windowForAllOptions.add(room_number);
        windowForAllOptions.add(bed_type);
        windowForAllOptions.add(status);
        windowForAllOptions.add(price);
        windowForAllOptions.add(updateRoom);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);

        //this controls what happens after user select an option from combobox
        room_number.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                try {
                    if(room_number.getSelectedItem()!=null){
                        rs = st.executeQuery("select * from room where room_no = "+(Integer)room_number.getSelectedItem()+";");
                        rs.next();
                        availability.setSelectedItem(rs.getString(2));
                        status.setSelectedItem(rs.getString(3));
                        price.setText(String.valueOf(rs.getFloat(4)));
                        bed_type.setSelectedItem(rs.getString(5).trim());
                        rs = st.executeQuery("select * from customer where room_no = "+(Integer)room_number.getSelectedItem()+";");
                        if(rs.next()){
                            availability.setEnabled(false);
                        } else{
                            availability.setEnabled(true);
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    //same as updateRoomStatus , just applies to a customer
    public void updateCustomerStatus() throws SQLException{
        windowForAllOptions = setWindow("Update Customer Status", 800, 500);

        blinkingLabelText = "Update Customer Status....";
        blinkingLabel.setBounds(20,5,300,70);

        customerStatusLabels = new JLabel[5];
        customerStatusLabels[0] = setDataLabels("CUSTOMER ID:",50);
        customerStatusLabels[1] = setDataLabels("ROOM NUMBER:",95);
        customerStatusLabels[2] = setDataLabels("NAME:",140);
        customerStatusLabels[3] = setDataLabels("AMOUNT PAID:",185);
        customerStatusLabels[4] = setDataLabels("PENDING AMOUNT:",230);

        ids = new JComboBox<>();
        ids.setBackground(Color.black);
        ids.setForeground(Color.white);
        rs = st.executeQuery("select id from customer;");
        while(rs.next()){
            ids.addItem(rs.getString(1));
        }
        ids.setBounds(200,68,300,30);
        ids.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        ids.setSelectedItem(null);
        
        room_number = new JComboBox<>();
        room_number.setBackground(Color.black);
        room_number.setForeground(Color.white);
        rs = st.executeQuery("select room_no from room;");
        while(rs.next()){
            room_number.addItem(rs.getInt(1));
        }
        room_number.setBounds(200,113,300,30);
        room_number.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        room_number.setSelectedItem(null);

        inputCustomerStatusFields = new JTextField[3];
        inputCustomerStatusFields[0] = inputData(158);
        inputCustomerStatusFields[1] = inputData(203);
        inputCustomerStatusFields[2] = inputData(248);
        inputCustomerStatusFields[2].setEditable(false);

        updateCustomer = setButton("UPDATE",200,300,300,30);

        windowForAllOptions.add(ids);
        windowForAllOptions.add(room_number);
        windowForAllOptions.add(updateCustomer);
        windowForAllOptions.add(inputCustomerStatusFields[0]);
        windowForAllOptions.add(inputCustomerStatusFields[1]);
        windowForAllOptions.add(inputCustomerStatusFields[2]);
        windowForAllOptions.add(customerStatusLabels[0]);
        windowForAllOptions.add(customerStatusLabels[1]);
        windowForAllOptions.add(customerStatusLabels[2]);
        windowForAllOptions.add(customerStatusLabels[3]);
        windowForAllOptions.add(customerStatusLabels[4]);
        windowForAllOptions.add(blinkingLabel);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);

        ids.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if(ids.getSelectedItem()!=null){
                    try {
                        rs = st.executeQuery("select * from customer where id = '"+ids.getSelectedItem()+"';");
                        rs.next();
                        room_number.setSelectedItem(rs.getInt(6));
                        room_no = rs.getInt(6);
                        inputCustomerStatusFields[0].setText(rs.getString(2).trim());
                        inputCustomerStatusFields[1].setText(String.valueOf(rs.getFloat(7)));
                        float rem = rs.getFloat(7);
                        ResultSet temp = st.executeQuery("select price from room where room_no = "+rs.getInt(6)+";");
                        temp.next();
                        float pending = temp.getFloat(1)-rem;
                        if(pending<0) pending = 0;
                        inputCustomerStatusFields[2].setText(String.valueOf(pending));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    //this function allow to search for a room based on bed type and availability
    public void searchRoom() throws SQLException{
        windowForAllOptions = setWindow("Search Room",1000,500);

        blinkingLabelText = "Search Room Here....";
        blinkingLabel.setBounds(20,5,300,70);

        JLabel chooseBedType = setDataLabels("Choose Bed Type:",62);
        chooseBedType.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));

        bed_type = new JComboBox<>(new String[]{"Single Bed","Double Bed"});
        bed_type.setBackground(Color.black);
        bed_type.setForeground(Color.white);
        bed_type.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        bed_type.setBounds(220,82,300,30);
        bed_type.setSelectedItem(null);

        isAvailable = new JCheckBox();
        isAvailable.setText("Display Only Available");
        isAvailable.setFocusable(false);
        isAvailable.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        isAvailable.setBounds(700,20,300,30);
        isAvailable.setOpaque(false);
        isAvailable.setForeground(Color.white);

        isUnavailable = new JCheckBox();
        isUnavailable.setText("Display Only Unavailable");
        isUnavailable.setFocusable(false);
        isUnavailable.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        isUnavailable.setBounds(700,60,300,30);
        isUnavailable.setOpaque(false);
        isUnavailable.setForeground(Color.white);

        String columns[] = {"ROOM NUMBER","AVAILABILITY","STATUS","PRICE","BED TYPE"};

        model = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        data = new JTable(model);
        data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i=0 ; i<5 ; i++){
            data.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        data.setBackground(Color.BLACK);
        data.setForeground(Color.white);
        data.setRowHeight(40); 
        data.setFont(new Font("Arial",Font.ROMAN_BASELINE,17));
        tableHeader = data.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.white);
        tableHeader.setFont(new Font("Arial",Font.BOLD,17));

        scrollForAll = new JScrollPane(data);
        scrollForAll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollForAll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollForAll.setPreferredSize(new Dimension(985,40));
        scrollForAll.setBounds(0,140,985,40);

        windowForAllOptions.add(scrollForAll);
        windowForAllOptions.add(isUnavailable);
        windowForAllOptions.add(isAvailable);
        windowForAllOptions.add(chooseBedType);
        windowForAllOptions.add(blinkingLabel);
        windowForAllOptions.add(bed_type);
        windowForAllOptions.add(overlayPanel3);
        windowForAllOptions.add(optionsImageLabel);
        windowForAllOptions.setVisible(true);

        //this is a common listener for controlling availability and bed type simultaneously
        ActionListener commonListener = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int C = 0;
                if(e.getSource()==isAvailable){
                    if(isAvailable.isSelected()){
                        isUnavailable.setSelected(false);
                        try {
                            rs = st.executeQuery("select count(*) from room where availability = 'Available' and bed_type = '"+bed_type.getSelectedItem()+"';");
                            rs.next();
                            C = rs.getInt(1);
                            rs = st.executeQuery("select * from room where availability = 'Available' and bed_type = '"+bed_type.getSelectedItem()+"';");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } else if(isAvailable.isSelected()==false && isUnavailable.isSelected()==false){
                        try {
                            rs = st.executeQuery("select count(*) from room where bed_type = '"+bed_type.getSelectedItem()+"';");
                            rs.next();
                            C = rs.getInt(1);
                            rs = st.executeQuery("select * from room where bed_type = '"+bed_type.getSelectedItem()+"';");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else if(e.getSource()==isUnavailable){
                    if(isUnavailable.isSelected()){ 
                        isAvailable.setSelected(false);
                        try {
                            rs = st.executeQuery("select count(*) from room where availability = 'Unavailable' and bed_type = '"+bed_type.getSelectedItem()+"';");
                            rs.next();
                            C = rs.getInt(1);
                            rs = st.executeQuery("select * from room where availability = 'Unavailable' and bed_type = '"+bed_type.getSelectedItem()+"';");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } else if(isAvailable.isSelected()==false && isUnavailable.isSelected()==false){
                        try {
                            rs = st.executeQuery("select count(*) from room where bed_type = '"+bed_type.getSelectedItem()+"';");
                            rs.next();
                            C = rs.getInt(1);
                            rs = st.executeQuery("select * from room where bed_type = '"+bed_type.getSelectedItem()+"';");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else if(e.getSource()==bed_type){
                    if(bed_type.getSelectedItem()!=null){
                        if(isAvailable.isSelected()){
                            try {
                                rs = st.executeQuery("select count(*) from room where availability = 'Available' and bed_type = '"+bed_type.getSelectedItem()+"';");
                                rs.next();
                                C = rs.getInt(1);
                                rs = st.executeQuery("select * from room where availability = 'Available' and bed_type = '"+bed_type.getSelectedItem()+"';");
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        } else if(isUnavailable.isSelected()){
                            try {
                                rs = st.executeQuery("select count(*) from room where availability = 'Unavailable' and bed_type = '"+bed_type.getSelectedItem()+"';");
                                rs.next();
                                C = rs.getInt(1);
                                rs = st.executeQuery("select * from room where availability = 'Unavailable' and bed_type = '"+bed_type.getSelectedItem()+"';");
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        } else{
                            try {
                                rs = st.executeQuery("select count(*) from room where bed_type = '"+bed_type.getSelectedItem()+"';");
                                rs.next();
                                C = rs.getInt(1);
                                rs = st.executeQuery("select * from room where bed_type = '"+bed_type.getSelectedItem()+"';");
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                //this function clears all the table / removes all the rows
                model.setRowCount(0);

                try {
                    while(rs.next()){
                        Object[] rowdata = {
                            rs.getInt(1),rs.getString(2),rs.getString(3),rs.getFloat(4),rs.getString(5)
                        };
                        model.addRow(rowdata);
                    }

                    int height = (C+1)*40;
                    if(height > 300) height = 300;
                    scrollForAll.setPreferredSize(new Dimension(985,height));
                    scrollForAll.setBounds(0,140,985,height);
                    windowForAllOptions.revalidate();
                    windowForAllOptions.repaint();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                colorCell(data, 1);
            }
        };

        bed_type.addActionListener(commonListener);
        isAvailable.addActionListener(commonListener);
        isUnavailable.addActionListener(commonListener);
    }

    //this function is to prevent user from entering duplicate ID for different persons
    public boolean checkUniqueID(String tablename , String inputID) throws SQLException{
        rs = st.executeQuery("select id from "+tablename+";");
        ArrayList<String>ids = new ArrayList<>();
        while(rs.next()){
            ids.add(rs.getString(1));
        }
        if(ids.contains(inputID))
            return true;
        else 
            return false;
    }

    //one single function to set up a JLabel
    public JLabel setDataLabels(String text , int y){
        JLabel temp = new JLabel(text);  
        temp.setPreferredSize(new Dimension(200,70));
        temp.setBounds(20,y,200,70);
        temp.setForeground(Color.WHITE);
        temp.setFont(new Font("Arial",Font.ROMAN_BASELINE,15));
        temp.setOpaque(false);
        return temp;
    }

    //one single function to set up a JTextfield
    public JTextField inputData(int y){
        JTextField temp = new JTextField();
        temp.setPreferredSize(new Dimension(300,30));
        temp.setBounds(200 , y , 300 , 30);
        temp.setBackground(Color.black);
        temp.setForeground(Color.white);
        temp.setCaretColor(Color.white);
        temp.setFont(new Font("Arial" , Font.PLAIN , 17));
        return temp;
    }

    //one single function to set up a JButton
    public JButton setButton(String text , int x , int y , int w , int h){
        JButton temp = new JButton(text);
        temp.setPreferredSize(new Dimension(w,h));
        temp.setBounds(x,y,w,h);
        temp.setBackground(Color.black);
        temp.setForeground(Color.white);
        temp.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
        temp.setFocusable(false);
        temp.addActionListener(this);
        return temp;
    }

    //one single function to set up a JFrame , this is for the all the 10 options in the options Window
    public JFrame setWindow(String text , int x , int y){
        JFrame temp;
        temp = new JFrame(text);

        ImageIcon tempIcon = new ImageIcon("options.jpg");
        Image loginImage = tempIcon.getImage();
        loginImage = loginImage.getScaledInstance(x,y, Image.SCALE_SMOOTH);
        tempIcon = new ImageIcon(loginImage);
        optionsImageLabel = new JLabel(tempIcon);
        optionsImageLabel.setBounds(0,0,x,y);

        overlayPanel3 = new JPanel();
        overlayPanel3.setPreferredSize(new Dimension(x,y));
        overlayPanel3.setBounds(0,0,x,y);
        overlayPanel3.setBackground(new Color(0, 0, 0, 120));

        temp.setLayout(null);
        temp.setSize(x,y);
        temp.setLocationRelativeTo(null);
        temp.setResizable(false);
        temp.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                optionsWindow.setVisible(true);
            }
        });
        return temp;
    }

    //this function colors one cell of a JTable
    public void colorCell(JTable table , int column){
        table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = table.getValueAt(row, column).toString();
                if(text.equals("Available")) c.setBackground(new Color(0, 100, 0));
                else c.setBackground(new Color(139, 0, 0));

                return c;
            }
        });
    }

    //one single function to set up a ImageIcon
    public ImageIcon setImage(String imageName){
        ImageIcon icon = new ImageIcon(imageName);
        mainHotelImage = icon.getImage();
        mainHotelImage = mainHotelImage.getScaledInstance(screenSize.width , screenSize.height-45, Image.SCALE_SMOOTH);
        icon = new ImageIcon(mainHotelImage);
        return icon;
    }

    public static void main(String[] args) throws SQLException {
       new Hotel();
    }
}