package battleship;

import java.util.Random;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.FontMetrics;

public class BoardFailedInit extends JFrame implements Runnable, Serializable, ActionListener {
	private static final Color[] TILE_COLORS = {
	    Color.BLUE,   // open
	    Color.GRAY,   // ship
	    Color.WHITE,  // miss
	    Color.RED     // hit
	};
	private static final String[] directions= {"up","down","left","right"};
	
	//1=open, 2=ship, 3=miss, 4=hit
	private static final long serialVersionUID = 1L;
	private int gamewidth=1000;
	private int gameheight=700;
	private int gridwidth=350;
	private int gridheight=350;
	private int cellwidth=gridwidth/10;
	private int cellheight=gridheight/10;
	
	private int mygrid[][]=new int[10][10];
	private int opgrid[][]=new int[10][10];
	private int xoffset=(gamewidth/2-gridwidth)/2;
	private int yoffset=(gameheight-gridheight)/2;
	private boolean gameinprogress;
	
	private JButton fire, btnOpenConnection, btnCloseConnection, btnSaveGame, btnDeleteGame, btnContinueGame;
	private JLabel deleteGameLabel;
	private JFrame frameDeleteGame;
	private JTextField enter;
	private String choice;
	private JTextArea messages, winner;
	private JScrollPane scroll;
	
	private ArrayList<Ship> myships=new ArrayList<Ship>();
	private ArrayList<Ship> opships=new ArrayList<Ship>();
	private ImagePanel leftboard;
	private ImagePanel rightboard;
	private String username;
	private String password;
	private int ophitsleft;
	private int myhitsleft;
	
	private Socket socket;
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	private int savedGameID = 0;
	
	/*
	JLabel display;
	JLabel warning;
	JRadioButton aButton, bButton, cButton, dButton, eButton, fButton, gButton, hButton, iButton, jButton;
	JRadioButton Button1, Button2, Button3, Button4, Button5, Button6, Button7, Button8, Button9, Button10;
	JRadioButton upButton, downButton, leftButton, rightButton;
	public String shipname;
	public int number;
	public char letter;
	public String direction;
	*/
	
