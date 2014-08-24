package sg.edu.nus.comp.cs3213;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A pipe within a data processing pipeline.
 * 
 * This is a buffered pipe that allows synchronized inter-thread communication.
 * Operations on the pipe are performed in a FIFO fashion.
 */
public class Pipe {
	// A buffer containing work units to be processed.
	private LinkedList<WorkUnit> mWorkBuffer;
	
	// Lock for reading and writing; enforces blocking behavior on the read()
	// and write() methods.
	private Lock mEmptyLock = new ReentrantLock();
	
	/**
	 * The pipe constructor.
	 */
	Pipe() {
		// Default state is locked, because there are no items in the buffer.
		mEmptyLock.lock();
	}
	
	/**
	 * Writes a work unit to this pipe.
	 * If the pipe is full, this call blocks.
	 */
	void write(WorkUnit work) {
		mWorkBuffer.add(work);
		mEmptyLock.unlock();
	}
	
	/**
	 * Reads a work unit from this pipe.
	 * If the pipe is empty, this call blocks.
	 */
	WorkUnit read() {
		if (mWorkBuffer.isEmpty()) {
			mEmptyLock.lock();
		}
		
		return mWorkBuffer.poll();
	}
}
