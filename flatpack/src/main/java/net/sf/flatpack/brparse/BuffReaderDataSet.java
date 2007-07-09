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
package net.sf.flatpack.brparse;

import java.io.IOException;

import net.sf.flatpack.DefaultDataSet;
import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.xml.MetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuffReaderDataSet extends DefaultDataSet {
    private final BuffReaderDelimParser brDelimPzParser;

    private final BuffReaderFixedParser brFixedPzParser;

    private final Logger logger = LoggerFactory.getLogger(BuffReaderDataSet.class);

    //    public BuffReaderPZDataSet(final Map columnMD2, final BuffReaderDelimPZParser brDelimPzParser) {
    //        super(columnMD2, brDelimPzParser);
    //        //register the parser with the dataset so we can fetch rows from 
    //        //the bufferedreader as needed
    //        this.brDelimPzParser = brDelimPzParser;
    //        this.brFixedPzParser = null;
    //    }
    //
    //    public BuffReaderPZDataSet(final Map columnMD2, final BuffReaderFixedPZParser brFixedPzParser) {
    //        super(columnMD2, brFixedPzParser);
    //        //register the parser with the dataset so we can fetch rows from 
    //        //the bufferedreader as needed
    //        this.brFixedPzParser = brFixedPzParser;
    //        this.brDelimPzParser = null;
    //    }

    public BuffReaderDataSet(final MetaData columnMD2, final BuffReaderDelimParser brDelimPzParser) {
        super(columnMD2, brDelimPzParser);
        //register the parser with the dataset so we can fetch rows from 
        //the bufferedreader as needed
        this.brDelimPzParser = brDelimPzParser;
        this.brFixedPzParser = null;
    }

    public BuffReaderDataSet(final MetaData columnMD2, final BuffReaderFixedParser brFixedPzParser) {
        super(columnMD2, brFixedPzParser);
        //register the parser with the dataset so we can fetch rows from 
        //the bufferedreader as needed
        this.brFixedPzParser = brFixedPzParser;
        this.brDelimPzParser = null;
    }

    public boolean next() {
        try {
            Row r = null;

            if (brDelimPzParser != null) {
                r = brDelimPzParser.buildRow(this);
            } else if (brFixedPzParser != null) {
                r = brFixedPzParser.buildRow(this);
            } else {
                //this should not happen, throw exception
                throw new RuntimeException("No parser available to fetch row");
            }

            if (r == null) {
                setPointer(-1);
                return false;
            }

            //make sure we have some MD
            //            if (getColumnMD() == null) {
            //                //create a new map so the user cannot change the internal 
            //                //DataSet representation of the MD through the parser
            //                setColumnMD(new LinkedHashMap(brDelimPzParser.getColumnMD()));
            //            }

            if (getMetaData() == null) {
                setMetaData(brDelimPzParser.getPzMetaData());
            }

            clearRows();
            addRow(r);

            setPointer(0);

            return true;

        } catch (final IOException ex) {
            logger.error("error building Row on next()", ex);
        }

        return false;
    }

    /**
     * Not Supported!
     * @return boolean
     */
    public boolean previous() {
        throw new UnsupportedOperationException("previous() is Not Implemented");
    }

    /**
     * Not Supported! 
     * @param ob - OrderBy object
     * @exception Exception
     * @see com.pz.reader.ordering.OrderBy
     * @see com.pz.reader.ordering.OrderColumn
     */
    public void orderRows(final OrderBy ob) throws Exception {
        throw new UnsupportedOperationException("orderRows() is Not Implemented");
    }

    /**
     * Not Supported!
     * @param localPointer - int
     * @exception IndexOutOfBoundsException
     */
    public void absolute(final int localPointer) {
        throw new UnsupportedOperationException("absolute() is Not Implemented");
    }

    /**
     *Not Supported!
     */
    public void remove() {
        throw new UnsupportedOperationException("remove() is Not Implemented");
    }

    /**
     * Not Supported!
     * @return int
     */
    public int getIndex() {
        throw new UnsupportedOperationException("getIndex() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    public void goBottom() {
        throw new UnsupportedOperationException("goBottom() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    public void goTop() {
        throw new UnsupportedOperationException("goTop() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    public void setValue(final String column, final String value) {
        throw new UnsupportedOperationException("setValue() is Not Implemented");
    }

}
