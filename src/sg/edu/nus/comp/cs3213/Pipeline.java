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
	private final Pipe mSourcePipe = new Pipe();
	
	// The filters in this pipeline.
	private final List<Filter> mFilters = new ArrayList<Filter>();
	
	// The threads owned by the pipeline.
	private final List<Thread> mThreads = new LinkedList<Thread>();
	
	// Callback to fire when a work unit is complete.
	private OnWorkUnitCompleteCallback mOnWorkUnitCompleteCallback;
	
	// Callback to fire when a filter is created.
	private OnFilterCreateCallback mOnFilterCreateCallback;
	
	/**
	 * Private constructor to prevent direct construction.
	 */
	private Pipeline(Builder builder) {
		// Copy the callback.
		mOnWorkUnitCompleteCallback = builder.mOnWorkUnitCompleteCallback;
		mOnFilterCreateCallback = builder.mOnFilterCreateCallback;
		
		// Create the filters.
		for (Class<? extends Filter> clazz : builder.mFilterClasses) {
			try {
				Filter filter = clazz.newInstance();
				
				if (mOnFilterCreateCallback != null) {
					mOnFilterCreateCallback.onCreate(filter);
				}
				
				mFilters.add(filter);
				
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
		
		// Connect the sink pipe to the sink filter.
		Filter filter = new SinkFilter();
		mFilters.add(filter);
		
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
		// Spawn a new thread to run each filter.
		for (Filter filter : mFilters) {
			Thread thread = new Thread(filter);
			thread.start();
			mThreads.add(thread);
		}
	}

	/**
	 * Stops the pipeline.
	 */
	public void stop() {
		for (Thread thread : mThreads) {
			thread.interrupt();
		}
	}
	
	/**
	 * Pumps a work unit through the pipeline.
	 */
	public void pump(WorkUnit work) {
		try {
			mSourcePipe.write(work);
		} catch (InterruptedException e) {
			System.out.println("Caught InterruptedException in pump()");
		}
	}
	
	/**
	 * Builds a data processing pipeline.
	 */
	public static class Builder {
		// The list of filters to be included in the pipeline.
		private List<Class<? extends Filter>> mFilterClasses =
				new ArrayList<Class<? extends Filter>>();
		
		// Callback to fire when a filter is created.
		private OnFilterCreateCallback mOnFilterCreateCallback;
		
		// Callback to fire when a work unit is complete.
		private OnWorkUnitCompleteCallback mOnWorkUnitCompleteCallback;
		
		/**
		 * Appends a filter to the pipeline.
		 */
		public void append(Class<? extends Filter> filter) {
			mFilterClasses.add(filter);
		}
		
		/**
		 * Sets the creation callback for a filter.
		 * This is used to set options for a filter.
		 */
		public void setOnFilterCreate(OnFilterCreateCallback callback) {
			mOnFilterCreateCallback = callback;
		}
		
		/**
		 * Sets the completion callback for a work unit.
		 */
		public void setOnWorkUnitComplete(OnWorkUnitCompleteCallback callback) {
			mOnWorkUnitCompleteCallback = callback;
		}
		
		/**
		 * Constructs the pipeline.
		 */
		public Pipeline build() {
			Pipeline pipeline = new Pipeline(this);
			return pipeline;
		}
	}
	
	/**
	 * A special filter to receive completed work units.
	 */
	private class SinkFilter extends Filter {
		@Override
		protected WorkUnit process(WorkUnit work) {
			if (mOnWorkUnitCompleteCallback != null) {
				mOnWorkUnitCompleteCallback.onComplete(work, Pipeline.this);
			}
			
			return work;
		}
	}

	/**
	 * Callback interface for completed work units.
	 */
	public interface OnWorkUnitCompleteCallback {
		void onComplete(WorkUnit work, Pipeline pipeline);
	}
	
	/**
	 * Callback interface for filter creation.
	 */
	public interface OnFilterCreateCallback {
		void onCreate(Filter filter);
	}
}
