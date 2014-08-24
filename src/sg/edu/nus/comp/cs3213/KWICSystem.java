/* Authors: Ang Hui Loon and Wong Yong Jie
 CS3213 KWIC System
 Assumptions: 
 - Program is not case sensitive (eg "the" == "THE")
 - First keyword in output will be in upper case while all other words will be in lower case 
 - Inputs will be separated by commas
 - Inputs either between double quotations or none (eg "Fast and Furious" or Fast and Furious)
*/

package sg.edu.nus.comp.cs3213;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class KWICSystem {

	public static void main (String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		String input, ignoreWords;		
		ArrayList <ArrayList<String>> listOfInput = new ArrayList<ArrayList<String>>();
		ArrayList <ArrayList<String>> listOfIgnoreWords = new ArrayList<ArrayList<String>>();
		ArrayList <String> listOfOutput = new ArrayList<String>();
		
		System.out.print("Please enter input: ");
		input = sc.nextLine();
		System.out.print("Please enter words to ignore: ");
		ignoreWords = sc.nextLine();
		
		listOfInput = indexWords(input);
		listOfIgnoreWords = indexWords(ignoreWords);
		
		listOfOutput = circularShift(listOfInput, listOfIgnoreWords);
		Collections.sort(listOfOutput);
		
		System.out.println("Output: ");
		for (int i = 0; i < listOfOutput.size(); i++) {
			System.out.println(listOfOutput.get(i));
		}
		
		sc.close();
	}
	
	// To trim, remove "" and split up each word
	private static ArrayList<ArrayList<String>> indexWords (String input) {
		ArrayList <ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		
		String[] inputTokens = input.split(",");
		for (int i = 0; i < inputTokens.length; i++) {
			inputTokens[i] = inputTokens[i].trim();
			if (inputTokens[i].startsWith("\"") && inputTokens[i].endsWith("\"")) {
				inputTokens[i] = inputTokens[i].substring(1, inputTokens[i].length()-1);
			}
			
			String[] wordCounter = inputTokens[i].split(" ");
			output.add(new ArrayList<String>());
			for (int j = 0; j < wordCounter.length; j++) {
				output.get(i).add(wordCounter[j]);
			}
		}
		return output;
	}
	
	// Check for ignore words and shift words accordingly. 
	private static ArrayList<String> circularShift (ArrayList<ArrayList<String>> listOfInput, ArrayList<ArrayList<String>> listOfIgnoreWords) {
		ArrayList <String> output = new ArrayList<String>();
		
		for (int i = 0; i < listOfInput.size(); i++) {
			for (int j = 0; j < listOfInput.get(i).size(); j++) {
				//If first word is a keyword, add it to output list and append word at the end of line
				if (!isIgnoreWord(listOfIgnoreWords, listOfInput.get(i).get(0))) {
					String outputString = listOfInput.get(i).get(0).toUpperCase() + " ";
					for (int k = 1; k < listOfInput.get(i).size(); k++) {
						outputString += listOfInput.get(i).get(k).toLowerCase() + " ";
					}
					output.add(outputString);
				} 
				//Append word to end of the line
				String temp = listOfInput.get(i).get(0);
				for (int k = 0; k < listOfInput.get(i).size()-1; k++) {
					listOfInput.get(i).set(k, listOfInput.get(i).get(k+1)); 
				}
				listOfInput.get(i).set(listOfInput.get(i).size()-1, temp);
			}
		}
		
		return output;
	}
	
	//Returns true boolean if current word is an ignore word
	private static boolean isIgnoreWord (ArrayList<ArrayList<String>> listOfIgnoreWords, String word) {
		for (int i = 0; i < listOfIgnoreWords.size(); i++) {
			for (int j = 0; j < listOfIgnoreWords.get(i).size(); j++) {
				if (listOfIgnoreWords.get(i).get(j).equalsIgnoreCase(word)) {
					return true;
				}
			}
		}
		return false;
	}
}