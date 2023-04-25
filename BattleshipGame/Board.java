package PartIV;



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

public class Board extends JFrame{
	private static final Color[] TILE_COLORS = {
	    Color.BLUE,   // open
	    Color.GRAY,   // ship
	    Color.WHITE,  // miss
	    Color.RED     // hit
	};
	
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
	
	private ArrayList<Ship> myships=new ArrayList<Ship>();
	private ArrayList<Ship> opships=new ArrayList<Ship>();
	private ImagePanel leftboard;
	private ImagePanel rightboard;
	
	public Board() {
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {
				mygrid[x][y]=4;
				opgrid[x][y]=4;
			}
		}
//		mygrid[5][5]=2;
//		mygrid[6][3]=3;
//		mygrid[1][1]=4;
//		opgrid[3][4]=2;
//		opgrid[7][7]=3;
//		opgrid[9][9]=4;
		initializeships();
		JFrame frame=new JFrame();
		this.setBackground(Color.LIGHT_GRAY);
		    setSize(gamewidth,gameheight);
		    createpanel();
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
	
	public void initializeships() {
		//Ship destroyer=new Ship("Destroyer",2,true);
		//Ship submarine=new Ship("Submarine",3,true);
		//Ship cruiser=new Ship("Cruiser",3,true);
		//Ship battleship=new Ship("Battleship",4,true);
		//Ship carrier=new Ship("Carrier",5,true);
		//Ship enemy_destroyer=new Ship("Destroyer",2,true);
		//Ship enemy_submarine=new Ship("Submarine",3,true);
		//Ship enemy_cruiser=new Ship("Cruiser",3,true);
		//Ship enemy_battleship=new Ship("Battleship",4,true);
		//Ship enemy_carrier=new Ship("Carrier",5,true);
		////myships.add(destroyer);
		////myships.add(submarine);
		////myships.add(cruiser);
		////myships.add();
		////myships.add();
		////opships.add();
		////opships.add();
		////opships.add();
		////opships.add();
		////opships.add();
	}
	
	public Color gettilecolor(int code, boolean thisismygrid) {
	    //int tileState = isMyGrid ? mygrid[x][y] : opgrid[x][y];
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
	
	public static void main(String[] args) {
		Board game=new Board();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setVisible(true);    
	    game.setResizable(true);
	}
}