	public BoardFailedInit() {
		this.username = "Jacinto";
		this.password = "12345";
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {
				mygrid[x][y]=1;
				opgrid[x][y]=1;
			}
		}
		ophitsleft=myhitsleft=17;
		createships();
		setopponentships();
		setuserships();
		//randomizeuserships();
		launchgame();
	}
	
	public BoardFailedInit(String username,String password) {
		this.username=username;
		this.password=password;
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {
				mygrid[x][y]=1;
				opgrid[x][y]=1;
			}
		}
		ophitsleft=myhitsleft=17;
		createships();
		setopponentships();
		setuserships();
		//randomizeuserships();
		launchgame();
	}
	
	public BoardFailedInit(String username,String password,int mygrid[][],int opgrid[][],ArrayList<Ship> myships,
			ArrayList<Ship> opships,int myhitsleft,int ophitsleft, boolean launchGame) {
		this.username=username;
		this.password=password;
		this.mygrid=mygrid;
		this.opgrid=opgrid;
		this.myships=myships;
		this.opships=opships;
		this.myhitsleft=myhitsleft;
		this.ophitsleft=ophitsleft;
		if(launchGame == true) {
			launchgame();
		}
	}
	
	public int[][] getMyGrid(){
		return this.mygrid;
	}
	
	public int[][] getOpGrid(){
		return this.opgrid;
	}
	
	public ArrayList<Ship> getMyships(){
		return this.myships;
	}
	
	public ArrayList<Ship> getOpships(){
		return this.opships;
	}
	
	public int getMyhitsleft() {
		return this.myhitsleft;
	}
	
	public int getOphitsleft() {
		return this.ophitsleft;
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
	
	public void launchgame() {
		gameinprogress=true;
		enter=new JTextField(5);
		enter.addActionListener(new textfieldlistener());
		fire=new JButton("Fire");
		fire.addActionListener(new addfirelistener());
		this.setBackground(Color.LIGHT_GRAY);
	    setSize(gamewidth,gameheight);
	    createpanel();
	    Thread t=new Thread(this);
	    t.start();
	}
	
	public void run() {
		while(gameinprogress) {
			while(choice==null) {
				timedelay(0.25);
			}
			myturn();
			leftboard.repaint();
	    	rightboard.repaint();
	    	computerturn();
	    	leftboard.repaint();
	    	rightboard.repaint();
	    	choice=null;
	    }
	}
	
	private class ImagePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawRect(xoffset-1, yoffset-1, gridwidth+2, gridheight+2);
        }
    }
	
	public void createpanel() {
		rightboard=new ImagePanel(){
	        public void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            int width = cellwidth;
	            int height = cellheight;
	            g.setColor(Color.BLACK);
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                    g.drawRect((x * width)+xoffset, (y * height)+yoffset, width, height);
	                }
	            }
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                	char corner=isthisacornerm(x,y);
	                	g.setColor(gettilecolor(mygrid[x][y],true));
	                    if(corner=='n') {
	                    	g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2);
	                    }else {
	                    	g.setColor(Color.BLUE);
	                    	g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2);
	                    	g.setColor(Color.GRAY);
	                    	if(corner=='l') {
	                    		g.fillRect((x * width)+xoffset+cellwidth/2+1, (y * height)+yoffset+1, width/2-2, height-2);
		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
	                    	}
	                    	if(corner=='r') {
	                    		g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width/2-2, height-2);
		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
	                    	}
	                    	if(corner=='t') {
	                    		g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+cellwidth/2+1, width-2, height/2-2);
		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
	                    	}
	                    	if(corner=='b') {
	                    		g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height/2-2);
		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
	                    	}
	                    }
	                }
	            }
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                	g.setColor(getholecolor(mygrid[x][y],true));
	                    g.fillOval((x * width)+xoffset+cellwidth/4, (y * height)+yoffset+cellwidth/4, width/2, height/2);
	                }
	            }
	            g.setColor(Color.BLACK);
	            g.setFont(new Font("Arial", Font.BOLD, 16));
	            for (int y = 0; y < 10; y++) {
	                g.setColor(Color.BLACK);
	                g.drawString(String.valueOf(convert(y)), xoffset-25, (y*cellheight+yoffset+cellheight/2)+5);
	            }
	            for(int x=0;x<10;x++) {
	            	g.drawString(String.valueOf(x+1),(xoffset+10)+x*cellwidth,yoffset-5);
	            }
	            FontMetrics fm = g.getFontMetrics();
	            g.setFont(new Font("Arial", Font.BOLD, 24));
	            g.drawString(username,xoffset + (gridwidth - fm.stringWidth(username)) / 2,yoffset-50);
	        }
	    };
		leftboard=new ImagePanel(){
			public void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            int width = cellwidth;
	            int height = cellheight;
	            g.setColor(Color.BLACK);
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                    g.drawRect((x * width)+xoffset, (y * height)+yoffset, width, height);
	                }
	            }
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                	char corner=isthisacornero(x,y);
	                	g.setColor(gettilecolor(opgrid[x][y],false));
	                   	g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2);
