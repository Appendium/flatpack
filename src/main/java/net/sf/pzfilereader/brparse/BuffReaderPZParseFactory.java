package net.sf.pzfilereader.brparse;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;

import net.sf.pzfilereader.PZParser;
import net.sf.pzfilereader.PZParserFactory;

/**
 * Provides a PZParser which obtains records directly from
 * a BufferedReader as an alternative to reading the
 * entire file into memory.
 * 
 * @author Paul Zepernick
 */
public class BuffReaderPZParseFactory implements PZParserFactory{
    private static final BuffReaderPZParseFactory INSTANCE = new BuffReaderPZParseFactory();

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
        //return new DBFixedLengthPZParser(con, dataSource, dataDefinition);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newFixedWidthParser(java.sql.Connection,
     *      java.io.InputStream, java.lang.String)
     */
    public PZParser newFixedLengthParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition) {
        //return new DBFixedLengthPZParser(con, dataSourceStream, dataDefinition);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.File,
     *      java.io.File)
     */
    public PZParser newFixedLengthParser(final File pzmapXML, final File dataSource) {
      //  return new FixedLengthPZParser(pzmapXML, dataSource);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.InputStream,
     *      java.io.InputStream)
     */
    public PZParser newFixedLengthParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream) {
      //  return new FixedLengthPZParser(pzmapXMLStream, dataSourceStream);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.sql.Connection,
     *      java.io.InputStream, java.lang.String, char, char, boolean)
     */
    public PZParser newDelimitedParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition,
            final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        //return new BuffReaderDelimPZParser(con, dataSourceStream, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.File,
     *      java.io.File, char, char, boolean)
     */
    public PZParser newDelimitedParser(final File pzmapXML, final File dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        return new BuffReaderDelimPZParser(pzmapXML, dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.InputStream,
     *      java.io.InputStream, char, char, boolean)
     */
    public PZParser newDelimitedParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream,
            final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        return new BuffReaderDelimPZParser(pzmapXMLStream, dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.File, char,
     *      char)
     */
    public PZParser newDelimitedParser(final File dataSource, final char delimiter, final char qualifier) {
        return new BuffReaderDelimPZParser(dataSource, delimiter, qualifier, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.PZParserFactory#newParser(java.io.InputStream,
     *      char, char)
     */
    public PZParser newDelimitedParser(final InputStream dataSourceStream, final char delimiter, final char qualifier) {
        return new BuffReaderDelimPZParser(dataSourceStream, delimiter, qualifier, false);
    }
}
