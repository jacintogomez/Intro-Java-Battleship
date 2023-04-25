package battleship;

import java.sql.*;

public class createDatabase {
	
	public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite://Users/matthewgabriele/Dropbox/School/Tandon Grad/Semester 4 Courses/Intro Java/Final Project/Code/Intro-Java-Battleship/BattleshipGame/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

	public static void main(String[] args) {
		createNewDatabase("Battleship.db");

	}

}
