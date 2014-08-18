// Author: Ang Hui Loon
// CS3213 KWIC System
// Keywords to ignore: is, the, of, and, as, a, after

import java.io.IOException;
import java.util.*;


public class KWIC_System {

	public static void main (String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		String input, ignoreWords;		
		ArrayList <ArrayList<String>> listOfInput = new ArrayList<ArrayList<String>>();
		
		System.out.print("Please enter input: ");
		input = sc.nextLine();
		System.out.print("Please enter words to ignore: ");
		ignoreWords = sc.nextLine();
		listOfInput = indexInput(input);
		circularShift(listOfInput);
		
		for (int i = 0; i < listOfInput.size(); i++) {
			for (int j = 0; j < listOfInput.get(i).size(); j++) {
				System.out.print(listOfInput.get(i).get(j) + " ");
			}
			System.out.println();
		}
		
		sc.close();
	}
	
	// To trim, remove "" and split each word into 1 arraylist
	private static ArrayList<ArrayList<String>> indexInput (String input) {
		ArrayList <ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		
		String[] inputTokens = input.split(",");
		for (int i = 0; i < inputTokens.length; i++) {
			inputTokens[i] = inputTokens[i].trim();
			inputTokens[i] = inputTokens[i].substring(1, inputTokens[i].length()-1);
			String[] wordCounter = inputTokens[i].split(" ");
			for (int j = 0; j < wordCounter.length; j++) {
				output.add(new ArrayList<String>());
				output.get(i).add(wordCounter[j]);
			}
		}
		
		return output;
	}
	
	// Check for ignore words and shift words accordingly
	private static ArrayList<ArrayList<String>> circularShift (ArrayList<ArrayList<String>> listOfInput) {
		ArrayList <ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		
		
		
		return output;
	}
}