package battleship;

import java.sql.*;

public class CreateObjectStoreTable {

	public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:Battleship.db";
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS objectstore (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	username text NOT NULL UNIQUE,\n"
                + "	password text NOT NULL,\n"
                + "	gamedata blob NOT NULL,\n"
                + "	serialid bigint NOT NULL\n"
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
