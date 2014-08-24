package sg.edu.nus.comp.cs3213;

/**
 * Represents a work unit in a data processing pipeline.
 * 
 * A work unit contains the data to be processed, as well as some information
 * about the work itself. For instance, the position of the work unit in the
 * stream of work units.
 */
public class WorkUnit {
	// Contents of this work unit.
	private String mData;
	
	/**
	 * Constructs a new work unit.
	 */
	WorkUnit(String data) {
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
}
