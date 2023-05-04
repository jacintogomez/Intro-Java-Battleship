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

public class ServerObjectTestUI extends JFrame implements Runnable {

	private JTextArea ta;
	private ServerSocket serverSocket;
	private Socket socket;
	private boolean userNamePword = false;
	private boolean loadGame = false;
	private boolean saveGame = false;
	//private ObjectInputStream inputFromClient;
    //private ObjectOutputStream outputToClient;
    
	public ServerObjectTestUI() {
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
						e.printStackTrace();
						break;
					}catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}
					if(userNamePword == true) {
						if(loadGame == false && saveGame == false) {
							String tempStr = (String)object;
							if(tempStr.equals("load")) {
								loadGame = true;
								String tempLoadStr = "Load flag turned";
								outputToClient.writeObject(tempLoadStr);
								outputToClient.flush();
							}
							else if(tempStr.equals("save")) {
								saveGame = true;
							}
						}
						else if(loadGame == true && saveGame == false) {
							String[] tempStr = (String[])object;
							String username = tempStr[0];
							String password = tempStr[1];
							ta.append("Username: " + username + "\nPassword: " + password + "\n");
							
							String statusString = checkGameForLoad(username, password);
					        if(statusString.equals("Game exists")) {
					        	testGame game1 = loadGame(username, password);
					        	outputToClient.writeObject(game1);
					        }
					        else {
					        	returnMessage = "You do not have a game saved.\nPlease start a new game!";
					        	outputToClient.writeObject(returnMessage);
					        	outputToClient.flush();
					        }
					        loadGame = false;
						}
						else if(saveGame == true && loadGame == false) {
							
						}
						
					}
					else if(userNamePword == false) {
						
						String tempStr = (String)object;
						String[] tempStrArr = tempStr.split(" ");
						String username = tempStrArr[0];
						String password = tempStrArr[1];
						String isNew = tempStrArr[2];
						ta.append(username + "\n" + password + "\n" + isNew + "\n" );
						if(isNew.equals("new")) {
							String statusPassword = checkUsername(username);
							if(statusPassword.equals("No Username")) {
								saveUsernamePassword(username, password);
					        	returnMessage = "Username and password saved!";
						        outputToClient.writeObject(returnMessage);
						        outputToClient.flush();
						        userNamePword = true;
							}
							else {
								returnMessage = "This username has already been chosen!\nPlease choose another username!";
						        outputToClient.writeObject(returnMessage);
						        outputToClient.flush();
								
							}
						}
						else if(isNew.equals("existing")) {
							String statusPassword = checkUsernamePassword(username, password);
							if(statusPassword.equals("No Username")) {
								returnMessage = "This username/password combination does not exist!\nPlease enter your existing username/password or register as a new user!";
						        outputToClient.writeObject(returnMessage);
						        outputToClient.flush();
							}
							else {
								returnMessage = "Welcome back!";
						        outputToClient.writeObject(returnMessage);
						        outputToClient.flush();
						        userNamePword = true;
							}
						}
					}
			        
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
	
	public static String checkUsername(String username) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		testGame receivedObject = null;
		ResultSet rs = null;
		String retString = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT username FROM usernamepassword WHERE username = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			rs = preparedStatement.executeQuery();
			if(!rs.isBeforeFirst() && rs.getRow() == 0) {
				return "No Username";
			}
			rs.next();
			
			retString = rs.getString(1);
			preparedStatement.close();
			connection.close();
			rs.close();	
			
		}catch (SQLException ex) {
		    ex.printStackTrace();
		}
		return retString;
	}
	
	public static String checkUsernamePassword(String username, String password) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		testGame receivedObject = null;
		ResultSet rs = null;
		String retString = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT username, password FROM usernamepassword WHERE username = ? and password = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			rs = preparedStatement.executeQuery();
			if(!rs.isBeforeFirst() && rs.getRow() == 0) {
				return "No Username";
			}
			rs.next();
			
			retString = rs.getString(1);
			preparedStatement.close();
			connection.close();
			rs.close();	
			
		}catch (SQLException ex) {
		    ex.printStackTrace();
		}
		return retString;
	}
	
	public static void saveUsernamePassword(String username, String password) {
		PreparedStatement preparedStatement;
		Connection connection = null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			out = new ObjectOutputStream(bos);
			out.writeObject(username);
			out.flush();
			String insertString = "INSERT INTO usernamepassword(username, password) VALUES (?,?)";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.execute();
			preparedStatement.close();
			connection.close();
		} catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
        } 
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
	
	public static String checkGameForLoad(String username, String password) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet rs = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT * FROM objectstore WHERE username = ? and password = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
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
	
	public static testGame loadGame(String username, String password) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		testGame receivedObject = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT gamedata FROM objectstore WHERE username = ? AND password = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			rs = preparedStatement.executeQuery();
			rs.next();
			
			byte[] buf = rs.getBytes(1);
			ObjectInputStream objectIn = null;
			if (buf != null) {
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			}
			
			Object deSerializedObject = objectIn.readObject();
			receivedObject = (testGame)deSerializedObject;
			rs.close();
			preparedStatement.close();
			connection.close();
			
			//System.out.println("receivedObject is: " + receivedObject.toString());
			return receivedObject;
			
		}catch (IOException | SQLException | ClassNotFoundException ex) {
		    ex.printStackTrace();
		}
		return receivedObject;
	}

	
	public static void main(String[] args) {
		ServerObjectTestUI s=  new ServerObjectTestUI();
	    s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    s.setVisible(true);

	}

}
