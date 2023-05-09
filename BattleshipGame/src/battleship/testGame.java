package battleship;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class testGame implements Serializable, ActionListener {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private int[][] gridArray = new int[10][10];
	private static int nextId = 0;
	private int Id;
	private int setNum = 2;
	JFrame frameSaveGame, frameDeleteGame;
	JLabel saveGameLabel, deleteGameLabel;
	JButton btnNewGame, btnDeleteGame, btnContinueGame;
	JTextArea ta = null;
	JButton openButton;
	JButton closeButton;
	private Socket socket;
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	private int savedGameID = 0;
	
	public testGame() {
		this.Id = nextId;
		nextId++;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				this.gridArray[i][j] = 1;
			}
		}
	}
	
	
	public testGame(String username, String password) {
		this();
		this.username = username;
		this.password = password;
		connectionUI();
		saveGameUI();
	}
	/*
	public testGame(String username, String password, Socket socket) throws IOException {
		this();
		this.socket = socket;
		this.username = username;
		this.password = password;
		
		saveGameUI();
	}
	*/
	
	public testGame(String username, String password, int[][] gridArray, int savedGameID) {
		this.Id = nextId;
		nextId++;
		this.username = username;
		this.password = password;
		this.gridArray = gridArray;
		this.savedGameID = savedGameID;
	}
	private void saveGameUI()
	{
		frameSaveGame = new JFrame();
		frameSaveGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameSaveGame.setSize(400,100);
		//Layout of Main Window
		frameSaveGame.setLayout(new BorderLayout());
		
		saveGameLabel = new JLabel("Do you want to save this game?");
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(saveGameLabel);
		btnNewGame = new JButton("Save Game");
		btnNewGame.addActionListener(this);
		
		
		JPanel pnlButton = new JPanel(new GridLayout(1,1));
		
		pnlButton.add(btnNewGame);
		
		
		frameSaveGame.add(pnlLabel, BorderLayout.NORTH);
		frameSaveGame.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
		frameSaveGame.setVisible(true);
	}
	
	private void connectionUI() {
		JFrame frame = new JFrame("Game Connection Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ta = new JTextArea(30,30);
		frame.setLayout(new BorderLayout());
	
		JPanel topPanel = new JPanel(new GridLayout(2,1));
		JPanel controlPanel = new JPanel();
		openButton = new JButton("Open Connection");
		closeButton = new JButton("Close Connection");
		closeButton.addActionListener(this);
		openButton.addActionListener(this);
		controlPanel.add(openButton);
		controlPanel.add(closeButton);
		topPanel.add(controlPanel);
		frame.add(topPanel, BorderLayout.NORTH);
	
		frame.add(ta, BorderLayout.CENTER);
		
		frame.setSize(400, 200);
		frame.setVisible(true);
	}
	
	private void deleteGameUI()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600,100);
		//Layout of Main Window
		frame.setLayout(new BorderLayout());
		
		deleteGameLabel = new JLabel("You already have a different game saved.\nUsers are permitted to have at most 1 game saved at a time.\nDo you want to delete this existing game and save your current game in its place?");
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(deleteGameLabel);
		btnDeleteGame = new JButton("Yes, delete previous saved game");
		btnDeleteGame.addActionListener(this);
		
		btnContinueGame = new JButton("No, continue current game");
		btnContinueGame.addActionListener(this);
		
		
		JPanel pnlButton = new JPanel(new GridLayout(1,2));
		
		pnlButton.add(btnDeleteGame);
		pnlButton.add(btnContinueGame);
		
		
		
		frame.add(pnlLabel, BorderLayout.NORTH);
		frame.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
		frame.setVisible(true);
	}
	
	public int getId() {
		return this.Id;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public long getSerialId() {
		return this.serialVersionUID;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {		
		String cmd = evt.getActionCommand();
		if(cmd.equals("Save Game")) {
			saveGame();
			//System.out.println("Let's Go!");
		}
		else if(cmd.equals("Yes, delete previous saved game")) {
			deleteGame();
		}
		else if(cmd.equals("No, continue current game")) {
			System.out.println("Nothing for ya");
		}
		else if(cmd.equals("Open Connection")) {
			try {
				socket = new Socket("localhost", 8000);
				try {
			    	  toServer = new ObjectOutputStream(socket.getOutputStream()); 
				      fromServer = new ObjectInputStream(socket.getInputStream());
					      
				    }
				    catch (IOException ex) {
				      ta.append(ex.toString() + '\n');
				    }
				ta.append("connected\n");
				
			} catch (IOException e1) {
				e1.printStackTrace();
				ta.append("connection Failure\n");
			}
		}
		else if(cmd.equals("Close Connection")) {
			try { 
				if(socket != null) {
					socket.close();
				}
				if(toServer != null) {
					toServer.close();
				}
				if(fromServer != null) {
					fromServer.close();
				}

				ta.append("connection closed\n");
			} catch (Exception e1) {
				System.err.println("error"); 
			}
		}
		
	}
	/*
	public void saveGameTest() {
		
		
		try {
			toServer = new ObjectOutputStream(socket.getOutputStream());
			//fromServer = new ObjectInputStream(socket.getInputStream());
			while(true) {
				String messageType = "test";
				ArrayList<Object> messageArray = new ArrayList<>();
				messageArray.add(messageType);
				toServer.writeObject(messageArray);
				toServer.flush();
				
				//Object object = fromServer.readObject();
				//String val = (String)object;
				//toServer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	*/
	
	public void saveGame() {
		String messageType = "save";
		this.setGridCell(5, 5, setNum);
		setNum++;
		this.printGrid();
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(this.username);
		messageArray.add(this.password);
		messageArray.add(this.gridArray);
		messageArray.add(this.savedGameID);
    	try {
			toServer.writeObject(messageArray);
			toServer.flush();

	        Object object = null;
			object = fromServer.readObject();
			ArrayList<Object> retArr = (ArrayList<Object>)object;
			
			int gameInt = (int)retArr.get(0);
			String gameString = (String)retArr.get(1);
			if(gameString.equals("You have a game saved.\nYou can only have 1 game saved at a time.\nPlease either delete this game in order to save the current game, or continue playing the current game.\n")) {
				ta.append(gameString.toString() + "\n");
				deleteGameUI();
			}
			else {
				this.savedGameID = gameInt;
				ta.append(gameString.toString() + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void deleteGame() {
		String messageType = "delete";
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(this.username);
		messageArray.add(this.password);
		messageArray.add(this.gridArray);
		messageArray.add(this.savedGameID);
		
    	try {
			toServer.writeObject(messageArray);
			toServer.flush();

	        Object object = null;
			object = fromServer.readObject();
			ArrayList<Object> retArr = (ArrayList<Object>)object;
			
			int gameInt = (int)retArr.get(0);
			String gameString = (String)retArr.get(1);
			
			this.savedGameID = gameInt;
			ta.append(gameString.toString());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*
	public void saveGame() {
		PreparedStatement preparedStatement;
		Connection connection = null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:Battleship.db");
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.flush();
			byte[] gameBytes = bos.toByteArray();
			String insertString = "INSERT INTO objectstore(username, password, gamedata, serialid) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1, this.username);
			preparedStatement.setString(2, this.password);
			preparedStatement.setBytes(3, gameBytes);
			preparedStatement.setLong(4, this.serialVersionUID);
			preparedStatement.execute();
			preparedStatement.close();
			connection.close();
		} catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
        } 
	}*/
	
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
	
	public void setGridCell(int coord1, int coord2, int value) {
		this.gridArray[coord1][coord2] = value;
	}
	
	public void printGrid() {
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				System.out.print(this.gridArray[i][j]);
				if(j == 9) {
					System.out.print("\n");
				}
				else {
					System.out.print("\t");
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "Game Object [Id=" + Id + ", username=" + username + ", password=" + password + "]";
	}

	public static void main(String[] args) {
		//testGame game1 = new testGame("genuser","genpword");
		testGame game1 = new testGame("newuser","newpword");
		//System.out.println(game1.toString() + "\n");
		//game1.printGrid();

	}

}