//	                    }else {
//	                    	g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2);
//	                    	if(corner=='l') {
//	                    		g.fillRect((x * width)+xoffset+cellwidth/2+1, (y * height)+yoffset+1, width/2-2, height-2);
//		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
//	                    	}
//	                    	if(corner=='r') {
//	                    		g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width/2-2, height-2);
//		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
//	                    	}
//	                    	if(corner=='t') {
//	                    		g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+cellwidth/2+1, width-2, height/2-2);
//		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
//	                    	}
//	                    	if(corner=='b') {
//	                    		g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height/2-2);
//		                    	g.fillArc((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2,0,360);
//	                    	}
//	                    }
	                }
	            }
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                	g.setColor(getholecolor(opgrid[x][y],false));
	                    g.fillOval((x * width)+xoffset+cellwidth/4, (y * height)+yoffset+cellwidth/4, width/2, height/2);
	                }
	            }
	            g.setColor(Color.BLACK);
	            g.setFont(new Font("Arial", Font.BOLD, 16));
	            for (int y = 0; y < 10; y++) {
	                g.drawString(String.valueOf(convert(y)), xoffset-25, (y*cellheight+yoffset+cellheight/2)+5);
	            }
	            for(int x=0;x<10;x++) {
	            	g.drawString(String.valueOf(x+1),(xoffset+10)+x*cellwidth,yoffset-5);
	            }
	            g.setFont(new Font("Arial", Font.BOLD, 24));
	            g.drawString("Opponent",xoffset+(gridwidth/3),yoffset-50);
	        }
	    };
		JPanel mainpan=new JPanel();
		mainpan.setLayout(new GridLayout(1,2));
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		JPanel connectionPanel = new JPanel();
		JPanel savePanel = new JPanel();
		btnSaveGame = new JButton("Save Game");
		savePanel.add(btnSaveGame);
		btnSaveGame.addActionListener(this);
		connectionPanel.setLayout(new GridLayout(1,2));
		btnOpenConnection = new JButton("Open Connection");
		btnCloseConnection = new JButton("Close Connection");
		btnOpenConnection.addActionListener(this);
		btnCloseConnection.addActionListener(this);
		connectionPanel.add(btnOpenConnection);
		connectionPanel.add(btnCloseConnection);
		
		//rightboard.add(enter,BorderLayout.NORTH);
		//rightboard.add(fire,BorderLayout.NORTH);
		messages=new JTextArea(6,0);
		messages.setEditable(false);
		messages.setLineWrap(true);
		scroll=new JScrollPane(messages);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(10, 10));
		winner=new JTextArea();
		winner.setEditable(false);
		leftboard.add(messages,BorderLayout.SOUTH);
		mainpan.add(leftboard);
		mainpan.add(rightboard);
		this.add(mainpan,BorderLayout.CENTER);
		rightboard.repaint();
		leftboard.repaint();
		JPanel enterPanel = new JPanel();
		enterPanel.setLayout(new GridLayout(1,2));
		enterPanel.add(enter);
		enterPanel.add(fire);
		topPanel.add(enterPanel,BorderLayout.EAST);
		topPanel.add(savePanel, BorderLayout.CENTER);
		topPanel.add(connectionPanel, BorderLayout.WEST);
		topPanel.add(winner,BorderLayout.NORTH);
		this.add(topPanel, BorderLayout.NORTH);
		this.add(messages,BorderLayout.SOUTH);
		this.add(topPanel,BorderLayout.NORTH);
	}
	
	public boolean isvalid(int x,int y) {
		return x>=0&&y>=0&&x<=9&&y<=9;
	}
	
	public void myturn() {
		System.out.println("my turns choice is "+choice);
		int x=0,y=0;
		do {
			try {
				y=reverse(choice.charAt(0));
				//int num = Integer.parseInt(choice.substring(1));
				x=(choice.length()==3)?9:Integer.parseInt((""+choice.charAt(1)))-1;
			}catch(Exception e) {
				System.out.println("invalid input");
			}
		}while(!isvalid(x,y));
		String hitormiss="";
		String aftermessage="";
//		if(!isvalid(x,y)) {
//			System.out.println("invalid input, your turn has been skipped");
//			return;
//		}
		if(opgrid[x][y]==1){
			hitormiss="Miss";
			opgrid[x][y]=3;
		} //I missed
		if(opgrid[x][y]==2) {
			hitormiss="Hit";
			opgrid[x][y]=4;
			ophitsleft--;
			for(Ship s:opships) {
				s.checkifstruck(new Coordinate(x,y));
			}
			for(Ship s:opships) {
				System.out.println("checking");
				if(s.active&&(s.holes==s.struck)) {
					s.active=false;
					aftermessage=username+" sunk the computer's "+s.name+'\n';
				}
			}
			checkifgameover();
		} //I hit a ship
		//calerts.setText(player.username+" guesses "+choice+" - "+hitormiss);
		//messages.insert(player.username+" guesses "+choice+" - "+hitormiss+'\n',0);
		//timedelay(0.25);
		System.out.println("Myturn grid: \n");
		printGrid();
		if(aftermessage!="") {
			timedelay(0.25);
			//messages.insert(aftermessage,0);
		}
	}
			
	public void computerturn() {
		timedelay(1);
		int x;
		int y;
		String hitormiss="";
		String computerguess="";
		String aftermessage="";
		do {
			x=pickspot(10);
			y=pickspot(10);
			System.out.println("result is "+mygrid[x][y]+", picking new target");
		}while(mygrid[x][y]!=1&&mygrid[x][y]!=2);
		computerguess+=convert(x)+y;
		if(mygrid[x][y]==1) {
			hitormiss="Miss";
			mygrid[x][y]=3;
		} //computer missed
		if(mygrid[x][y]==2) {
			hitormiss="Hit";
			mygrid[x][y]=4;
			myhitsleft--;
			for(Ship s:myships) {
				s.checkifstruck(new Coordinate(x,y));
			}
			for(Ship s:myships) {
				if(s.active&&(s.holes==s.struck)) {
					s.active=false;
					aftermessage="Computer sunk "+ username+"'s "+s.name+'\n';
				}
			}
			checkifgameover();
		} //my ship has been hit
		//messages.insert("Computer guesses "+computerguess+" - "+hitormiss+'\n',0);
		//timedelay(0.25);
		if(aftermessage!="") {
			timedelay(0.25);
			//messages.insert(aftermessage,0);
		}
	}
		
	public void createships() {
		Ship destroyer=new Ship("Destroyer",3);
		Ship submarine=new Ship("Submarine",3);
		Ship patrol=new Ship("Patrol Boat",2);
		Ship battleship=new Ship("Battleship",4);
		Ship carrier=new Ship("Carrier",5);
		Ship enemy_destroyer=new Ship("Destroyer",3);
		Ship enemy_submarine=new Ship("Submarine",3);
		Ship enemy_patrol=new Ship("Patrol Boat",2);
		Ship enemy_battleship=new Ship("Battleship",4);
		Ship enemy_carrier=new Ship("Carrier",5);
		myships.add(destroyer);
		myships.add(submarine);
		myships.add(patrol);
		myships.add(battleship);
		myships.add(carrier);
		opships.add(enemy_destroyer);
		opships.add(enemy_submarine);
		opships.add(enemy_patrol);
		opships.add(enemy_battleship);
		opships.add(enemy_carrier);
	}
	
	public int pickspot(int rng) {
		Random r=new Random();
		return r.nextInt(rng);
	}
	
	public boolean conflicts(int col,int row,String dir,int len,boolean my) {
		int grid[][];
		grid=(my)?mygrid:opgrid;
		if(dir=="up") {
			if(len-row-1>0) {return true;}
			while(len>0&&row>=0) {
				if(grid[col][row]==2) {return true;};
				len--;
				row--;
			}
		}
		if(dir=="down") {
			if(len-1+row>9) {return true;}
			while(len>0&&row<=9) {
				if(grid[col][row]==2) {return true;};
				len--;
				row++;
			}
		}
		if(dir=="left") {
			if(len-col-1>0) {return true;}
			while(len>0&&col>=0) {
				if(grid[col][row]==2) {return true;};
				len--;
				col--;
			}
		}
		if(dir=="right") {
			if(len-1+col>9) {return true;}
			while(len>0&&col<=9) {
				if(grid[col][row]==2) {return true;};
				len--;
				col++;
			}
		}
		return false;
	}
	
	public void randomizeuserships() {
		for(Ship s:myships) {
			int col=pickspot(10);
			int row=pickspot(10);
			String dir=directions[pickspot(4)];
			int len=s.getHoles();
			while(conflicts(col,row,dir,len,true)) {
				col=pickspot(10);
				row=pickspot(10);
				dir=directions[pickspot(4)];
				len=s.getHoles();
			}
			if(dir=="up") {
				while(len>0&&row>=0) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row--;
				}
				s.coords.get(0).special='b';
				s.coords.get(s.holes-1).special='t';
			}
			if(dir=="down") {
				while(len>0&&row<=9) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row++;
				}
				s.coords.get(0).special='t';
				s.coords.get(s.holes-1).special='b';
			}
			if(dir=="left") {
				while(len>0&&col>=0) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col--;
				}
				s.coords.get(0).special='r';
				s.coords.get(s.holes-1).special='l';
			}
			if(dir=="right") {
				while(len>0&&col<=9) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col++;
				}
				s.coords.get(0).special='l';
				s.coords.get(s.holes-1).special='r';
			}
		}
