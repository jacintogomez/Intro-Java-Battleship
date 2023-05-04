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
	private int Id;
	int numberofwins;
	JButton btnNewUser, btnExistingUser, btnUsernameAndPassword, btnNewGame, btnLoadGame;
	JLabel questionForUser, passwordLabel, usernameLabel, newLoadGameLabel;
	JTextField txtUser,txtPword;
	
	JTextField textField = null;
	JTextArea ta = null;
	Socket socket = null;
    JButton openButton;
	JButton closeButton;
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	
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
			Board newGame = new Board(this.username, this.password);
			newGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    newGame.setVisible(true);    
		    newGame.setResizable(true);
		}
		else if(cmd.equals("Load Existing Game")) {
			loadGame();
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
		txtUser.setText("");
		txtPword.setText("");
		String varToPass = null;
		if(this.isNew == false) {
			varToPass = username + " " + password + " existing";
		}
		else {
			varToPass = username + " " + password + " new";
		}
		try {
			toServer.writeObject(varToPass);
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
	
	public void loadGame() {
		if(loadedGame == false) {
			String loadStr = "load";
			try {
				toServer.writeObject(loadStr);
				toServer.flush();
				Object object = null;
				object = fromServer.readObject();
				String messageForPlayer = (String)object;
				if(messageForPlayer.equals("Load flag turned")) {
					loadedGame = true;
				}
		        ta.append(messageForPlayer + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(loadedGame == true) {
		
			String[] usernameAndPassword = new String[2];
	    	usernameAndPassword[0] = this.username;
	    	usernameAndPassword[1] = this.password;
	    	try {
				toServer.writeObject(usernameAndPassword);
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
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
  
	}

	public static void main(String[] args) {
		Player obj = new Player();
	}
	
}