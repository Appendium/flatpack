package net.sf.flatpack.writer;

/**
 * Defines options for various Writer behaviours
 *
 * @author Paul Zepernick
 */
public class WriterOptions {

    private boolean autoPrintHeader = true;
    private String lineSeparator = System.lineSeparator();

    /**
     * Returns a DelimiterWriterOptions instance
     *
     * @return DelimiterWriterOptions
     */
    public static WriterOptions getInstance() {
        return new WriterOptions();
    }

    /**
     * @return the noColumnMappings
     */
    public boolean isAutoPrintHeader() {
        return autoPrintHeader;
    }

    /**
     * When this is set to true, the addRecordEntry(column, value) will throw an exception.  You
     * must use addRecordEntry(value).
     *
     * @param autoPrintHeader the autoPrintHeader to set
     */
    public WriterOptions autoPrintHeader(final boolean autoPrintHeader) {
        this.autoPrintHeader = autoPrintHeader;
        return this;
    }

    /**
     * Get the current line separator. Default is the system line separator.
     * @return
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Set the line leperator.
     * @param lineSeparator the line separator
     * @return
     */
    public WriterOptions setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }
}
