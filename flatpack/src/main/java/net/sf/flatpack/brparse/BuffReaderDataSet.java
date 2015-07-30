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
package net.sf.flatpack.brparse;

import net.sf.flatpack.AbstractParser;
import net.sf.flatpack.DefaultDataSet;
import net.sf.flatpack.Parser;
import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.xml.MetaData;

/**
 *
 *
 * @author Paul Zepernick
 */
public class BuffReaderDataSet extends DefaultDataSet {
    private final InterfaceBuffReaderParse brParser;

    /**
     *
     * @param columnMD2
     * @param brParser
     */
    public BuffReaderDataSet(final MetaData columnMD2, final InterfaceBuffReaderParse brParser) {
        super(columnMD2, (Parser) brParser);
        // register the parser with the dataset so we can fetch rows from
        // the bufferedreader as needed
        this.brParser = brParser;
    }

    @Override
    public boolean next() {

        if (brParser == null) {
            // this should not happen, throw exception
            throw new RuntimeException("No parser available to fetch row");
        }

        if (getMetaData() == null) {
            setMetaData(((AbstractParser) brParser).getPzMetaData());
        }

        clearRows();
        final Row r = brParser.buildRow(this);
        if (r != null) {
            addRow(r);
        }

        return super.next();
    }

    /**
     * Not Supported!
     * @return boolean
     */
    @Override
    public boolean previous() {
        throw new UnsupportedOperationException("previous() is Not Implemented");
    }

    /**
     * Not Supported!
     * @param ob - OrderBy object
     * @see net.sf.flatpack.ordering.OrderBy
     * @see net.sf.flatpack.ordering.OrderColumn
     */
    @Override
    public void orderRows(final OrderBy ob) {
        throw new UnsupportedOperationException("orderRows() is Not Implemented");
    }

    /**
     * Not Supported!
     * @param localPointer - int
     * @exception IndexOutOfBoundsException
     */
    @Override
    public void absolute(final int localPointer) {
        throw new UnsupportedOperationException("absolute() is Not Implemented");
    }

    /**
     *Not Supported!
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is Not Implemented");
    }

    /**
     * Not Supported!
     * @return int
     */
    @Override
    public int getIndex() {
        throw new UnsupportedOperationException("getIndex() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    @Override
    public void goBottom() {
        throw new UnsupportedOperationException("goBottom() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    @Override
    public void goTop() {
        throw new UnsupportedOperationException("goTop() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    @Override
    public void setValue(final String column, final String value) {
        throw new UnsupportedOperationException("setValue() is Not Implemented");
    }

    /**
     * Not Supported!
     */
    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("getRowCount() is Not Implemented");
    }

}
