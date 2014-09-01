package sg.edu.nus.comp.cs3213;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

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
	
	// Semaphore for reading and writing; enforces blocking behavior on the
	// read() and write() methods.
	private final Semaphore mSemaphore = new Semaphore(BUFFER_SIZE, true);
	
	/**
	 * Writes a work unit to this pipe.
	 * If the pipe is full, this call blocks.
	 */
	public synchronized void write(WorkUnit work) throws InterruptedException {
		mSemaphore.acquire();
		mWorkBuffer.add(work);
		this.notify();
	}
	
	/**
	 * Reads a work unit from this pipe.
	 * If the pipe is empty, this call blocks.
	 */
	public synchronized WorkUnit read() throws InterruptedException {
		if (mWorkBuffer.isEmpty()) {
			this.wait();
		}
		
		mSemaphore.release();
		return mWorkBuffer.poll();
	}
}
