package battleship;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

public class Player extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private JTextArea ta;
	private JTextField tf;
	DataOutputStream toServer;
	DataInputStream fromServer;
	private Socket socket;
	private static int numPlayers = 0;

	
	public Player() throws IOException {
		super("Player");
		this.setSize(Player.WIDTH, Player.HEIGHT);
		this.setLayout(new BorderLayout());
		createMenu();
		ta = new JTextArea();
		JScrollPane sp = new JScrollPane(ta);
		this.add(sp, BorderLayout.CENTER);
		tf = new JTextField();
		tf.addKeyListener(new TextFieldListener());
		this.add(tf, BorderLayout.SOUTH);
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> {
			System.exit(0);
		});
		JMenuItem connectItem = new JMenuItem("Connect");
		connectItem.addActionListener((e) -> {
			Thread t = new Thread(this);
			t.start();
		});
		menu.add(connectItem);
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	class TextFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				try {
					  
					toServer = new DataOutputStream(socket.getOutputStream());
	
				} catch (IOException e1) {
					closeEverything(socket, toServer, fromServer);
					e1.printStackTrace();
				}
			    try {
			    	String name = tf.getText().trim();
		            toServer.writeUTF(name);
			        toServer.flush();
			        ta.append(name + "\n");
			        tf.setText("");
	
			      }
			      catch (IOException ex) {
			        System.err.println(ex);
			        closeEverything(socket, toServer, fromServer);
			      } catch (NumberFormatException nfe) {
			    	  System.err.println(nfe);
			    	  ta.append("Bad format!\n");
			      }
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}		
	  }
		
	@Override
	public void run() {
			try {
				socket = new Socket("localhost", 8000);
				ta.append("connected\n");
				fromServer = new DataInputStream(socket.getInputStream());
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			while(true) {
				String clientInfo = null;
				try {
					clientInfo = fromServer.readUTF();
					ta.append(clientInfo + "\n");
				} catch (IOException e) {
					closeEverything(socket, toServer, fromServer);
					e.printStackTrace();
				} 
			}
	}
	
	public void closeEverything(Socket socket, DataOutputStream outputStream, DataInputStream inputStream) {
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

	public static void main(String[] args) throws IOException{
		Player battleshipPlayer = new Player();
		battleshipPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		battleshipPlayer.setVisible(true);

	}

}
