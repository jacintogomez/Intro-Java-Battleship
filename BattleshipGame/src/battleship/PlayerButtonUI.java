package battleship;

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
import java.util.*;

public class PlayerButtonUI implements ActionListener {
	private String username;
	private String password;
	private boolean isNew;
	private boolean loadedGame = false, savedGame = false, deletedGame = false;
	private int savedGameID = 0;
	private static int nextId = 0;
	private int Id;
	JButton btnNewUser, btnExistingUser, btnUsernameAndPassword, btnNewGame, btnLoadGame, btnDeleteGame, btnContinueGame;
	JLabel questionForUser, passwordLabel, usernameLabel, newLoadGameLabel, saveGameLabel, deleteGameLabel;
	JTextField txtUser,txtPword;
	
	JTextField textField = null;
	JTextArea ta = null;
	Socket socket = null;
    JButton openButton;
	JButton closeButton;
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	
	testGame newGame, loadGame;
	
	public PlayerButtonUI(){
		nextId++;
		this.Id = nextId;
		connectionUI();
		
	}
	
	private void connectionUI() {
		JFrame frame = new JFrame("Connection Panel");
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
	
	private void userTypeUI()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,100);
		//Layout of Main Window
		frame.setLayout(new BorderLayout());
		
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
		
		
		frame.add(pnlLabel, BorderLayout.NORTH);
		frame.add(pnlButton, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	private void enterInfoUI() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,150);
		
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
		
		frame.add(pnlInput, BorderLayout.NORTH);
		frame.add(pnlButton, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	private void newOrLoadGameUI()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,100);
		//Layout of Main Window
		frame.setLayout(new BorderLayout());
		
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
		
		
		frame.add(pnlLabel, BorderLayout.NORTH);
		frame.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
		frame.setVisible(true);
	}
	
	private void saveGameUI()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,100);
		//Layout of Main Window
		frame.setLayout(new BorderLayout());
		
		saveGameLabel = new JLabel("Do you want to save this game?");
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(saveGameLabel);
		btnNewGame = new JButton("Save Game");
		btnNewGame.addActionListener(this);
		
		
		JPanel pnlButton = new JPanel(new GridLayout(1,1));
		
		pnlButton.add(btnNewGame);
		
		
		frame.add(pnlLabel, BorderLayout.NORTH);
		frame.add(pnlButton, BorderLayout.CENTER);
		
		//frame.pack();
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
			System.out.println("Username and Pword!");
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
			newGame = new testGame(this.username, this.password);
			saveGameUI();
		}
		else if(cmd.equals("Load Existing Game")) {
			loadGame();
		}
		else if(cmd.equals("Save Game")) {
			saveGame(newGame);
		}
		else if(cmd.equals("Yes, delete previous saved game")) {
			deleteGame(newGame);
		}
		else if(cmd.equals("No, continue current game")) {
			System.out.println("Nothing for ya");
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
	
public void saveGame(testGame game) {
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
	
	public void loadGame() {
		String messageType = "load";
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		String[] unamePword = new String[2];
		unamePword[0] = this.username;
		unamePword[1] = this.password;
		messageArray.add(unamePword);
	
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
				testGame game = (testGame)object;
				ta.append(game.toString() + "\n");
				game.printGrid();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteGame(testGame game) {
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
		PlayerButtonUI obj = new PlayerButtonUI();
	}

}
