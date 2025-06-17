# 🏨 Hotel JDBC Project

This project demonstrates the use of **JDBC (Java Database Connectivity)** in Java to connect to a MySQL database for a basic hotel management system.

## ⚙️ Requirements

- Java JDK (8 or later)
- MySQL Database
- MySQL JDBC Driver (`mysql-connector-java-x.x.xx.jar`)

## 🛠 How to Run
- Enter username and password so you can make databases and tables (this is done on line 337 and 341)
- then compile the code with the command:
- On Windows:  javac -cp ".;mysql-connector-j-x.x.xx.jar" Hotel.java
- On Linux:  javac -cp ".:mysql-connector-j-x.x.xx.jar" Hotel.java 
- then run the code with the commmand:
- On Windows:  java -cp ".;mysql-connector-j-x.x.xx.jar" Hotel
- On Linus:  java -cp ".:mysql-connector-j-x.x.xx.jar" Hotel
