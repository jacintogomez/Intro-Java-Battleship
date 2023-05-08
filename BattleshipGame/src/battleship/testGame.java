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
	JFrame frameSaveGame;
	JLabel saveGameLabel;
	JButton btnNewGame;
	private Socket socket;
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	 
	
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
		saveGameUI();
	}
	
	public testGame(String username, String password, Socket socket) throws IOException {
		this();
		this.socket = socket;
		this.username = username;
		this.password = password;
		
		saveGameUI();
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
			saveGameTest();
			//System.out.println("Let's Go!");
		}
		
	}
	
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
	/*
	public void saveGame(testGame game) {
		String messageType = "save";
		//game.printGrid();
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(game);
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

	}*/
	
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
		testGame game1 = loadGame("genuser", "genpword");
		System.out.println(game1.toString() + "\n");
		game1.printGrid();

	}

}
