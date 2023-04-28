package battleship;

import java.io.*;
import java.net.*;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerObjectTest extends JFrame implements Runnable {

	private JTextArea ta;
	private ServerSocket serverSocket;
	private Socket socket;
	//private ObjectInputStream inputFromClient;
    //private ObjectOutputStream outputToClient;
    
	public ServerObjectTest() {
		super("Game Server");
		ta = new JTextArea();
		this.add(ta);

	    setSize(400, 200);
	    Thread t = new Thread(this);
	    t.start();
	}
	
	public void run() {
	    
		try {
	        serverSocket = new ServerSocket(8000);
	        

	        while (true) {
	        	  
	        	ta.append("Waiting for connection on port 8000...\n");
		        socket = serverSocket.accept();
		        InetAddress inetAddress = socket.getInetAddress();
	        	ta.append("Got connection... from " + inetAddress.getHostName() + "("+inetAddress.getHostAddress()+")\n");
		  
		        
		        ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
		        ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
	          
		        while(true) {
		        	String returnMessage = null;
		        	//inputFromClient = new ObjectInputStream(socket.getInputStream());

        	        Object object = null;
					try {
						object = inputFromClient.readObject();
					} 
					catch (EOFException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
        	        
        	        testGame g = (testGame)object;
        	        String temp = g.toString();
        	        ta.append("Game: " + temp);

			        ta.append("\n");
			        
			        String statusString = checkGame(g);
			        if(statusString.equals("No such game exists")) {
			        	saveGame(g);
			        	returnMessage = "Saved to database!";
			        	outputToClient.writeObject(returnMessage);
			        }
			        else {
			        	returnMessage = "You already have a game saved! Please load your existing game and finish playing it, or delete it!";
			        	outputToClient.writeObject(returnMessage);
			        	outputToClient.flush();
			        	
			        }
			        
			        
			        
			      /*  
		          double radius = ByteBuffer.wrap(inbuf).getDouble(); //inputFromClient.readDouble();
		  
		          // Compute area
		          double area = radius * radius * Math.PI;
		  
		          // Send area back to the client
		          try {
		        	  outputToClient.writeDouble(area);
		          } catch (IOException ioe) {
			        	ta.append("Connection lost\n");
			        	break;
			        }
		          ta.append("Radius received from client: " 
		              + radius + '\n');
		            ta.append("Area is: " + area + '\n'); 
		            */
		        }
		        
		        try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	          }
	        
	      }
	      catch(IOException ex) {
	    	  try {
					if(socket != null) {
						socket.close();
					}
					if(serverSocket != null) {
						serverSocket.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
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
	
	public static String checkGame(testGame game) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet rs = null;
		String inputUsername = game.getUsername();
		String inputPassword = game.getPassword();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT * FROM objectstore WHERE username = ? and password = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, inputUsername);
			preparedStatement.setString(2, inputPassword);
			rs = preparedStatement.executeQuery();
			if(!rs.isBeforeFirst() && rs.getRow() == 0) {
				return "No such game exists";
			}
			preparedStatement.close();
			connection.close();
			rs.close();
			
		}catch (SQLException ex) {
		    ex.printStackTrace();
		}
		return "Game exists";
	}

	
	public static void main(String[] args) {
		ServerObjectTest s=  new ServerObjectTest();
	    s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    s.setVisible(true);

	}

}
