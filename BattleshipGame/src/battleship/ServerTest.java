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

public class ServerTest extends JFrame implements Runnable {

	private JTextArea ta;
	private ServerSocket serverSocket;
	private Socket socket;
    
	public ServerTest() {
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
		  
		        
		        DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
		        DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
	          
		        while(true) {
			        byte[] inbuf = new byte[256];
			        String returnMessage = null;
			        int numbytes = 0;
			        try {
			        	 numbytes = inputFromClient.read(inbuf);
			        } catch (IOException ioe) {
			        	ta.append("Connection lost\n");
			        	break;
			        }
			        if(numbytes == -1) {
			        	ta.append("Connection lost\n");
			        	break;
			        }

			        ta.append("\n");
			        
			        String username = new String(inbuf);
			        
			        ta.append("Username is " + username + '\n');
			        
			        String statusString = checkUsername(username);
			        if(statusString.equals("No Username")) {
			        	saveUsername(username);
			        	returnMessage = "Saved to database!";
				        byte[] outbuf = returnMessage.getBytes();
				        outputToClient.write(outbuf);
				        outputToClient.flush();
			        }
			        else {
			        	returnMessage = "This user name has already been chosen! Please choose another username!";
				        byte[] outbuf = returnMessage.getBytes();
				        outputToClient.write(outbuf);
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
				
	            Thread.sleep(1);
	          }
	        
	      }
	      catch(IOException | InterruptedException ex) {
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
	
	public static String checkUsername(String username) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		testGame receivedObject = null;
		ResultSet rs = null;
		String retString = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT username FROM users WHERE username = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			rs = preparedStatement.executeQuery();
			if(!rs.isBeforeFirst() && rs.getRow() == 0) {
				return "No Username";
			}
			rs.next();
			
			retString = rs.getString(1);
			
			
			
		}catch (SQLException ex) {
		    ex.printStackTrace();
		}
		return retString;
	}

	public static void saveUsername(String username) {
		PreparedStatement preparedStatement;
		Connection connection = null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			out = new ObjectOutputStream(bos);
			out.writeObject(username);
			out.flush();
			String insertString = "INSERT INTO users(username) VALUES (?)";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1, username);
			preparedStatement.execute();
			preparedStatement.close();
			connection.close();
		} catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
        } 
	}
	
	public static void main(String[] args) {
		ServerTest s=  new ServerTest();
	    s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    s.setVisible(true);

	}

}
