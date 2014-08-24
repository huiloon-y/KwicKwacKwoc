package sg.edu.nus.comp.cs3213;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a data processing pipeline.
 * 
 * This class should not be instantiated directly. Instead, rely on the
 * PipelineBuilder class.
 */
public class Pipeline {
	// Remember the source pipe so we can feed in more data in case it's
	// needed later.
	private Pipe mSourcePipe = new Pipe();
	
	// The builder used to construct this pipeline.
	private Builder mBuilder;
	
	/**
	 * Private constructor to prevent direct construction.
	 */
	private Pipeline(Builder builder) {
		// Save the builder.
		mBuilder = builder;
		
		// Set the input port to the source pipe.
		try {
			Filter filter = builder.mFilters.get(0);
			filter.setInputPort(mSourcePipe);
		} catch (IndexOutOfBoundsException e) {
			// No filters!
			return;
		}
		
		for (int i = 0; i < builder.mFilters.size(); ++i) {
			// Create new pipe for linking filters together.
			Pipe pipe = new Pipe();
			
			// Connect the output for the current filter to the pipe.
			Filter currentFilter = builder.mFilters.get(i);
			currentFilter.setOutputPort(pipe);
			
			// If there exists a next filter, connect it to the pipe.
			if (i + 1 < builder.mFilters.size()) {
				Filter nextFilter = builder.mFilters.get(i + 1);
				nextFilter.setInputPort(pipe);
			}
		}
	}
	
	/**
	 * Starts the pipeline.
	 */
	public void start() {
		for (Filter filter : mBuilder.mFilters) {
			filter.run();
		}
	}
	
	/**
	 * Builds a data processing pipeline.
	 */
	public static class Builder {
		// The list of filters to be included in the pipeline.
		private List<Filter> mFilters = new ArrayList<Filter>();
		
		/**
		 * Appends a filter to the pipeline.
		 */
		public void append(Filter filter) {
			mFilters.add(filter);
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
