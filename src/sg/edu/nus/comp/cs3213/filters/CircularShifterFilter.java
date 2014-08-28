package sg.edu.nus.comp.cs3213.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import sg.edu.nus.comp.cs3213.Filter;
import sg.edu.nus.comp.cs3213.WorkUnit;

/**
 * A filter that performs circular shifts.
 * 
 * This filter accepts input in the following format:
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
 * This filter generates work units in the following format:
 * 
 *     WorkUnit: Circular Shift 1
 *     WorkUnit: Circular Shift 2
 *     WorkUnit: Circular Shift ...
 *     WorkUnit: Circular Shift n
 *     WorkUnit: Empty
 *     WorkUnit: Word to Ignore 1
 *     WorkUnit: Word to Ignore 2
 *     WorkUnit: Word to Ignore ...
 *     WorkUnit: Word to Ignore n
 *     WorkUnit: Empty, last bit set
 */
public class CircularShifterFilter extends Filter {
	
	// Stores the list of titles.
	private final List<String> mTitles = new ArrayList<String>();
	
	// Stores the list of words to ignore.
	private final List<String> mWordsToIgnore = new ArrayList<String>();
	
	// State of the filter.
	private static enum State {
		RECEIVING_TITLES,
		RECEIVING_WORDS_TO_IGNORE
	};
	
	private State mState = State.RECEIVING_TITLES;
	
	// Logger for this filter.
	private final static String TAG = "sg.edu.nus.comp.cs3213.filters.CircularShifterFilter";
	private final Logger mLogger = Logger.getLogger(TAG);

	@Override
	protected void process(WorkUnit work) {
		switch (mState) {
		case RECEIVING_TITLES:
			if (!work.getData().isEmpty()) {
				mTitles.add(work.getData());
			} else {
				mState = State.RECEIVING_WORDS_TO_IGNORE;
			}
			
			break;
			
		case RECEIVING_WORDS_TO_IGNORE:
			if (!work.getData().isEmpty()) {
				mWordsToIgnore.add(work.getData());
			} else {
				mState = State.RECEIVING_TITLES;
			}
			
			break;
		}
		
		if (work.isLast()) {
			List<WorkUnit> workUnits = new ArrayList<WorkUnit>();
			
			// Perform circular shifting here.
			for (int i = 0; i < mTitles.size(); ++i) {
				String[] titleTokens = mTitles.get(i).split(" ");
				
				for (int j = 0; j < titleTokens.length; ++j) {
					// If first word is a keyword, add it to output list and append
					// word at the end of line.
					String outputString = titleTokens[0].substring(0, 1).toUpperCase();
					if (titleTokens[0].length() > 1) {
						outputString += titleTokens[0].substring(1) + " ";
					} else {
						outputString += " ";
					}
					
					for (int k = 1; k < titleTokens.length; ++k) {
						outputString += titleTokens[k] + " ";
					}
					outputString = outputString.trim();
					
					workUnits.add(new WorkUnit(outputString));
					mLogger.finest("Created WorkUnit: " + outputString);
					
					// Append word to end of line.
					String temp = titleTokens[0];
					for (int k = 0; k < titleTokens.length - 1; ++k) {
						titleTokens[k] = titleTokens[k + 1];
					}
					titleTokens[titleTokens.length - 1] = temp;
				}
			}
			
			workUnits.add(new WorkUnit());
			
			for (String wordToIgnore : mWordsToIgnore) {
				workUnits.add(new WorkUnit(wordToIgnore));
			}
			
			WorkUnit lastWorkUnit = new WorkUnit();
			lastWorkUnit.setLast(true);
			workUnits.add(lastWorkUnit);
			
			// Write the work units.
			for (WorkUnit workUnit : workUnits) {
				try {
					getOutputPort().write(workUnit);
				} catch (InterruptedException e) {
					return;
				}
			}
			
			// Clear the state variables.
			mTitles.clear();
			mWordsToIgnore.clear();
		}
	}

}
