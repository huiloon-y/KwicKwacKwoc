package sg.edu.nus.comp.cs3213;

/**
 * A filter base class, representing a filter within a data processing pipeline.
 * 
 * The filter runs on a separate thread so that connected filters within the
 * pipeline can run concurrently.
 */
public abstract class Filter implements Runnable {
	// The input port of the filter.
	private Pipe mInputPort;
	
	// The output port of the filter.
	private Pipe mOutputPort;
	
	/**
	 * Sets the input port of the filter.
	 */
	protected void setInputPort(Pipe inputPort) {
		mInputPort = inputPort;
	}
	
	/**
	 * Sets the output port of the filter.
	 */
	protected void setOutputPort(Pipe outputPort) {
		mOutputPort = outputPort;
	}
	
	/**
	 * Returns the input port of the filter.
	 */
	protected Pipe getInputPort() {
		return mInputPort;
	}
	
	/**
	 * Returns the output port of the filter.
	 */
	protected Pipe getOutputPort() {
		return mOutputPort;
	}
	
	/**
	 * Method to be overridden that does the actual work.
	 */
	abstract protected void process(WorkUnit work);

	/**
	 * Implements the run() method of the threading system. This takes every
	 * input in the input port, processes it, and puts the results in the
	 * output port.
	 */
	@Override
	public void run() {
		while (true) {
			if (mInputPort == null || mOutputPort == null) {
				throw new IllegalStateException("No input port was set for " +
						"this filter. An input port must be set before the " +
						"filter is invoked.");
			}
			
			try {
				WorkUnit work = mInputPort.read();
				this.process(work);
			} catch (InterruptedException e) {
				// We got interrupted, kill the thread.
				return;
			}
		}
	}
}
