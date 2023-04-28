package PartIV;

import java.util.*;

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
	
//	public static void main(String[] args) {
//		Double[] nums={2.4,55.6,90.12,26.6};
//        Set<Double> set=new HashSet<>(Arrays.asList(nums));
//        System.out.println("count is "+set.stream().filter(e->e>60).count());
//	}
	
}