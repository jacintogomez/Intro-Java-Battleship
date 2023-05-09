package battleship;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Server extends JFrame implements Runnable {

	private JTextArea ta;
	private ServerSocket serverSocket;
	private Socket socket;
	private int setNum = 2;
	//private ObjectInputStream inputFromClient;
    //private ObjectOutputStream outputToClient;
    
	public Server() {
		super("Game Server");
		ta = new JTextArea();
		JScrollPane sp = new JScrollPane(ta);
		this.add(sp);

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
	        	
	        	new Thread(new HandleAPlayer(socket)).start();
				
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
	
	class HandleAPlayer implements Runnable {
	    private Socket socket; // A connected socket
	    
	    
	    /** Construct a thread */
	    public HandleAPlayer(Socket socket) {
	      this.socket = socket;
	      
	    }

	    /** Run a thread */
	    public void run() {
	      try {
	       
	        	ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
		        ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
	          
		        while(true) {
		        	String returnMessage = null;
		        	String typeStr = null;
		        	//inputFromClient = new ObjectInputStream(socket.getInputStream());

        	        Object object = null;
					try {
						object = inputFromClient.readObject();
					} 
					catch (EOFException e) {
						e.printStackTrace();
						break;
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}
					ArrayList<Object> sentArr = (ArrayList<Object>)object;
					typeStr = (String)sentArr.get(0);
					ta.append(typeStr + "\n");
					if(typeStr.equals("userInfo")) {
						String[] sentStr = (String[])sentArr.get(1);
						String username = sentStr[0];
						String password = sentStr[1];
						String isNew = sentStr[2];
						if(isNew.equals("new")) {
							String statusPassword = checkUsername(username);
							if(statusPassword.equals("No Username")) {
								saveUsernamePassword(username, password);
					        	returnMessage = "Username and password saved!";
						        outputToClient.writeObject(returnMessage);
						        outputToClient.flush();
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
							}
						}
					}
					else if(typeStr.equals("save")) {
						String tempUsername = (String)sentArr.get(1);
						String tempPassword = (String)sentArr.get(2);
						int[][] tempMyGrid = (int[][])sentArr.get(3);
						int[][] tempOpGrid = (int[][])sentArr.get(4);
						ArrayList<Ship> tempMyship = (ArrayList<Ship>)sentArr.get(5);
						ArrayList<Ship> tempOpship = (ArrayList<Ship>)sentArr.get(6);
						int tempMyHitsLeft = (int)sentArr.get(7);
						int tempOpHitsLeft = (int)sentArr.get(8);
						int savedGameID = (int)sentArr.get(9);
						Board gameToSave = new Board(tempUsername, tempPassword, tempMyGrid, tempOpGrid, tempMyship,
								tempOpship, tempMyHitsLeft, tempOpHitsLeft, false);
						String gameStatus = checkGame(gameToSave);
						if(gameStatus.equals("Game exists")) {
							int storedGameID = getSavedGameID(gameToSave);
							if(savedGameID == storedGameID) {
								gameToSave.printGrid();
								ta.append("About to update!\n");
								updateGame(gameToSave);
								int gameID = getSavedGameID(gameToSave);
								returnMessage = "Game updated!";
								ArrayList<Object> retArrList = new ArrayList<>();
								retArrList.add(gameID);
								retArrList.add(returnMessage);
								outputToClient.writeObject(retArrList);
								outputToClient.flush();
							}
							else {
								ArrayList<Object> retArrList = new ArrayList<>();
								returnMessage = "You have a game saved.\nYou can only have 1 game saved at a time.\nPlease either delete this game in order to save the current game, or continue playing the current game.\n";
								int gameID = 0;
								retArrList.add(gameID);
								retArrList.add(returnMessage);
								outputToClient.writeObject(retArrList);
								outputToClient.flush();
							}
						}
						else if(gameStatus.equals("No such game exists")) {
							saveGame(gameToSave);
							int gameID = getSavedGameID(gameToSave);
							returnMessage = "Game saved!";
							ArrayList<Object> retArrList = new ArrayList<>();
							retArrList.add(gameID);
							retArrList.add(returnMessage);
							outputToClient.writeObject(retArrList);
							outputToClient.flush();
						}
						
					}
					else if(typeStr.equals("load")) {
						String[] tempStr = (String[])sentArr.get(1);
						String username = tempStr[0];
						String password = tempStr[1];
						ta.append("Username: " + username + "\nPassword: " + password + "\n");
						
						String statusString = checkGameForLoad(username, password);
				        if(statusString.equals("Game exists")) {
				        	Board game1 = loadGame(username, password);
				        	int gameID = getSavedGameID(game1);
				        	returnMessage = "Game loaded!\n";
				        	ArrayList<Object> retArrList = new ArrayList<>();
							retArrList.add(gameID);
							retArrList.add(returnMessage);
							retArrList.add(game1);
				        	outputToClient.writeObject(retArrList);
				        }
				        else {
				        	returnMessage = "You do not have a game saved.\nPlease start a new game!";
				        	outputToClient.writeObject(returnMessage);
				        	outputToClient.flush();
				        }
					}
					else if(typeStr.equals("delete")) {
						String tempUsername = (String)sentArr.get(1);
						String tempPassword = (String)sentArr.get(2);
						int[][] tempMyGrid = (int[][])sentArr.get(3);
						int[][] tempOpGrid = (int[][])sentArr.get(4);
						ArrayList<Ship> tempMyship = (ArrayList<Ship>)sentArr.get(5);
						ArrayList<Ship> tempOpship = (ArrayList<Ship>)sentArr.get(6);
						int tempMyHitsLeft = (int)sentArr.get(7);
						int tempOpHitsLeft = (int)sentArr.get(8);
						int savedGameID = (int)sentArr.get(9);
						Board gameToSave = new Board(tempUsername, tempPassword, tempMyGrid, tempOpGrid, tempMyship,
								tempOpship, tempMyHitsLeft, tempOpHitsLeft, false);
						deleteGame(tempUsername, tempPassword);
						saveGame(gameToSave);
						int gameID = getSavedGameID(gameToSave);
						returnMessage = "Game saved!";
						ArrayList<Object> retArrList = new ArrayList<>();
						retArrList.add(gameID);
						retArrList.add(returnMessage);
						outputToClient.writeObject(retArrList);
						outputToClient.flush();
					}
					else if(typeStr.equals("test")) {
						System.out.println("test");
						//returnMessage = "Hi!";
						//outputToClient.writeObject(returnMessage);
						//outputToClient.flush();
					}
			        
		        }
	      }
	      catch(IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	}

	
	public static String checkUsername(String username) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
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

	public static void deleteGame(String username, String password) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "DELETE from objectstore WHERE username = ? and password = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();
			
		}catch (SQLException ex) {
		    ex.printStackTrace();
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

	public static void saveGame(Board g) {
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
	
	public static void updateGame(Board g) {
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
			String insertString = "UPDATE objectstore SET gamedata = ? WHERE username = ? AND password = ?";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setBytes(1, gameBytes);
			preparedStatement.setString(2, g.getUsername());
			preparedStatement.setString(3, g.getPassword());
			preparedStatement.execute();
			preparedStatement.close();
			connection.close();
		} catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
        } 
	}
	
	public static int getSavedGameID(Board g) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet rs = null;
		String inputUsername = g.getUsername();
		String inputPassword = g.getPassword();
		int retID = 0;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			String queryString = "SELECT id FROM objectstore WHERE username = ? and password = ?";
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, inputUsername);
			preparedStatement.setString(2, inputPassword);
			rs = preparedStatement.executeQuery();
			rs.next();
			retID = rs.getInt("id");
			preparedStatement.close();
			connection.close();
			rs.close();
		}catch (SQLException ex) {
		    ex.printStackTrace();
		}
		return retID;
	}
	
	public static String checkGame(Board game) {
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
	
	public static Board loadGame(String username, String password) {
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		Board receivedObject = null;
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
			receivedObject = (Board)deSerializedObject;
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
		Server s=  new Server();
	    s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    s.setVisible(true);

	}

}