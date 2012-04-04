package net.sf.flatpack.writer;

/**
 * Defines options for various DelimiterWriter behaviors
 *
 * @author Paul Zepernick
 */
public class DelimiterWriterOptions {
	
	private boolean noColumnMappings;
	
	
	
	/**
	 * Returns a DelimiterWriterOptions instance
	 * 
	 * @return DelimiterWriterOptions
	 */
	public static DelimiterWriterOptions getInstance() {
		return new DelimiterWriterOptions();
	}



	/**
	 * @return the noColumnMappings
	 */
	public boolean isNoColumnMappings() {
		return noColumnMappings;
	}



	/**
	 * When this is set to true, the addRecordEntry(column, value) will throw an exception.  You
	 * must use addRecordEntry(value).
	 * 
	 * @param noColumnMappings the noColumnMappings to set
	 */
	public void setNoColumnMappings(boolean noColumnMappings) {
		this.noColumnMappings = noColumnMappings;
	}

}
