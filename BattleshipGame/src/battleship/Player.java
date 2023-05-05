package battleship;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Player implements ActionListener{
	String username;
	private String password;
	private boolean isNew;
	private boolean loadedGame = false;
	private static int nextId = 0;
	private int savedGameID = 0;
	private int Id;
	int numberofwins;
	JButton btnNewUser, btnExistingUser, btnUsernameAndPassword, btnNewGame, btnLoadGame, btnDeleteGame, btnContinueGame;
	JLabel questionForUser, passwordLabel, usernameLabel, newLoadGameLabel, saveGameLabel, deleteGameLabel;
	JTextField txtUser,txtPword;
	JFrame frameNewLoad, frameEnterInfo, frameConnection, frameUserType, frameSaveGame, frameDeleteGame;
	JTextField textField = null;
	JTextArea ta = null;
	Socket socket = null;
    JButton openButton;
	JButton closeButton;
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	Board newGame = null;
	
	public Player(){
		nextId++;
		this.Id = nextId;
		connectionUI();
		
	}
	public Player(String username,String password,int id) {
		this(username,password,id,0);
	}
	
	public Player(String username,String password,int id,int numberofwins) {
		this();
		this.username=username;
		this.password=password;
		this.Id=id;
		this.numberofwins=numberofwins;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	private void connectionUI() {
		frameConnection = new JFrame("Connection Panel");
		frameConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ta = new JTextArea(30,30);
		frameConnection.setLayout(new BorderLayout());
	
		JPanel topPanel = new JPanel(new GridLayout(2,1));
		JPanel controlPanel = new JPanel();
		openButton = new JButton("Open Connection");
		closeButton = new JButton("Close Connection");
		closeButton.addActionListener(this);
		openButton.addActionListener(this);
		controlPanel.add(openButton);
		controlPanel.add(closeButton);
		topPanel.add(controlPanel);
		frameConnection.add(topPanel, BorderLayout.NORTH);
	
		frameConnection.add(ta, BorderLayout.CENTER);
		
		frameConnection.setSize(400, 200);
		frameConnection.setVisible(true);
	}
	
	private void userTypeUI()
	{
		frameUserType = new JFrame();
		frameUserType.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameUserType.setSize(400,100);
		//Layout of Main Window
		frameUserType.setLayout(new BorderLayout());
		
		questionForUser = new JLabel("Are You a New or Existing User?");
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(questionForUser);
		btnNewUser = new JButton("New User");
		btnNewUser.addActionListener(this);
		
		btnExistingUser = new JButton("Existing User");
		btnExistingUser.addActionListener(this);
		
		JPanel pnlButton = new JPanel(new GridLayout(1,2));
		
		pnlButton.add(btnNewUser);
		pnlButton.add(btnExistingUser);
		
		
		frameUserType.add(pnlLabel, BorderLayout.NORTH);
		frameUserType.add(pnlButton, BorderLayout.CENTER);
		
		frameUserType.setVisible(true);
	}
	
	private void enterInfoUI() {
		frameEnterInfo = new JFrame();
		frameEnterInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameEnterInfo.setSize(400,150);
		
		usernameLabel = new JLabel("Username: ");
		passwordLabel = new JLabel("Password: ");
		
		txtUser = new JTextField("",15);//To adjust width
		txtPword = new JTextField();
		
		JPanel pnlInput = new JPanel(new GridLayout(2,2));
		
		pnlInput.add(usernameLabel);
		pnlInput.add(txtUser);
		
		pnlInput.add(passwordLabel);
		pnlInput.add(txtPword);
		
		btnUsernameAndPassword = new JButton("Enter Username and Password");
		btnUsernameAndPassword.addActionListener(this);
		JPanel pnlButton = new JPanel(new GridLayout(1,1));
		
		pnlButton.add(btnUsernameAndPassword);
		
		frameEnterInfo.add(pnlInput, BorderLayout.NORTH);
		frameEnterInfo.add(pnlButton, BorderLayout.CENTER);
		
		frameEnterInfo.setVisible(true);
	}
	
	private void newOrLoadGameUI()
	{
		frameNewLoad = new JFrame();
		frameNewLoad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameNewLoad.setSize(400,100);
		//Layout of Main Window
		frameNewLoad.setLayout(new BorderLayout());
		
		newLoadGameLabel = new JLabel("Start a New Game or Load an Existing Game");
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(newLoadGameLabel);
		btnNewGame = new JButton("Start New Game");
		btnNewGame.addActionListener(this);
		
		btnLoadGame = new JButton("Load Existing Game");
		btnLoadGame.addActionListener(this);
		
		JPanel pnlButton = new JPanel(new GridLayout(1,2));
		
		pnlButton.add(btnNewGame);
		pnlButton.add(btnLoadGame);
		
		
		frameNewLoad.add(pnlLabel, BorderLayout.NORTH);
		frameNewLoad.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
		frameNewLoad.setVisible(true);
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
	
	private void deleteGameUI()
	{
		frameDeleteGame = new JFrame();
		frameDeleteGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameDeleteGame.setSize(1600,100);
		//Layout of Main Window
		frameDeleteGame.setLayout(new BorderLayout());
		
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
		
		
		
		frameDeleteGame.add(pnlLabel, BorderLayout.NORTH);
		frameDeleteGame.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
		frameDeleteGame.setVisible(true);
	}
	
	/*
	public class addfirelistener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Hey! From Player Class!\n");
		}
	}
	*/
	
	@Override
	public void actionPerformed(ActionEvent evt) {		
		String cmd = evt.getActionCommand();
		
		if(cmd.equals("New User"))
		{
			newUser();
		}else if(cmd.equals("Existing User"))
		{
			existingUser();
		}
		else if(cmd.equals("Enter Username and Password")) {
			//System.out.println("Username and Pword!");
			userPwordEnter();
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
				userTypeUI();
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
		else if(cmd.equals("Start New Game")) {
			frameNewLoad.setVisible(false);
			newGame = new Board(this.username, this.password);
			newGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    newGame.setVisible(true);    
		    newGame.setResizable(true);
		}
		else if(cmd.equals("Load Existing Game")) {
			Board loadedGame = loadGame();
			if(loadedGame != null) {
				frameNewLoad.setVisible(false);
			}
		}
		else if(cmd.equals("Save Game")) {
			saveGame(newGame);
		}
		else if(cmd.equals("Yes, delete previous saved game")) {
			deleteGame(newGame);
		}
		else if(cmd.equals("No, continue current game")) {
			frameDeleteGame.setVisible(false);
		}
	}
	
	public void newUser() {
		this.isNew = true;
		System.out.println(this.isNew);
		enterInfoUI();
	}
	
	public void existingUser() {
		this.isNew = false;
		System.out.println(this.isNew);
		enterInfoUI();
	}
	
	public void userPwordEnter() {
		String username = txtUser.getText().trim();
		String password = txtPword.getText().trim();
		String messageType = "userInfo";
		String userType = null;
		if(this.isNew == true) {
			userType = "new";
		}
		else if(this.isNew == false) {
			userType = "existing";
		}
		txtUser.setText("");
		txtPword.setText("");
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		String[] userInfo = new String[3];
		userInfo[0] = username;
		userInfo[1] = password;
		userInfo[2] = userType;
		messageArray.add(userInfo);
		try {
			toServer.writeObject(messageArray);
			toServer.flush();
			
			Object object = null;
			object = fromServer.readObject();
			String messageForPlayer = (String)object;
	        ta.append(messageForPlayer + "\n");
	        if(messageForPlayer.equals("Welcome back!") || messageForPlayer.equals("Username and password saved!")) {
	        	this.username = username;
	        	this.password = password;
	        	newOrLoadGameUI();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
public void saveGame(Board game) {
		String messageType = "save";
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

	}
	
	public Board loadGame() {
		String messageType = "load";
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		String[] unamePword = new String[2];
		unamePword[0] = this.username;
		unamePword[1] = this.password;
		messageArray.add(unamePword);
		Board loadedGame = null;
	
    	try {
			toServer.writeObject(messageArray);
			toServer.flush();
			
			String tempStr = "You do not have a game saved. Please start a new game!";
	        Object object = null;
			
			object = fromServer.readObject();
			if(object.toString().equals(tempStr.toString())) {
				ta.append(tempStr + "\n");
			}
			else {
				loadedGame = (Board)object;
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return loadedGame;
	}
	
	public void deleteGame(Board game) {
		String messageType = "delete";
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(game);
		
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

	public static void main(String[] args) {
		Player obj = new Player();
	}
	
}