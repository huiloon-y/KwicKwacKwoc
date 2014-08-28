package sg.edu.nus.comp.cs3213.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import sg.edu.nus.comp.cs3213.Filter;
import sg.edu.nus.comp.cs3213.WorkUnit;

/**
 * A filter that performs sorting.
 * 
 * This filter accepts input in the following format:
 * 
 *     WorkUnit: Data 1
 *     WorkUnit: Data 2
 *     WorkUnit: Data ...
 *     WorkUnit: Data n
 *     WorkUnit: Empty, last bit set
 *
 * This filter generates work units in the following format:
 *     
 *     WorkUnit: Data 1
 *     WorkUnit: Data 2
 *     WorkUnit: Data ...
 *     WorkUnit: Data n
 *     WorkUnit: Empty, last bit set
 * 
 */
public class AlphabetizerFilter extends Filter {
	
	// Stores the items to be alphabetized.
	private final List<WorkUnit> mData = new ArrayList<WorkUnit>();
	
	// Logger for this filter.
	private final static String TAG = "sg.edu.nus.comp.cs3213.filters.AlphabetizerFilter";
	private final Logger mLogger = Logger.getLogger(TAG);

	@Override
	protected void process(WorkUnit work) {
		if (!work.getData().isEmpty()) {
			mData.add(work);
		}
		
		if (work.isLast()) {
			Collections.sort(mData);
			
			// Write each work unit to the output.
			for (WorkUnit workUnit : mData) {
				try {
					getOutputPort().write(workUnit);
					mLogger.finest("Wrote WorkUnit: " + workUnit.getData());
				} catch (InterruptedException e) {
					return;
				}	
			}
			
			mData.clear();
		}
	}

}
