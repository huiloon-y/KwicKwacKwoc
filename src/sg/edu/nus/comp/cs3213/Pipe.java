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
	private Queue<WorkUnit> mWorkBuffer = new LinkedList<WorkUnit>();
	
	// Semaphore for reading and writing; enforces blocking behavior on the
	// read() and write() methods.
	private Semaphore mSemaphore = new Semaphore(20, true);
	
	/**
	 * Writes a work unit to this pipe.
	 * If the pipe is full, this call blocks.
	 */
	public synchronized void write(WorkUnit work) {
		try {
			mSemaphore.acquire();
			mWorkBuffer.add(work);
			this.notify();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a work unit from this pipe.
	 * If the pipe is empty, this call blocks.
	 */
	public synchronized WorkUnit read() {
		if (mWorkBuffer.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		mSemaphore.release();
		return mWorkBuffer.poll();
	}
}
