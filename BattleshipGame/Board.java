package PartIV;

import java.util.Random;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Font;

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
	
	private ArrayList<Ship> myships=new ArrayList<Ship>();
	private ArrayList<Ship> opships=new ArrayList<Ship>();
	private ImagePanel leftboard;
	private ImagePanel rightboard;
	
	public Board() {
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {
				mygrid[x][y]=1;
				opgrid[x][y]=1;
			}
		}
//		mygrid[5][5]=2;
//		mygrid[6][3]=3;
//		mygrid[1][1]=4;
//		opgrid[3][4]=2;
//		opgrid[7][7]=3;
//		opgrid[9][9]=4;
		//mygrid[0][9]=4;
		gameinprogress=true;
		createships();
		setopponentships();
		setuserships();
		JFrame frame=new JFrame();
		this.setBackground(Color.LIGHT_GRAY);
	    setSize(gamewidth,gameheight);
	    createpanel();
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
	    
	}
	
	public void run() {
		while(gameinprogress) {
	    	computerturn();
	    	System.out.println("new turn now");
	    	leftboard.repaint();
	    	rightboard.repaint();
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
	        }
	    };
		//        JPanel centerboard=new JPanel();
		//centerboard.add(rightboard,BorderLayout.WEST);
		//centerboard.add(leftboard,BorderLayout.EAST);
		JPanel mainpan=new JPanel();
		mainpan.setLayout(new GridLayout(1,2));
		mainpan.add(leftboard);
		mainpan.add(rightboard);
		this.add(mainpan,BorderLayout.CENTER);
		rightboard.repaint();
		leftboard.repaint();
	}
	
	public void computerturn() {
		timedelay(1000);
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
			System.out.println("direction is "+dir+" start at ["+col+"] ["+row+"]");
			System.out.println("this has "+len+" holes");
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
		for(Ship s:myships) {
			s.printcoords();
			System.out.println();
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
//			System.out.println("direction is "+dir+" start at ["+col+"] ["+row+"]");
//			System.out.println("this has "+len+" holes");
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
		for(Ship s:opships) {
			s.printcoords();
			System.out.println();
		}
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
	
	public void timedelay(int time) {
		double start=System.currentTimeMillis();
		while(System.currentTimeMillis()<start+time);
		System.out.println("wait 1 second");
	}
	
	public static void main(String[] args) {
		Board game=new Board();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setVisible(true);    
	    game.setResizable(true);
	}
}