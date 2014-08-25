package sg.edu.nus.comp.cs3213;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a data processing pipeline.
 * 
 * This class should not be instantiated directly. Instead, rely on the
 * PipelineBuilder class.
 */
public class Pipeline {
	// Create a source pipe where we can feed in new work information.
	private Pipe mSourcePipe = new Pipe();
	
	// The filters in this pipeline.
	private List<Filter> mFilters = new ArrayList<Filter>();
	
	/**
	 * Private constructor to prevent direct construction.
	 */
	private Pipeline(Builder builder) {		
		// Create the filters.
		for (Class<? extends Filter> clazz : builder.mFilterClasses) {
			try {
				mFilters.add(clazz.newInstance());
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("Unable to instantiate " +
						"filter " + e.getClass().getName());
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Unable to instantiate " +
						"filter " + e.getClass().getName());
			}
		}
		
		// Set the input port to the source pipe.
		try {
			Filter filter = mFilters.get(0);
			filter.setInputPort(mSourcePipe);
		} catch (IndexOutOfBoundsException e) {
			// No filters!
			return;
		}
		
		// Create pipes for each filter.
		for (int i = 0; i < mFilters.size(); ++i) {
			Pipe pipe = new Pipe();
			
			// Connect the output for the current filter to the pipe.
			Filter currentFilter = mFilters.get(i);
			currentFilter.setOutputPort(pipe);
			
			// If there exists a next filter, connect it to the pipe.
			if (i + 1 < mFilters.size()) {
				Filter nextFilter = mFilters.get(i + 1);
				nextFilter.setInputPort(pipe);
			}
		}
	}
	
	/**
	 * Starts the pipeline.
	 */
	public void start() {
		List<Thread> threads = new LinkedList<Thread>();
		
		// Spawn a new thread to run each filter.
		for (Filter filter : mFilters) {
			Thread thread = new Thread(filter);
			thread.start();
			threads.add(thread);
		}
	}
	
	/**
	 * Pumps a work unit through the pipeline.
	 */
	public void pump(WorkUnit work) {
		mSourcePipe.write(work);
	}
	
	/**
	 * Builds a data processing pipeline.
	 */
	public static class Builder {
		// The list of filters to be included in the pipeline.
		private List<Class<? extends Filter>> mFilterClasses =
				new ArrayList<Class<? extends Filter>>();
		
		/**
		 * Appends a filter to the pipeline.
		 */
		public void append(Class<? extends Filter> filter) {
			mFilterClasses.add(filter);
		}
		
		/**
		 * Constructs the pipeline.
		 */
		public Pipeline build() {
			Pipeline pipeline = new Pipeline(this);
			return pipeline;
		}
	}

}
