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
package net.sf.flatpack;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import net.sf.flatpack.util.ParserUtils;

/**
 * @author xhensevb
 * @author zepernick
 * 
 */
public class DBDelimiterPZParser extends AbstractDelimiterPZParser {
    private Connection con;

    private InputStream dataSourceStream;

    public DBDelimiterPZParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        super(null, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
        this.con = con;
        this.dataSourceStream = dataSourceStream;
    }

    public DBDelimiterPZParser(final Connection con, final Reader dataSourceReader, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceReader, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
        this.con = con;
    }

    protected void init() {
        try {
            //check to see if the user is using a InputStream.  This is 
            //here for backwards compatability
            if (dataSourceStream != null) {
                final Reader r = new InputStreamReader(dataSourceStream);
                setDataSourceReader(r);
                addToCloseReaderList(r);
            }

            final List cmds = ParserUtils.buildMDFromSQLTable(con, getDataDefinition());
            addToMetaData(cmds);
            //            addToColumnMD(PZConstants.DETAIL_ID, cmds);
            //            addToColumnMD(PZConstants.COL_IDX, ParserUtils.buidColumnIndexMap(cmds, this));

            if (cmds.isEmpty()) {
                throw new FileNotFoundException("DATA DEFINITION CAN NOT BE FOUND IN THE DATABASE " + getDataDefinition());
            }
            setInitialised(true);
        } catch (final SQLException e) {
            throw new InitialisationException(e);
        } catch (final FileNotFoundException e) {
            throw new InitialisationException(e);
        }
    }

    protected boolean shouldCreateMDFromFile() {
        return true;
    }
}
