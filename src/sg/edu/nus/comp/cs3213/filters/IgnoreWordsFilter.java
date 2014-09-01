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
 *     WorkUnit: Data 1
 *     WorkUnit: Data 2
 *     WorkUnit: Data ...
 *     WorkUnit: Data n
 *     WorkUnit: Empty
 *     WorkUnit: Word to Ignore 1
 *     WorkUnit: Word to Ignore 2
 *     WorkUnit: Word to Ignore ...
 *     WorkUnit: Word to Ignore n
 *     WorkUnit: Empty, last bit set
 *     
 * This filter generates work units in the following format:
 * 
 *     WorkUnit: Data 1
 *     WorkUnit: Data 2
 *     WorkUnit: Data ...
 *     WorkUnit: Data n
 *     WorkUnit: Empty, last bit set
 */
public class IgnoreWordsFilter extends Filter {
	
	// Stores the incoming data.
	private final List<String> mData = new ArrayList<String>();
	
	// Stores the list of words to ignore.
	private final List<String> mWordsToIgnore = new ArrayList<String>();
	
	// State of the filter.
	private static enum State {
		RECEIVING_DATA,
		RECEIVING_WORDS_TO_IGNORE
	};
	
	private State mState = State.RECEIVING_DATA;
	
	// Logger for this filter.
	private final static String TAG = "sg.edu.nus.comp.cs3213.filters.IgnoreWordsFilter";
	private final Logger mLogger = Logger.getLogger(TAG);

	@Override
	protected void process(WorkUnit work) {
		switch (mState) {
		case RECEIVING_DATA:
			if (!work.getData().isEmpty()) {
				mData.add(work.getData());
			} else {
				mState = State.RECEIVING_WORDS_TO_IGNORE;
			}
			
			break;
			
		case RECEIVING_WORDS_TO_IGNORE:
			if (!work.getData().isEmpty()) {
				mWordsToIgnore.add(work.getData());
			} else {
				mState = State.RECEIVING_DATA;
			}
			
			break;
		}
		
		if (work.isLast()) {
			List<WorkUnit> workUnits = new ArrayList<WorkUnit>();
			
			// Perform removal of data here.
			for (int i = 0; i < mData.size(); ++i) {
				String[] titleTokens = mData.get(i).split(" ");
				if (!isIgnoreWord(titleTokens[0], mWordsToIgnore)) {
					mLogger.finest("Added WorkUnit: " + work.getData());
					workUnits.add(new WorkUnit(mData.get(i)));
				}
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
			mData.clear();
			mWordsToIgnore.clear();
		}
	}
	
	// Returns true boolean if current word is an ignore word
	private boolean isIgnoreWord (String word, List<String> wordsToIgnore) {
		for (int i = 0; i < wordsToIgnore.size(); i++) {
			if (wordsToIgnore.get(i).equalsIgnoreCase(word)) {
				return true;
			}
		}
		
		return false;
	}

}
