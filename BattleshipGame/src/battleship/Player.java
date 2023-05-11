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
	int numberofwins = 0, numberoflosses = 0;
	JButton btnNewUser, btnExistingUser, btnUsernameAndPassword, btnNewGame, btnLoadGame, btnDeleteGame, btnContinueGame,
		btnWinLossRecord;
	JLabel questionForUser, passwordLabel, usernameLabel, newLoadGameLabel, saveGameLabel, deleteGameLabelOne, deleteGameLabelTwo;
	JTextField txtUser,txtPword;
	JFrame frameNewLoad, frameEnterInfo, frameConnection, frameUserType, frameSaveGame, frameDeleteGame, frameWinsLosses;
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
	
	public void createWinsLossesUI(int numWins, int numLosses) {
		frameWinsLosses = new JFrame();
		frameWinsLosses.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameWinsLosses.setSize(400,200);
		frameWinsLosses.setLayout(new BorderLayout());
		String forTitleLabel = username + "'s Win/Loss Record";
		JLabel titleLabel = new JLabel(forTitleLabel, SwingConstants.CENTER);
		JPanel topPanel = new JPanel();
		topPanel.add(titleLabel);
		
		JPanel midPanel = new JPanel(new GridLayout(3,2));
		JLabel numWinsLabel = new JLabel("Number of wins: ");
		JLabel numLossesLabel = new JLabel("Number of losses: ");
		JLabel numgamesLabel = new JLabel("Total games completed: ");
		JLabel numWinsLabelVal = new JLabel(String.valueOf(numWins), SwingConstants.CENTER);
		JLabel numLossesLabelVal = new JLabel(String.valueOf(numLosses), SwingConstants.CENTER);
		JLabel numgamesLabelVal = new JLabel(String.valueOf(numWins + numLosses), SwingConstants.CENTER);
		
		midPanel.add(numWinsLabel);
		midPanel.add(numWinsLabelVal);
		midPanel.add(numLossesLabel);
		midPanel.add(numLossesLabelVal);
		midPanel.add(numgamesLabel);
		midPanel.add(numgamesLabelVal);
		
		JPanel bottomPanel = new JPanel();
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		bottomPanel.add(closeButton);
		
		frameWinsLosses.add(topPanel, BorderLayout.NORTH);
		frameWinsLosses.add(midPanel, BorderLayout.CENTER);
		frameWinsLosses.add(bottomPanel, BorderLayout.SOUTH);
		
		frameWinsLosses.setVisible(true);
	}
	
	private void userLoginUI() {
		frameEnterInfo = new JFrame();
		frameEnterInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameEnterInfo.setSize(400,150);
		frameEnterInfo.setLayout(new BorderLayout());
		JLabel usrLabel = new JLabel("Register as new user or login as existing user", SwingConstants.CENTER);
		frameEnterInfo.add(usrLabel, BorderLayout.NORTH);
		
		usernameLabel = new JLabel("Username: ");
		passwordLabel = new JLabel("Password: ");
		
		txtUser = new JTextField("",15);//To adjust width
		txtPword = new JTextField();
		
		JButton btnRegisterNewUser = new JButton("Register as new user");
		btnRegisterNewUser.addActionListener(this);
		JButton btnLoginExistingUser = new JButton("Login as existing user");
		btnLoginExistingUser.addActionListener(this);
		
		JPanel pnlInput = new JPanel(new GridLayout(3,3));
		
		pnlInput.add(usernameLabel);
		pnlInput.add(txtUser);
		
		pnlInput.add(passwordLabel);
		pnlInput.add(txtPword);
		
		pnlInput.add(btnRegisterNewUser);
		pnlInput.add(btnLoginExistingUser);
		frameEnterInfo.add(pnlInput, BorderLayout.CENTER);
		frameEnterInfo.setVisible(true);
		
	}
	
	private void connectionUI() {
		frameConnection = new JFrame("Connection Panel");
		frameConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ta = new JTextArea(30,30);
		ta.setWrapStyleWord(true);
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
		JScrollPane sp = new JScrollPane(ta);
		frameConnection.add(sp, BorderLayout.CENTER);
		//frameConnection.add(ta, BorderLayout.CENTER);
		
		frameConnection.setSize(400, 200);
		frameConnection.setVisible(true);
	}

	private void newOrLoadGameUI()
	{
		frameNewLoad = new JFrame();
		frameNewLoad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameNewLoad.setSize(600,100);
		//Layout of Main Window
		frameNewLoad.setLayout(new BorderLayout());
		
		newLoadGameLabel = new JLabel("Start a New Game, Load an Existing Game, or View Your Win/Loss Record");
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(newLoadGameLabel);
		btnNewGame = new JButton("Start New Game");
		btnNewGame.addActionListener(this);
		
		btnLoadGame = new JButton("Load Existing Game");
		btnLoadGame.addActionListener(this);
		
		btnWinLossRecord = new JButton("View Win/Loss Record");
		btnWinLossRecord.addActionListener(this);
		JPanel pnlButton = new JPanel(new GridLayout(1,3));
		
		pnlButton.add(btnNewGame);
		pnlButton.add(btnLoadGame);
		pnlButton.add(btnWinLossRecord);
		
		
		frameNewLoad.add(pnlLabel, BorderLayout.NORTH);
		frameNewLoad.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
		frameNewLoad.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {		
		String cmd = evt.getActionCommand();
		
		if(cmd.equals("Register as new user")) {
			this.isNew = true;
			userPwordEnter();
		}
		else if(cmd.equals("Login as existing user")) {
			this.isNew = false;
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
				userLoginUI();
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
				Queue<Coordinate> tempAttack = loadedGame.getAttack();
				newGame = new Board(tempUsername,tempPassword,tempMygrid,tempOpgrid,tempMyships,
						tempOpships,tempMyhitsleft,tempOphitsleft, true, tempAttack);
				newGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    newGame.setVisible(true);    
			    newGame.setResizable(true);
				//saveGameUI();
			}
		}
		else if(cmd.equals("View Win/Loss Record")) {
			getNumWinsLosses();
		}
		else if(cmd.equals("Close")) {
			frameWinsLosses.dispose();
		}
		
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
	        	//frameUserType.dispose();
	        	frameEnterInfo.dispose();
	        	newOrLoadGameUI();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getNumWinsLosses() {
		String messageType = "WinsandLosses";
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(this.username);
		
		try {
			toServer.writeObject(messageArray);
			toServer.flush();
			
	        Object object = null;
			
			object = fromServer.readObject();
			
			ArrayList<Object> retArr = (ArrayList<Object>)object;
			int numWins = (int)retArr.get(0);
			int numLosses = (int)retArr.get(1);
			
			createWinsLossesUI(numWins, numLosses);	
			
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
				ArrayList<Object> retArr = (ArrayList<Object>)object;
				int gameInt = (int)retArr.get(0);
				String gameString = (String)retArr.get(1);
				loadedGame = (Board)retArr.get(2);
				this.savedGameID = gameInt;
				ta.append("Game ID: " + this.savedGameID + "\n");
				ta.append(gameString.toString());
				//System.out.println("Loaded Game Grid: \n");
				//loadedGame.printGrid();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	return loadedGame;
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
		Player obj = new Player();

	}
	
}
