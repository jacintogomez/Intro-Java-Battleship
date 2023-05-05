package battleship;

import java.util.ArrayList;

public class ArrayListTest {

	public static void main(String[] args) {
		ArrayList<Object> newArr = new ArrayList<>();
		String str = "Test message";
		int intsample = 1;
		newArr.add(str);
		newArr.add(intsample);
		
		String str2 = (String)newArr.get(0);
		int intsample2 = (int)newArr.get(1);
		
		System.out.println("Returned string: " + str2 + "\nReturned integer: " + intsample2);

	}

}
