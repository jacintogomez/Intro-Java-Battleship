package battleship;

import java.io.*;
import java.sql.*;

public class storeGame {
	

	public static void main(String[] args) {
		PreparedStatement preparedStatement;
		Connection connection = null;
		
		testGame game1 = new testGame("genericusername3","genericpassword");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			out = new ObjectOutputStream(bos);
			out.writeObject(game1);
			out.flush();
			byte[] gameBytes = bos.toByteArray();
			String insertString = "INSERT INTO objectstore(username, password, gamedata, serialid) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1, game1.getUsername());
			preparedStatement.setString(2, game1.getPassword());
			preparedStatement.setBytes(3, gameBytes);
			preparedStatement.setLong(4, game1.getSerialId());
			preparedStatement.execute();
			
			String queryString = "SELECT gamedata FROM objectstore WHERE username = ? AND password = ?";
			PreparedStatement preparedStatement2 = connection.prepareStatement(queryString);
			preparedStatement2.setString(1, "genericusername");
			preparedStatement2.setString(2, "genericpassword");
			ResultSet rs = preparedStatement2.executeQuery();
			rs.next();
			
			byte[] buf = rs.getBytes(1);
			ObjectInputStream objectIn = null;
			if (buf != null) {
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			}
			
			Object deSerializedObject = objectIn.readObject();
			testGame receivedObject = (testGame)deSerializedObject;
			rs.close();
			preparedStatement.close();
			preparedStatement2.close();
			//connection.close();
			System.out.println("receivedObject is: " + receivedObject.toString());
				  
		  
		} catch (IOException | SQLException | ClassNotFoundException ex) {
		    ex.printStackTrace();
		}

	}

}
