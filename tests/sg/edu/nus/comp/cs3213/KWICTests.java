package sg.edu.nus.comp.cs3213;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sg.edu.nus.comp.cs3213.Pipeline.OnWorkUnitCompleteCallback;
import sg.edu.nus.comp.cs3213.filters.AlphabetizerFilter;
import sg.edu.nus.comp.cs3213.filters.CircularShifterFilter;
import sg.edu.nus.comp.cs3213.filters.IgnoreWordsFilter;

public class KWICTests {

	@Test
	public void test() {
		// Build the data processing pipeline.
		Pipeline.Builder builder = new Pipeline.Builder();
		builder.append(CircularShifterFilter.class);
		builder.append(IgnoreWordsFilter.class);
		builder.append(AlphabetizerFilter.class);
		builder.setOnWorkUnitComplete(new OnWorkUnitCompleteCallback() {
			// Collects all the output of the pipeline.
			private final List<String> mOutput = new ArrayList<String>();
			
			@Override
			public void onComplete(WorkUnit work, Pipeline pipeline) {
				if (!work.getData().isEmpty()) {
					mOutput.add(work.getData());
				}
				
				if (work.isLast()) {
					assertEquals("DAY after Tomorrow The", mOutput.get(0));
					assertEquals("FAST and Furious", mOutput.get(1));
					assertEquals("FURIOUS Fast and", mOutput.get(2));
					assertEquals("MAN of Steel", mOutput.get(3));
					assertEquals("STEEL Man of", mOutput.get(4));
					assertEquals("TOMORROW The Day after", mOutput.get(5));
					
					pipeline.stop();
				}
			}
		});
		
		final Pipeline pipeline = builder.build();
		pipeline.start();
		
		// Create the test data.
		List<String> titles = new ArrayList<String>();
		titles.add("The Day after Tomorrow");
		titles.add("Fast and Furious");
		titles.add("Man of Steel");
		
		List<String> wordsToIgnore = new ArrayList<String>();
		wordsToIgnore.add("is");
		wordsToIgnore.add("the");
		wordsToIgnore.add("of");
		wordsToIgnore.add("and");
		wordsToIgnore.add("as");
		wordsToIgnore.add("a");
		wordsToIgnore.add("after");
		
		// Generate the work units.
		List<WorkUnit> workUnits = new ArrayList<WorkUnit>();
		for (String title : titles) {
			workUnits.add(new WorkUnit(title));
		}
		
		workUnits.add(new WorkUnit());
		
		for (String wordToIgnore : wordsToIgnore) {
			workUnits.add(new WorkUnit(wordToIgnore));
		}
		
		WorkUnit lastWorkUnit = new WorkUnit();
		lastWorkUnit.setLast(true);
		workUnits.add(lastWorkUnit);
		
		// Pump the test data into the pipeline.
		for (WorkUnit workUnit : workUnits) {
			pipeline.pump(workUnit);
		}
	}

}
