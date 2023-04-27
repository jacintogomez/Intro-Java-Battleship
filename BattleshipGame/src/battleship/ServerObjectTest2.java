package battleship;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerObjectTest2 {
 private ObjectOutputStream outputToFile;
 private ObjectInputStream inputFromClient;

 public static void main(String[] args) {
   new ServerObjectTest2();
 }

 public ServerObjectTest2() {
   try {
     // Create a server socket
     ServerSocket serverSocket = new ServerSocket(8000);
     System.out.println("Server started ");

     // Create an object ouput stream
     //outputToFile = new ObjectOutputStream(new FileOutputStream("student.dat", true));

     while (true) {
       // Listen for a new connection request
       Socket socket = serverSocket.accept();

       // Create an input stream from the socket
       inputFromClient =
         new ObjectInputStream(socket.getInputStream());

       // Read from input
       Object object = inputFromClient.readObject();

       // Write to the file
       testGame s = (testGame)object;
       System.out.println("got object " + object.toString());
       saveGame(s);
       //outputToFile.flush();
       System.out.println("A new object is stored");
     }
   }
   catch(ClassNotFoundException ex) {
     ex.printStackTrace();
   }
   catch(IOException ex) {
     ex.printStackTrace();
   }
   finally {
     try {
       inputFromClient.close();
       outputToFile.close();
     }
     catch (Exception ex) {
       ex.printStackTrace();
     }
   }
 }
 
 public static void saveGame(testGame g) {
		PreparedStatement preparedStatement;
		Connection connection = null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			out = new ObjectOutputStream(bos);
			out.writeObject(g);
			out.flush();
			byte[] gameBytes = bos.toByteArray();
			String insertString = "INSERT INTO objectstore(username, password, gamedata, serialid) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1, g.getUsername());
			preparedStatement.setString(2, g.getPassword());
			preparedStatement.setBytes(3, gameBytes);
			preparedStatement.setLong(4, g.getSerialId());
			preparedStatement.execute();
			preparedStatement.close();
			connection.close();
		} catch (IOException | SQLException ex) {
         System.out.println(ex.getMessage());
     } 
	}
}
