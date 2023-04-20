package battleship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;

public class Server extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private JTextArea ta;
	private int playerNum = 0;
	public static ArrayList<HandleAPlayer> clientHandlers = new ArrayList<>();
	private Socket socket;
	private ServerSocket serverSocket;
	
	public Server() {
		super("Game Server");
		this.setSize(Server.WIDTH, Server.HEIGHT);
		createMenu();
		ta = new JTextArea();
		JScrollPane sp = new JScrollPane(ta);
		this.add(sp);
		Thread t = new Thread(this);
		t.start();
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> {
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
			System.exit(0);
		});
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	

	public void run() {
		
		try{
			serverSocket = new ServerSocket(8000);
		
			ta.append("Battleship Game server started at " + new Date() + '\n');
	    
			while (playerNum < 2) {
				
					socket = serverSocket.accept();
					playerNum++;
		          
					ta.append("Starting thread for Player " + playerNum + " at " + new Date() + '\n');
	
					InetAddress inetAddress = socket.getInetAddress();
					ta.append("Client " + playerNum + "'s host name is " + inetAddress.getHostName() + "\n");
					ta.append("Client " + playerNum + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
		          
					new Thread(new HandleAPlayer(socket, playerNum)).start();
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
		  private Socket socket; 
		  private int clientNum;
		  DataInputStream inputFromClient;
	  	  DataOutputStream outputToClient;

		  public HandleAPlayer(Socket socket, int clientNum) throws IOException {
			  this.socket = socket;
			  this.clientNum = clientNum;
			  clientHandlers.add(this);
		  }

		  public void run() {			
			  try {
				  inputFromClient = new DataInputStream(socket.getInputStream());
				  outputToClient = new DataOutputStream(socket.getOutputStream());  
			
				  while (true) {
					  String name = null;
					  name = inputFromClient.readUTF();
					  String toClient = "Client " + this.clientNum + ": " + name;
					  broadcastMessage(toClient);
				  }
			  } catch (IOException e1) {
				  closeEverything(socket, outputToClient, inputFromClient);
			  }
		  }
		  
		  public void broadcastMessage(String messageToSend) {
			  for(HandleAPlayer i : clientHandlers) {
				  try {
					  if(i.clientNum != this.clientNum) {
						  i.outputToClient.writeUTF(messageToSend);
						  i.outputToClient.flush();
						  
					  }
				  }catch(IOException ex) {
					    closeEverything(socket, outputToClient, inputFromClient);
				  }
			  }
		  }
		  
		  public void removeHandleAClient() {
			  clientHandlers.remove(this);
		  }
		  
		  public void closeEverything(Socket socket, DataOutputStream outputStream, DataInputStream inputStream) {
			  removeHandleAClient();
			  try {
				  if(inputStream != null) {
					  inputStream.close();
				  }
				  if(outputStream != null) {
					  outputStream.close();
				  }
				  if(socket != null) {
					  socket.close();
				  }
			  }catch(IOException ex) {
	        		ex.printStackTrace();
	        	}
		  }
	}
	
	public void closeEverything(ServerSocket serverSocket, Socket socket, DataOutputStream outputStream, DataInputStream inputStream) {
		try {
			if(inputStream != null) {
			  inputStream.close();
			}
			if(outputStream != null) {
			  outputStream.close();
			}
			if(socket != null) {
			  socket.close();
			}
			if(serverSocket != null) {
			  serverSocket.close();
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server gameServer = new Server();
		gameServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameServer.setVisible(true);

	}

}
