package PartIV;

import java.util.Random;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.FontMetrics;

public class Board extends JFrame implements Runnable{
	private static final Color[] TILE_COLORS = {
	    Color.BLUE,   // open
	    Color.GRAY,   // ship
	    Color.WHITE,  // miss
	    Color.RED     // hit
	};
	private static final String[] directions= {"up","down","left","right"};
	
	//1=open, 2=ship, 3=miss, 4=hit
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
//	private String test[]= {"A4","G5","J1","A8","F9","C10"};
//	private int on=0;
	
	private JButton fire;
	private JTextField enter;
	private String choice;
	private String lastchoice;
	private boolean ispaused;
	private boolean itsmyturn;
	
	private ArrayList<Ship> myships=new ArrayList<Ship>();
	private ArrayList<Ship> opships=new ArrayList<Ship>();
	private ImagePanel leftboard;
	private ImagePanel rightboard;
	private Player player;
	
	public Board() {
		player=new Player("Jacinto","jg6243",777);
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {
				mygrid[x][y]=1;
				opgrid[x][y]=1;
			}
		}
		gameinprogress=true;
		itsmyturn=true;
		createships();
		setopponentships();
		setuserships();
		enter=new JTextField(5);
		enter.addActionListener(new textfieldlistener());
		fire=new JButton("Fire");
		fire.addActionListener(new addfirelistener());
		this.setBackground(Color.LIGHT_GRAY);
	    setSize(gamewidth,gameheight);
	    createpanel();
	    ispaused=false;
//	    while(gameinprogress) {
//	    	computerturn();
//	    	System.out.println("new turn now");
//	    	Thread t=new Thread(this);
//	    	t.start();
//	    	leftboard.repaint();
//	    	rightboard.repaint();
//	    }
	    Thread t=new Thread(this);
    	t.start();
    	Thread d=new Thread(new Computer());
    	d.start();
	}
	
	public void run() {
		if(gameinprogress) {
			synchronized(this) {
				while(!itsmyturn) {
					try {
						System.out.println("my thread start waiting");
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				notify();
			}
			System.out.println("my thread running again");
			myturn();
			leftboard.repaint();
			rightboard.repaint();
			System.out.println("my turn is complete");
		}
	}
	
	public class Computer implements Runnable{
		public void run() {
			if(gameinprogress) {
				synchronized(this) {
					while(itsmyturn) {
						try {
							System.out.println("computer thread start waiting");
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					notify();
				}
				System.out.println("computer thread running again");
				myturn();
				leftboard.repaint();
				rightboard.repaint();
				System.out.println("computer turn is complete");
			}
		}
	}
	
//	public void run() {
//		while(gameinprogress) {
//			
//			synchronized(this) {
//				try {
//					wait();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				while(choice==null||lastchoice==null);
//				notify();
//			}
//			myturn();
//			leftboard.repaint();
//	    	rightboard.repaint();
//	    	computerturn();
//	    	System.out.println("new turn now");
//	    	leftboard.repaint();
//	    	rightboard.repaint();
//	    }
//	}
	
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
	            //g.setColor(Color.BLUE);
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                	g.setColor(gettilecolor(mygrid[x][y],true));
	                    g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2);
	                }
	            }
	            //g.setColor(Color.GRAY);
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
	            g.drawString(player.username,xoffset + (gridwidth - fm.stringWidth(player.username)) / 2,yoffset-50);
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
	            //g.setColor(Color.BLUE);
	            for (int x = 0; x < 10; x++) {
	                for (int y = 0; y < 10; y++) {
	                	g.setColor(gettilecolor(opgrid[x][y],false));
	                    g.fillRect((x * width)+xoffset+1, (y * height)+yoffset+1, width-2, height-2);
	                }
	            }
	            //g.setColor(Color.GRAY);
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
		rightboard.add(enter,BorderLayout.SOUTH);
		rightboard.add(fire,BorderLayout.SOUTH);
		mainpan.add(leftboard);
		mainpan.add(rightboard);
		this.add(mainpan,BorderLayout.CENTER);
		rightboard.repaint();
		leftboard.repaint();
	}
	
	public void myturn() {
		timedelay(1);
		String input=choice;
		System.out.println("my turns choice is "+choice);
		//if(input!=null) {
//			String input=test[on];
			//synchronized(input) {
				int y=reverse(input.charAt(0));
				int x=Integer.parseInt((""+input.charAt(1)))-1;
				if(opgrid[x][y]==1){opgrid[x][y]=3;} //I missed
				if(opgrid[x][y]==2) {opgrid[x][y]=4;} //I hit a ship
			//}
		//}
//		this.on++;
	}
			
	public void computerturn() {
		timedelay(1);
		int x;
		int y;
		do {
			x=pickspot(10);
			y=pickspot(10);
			System.out.println("result is "+mygrid[x][y]+", picking new target");
		}while(mygrid[x][y]!=1&&mygrid[x][y]!=2);
		if(mygrid[x][y]==1) {mygrid[x][y]=3;} //computer missed
		if(mygrid[x][y]==2) {mygrid[x][y]=4;} //my ship has been hit
	}
		
	public void createships() {
		Ship destroyer=new Ship("Destroyer",2);
		Ship submarine=new Ship("Submarine",3);
		Ship cruiser=new Ship("Cruiser",3);
		Ship battleship=new Ship("Battleship",4);
		Ship carrier=new Ship("Carrier",5);
		Ship enemy_destroyer=new Ship("Destroyer",2);
		Ship enemy_submarine=new Ship("Submarine",3);
		Ship enemy_cruiser=new Ship("Cruiser",3);
		Ship enemy_battleship=new Ship("Battleship",4);
		Ship enemy_carrier=new Ship("Carrier",5);
		myships.add(destroyer);
		myships.add(submarine);
		myships.add(cruiser);
		myships.add(battleship);
		myships.add(carrier);
		opships.add(enemy_destroyer);
		opships.add(enemy_submarine);
		opships.add(enemy_cruiser);
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
	
	public void setuserships() {
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
			}
			if(dir=="down") {
				while(len>0&&row<=9) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row++;
				}
			}
			if(dir=="left") {
				while(len>0&&col>=0) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col--;
				}
			}
			if(dir=="right") {
				while(len>0&&col<=9) {
					mygrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col++;
				}
			}
		}
//		for(Ship s:myships) {
//			s.printcoords();
//			System.out.println();
//		}
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
			}
			if(dir=="down") {
				while(len>0&&row<=9) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					row++;
				}
			}
			if(dir=="left") {
				while(len>0&&col>=0) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col--;
				}
			}
			if(dir=="right") {
				while(len>0&&col<=9) {
					opgrid[col][row]=2;
					s.addcoord(new Coordinate(col,row));
					len--;
					col++;
				}
			}
		}
//		for(Ship s:opships) {
//			s.printcoords();
//			System.out.println();
//		}
	}
		
	public Color gettilecolor(int code, boolean thisismygrid) {
	    Color col=Color.BLUE;
	    //if(!thisismygrid) {return col;}
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
	
	public void timedelay(int time) {
		double start=System.currentTimeMillis();
		while(System.currentTimeMillis()<start+time*1000);
		System.out.println("wait 1 second");
	}
	
	public class textfieldlistener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String temp=choice;
			choice=enter.getText().trim();
			lastchoice=temp;
			System.out.println("the choice is "+choice);
			enter.setText("");
		}
	}
	
	public class addfirelistener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	public static void main(String[] args) {
		Board game=new Board();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setVisible(true);    
	    game.setResizable(true);
	}
}