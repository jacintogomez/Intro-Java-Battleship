package battleship;

import java.sql.*;

public class CreateUserTable {

	public static void createNewTable() {
	        
	        String url = "jdbc:sqlite:Battleship.db";
	        
	        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
	                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
	                + "	username text NOT NULL UNIQUE,\n"
	                + "	password text NOT NULL UNIQUE,\n"
	                + "	numberofwins integer NOT NULL,\n"
	                + "	numberoflosses integer NOT NULL\n"
	                + ");";
	        
	        try (Connection conn = DriverManager.getConnection(url);
	                Statement stmt = conn.createStatement()) {
	            // create a new table
	            stmt.execute(sql);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	
	public static void main(String[] args) {
		createNewTable();

	}

}
