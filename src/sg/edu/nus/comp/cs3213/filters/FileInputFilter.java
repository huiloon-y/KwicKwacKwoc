package sg.edu.nus.comp.cs3213.filters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import sg.edu.nus.comp.cs3213.Filter;
import sg.edu.nus.comp.cs3213.WorkUnit;

public class FileInputFilter extends Filter {
	
	// Logger for this filter.
	private final static String TAG = "sg.edu.nus.comp.cs3213.filters.FileInputFilter";
	private final Logger mLogger = Logger.getLogger(TAG);
	
	@Override
	public void run() {
		// Read titles from file.
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("/home/yjwong/Documents/KwicKwacKwoc/large_input.list"));
			String titles = br.readLine();
			String wordsToIgnore = br.readLine();
			br.close();
			
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
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
