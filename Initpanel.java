package PartIV;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Initpanel extends JFrame{
	
	JLabel display;
	JRadioButton aButton;
	JRadioButton bButton;
	JRadioButton cButton;
	JRadioButton dButton;
	JRadioButton eButton;
	JRadioButton fButton;
	JRadioButton gButton;
	JRadioButton hButton;
	JRadioButton iButton;
	JRadioButton jButton;
	JRadioButton Button1;
	JRadioButton Button2;
	JRadioButton Button3;
	JRadioButton Button4;
	JRadioButton Button5;
	JRadioButton Button6;
	JRadioButton Button7;
	JRadioButton Button8;
	JRadioButton Button9;
	JRadioButton Button10;
	JRadioButton upButton;
	JRadioButton downButton;
	JRadioButton leftButton;
	JRadioButton rightButton;
	public String shipname;
	public char number;
	public char letter;
	public String direction;
	
	public Initpanel(String shipname) {
		this.shipname=shipname;
		display=new JLabel(shipname);
		JPanel toppanel=new JPanel();
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
		toppanel.add(display);
		this.add(toppanel,BorderLayout.NORTH);
		this.add(panel);
		setSize(300,300);
		
	}
	
	public class pressa implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button a");
			letter='a';
		}
	}
	
	public class pressb implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button b");
			letter='b';
		}
	}
	
	public class pressc implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button c");
			letter='c';
		}
	}
	
	public class pressd implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button d");
			letter='d';
		}
	}
	
	public class presse implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button e");
			letter='e';
		}
	}
	
	public class pressf implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button f");
			letter='f';
		}
	}
	
	public class pressg implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button g");
			letter='g';
		}
	}
	
	public class pressh implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button h");
			letter='h';
		}
	}
	
	public class pressi implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button i");
			letter='i';
		}
	}
	
	public class pressj implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button j");
			letter='j';
		}
	}
	
	public class press1 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 1");
			number='0';
		}
	}
	
	public class press2 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 2");
			number='1';
		}
	}
	
	public class press3 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 3");
			number='2';
		}
	}
	
	public class press4 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 4");
			number='3';
		}
	}
	
	public class press5 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 5");
			number='4';
		}
	}
	
	public class press6 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 6");
			number='5';
		}
	}
	
	public class press7 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 7");
			number='6';
		}
	}
	
	public class press8 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 8");
			number='7';
		}
	}
	
	public class press9 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 9");
			number='8';
		}
	}
	
	public class press10 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked radio button 10");
			number='9';
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
	
	public static void main(String[] args) {
		JFrame frame = new Initpanel("Name of Ship");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); 
	}
}