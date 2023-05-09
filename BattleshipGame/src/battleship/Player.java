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

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.FontMetrics;

public class Player implements ActionListener {
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
			frameNewLoad.dispose();
			//System.out.println("BLAHBLAHBLAH" + this.username + "\n" + this.password + "BLAHBLAHBLAH");
			newGame = new Board(this.username, this.password);
			newGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    newGame.setVisible(true);    
		    newGame.setResizable(true);
		   
		    //saveGameUI();
		}
		else if(cmd.equals("Load Existing Game")) {
			Board loadedGame = loadGame();
			if(loadedGame != null) {
				frameNewLoad.dispose();
				String tempUsername = loadedGame.getUsername();
				String tempPassword = loadedGame.getPassword();
				int[][] tempMygrid = loadedGame.getMyGrid();
				int[][] tempOpgrid = loadedGame.getOpGrid();
				ArrayList<Ship> tempMyships = loadedGame.getMyships();
				ArrayList<Ship> tempOpships = loadedGame.getOpships();
				int tempMyhitsleft = loadedGame.getMyhitsleft();
				int tempOphitsleft = loadedGame.getOphitsleft();
				newGame = new Board(tempUsername,tempPassword,tempMygrid,tempOpgrid,tempMyships,
						tempOpships,tempMyhitsleft,tempOphitsleft, true);
				newGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    newGame.setVisible(true);    
			    newGame.setResizable(true);
				//saveGameUI();
			}
		}
		else if(cmd.equals("Save Game")) {
			saveGame(newGame);
		}
		else if(cmd.equals("Yes, delete previous saved game")) {
			frameDeleteGame.dispose();
			deleteGame(newGame);
			
		}
		else if(cmd.equals("No, continue current game")) {
			frameDeleteGame.dispose();
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
	        	frameUserType.dispose();
	        	frameEnterInfo.dispose();
	        	newOrLoadGameUI();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
public void saveGame(Board game) {
		System.out.println("Check game to save: \n");
		game.printGrid();
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
				ta.append(gameString.toString());
				deleteGameUI();
			}
			else {
				this.savedGameID = gameInt;
				ta.append(gameString.toString());
				ta.append("Game ID: " + this.savedGameID + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	//frameSaveGame.dispose();
    	//saveGameUI();

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
				ArrayList<Object> retArr = (ArrayList<Object>)object;
				int gameInt = (int)retArr.get(0);
				String gameString = (String)retArr.get(1);
				loadedGame = (Board)retArr.get(2);
				this.savedGameID = gameInt;
				ta.append("Game ID: " + this.savedGameID + "\n");
				ta.append(gameString.toString());
				System.out.println("Loaded Game Grid: \n");
				loadedGame.printGrid();
				
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
	
	public static int[][] flip(int grid[][]){
		for(int x=0;x<10;x++) {
			for(int y=0;y<x;y++) {
				if(x!=y) {
					int temp=grid[x][y];
					grid[x][y]=grid[y][x];
					grid[y][x]=temp;
				}
			}
		}
		return grid;
	}
	
	public static void main(String[] args) {
		//Player obj = new Player();
//		int myhitsleft=0,ophitsleft=0;
//		int l=10;
//		String username="Jacinto Rigal";
//		String password="jg777";
//		int opgrid[][]= {
//				{2,2,2,1,1,1,1,1,1,1},
//				{1,2,2,1,1,1,1,1,1,1},
//				{1,1,1,1,1,1,1,3,1,1},
//				{1,1,1,2,1,1,1,1,1,1},
//				{1,1,1,2,1,1,1,1,1,1},
//				{1,1,1,2,1,1,1,1,1,1},
//				{3,1,1,1,1,1,1,1,1,1},
//				{1,1,1,1,4,4,4,2,1,1},
//				{1,3,1,1,1,1,1,3,3,1},
//				{1,1,1,2,2,2,2,2,1,1},
//		};
//		int mygrid[][]={
//				{1,1,1,1,1,1,1,1,1,1},
//				{1,1,1,1,1,4,1,1,2,1},
//				{1,1,1,1,1,2,1,1,2,1},
//				{1,1,1,1,1,1,1,1,2,1},
//				{1,2,2,2,1,1,1,1,2,1},
//				{2,1,1,1,1,1,1,1,2,1},
//				{4,1,1,1,1,4,2,2,2,1},
//				{4,1,1,3,1,1,1,1,1,3},
//				{1,1,1,1,3,1,1,1,3,1},
//				{1,1,1,1,1,1,3,1,1,1},
//		};
//		mygrid=flip(mygrid);
//		opgrid=flip(opgrid);
//		ArrayList<Ship> myships=new ArrayList<Ship>();
//		ArrayList<Ship> opships=new ArrayList<Ship>();
//		ArrayList<Coordinate> cords=new ArrayList<Coordinate>();
//		Coordinate c1=new Coordinate(7,4);
//		Coordinate c2=new Coordinate(7,5);
//		Coordinate c3=new Coordinate(7,6);
//		Coordinate c4=new Coordinate(7,7);
//		cords.add(c1);
//		cords.add(c2);
//		cords.add(c3);
//		cords.add(c4);
//		Ship destroyer=new Ship("Destroyer",3);
//		Ship submarine=new Ship("Submarine",3);
//		Ship patrol=new Ship("Patrol Boat",2);
//		Ship battleship=new Ship("Battleship",4);
//		Ship carrier=new Ship("Carrier",5);
//		Ship enemy_destroyer=new Ship("Destroyer",3);
//		Ship enemy_submarine=new Ship("Submarine",3);
//		Ship enemy_patrol=new Ship("Patrol Boat",2);
//		Ship enemy_battleship=new Ship("Battleship",4,3,true);
//		Ship enemy_carrier=new Ship("Carrier",5);
//		enemy_battleship.coords=cords;
//		myships.add(destroyer);
//		myships.add(submarine);
//		myships.add(patrol);
//		myships.add(battleship);
//		myships.add(carrier);
//		opships.add(enemy_destroyer);
//		opships.add(enemy_submarine);
//		opships.add(enemy_patrol);
//		opships.add(enemy_battleship);
//		opships.add(enemy_carrier);
//		
//		for(int x=0;x<l;x++) {
//			for(int y=0;y<l;y++) {
//				if(mygrid[x][y]==4) {myhitsleft++;};
//				if(opgrid[x][y]==4) {ophitsleft++;};
//			}
//		}
//		Board game=new Board(username,password,mygrid,opgrid,myships,opships,myhitsleft,ophitsleft);
		Board game=new Board("Lisa","cheese");
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setVisible(true);    
	    game.setResizable(true);
	    
	}
	
}
