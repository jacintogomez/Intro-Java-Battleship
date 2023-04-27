package battleship;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class PlayerObjectTest extends JFrame {
	private String username;
	private String password;
	private static int nextId = 0;
	private int Id;
	
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	JTextField textField = null;
	JTextArea textArea = null;
	Socket socket = null;
    JButton openButton;
	JButton closeButton;

	public PlayerObjectTest() {
		super("Game Player");
		this.Id = nextId;
		nextId++;
		
		textField = new JTextField(5);
		textArea = new JTextArea(30,30);
		this.setLayout(new BorderLayout());
		//this.add(textField, BorderLayout.NORTH);
		textField.addKeyListener(new TextFieldListener());
	
		JPanel topPanel = new JPanel(new GridLayout(2,1));
		JPanel controlPanel = new JPanel();
		topPanel.add(textField);
		openButton = new JButton("Open Connection");
		closeButton = new JButton("Close Connection");
		controlPanel.add(openButton);
		controlPanel.add(closeButton);
		topPanel.add(controlPanel);
		this.add(topPanel, BorderLayout.NORTH);
	
		this.add(textArea, BorderLayout.CENTER);
		closeButton.addActionListener((e) -> { 
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

				textArea.append("connection closed\n");
			} catch (Exception e1) {
				System.err.println("error"); 
			}
		});
		openButton.addActionListener(new OpenConnectionListener());
		setSize(400, 200);
	}
	
	class OpenConnectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				socket = new Socket("localhost", 8000);
				textArea.append("connected\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				textArea.append("connection Failure\n");
			}
		}
		  
	  }
	
	class TextFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				textArea.append("Test!\n");
			    try {
			    	  toServer = new ObjectOutputStream(socket.getOutputStream()); //if put ObjectInputStream first, doesn't work. WHY???
				      fromServer = new ObjectInputStream(socket.getInputStream());
					      
				    }
				    catch (IOException ex) {
				      textArea.append(ex.toString() + '\n');
				    }
			    
			    try {
			    	String userInputName = textField.getText().trim();
			        testGame game1 = new testGame(userInputName, "genericpassword"); 
			        textArea.append("About to write!\n");
			        toServer.writeObject(game1);
			        //toServer.flush();
	
			        
			      }
			    	catch (EOFException ex) {
			        System.err.println(ex);
			        textArea.append("End of object!\n");
			        
			      }
			      catch (IOException ex) {
			        System.err.println(ex);
			        textArea.append("Didn't work!\n");
			      } catch (NumberFormatException nfe) {
			    	  System.err.println(nfe);
			    	  textArea.append("Bad format!\n");
	
			      }
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	  }

	public static void main(String[] args) {
		PlayerObjectTest testPlayer = new PlayerObjectTest();
		testPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testPlayer.setVisible(true);

	}

}