//		for(Ship s:myships) {
//			s.printcoords();
//			System.out.println();
//		}
	}
	/*
	public JFrame InitializePanel(String shipname) {
		JFrame retFrame = new JFrame();
		retFrame.setLayout(new BorderLayout());
		this.number=-1;
		this.letter='a';
		this.shipname=shipname;
		display=new JLabel(shipname);
		warning=new JLabel("");
		warning.setForeground(Color.RED);
		JLabel instruction=new JLabel("this will");
		JPanel toppanel=new JPanel();
		JPanel instructionpanel=new JPanel();
		JPanel bottompanel=new JPanel();
		JPanel panel=new JPanel();
		JPanel panel1 = new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		panel1.setLayout(new GridLayout(3,1));
		panel2.setLayout(new GridLayout(3,1));
		panel3.setLayout(new GridLayout(3,1));
		aButton = new JRadioButton("A");
		aButton.addActionListener(new pressa());
		bButton = new JRadioButton("B");
		bButton.addActionListener(new pressb());
		cButton = new JRadioButton("C");
		cButton.addActionListener(new pressc());
		dButton = new JRadioButton("D");
		dButton.addActionListener(new pressd());
		eButton = new JRadioButton("E");
		eButton.addActionListener(new presse());
		fButton = new JRadioButton("F");
		fButton.addActionListener(new pressf());
		gButton = new JRadioButton("G");
		gButton.addActionListener(new pressg());
		hButton = new JRadioButton("H");
		hButton.addActionListener(new pressh());
		iButton = new JRadioButton("I");
		iButton.addActionListener(new pressi());
		jButton = new JRadioButton("J");
		jButton.addActionListener(new pressj());
		Button1 = new JRadioButton("1");
		Button1.addActionListener(new press1());
		Button2 = new JRadioButton("2");
		Button2.addActionListener(new press2());
		Button3 = new JRadioButton("3");
		Button3.addActionListener(new press3());
		Button4 = new JRadioButton("4");
		Button4.addActionListener(new press4());
		Button5 = new JRadioButton("5");
		Button5.addActionListener(new press5());
		Button6 = new JRadioButton("6");
		Button6.addActionListener(new press6());
		Button7 = new JRadioButton("7");
		Button7.addActionListener(new press7());
		Button8 = new JRadioButton("8");
		Button8.addActionListener(new press8());
		Button9 = new JRadioButton("9");
		Button9.addActionListener(new press9());
		Button10 = new JRadioButton("10");
		Button10.addActionListener(new press10());
		upButton = new JRadioButton("Up");
		upButton.addActionListener(new pressup());
		downButton = new JRadioButton("Down");
		downButton.addActionListener(new pressdown());
		leftButton = new JRadioButton("Left");
		leftButton.addActionListener(new pressleft());
		rightButton = new JRadioButton("Right");
		rightButton.addActionListener(new pressright());
		ButtonGroup group1=new ButtonGroup();
		ButtonGroup group2=new ButtonGroup();
		ButtonGroup group3=new ButtonGroup();
		group1.add(aButton);
		group1.add(bButton);
		group1.add(cButton);
		group1.add(dButton);
		group1.add(eButton);
		group1.add(fButton);
		group1.add(gButton);
		group1.add(hButton);
		group1.add(iButton);
		group1.add(jButton);
		group2.add(Button1);
		group2.add(Button2);
		group2.add(Button3);
		group2.add(Button4);
		group2.add(Button5);
		group2.add(Button6);
		group2.add(Button7);
		group2.add(Button8);
		group2.add(Button9);
		group2.add(Button10);
		group3.add(upButton);
		group3.add(downButton);
		group3.add(leftButton);
		group3.add(rightButton);
		
		panel1.add(aButton);
		panel1.add(bButton);
		panel1.add(cButton);
		panel1.add(dButton);
		panel1.add(eButton);
		panel1.add(fButton);
		panel1.add(gButton);
		panel1.add(hButton);
		panel1.add(iButton);
		panel1.add(jButton);
		panel2.add(Button1);
		panel2.add(Button2);
		panel2.add(Button3);
		panel2.add(Button4);
		panel2.add(Button5);
		panel2.add(Button6);
		panel2.add(Button7);
		panel2.add(Button8);
		panel2.add(Button9);
		panel2.add(Button10);
		panel3.add(upButton);
		panel3.add(downButton);
		panel3.add(leftButton);
		panel3.add(rightButton);
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		toppanel.add(display,BorderLayout.NORTH);
		toppanel.add(instruction,BorderLayout.SOUTH);
		//instructionpanel.add(instruction);
		retFrame.add(toppanel,BorderLayout.NORTH);
		//this.add(instructionpanel,BorderLayout.CENTER);
		bottompanel.add(warning);
		retFrame.add(panel);
		retFrame.add(bottompanel,BorderLayout.SOUTH);
		retFrame.setSize(300,300);
		retFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        retFrame.setVisible(true); 
		retFrame.setResizable(false);
		return retFrame;
	}
	*/
	
	public void setuserships() {
		boolean initialized=false;
		for(Ship s:myships) {
			Initpanel frame = new Initpanel("Set your "+s.name+" ("+s.holes+" spaces long)");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setVisible(true); 
	        int counter=0;
	        int col;
			int row;
			String dir;
			int len;
	        do {
	        	counter++;
	        	if(counter>1) {frame.setwarning("invalid/overlapping location; pick again");}
	        	synchronized(this) {
	        		while(!initialized) {
	        	
		        	System.out.println("uninitialized");
		        	if(frame.direction!=null&&frame.number!=-1&&frame.letter!='a') {
		        		initialized=true;
		        	}
		        }
	        	}
	        	System.out.println("initialized now");
	        	initialized=false;
	        	col=frame.number;
				row=reverse(frame.letter);
				dir=frame.direction;
				len=s.getHoles();
				System.out.println("coordinate for "+s.name+" is "+row+":"+col+" "+dir);
	        }while(conflicts(col,row,dir,len,true));
	        System.out.println("already out");
	        initialized=false;
			if(dir=="up") {
				while(len>0&&row>=0) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row--;
				}
				s.coords.get(0).special='b';
				s.coords.get(s.holes-1).special='t';
			}
			if(dir=="down") {
				while(len>0&&row<=9) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row++;
				}
				s.coords.get(0).special='t';
				s.coords.get(s.holes-1).special='b';
			}
			if(dir=="left") {
				while(len>0&&col>=0) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col--;
				}
				s.coords.get(0).special='r';
				s.coords.get(s.holes-1).special='l';
			}
			if(dir=="right") {
				while(len>0&&col<=9) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col++;
				}
				s.coords.get(0).special='l';
				s.coords.get(s.holes-1).special='r';
			}
			frame.dispose();
		}
	}
	
	public void setopponentships() {
		for(Ship s:opships) {
			int col=pickspot(10);
			int row=pickspot(10);
			String dir=directions[pickspot(4)];
			int len=s.getHoles();
			while(conflicts(col,row,dir,len,false)) {
				col=pickspot(10);
				row=pickspot(10);
				dir=directions[pickspot(4)];
				len=s.getHoles();
			}
			if(dir=="up") {
				while(len>0&&row>=0) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row--;
				}
				s.coords.get(0).special='b';
				s.coords.get(s.holes-1).special='t';
			}
			if(dir=="down") {
				while(len>0&&row<=9) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row++;
				}
				s.coords.get(0).special='t';
				s.coords.get(s.holes-1).special='b';
			}
			if(dir=="left") {
				while(len>0&&col>=0) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col--;
				}
				s.coords.get(0).special='r';
				s.coords.get(s.holes-1).special='l';
			}
			if(dir=="right") {
				while(len>0&&col<=9) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col++;
				}
				s.coords.get(0).special='l';
				s.coords.get(s.holes-1).special='r';
			}
		}
