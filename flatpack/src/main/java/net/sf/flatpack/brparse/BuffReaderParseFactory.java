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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;

import net.sf.flatpack.Parser;
import net.sf.flatpack.ParserFactory;

/**
 * Provides a PZParser which obtains records directly from
 * a BufferedReader as an alternative to reading the
 * entire file into memory.
 *
 * Database column mappings are not supported by this factory
 * at the present time.  This class is meant to mimic the LargeDataSet
 * class of pre 3.0 versions, which did not support database mappings
 * either.  A RuntimeExcpetion will be thrown if trying to obtain a parser
 * for a database map.
 *
 * @author Paul Zepernick
 */
public class BuffReaderParseFactory implements ParserFactory {
    private static final BuffReaderParseFactory INSTANCE = new BuffReaderParseFactory();

    public static ParserFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Not supported at this time.
     */
    public Parser newFixedLengthParser(final Connection con, final File dataSource, final String dataDefinition) {
        throw new UnsupportedOperationException("Not supported...");
    }

    /**
     * Not supported at this time.
     */
    public Parser newFixedLengthParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition) {
        throw new UnsupportedOperationException("Not supported...");
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParserFactory#newParser(java.io.File,
     *      java.io.File)
     */
    public Parser newFixedLengthParser(final File pzmapXML, final File dataSource) {
        return new BuffReaderFixedParser(pzmapXML, dataSource);
    }


    public Parser newFixedLengthParser(final Connection con, final Reader dataSource, final String dataDefinition) {
    	return new DBBuffReaderFixedParser(con, dataSource, dataDefinition);
    }

    public Parser newFixedLengthParser(final Reader pzmapXMLStream, final Reader dataSource) {
        return new BuffReaderFixedParser(pzmapXMLStream, dataSource);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParserFactory#newParser(java.io.InputStream,
     *      java.io.InputStream)
     */
    public Parser newFixedLengthParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream) {
        return new BuffReaderFixedParser(pzmapXMLStream, dataSourceStream);
    }

    
    public Parser newDelimitedParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        throw new UnsupportedOperationException("Not supported.  Use 'Reader' Constructor Instead Of InputStream.");
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParserFactory#newParser(java.io.File,
     *      java.io.File, char, char, boolean)
     */
    public Parser newDelimitedParser(final File pzmapXML, final File dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        return new BuffReaderDelimParser(pzmapXML, dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParserFactory#newParser(java.io.InputStream,
     *      java.io.InputStream, char, char, boolean)
     */
    public Parser newDelimitedParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        return new BuffReaderDelimParser(pzmapXMLStream, dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParserFactory#newParser(java.io.File, char,
     *      char)
     */
    public Parser newDelimitedParser(final File dataSource, final char delimiter, final char qualifier) {
        return new BuffReaderDelimParser(dataSource, delimiter, qualifier, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.PZParserFactory#newParser(java.io.InputStream,
     *      char, char)
     */
    public Parser newDelimitedParser(final InputStream dataSourceStream, final char delimiter, final char qualifier) {
        return new BuffReaderDelimParser(dataSourceStream, delimiter, qualifier, false);
    }

    /**
     * Not supported at this time.
     */
    public Parser newDelimitedParser(final Connection con, final Reader dataSource, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        //throw new UnsupportedOperationException("Not supported...");
    	return new DBBuffReaderDelimParser(con, dataSource, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
    }

    public Parser newDelimitedParser(final Reader dataSource, final char delimiter, final char qualifier) {
        return new BuffReaderDelimParser(dataSource, delimiter, qualifier, false);
    }

    public Parser newDelimitedParser(final Reader pzmapXML, final Reader dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        return new BuffReaderDelimParser(pzmapXML, dataSource, delimiter, qualifier, ignoreFirstRecord);
    }
}
