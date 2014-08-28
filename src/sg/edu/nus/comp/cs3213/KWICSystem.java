/* Authors: Ang Hui Loon and Wong Yong Jie
 CS3213 KWIC System
 Assumptions: 
 - Program is not case sensitive (eg "the" == "THE")
 - First keyword in output will be in upper case while all other words will be in lower case 
 - Inputs will be separated by commas
 - Inputs either between double quotations or none (eg "Fast and Furious" or Fast and Furious)
*/

package sg.edu.nus.comp.cs3213;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;

import sg.edu.nus.comp.cs3213.Pipeline.OnWorkUnitCompleteCallback;
import sg.edu.nus.comp.cs3213.filters.AlphabetizerFilter;
import sg.edu.nus.comp.cs3213.filters.CircularShifterFilter;
import sg.edu.nus.comp.cs3213.filters.InputFilter;

public class KWICSystem {

	public static void main (String[] args) throws IOException {
		setUpLogging();
		
		// Build the data processing pipeline.
		Pipeline.Builder builder = new Pipeline.Builder();
		builder.append(InputFilter.class);
		builder.append(CircularShifterFilter.class);
		builder.append(AlphabetizerFilter.class);
		builder.setOnWorkUnitComplete(new OnWorkUnitCompleteCallback() {
			@Override
			public void onComplete(WorkUnit work, Pipeline pipeline) {
				if (!work.getData().isEmpty()) {
					System.out.println(work.getData());
				}
				
				if (work.isLast()) {
					pipeline.stop();
				}
			}
		});
		
		final Pipeline pipeline = builder.build();
		pipeline.start();
	}
	
	private static void setUpLogging() {
		String propertiesFile = System.getProperty("user.dir") + "/logging.properties";
		
		try {
			LogManager.getLogManager().readConfiguration(
					new FileInputStream(propertiesFile));
		} catch (SecurityException e) {
			System.err.println("Got SecurityException while setting up logging.");
		} catch (FileNotFoundException e) {
			System.err.println("Got FileNotFoundException while setting up logging.");
		} catch (IOException e) {
			System.err.println("Got IOException while setting up logging.");
		}
	}
}