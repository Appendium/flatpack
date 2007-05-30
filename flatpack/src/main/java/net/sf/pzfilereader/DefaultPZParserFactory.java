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
import java.io.Reader;
import java.sql.Connection;

/**
 * @author xhensevb
 * 
 */
public class DefaultPZParserFactory implements PZParserFactory {
    private static final DefaultPZParserFactory INSTANCE = new DefaultPZParserFactory();

    public static PZParserFactory getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newFixedWidthParser(java.sql.Connection,
     *      java.io.File, java.lang.String)
     */
    public PZParser newFixedLengthParser(final Connection con, final File dataSource, final String dataDefinition) {
        return new DBFixedLengthPZParser(con, dataSource, dataDefinition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newFixedWidthParser(java.sql.Connection,
     *      java.io.InputStream, java.lang.String)
     */
    public PZParser newFixedLengthParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition) {
        return new DBFixedLengthPZParser(con, dataSourceStream, dataDefinition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.File,
     *      java.io.File)
     */
    public PZParser newFixedLengthParser(final File pzmapXML, final File dataSource) {
        return new FixedLengthPZParser(pzmapXML, dataSource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.InputStream,
     *      java.io.InputStream)
     */
    public PZParser newFixedLengthParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream) {
        return new FixedLengthPZParser(pzmapXMLStream, dataSourceStream);
    }

    public PZParser newFixedLengthParser(final Connection con, final Reader dataSource, final String dataDefinition) {
        return new DBFixedLengthPZParser(con, dataSource, dataDefinition);
    }

    public PZParser newFixedLengthParser(final Reader pzmapXMLStream, final Reader dataSource) {
        return new FixedLengthPZParser(pzmapXMLStream, dataSource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.sql.Connection,
     *      java.io.InputStream, java.lang.String, char, char, boolean)
     */
    public PZParser newDelimitedParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        return new DBDelimiterPZParser(con, dataSourceStream, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.File,
     *      java.io.File, char, char, boolean)
     */
    public PZParser newDelimitedParser(final File pzmapXML, final File dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        return new DelimiterPZParser(pzmapXML, dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.InputStream,
     *      java.io.InputStream, char, char, boolean)
     */
    public PZParser newDelimitedParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        return new DelimiterPZParser(pzmapXMLStream, dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.File, char,
     *      char)
     */
    public PZParser newDelimitedParser(final File dataSource, final char delimiter, final char qualifier) {
        return new DelimiterPZParser(dataSource, delimiter, qualifier, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.InputStream,
     *      char, char)
     */
    public PZParser newDelimitedParser(final InputStream dataSourceStream, final char delimiter, final char qualifier) {
        return new DelimiterPZParser(dataSourceStream, delimiter, qualifier, false);
    }

    public PZParser newDelimitedParser(final Connection con, final Reader dataSource, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        return new DBDelimiterPZParser(con, dataSource, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
    }

    public PZParser newDelimitedParser(final Reader dataSource, final char delimiter, final char qualifier) {
        return new DelimiterPZParser(dataSource, delimiter, qualifier, false);
    }

    public PZParser newDelimitedParser(final Reader pzmapXML, final Reader dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        return new DelimiterPZParser(pzmapXML, dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

}
