/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting FlatPack.
 *
 * Based in London, we are world leaders in the design and development
 * of bespoke applications for the securities financing markets.
 *
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 74 2006-10-24 22:19:05Z benoitx $
 *
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.flatpack;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.flatpack.util.ParserUtils;
import net.sf.flatpack.xml.MetaData;

/**
 * @author xhensevb
 * @author zepernick
 *
 */
public abstract class AbstractParser implements Parser {

    private boolean handlingShortLines = false;
    private boolean ignoreExtraColumns = false;
    private boolean columnNamesCaseSensitive = false;
    private boolean initialised = false;
    private boolean ignoreParseWarnings = false;
    private boolean nullEmptyStrings = false;

    /** Map of column metadata's */
    //    private Map columnMD = null;
    private MetaData pzMetaData = null;
    private String dataDefinition = null;
    private Reader dataSourceReader = null;
    private List readersToClose = null;

    protected AbstractParser(final Reader dataSourceReader) {
        this.dataSourceReader = dataSourceReader;
    }

    protected AbstractParser(final Reader dataSourceReader, final String dataDefinition) {
        this.dataSourceReader = dataSourceReader;
        this.dataDefinition = dataDefinition;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParser#isHandlingShortLines()
     */
    public boolean isHandlingShortLines() {
        return handlingShortLines;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParser#setHandlingShortLines(boolean)
     */
    public void setHandlingShortLines(final boolean handleShortLines) {
        this.handlingShortLines = handleShortLines;
    }

    public boolean isIgnoreExtraColumns() {
        return ignoreExtraColumns;
    }

    public void setIgnoreExtraColumns(final boolean ignoreExtraColumns) {
        this.ignoreExtraColumns = ignoreExtraColumns;
    }

    public final DataSet parse() {
        if (!initialised) {
            init();
        }
        return doParse();
    }

    protected abstract DataSet doParse();

    protected abstract void init();

    /**
     * @deprecated
     */
    //    protected void setColumnMD(final Map map) {
    //        columnMD = map;
    //    }
    //this is used for backward compatability.  We are instantiating Readers from
    //InputStream and File from previous versions. Close out any Readers in the
    //readersToClose list.  This can be removed after we remove the deprecated methods
    protected void closeReaders() throws IOException {
        if (readersToClose != null) {
            final Iterator readersToCloseIt = readersToClose.iterator();
            while (readersToCloseIt.hasNext()) {
                final Reader r = (Reader) readersToCloseIt.next();
                r.close();
            }
        }
    }

    //adds a reader to the close list.  the list will be processed after parsing is
    //completed.
    protected void addToCloseReaderList(final Reader r) {
        if (readersToClose == null) {
            readersToClose = new ArrayList();
        }
        readersToClose.add(r);
    }

    protected void addToMetaData(final List columns) {
        if (pzMetaData == null) {
            pzMetaData = new MetaData(columns, ParserUtils.buidColumnIndexMap(columns, this));
        } else {
            pzMetaData.setColumnsNames(columns);
            pzMetaData.setColumnIndexMap(ParserUtils.buidColumnIndexMap(columns, this));
        }
    }

    //    protected void addToColumnMD(final Object key, final Object value) {
    //        if (columnMD == null) {
    //            columnMD = new LinkedHashMap();
    //        }
    //        columnMD.put(key, value);
    //    }

    protected boolean isInitialised() {
        return initialised;
    }

    protected void setInitialised(final boolean initialised) {
        this.initialised = initialised;
    }

    protected String getDataDefinition() {
        return dataDefinition;
    }

    protected void setDataDefinition(final String dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    /*  protected File getDataSource() {
     return dataSource;
     }

     protected void setDataSource(final File dataSource) {
     this.dataSource = dataSource;
     }

     protected InputStream getDataSourceStream() {
     return dataSourceStream;
     }

     protected void setDataSourceStream(final InputStream dataSourceStream) {
     this.dataSourceStream = dataSourceStream;
     }*/

    //    protected Map getColumnMD() {
    //        return columnMD;
    //    }
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
        if (errorLevel == 1 && isIgnoreParseWarnings()) {
            //user has selected to not log warnings in the parser
            return;
        }
        final DataError de = new DataError(errorDesc, lineNo, errorLevel);
        ds.addError(de);
    }

    /**
     * @return the dataSourceReader
     */
    protected Reader getDataSourceReader() {
        return dataSourceReader;
    }

    /**
     * @param dataSourceReader the dataSourceReader to set
     */
    protected void setDataSourceReader(final Reader dataSourceReader) {
        this.dataSourceReader = dataSourceReader;
    }

    public boolean isColumnNamesCaseSensitive() {
        return columnNamesCaseSensitive;
    }

    public void setColumnNamesCaseSensitive(final boolean columnNamesCaseSensitive) {
        this.columnNamesCaseSensitive = columnNamesCaseSensitive;
    }

    public boolean isIgnoreParseWarnings() {
        return ignoreParseWarnings;
    }

    public void setIgnoreParseWarnings(final boolean ignoreParseWarnings) {
        this.ignoreParseWarnings = ignoreParseWarnings;
    }

    public boolean isNullEmptyStrings() {
        return nullEmptyStrings;
    }

    public void setNullEmptyStrings(final boolean nullEmptyStrings) {
        this.nullEmptyStrings = nullEmptyStrings;
    }

    public MetaData getPzMetaData() {
        return pzMetaData;
    }

    public void setPzMetaData(final MetaData pzMap) {
        this.pzMetaData = pzMap;
    }

}
