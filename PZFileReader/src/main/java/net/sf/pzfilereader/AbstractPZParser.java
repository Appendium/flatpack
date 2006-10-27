/**
 * 
 */
package net.sf.pzfilereader;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xhensevb
 * 
 */
public abstract class AbstractPZParser implements PZParser {

    private boolean handlingShortLines = false;

    private boolean initialised = false;

    /** Map of column metadata's */
    private Map columnMD = null;

    private String dataDefinition = null;

    private InputStream dataSourceStream = null;

    private File dataSource = null;

    protected AbstractPZParser(File dataSource) {
        this.dataSource = dataSource;
    }

    protected AbstractPZParser(InputStream dataSourceStream) {
        this.dataSourceStream = dataSourceStream;
    }

    protected AbstractPZParser(File dataSource, String dataDefinition) {
        this.dataSource = dataSource;
        this.dataDefinition = dataDefinition;
    }

    protected AbstractPZParser(InputStream dataSourceStream, String dataDefinition) {
        this.dataSourceStream = dataSourceStream;
        this.dataDefinition = dataDefinition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParser#isHandlingShortLines()
     */
    public boolean isHandlingShortLines() {
        return handlingShortLines;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParser#setHandlingShortLines(boolean)
     */
    public void setHandlingShortLines(boolean handleShortLines) {
        this.handlingShortLines = handleShortLines;
    }

    public final IDataSet parse() {
        if (!initialised) {
            init();
        }
        return doParse();
    }

    protected abstract IDataSet doParse();

    protected abstract void init();

    protected void setColumnMD(final Map map) {
        columnMD = map;
    }

    protected void addToColumnMD(final Object key, final Object value) {
        if (columnMD == null) {
            columnMD = new LinkedHashMap();
        }
        columnMD.put(key, value);
    }

    protected boolean isInitialised() {
        return initialised;
    }

    protected void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected String getDataDefinition() {
        return dataDefinition;
    }

    protected void setDataDefinition(String dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    protected File getDataSource() {
        return dataSource;
    }

    protected void setDataSource(File dataSource) {
        this.dataSource = dataSource;
    }

    protected InputStream getDataSourceStream() {
        return dataSourceStream;
    }

    protected void setDataSourceStream(InputStream dataSourceStream) {
        this.dataSourceStream = dataSourceStream;
    }

    protected Map getColumnMD() {
        return columnMD;
    }

    /**
     * Adds a new error to this DataSet. These can be collected, and retreived
     * after processing
     * 
     * @param errorDesc -
     *            String description of error
     * @param lineNo -
     *            int line number error occured on
     * @param errorLevel -
     *            int errorLevel 1,2,3 1=warning 2=error 3= severe error
     */
    protected void addError(final DefaultDataSet ds, final String errorDesc, final int lineNo, final int errorLevel) {
        final DataError de = new DataError(errorDesc,lineNo,errorLevel);
        ds.addError(de);
    }
    
}
