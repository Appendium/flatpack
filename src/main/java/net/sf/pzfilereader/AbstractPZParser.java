/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting PZFileReader.
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

    protected AbstractPZParser(final File dataSource) {
        this.dataSource = dataSource;
    }

    protected AbstractPZParser(final InputStream dataSourceStream) {
        this.dataSourceStream = dataSourceStream;
    }

    protected AbstractPZParser(final File dataSource, final String dataDefinition) {
        this.dataSource = dataSource;
        this.dataDefinition = dataDefinition;
    }

    protected AbstractPZParser(final InputStream dataSourceStream, final String dataDefinition) {
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
    public void setHandlingShortLines(final boolean handleShortLines) {
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

    protected void setInitialised(final boolean initialised) {
        this.initialised = initialised;
    }

    protected String getDataDefinition() {
        return dataDefinition;
    }

    protected void setDataDefinition(final String dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    protected File getDataSource() {
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
        final DataError de = new DataError(errorDesc, lineNo, errorLevel);
        ds.addError(de);
    }

}
