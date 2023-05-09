package PartIV;

import java.util.*;

import javax.swing.JFrame;

public class Player{
	String username;
	String password;
	int id;
	int numberofwins;
	
	public Player(String username,String password,int id) {
		this(username,password,id,0);
	}
	
	public Player(String username,String password,int id,int numberofwins) {
		this.username=username;
		this.password=password;
		this.id=id;
		this.numberofwins=numberofwins;
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
//		int myhitsleft=0,ophitsleft=0;
//		int l=10;
//		String username="Jacinto Rigal";
//		String password="jg777";
//		int opgrid[][]= {
//				{2,2,2,1,1,1,1,1,1,1},
//				{1,2,2,1,1,1,1,1,1,1},
//				{1,1,1,1,1,1,1,3,1,1},
//				{1,1,1,2,1,1,1,1,1,1},
//				{1,1,1,2,1,1,1,1,1,1},
//				{1,1,1,2,1,1,1,1,1,1},
//				{3,1,1,1,1,1,1,1,1,1},
//				{1,1,1,1,4,4,4,2,1,1},
//				{1,3,1,1,1,1,1,3,3,1},
//				{1,1,1,2,2,2,2,2,1,1},
//		};
//		int mygrid[][]={
//				{1,1,1,1,1,1,1,1,1,1},
//				{1,1,1,1,1,4,1,1,2,1},
//				{1,1,1,1,1,2,1,1,2,1},
//				{1,1,1,1,1,1,1,1,2,1},
//				{1,2,2,2,1,1,1,1,2,1},
//				{2,1,1,1,1,1,1,1,2,1},
//				{4,1,1,1,1,4,2,2,2,1},
//				{4,1,1,3,1,1,1,1,1,3},
//				{1,1,1,1,3,1,1,1,3,1},
//				{1,1,1,1,1,1,3,1,1,1},
//		};
//		mygrid=flip(mygrid);
//		opgrid=flip(opgrid);
//		ArrayList<Ship> myships=new ArrayList<Ship>();
//		ArrayList<Ship> opships=new ArrayList<Ship>();
//		ArrayList<Coordinate> cords=new ArrayList<Coordinate>();
//		Coordinate c1=new Coordinate(7,4);
//		Coordinate c2=new Coordinate(7,5);
//		Coordinate c3=new Coordinate(7,6);
//		Coordinate c4=new Coordinate(7,7);
//		cords.add(c1);
//		cords.add(c2);
//		cords.add(c3);
//		cords.add(c4);
//		Ship destroyer=new Ship("Destroyer",3);
//		Ship submarine=new Ship("Submarine",3);
//		Ship patrol=new Ship("Patrol Boat",2);
//		Ship battleship=new Ship("Battleship",4);
//		Ship carrier=new Ship("Carrier",5);
//		Ship enemy_destroyer=new Ship("Destroyer",3);
//		Ship enemy_submarine=new Ship("Submarine",3);
//		Ship enemy_patrol=new Ship("Patrol Boat",2);
//		Ship enemy_battleship=new Ship("Battleship",4,3,true);
//		Ship enemy_carrier=new Ship("Carrier",5);
//		enemy_battleship.coords=cords;
//		myships.add(destroyer);
//		myships.add(submarine);
//		myships.add(patrol);
//		myships.add(battleship);
//		myships.add(carrier);
//		opships.add(enemy_destroyer);
//		opships.add(enemy_submarine);
//		opships.add(enemy_patrol);
//		opships.add(enemy_battleship);
//		opships.add(enemy_carrier);
//		
//		for(int x=0;x<l;x++) {
//			for(int y=0;y<l;y++) {
//				if(mygrid[x][y]==4) {myhitsleft++;};
//				if(opgrid[x][y]==4) {ophitsleft++;};
//			}
//		}
//		Board game=new Board(username,password,mygrid,opgrid,myships,opships,myhitsleft,ophitsleft);
		Board game=new Board("jack","jill");
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setVisible(true);    
	    game.setResizable(true);
	}
	
}