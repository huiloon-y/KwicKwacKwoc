package sg.edu.nus.comp.cs3213;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A pipe within a data processing pipeline.
 * 
 * This is a buffered pipe that allows synchronized inter-thread communication.
 * Operations on the pipe are performed in a FIFO fashion.
 */
public class Pipe {
	// A buffer containing work units to be processed.
	private final Queue<WorkUnit> mWorkBuffer = new LinkedList<WorkUnit>();
	
	// Buffer size for the pipe.
	private final static int BUFFER_SIZE = 20;
	
	/**
	 * Writes a work unit to this pipe.
	 * If the pipe is full, this call blocks.
	 */
	public synchronized void write(WorkUnit work) throws InterruptedException {
		while (mWorkBuffer.size() > BUFFER_SIZE) {
			this.wait();
		}
		
		mWorkBuffer.add(work);
		this.notify();
	}
	
	/**
	 * Reads a work unit from this pipe.
	 * If the pipe is empty, this call blocks.
	 */
	public synchronized WorkUnit read() throws InterruptedException {
		while (mWorkBuffer.isEmpty()) {
			this.wait();
		}
		
		WorkUnit work = mWorkBuffer.poll();
		this.notify();
		return work;
	}
}
