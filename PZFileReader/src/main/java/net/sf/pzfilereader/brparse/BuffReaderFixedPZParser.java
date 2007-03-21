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
package net.sf.pzfilereader.brparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.pzfilereader.DataSet;
import net.sf.pzfilereader.DefaultDataSet;
import net.sf.pzfilereader.FixedLengthPZParser;
import net.sf.pzfilereader.structure.Row;
import net.sf.pzfilereader.util.FixedWidthParserUtils;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

public class BuffReaderFixedPZParser extends FixedLengthPZParser{
    private BufferedReader br = null;
     
    private int lineCount = 0;
    
    private Map recordLengths = null;
    
    private final Logger logger = LoggerFactory.getLogger(BuffReaderFixedPZParser.class); 
    
    public BuffReaderFixedPZParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream) {
        super(pzmapXMLStream, dataSourceStream);
    }

    public BuffReaderFixedPZParser(final File pzmapXML, final File dataSource) {
        super(pzmapXML, dataSource);
    }
    
    public BuffReaderFixedPZParser(final Reader pzmapXML, final Reader dataSource) {
        super(pzmapXML, dataSource);
    }
    
    
    public DataSet doParse() {
        final DataSet ds = new BuffReaderPZDataSet(getColumnMD(), this);
        lineCount = 0;
        recordLengths = ParserUtils.calculateRecordLengths(getColumnMD());
        try {
            //gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());
            
            br = new BufferedReader(getDataSourceReader());
            
            return ds;
        
        } catch(IOException ex) {
            logger.error("error accessing/creating inputstream", ex);
        }
        
        return null;
    }
    
    
    /**
     * Reads in the next record on the file and return a row
     * 
     * @param ds
     * @return Row
     * @throws IOException
     */
    public Row buildRow(final DefaultDataSet ds) throws IOException{
        String line = null;
        
        while ((line = br.readLine()) != null) {
            lineCount++;
            // empty line skip past it
            if (line.trim().length() == 0) {
                continue;
            }

            final String mdkey = FixedWidthParserUtils.getCMDKey(getColumnMD(), line);
            final int recordLength = ((Integer) recordLengths.get(mdkey)).intValue();

            if (line.length() > recordLength) {
                // Incorrect record length on line log the error. Line will not
                // be included in the
                // dataset
                if (isIgnoreExtraColumns()) {
                    //user has choosen to ignore the fact that we have too many bytes in the fixed
                    //width file.  Truncate the line to the correct length
                    line = line.substring(0, recordLength);
                    addError(ds, "TRUNCATED LINE TO CORRECT LENGTH", lineCount, 1);
                } else {
                    addError(ds, "LINE TOO LONG. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount, 2);
                    continue;
                }
            } else if (line.length() < recordLength) {
                if (isHandlingShortLines()) {
                    // We can pad this line out
                    line += ParserUtils.padding(recordLength - line.length(), ' ');

                    // log a warning
                    addError(ds, "PADDED LINE TO CORRECT RECORD LENGTH", lineCount, 1);

                } else {
                    addError(ds, "LINE TOO SHORT. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount,
                            2);
                    continue;
                }
            }

            final Row row = new Row();
            row.setMdkey(mdkey.equals(PZConstants.DETAIL_ID) ? null : mdkey); 

            final List cmds = ParserUtils.getColumnMetaData(mdkey, getColumnMD());
            row.addColumn(FixedWidthParserUtils.splitFixedText(cmds, line));

            row.setRowNumber(lineCount);
            
            return row;
        }
        
        return null;
    }
    
    /**
     * Closes out the file readers
     *
     *@throws IOException
     */
    public void close() throws IOException{
        if (br != null) {
            br.close();
        }
    }
    
    //try to clean up the file handles automatically if
    //the close was not called
    protected void finalize() throws Throwable {
        try {
            close();
        } catch (IOException ex) {
            logger.warn("Problem trying to auto close file handles...", ex);
        } finally {
            super.finalize();
        }
    }
    
    /**
     * Returns the meta data describing the columns
     */
    public Map getColumnMD() {
        return super.getColumnMD();
    }
}