//		for(Ship s:opships) {
//			s.printcoords();
//			System.out.println();
//		}
	}
		
	public Color gettilecolor(int code, boolean thisismygrid) {
	    Color col=Color.BLUE;
	    if(!thisismygrid) {return col;}
	    if(code%2==0) {col=Color.GRAY;}
	    return col;
	}
	
	public Color getholecolor(int code,boolean thisismygrid) {
		Color col=Color.blue;
		if(code==2) {
			if(thisismygrid) {col=Color.gray;}
		}
		if(code==3) {col=Color.white;}
		if(code==4) {col=Color.red;}
		return col;
	}
	
	public char convert(int row) {
		return (char)(65+row);
	}
	
	public int reverse(char row) {
		return row-65;
	}
	
	public void timedelay(double time) {
		double start=System.currentTimeMillis();
		while(System.currentTimeMillis()<start+time*1000);
		System.out.println("wait "+time+" second(s)");
	}
	
	public class textfieldlistener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			choice=enter.getText().trim();
			System.out.println("the choice is "+choice);
			enter.setText("");
		}
	}
	
	public class addfirelistener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {		
		String cmd = evt.getActionCommand();
		
		
		if(cmd.equals("Open Connection")) {
			try {
				socket = new Socket("localhost", 8000);
				try {
			    	  toServer = new ObjectOutputStream(socket.getOutputStream()); 
				      fromServer = new ObjectInputStream(socket.getInputStream());
					      
				    }
				    catch (IOException ex) {
				      messages.append(ex.toString() + '\n');
				    }
				messages.append("connected\n");
			} catch (IOException e1) {
				e1.printStackTrace();
				messages.append("connection Failure\n");
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

				messages.append("connection closed\n");
			} catch (Exception e1) {
				System.err.println("error"); 
			}
		}
		
		
		else if(cmd.equals("Save Game")) {
			saveGame();
		}
		else if(cmd.equals("Yes, delete previous saved game")) {
			frameDeleteGame.dispose();
			deleteGame();
			
		}
		else if(cmd.equals("No, continue current game")) {
			frameDeleteGame.dispose();
		}
	}
	
	public void checkifgameover() {
		if(myhitsleft>0&&ophitsleft>0) {
			System.out.println("game continuing");
			return;
		}
		if(myhitsleft==0) {
			winner.append("Computer Wins!");
			System.out.println("Computer Wins!");
		}else {
			winner.append(username+" Wins!");
			System.out.println(username+" Wins!");
		}
		gameinprogress=false;
	}
	
	public char isthisacornerm(int x,int y) {
		Coordinate c=new Coordinate(x,y);
		for(Ship s:myships) {
			if(s.coords.get(0).equals(c)) {return s.coords.get(0).special;}
			if(s.coords.get(s.holes-1).equals(c)) {return s.coords.get(s.holes-1).special;}
		}
		return 'n';
	}
	
	public char isthisacornero(int x,int y) {
		Coordinate c=new Coordinate(x,y);
		for(Ship s:opships) {
			System.out.println(s.coords.get(0).special);
			if(s.coords.get(0).equals(c)) {return s.coords.get(0).special;}
			if(s.coords.get(s.holes-1).equals(c)) {return s.coords.get(s.holes-1).special;}
		}
		return 'n';
	}
	
	public void printGrid() {
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {	
				System.out.print(opgrid[y][x] + "\t");
				if(y == 9) {
					System.out.print("\n");
				}
			}
		}
		System.out.println("\n");
	}
	
	public void saveGame() {
		String messageType = "save";
		//this.setGridCell(5, 5, setNum);
		//setNum++;
		this.printGrid();
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(this.username);
		messageArray.add(this.password);
		messageArray.add(this.mygrid);
		messageArray.add(this.opgrid);
		messageArray.add(this.myships);
		messageArray.add(this.opships);
		messageArray.add(this.myhitsleft);
		messageArray.add(this.ophitsleft);
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
				messages.append(gameString.toString() + "\n");
				deleteGameUI();
			}
			else {
				this.savedGameID = gameInt;
				messages.append(gameString.toString() + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

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
	
	public void deleteGame() {
		String messageType = "delete";
		ArrayList<Object> messageArray = new ArrayList<>();
		messageArray.add(messageType);
		messageArray.add(this.username);
		messageArray.add(this.password);
		messageArray.add(this.mygrid);
		messageArray.add(this.opgrid);
		messageArray.add(this.myships);
		messageArray.add(this.opships);
		messageArray.add(this.myhitsleft);
		messageArray.add(this.ophitsleft);
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
			messages.append(gameString.toString());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/*
	public void setwarning(String warn) {
		this.warning.setText(warn);
	}
	
	public class pressa implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button a");
			letter='A';
		}
	}
	
	public class pressb implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button b");
			letter='B';
		}
	}
	
	public class pressc implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button c");
			letter='C';
		}
	}
	
	public class pressd implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button d");
			letter='D';
		}
	}
	
	public class presse implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button e");
			letter='E';
		}
	}
	
	public class pressf implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button f");
			letter='F';
		}
	}
	
	public class pressg implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button g");
			letter='G';
		}
	}
	
	public class pressh implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button h");
			letter='H';
		}
	}
	
	public class pressi implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button i");
			letter='I';
		}
	}
	
	public class pressj implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button j");
			letter='J';
		}
	}
	
	public class press1 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 1");
			number=0;
		}
	}
	
	public class press2 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 2");
			number=1;
		}
	}
	
	public class press3 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 3");
			number=2;
		}
	}
	
	public class press4 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 4");
			number=3;
		}
	}
	
	public class press5 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 5");
			number=4;
		}
	}
	
	public class press6 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 6");
			number=5;
		}
	}
	
	public class press7 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 7");
			number=6;
		}
	}
	
	public class press8 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 8");
			number=7;
		}
	}
	
	public class press9 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 9");
			number=8;
		}
	}
	
	public class press10 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 10");
			number=9;
		}
	}
	
	public class pressup implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button up");
			direction="up";
		}
	}
	
	public class pressdown implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button down");
			direction="down";
		}
	}
	
	public class pressleft implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button left");
			direction="left";
		}
	}
	
	public class pressright implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button right");
			direction="right";
		}
	}
	*/
	
	public static void main(String[] args) {
		BoardFailedInit game=new BoardFailedInit("me","12345");
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setVisible(true);    
	    game.setResizable(true);
	    game.printGrid();
	}

}