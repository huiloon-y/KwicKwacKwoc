package sg.edu.nus.comp.cs3213.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import sg.edu.nus.comp.cs3213.Filter;
import sg.edu.nus.comp.cs3213.WorkUnit;

/**
 * A filter that retrieves input data from standard input.
 * 
 * This filter does not accept input from the pipe.
 * This filter generates work units in the following format:
 * 
 *     WorkUnit: Title 1
 *     WorkUnit: Title 2
 *     WorkUnit: Title ...
 *     WorkUnit: Title n
 *     WorkUnit: Empty
 *     WorkUnit: Word to Ignore 1
 *     WorkUnit: Word to Ignore 2
 *     WorkUnit: Word to Ignore ...
 *     WorkUnit: Word to Ignore n
 *     WorkUnit: Empty, last bit set
 * 
 * When the last bit is set on the WorkUnit, this filter resets itself to
 * the initial state and writes the generated work units to the output pipe.
 */
public class InputFilter extends Filter {

	// Prompt messages.
	private final static String MSG_TITLE_PROMPT = "Please input the list of titles: ";
	private final static String MSG_WORDS_TO_IGNORE_PROMPT = "Please input the list of words to ignore: ";
	
	// Logger for this filter.
	private final static String TAG = "sg.edu.nus.comp.cs3213.filters.InputFilter";
	private final Logger mLogger = Logger.getLogger(TAG);
	
	@Override
	public void run() {
		String titles, wordsToIgnore;
		
		// Read titles from standard input.
		Scanner sc = new Scanner(System.in);
		System.out.print(MSG_TITLE_PROMPT);
		titles = sc.nextLine();
		System.out.print(MSG_WORDS_TO_IGNORE_PROMPT);
		wordsToIgnore = sc.nextLine();
		sc.close();
		
		// Parse the comma-delimited list of words.
		List<String> listOfTitles = parseInput(titles); 
		List<String> listOfWordsToIgnore = parseInput(wordsToIgnore);
		
		// Create the work units.
		List<WorkUnit> workUnits = new ArrayList<WorkUnit>();
		for (int i = 0; i < listOfTitles.size(); ++i) {
			String title  = listOfTitles.get(i);
			
			mLogger.finest("Created WorkUnit for titles: " + title);
			WorkUnit work = new WorkUnit(title);
			workUnits.add(work);
		}
		
		workUnits.add(new WorkUnit());
		
		for (int i = 0; i < listOfWordsToIgnore.size(); ++i) {
			String wordToIgnore = listOfWordsToIgnore.get(i);
			
			mLogger.finest("Created WorkUnit for wordsToIgnore: " + wordToIgnore);
			WorkUnit work = new WorkUnit(wordToIgnore);
			workUnits.add(work);
		}
		
		WorkUnit lastWorkUnit = new WorkUnit();
		lastWorkUnit.setLast(true);
		workUnits.add(lastWorkUnit);
		
		// Write work units to the output port.
		for (WorkUnit work : workUnits) {
			try {
				getOutputPort().write(work);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	@Override
	protected void process(WorkUnit work) {
		return;
	}
	
	private List<String> parseInput(String input) {
		String[] inputTokens = input.split(",");
		
		for (int i = 0; i < inputTokens.length; i++) {
			inputTokens[i] = inputTokens[i].trim();
			if (inputTokens[i].startsWith("\"") && inputTokens[i].endsWith("\"")) {
				inputTokens[i] = inputTokens[i].substring(1, inputTokens[i].length() - 1);
			}
		}
		
		return Arrays.asList(inputTokens);
	}

}
