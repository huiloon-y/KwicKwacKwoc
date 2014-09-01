package sg.edu.nus.comp.cs3213;

/**
 * Represents a work unit in a data processing pipeline.
 * 
 * A work unit contains the data to be processed, as well as some information
 * about the work itself. For instance, the position of the work unit in the
 * stream of work units.
 */
public class WorkUnit implements Comparable<WorkUnit> {
	// Contents of this work unit.
	private String mData;
	
	// Is this work unit the last one?
	private boolean mIsLast = false;
	
	/**
	 * Constructs a new work unit.
	 */
	public WorkUnit() {
		this.setData(new String());
	}
	
	public WorkUnit(String data) {
		this.setData(data);
	}
	
	/**
	 * Sets the data contained within this work unit.
	 */
	public void setData(String data) {
		mData = data;
	}
	
	/**
	 * Returns the data contained within this work unit.
	 */
	public String getData() {
		return mData;
	}
	
	/**
	 * Is this work unit the last one?
	 */
	public boolean isLast() {
		return mIsLast;
	}
	
	/**
	 * Designates this work unit to be the last work unit.
	 */
	public void setLast(boolean last) {
		mIsLast = last;
	}

	@Override
	public int compareTo(WorkUnit work) {
		return this.mData.compareTo(work.mData);
	}
}
