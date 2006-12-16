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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

/**
 * @author xhensevb
 * 
 */
public class DBDelimiterPZParser extends AbstractDelimiterPZParser {

    private Connection con;

    public DBDelimiterPZParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition,
            final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceStream, dataDefinition, delimiter, qualifier, ignoreFirstRecord);
        this.con = con;
    }

    protected void init() {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {
            final String sql = "SELECT * FROM DATAFILE INNER JOIN DATASTRUCTURE ON "
                    + "DATAFILE.DATAFILE_NO = DATASTRUCTURE.DATAFILE_NO " 
                    + "WHERE DATAFILE.DATAFILE_DESC = ? "
                    + "ORDER BY DATASTRUCTURE_COL_ORDER";

            stmt = con.prepareStatement(sql); // always use PreparedStatement
            stmt.setString(1, getDataDefinition());
            // as the DB can do clever things.
            rs = stmt.executeQuery();

            int recPosition = 1;
            final List cmds = new ArrayList();
            // put array of columns together. These will be used to put together
            // the dataset when reading in the file
            while (rs.next()) {

                final ColumnMetaData column = new ColumnMetaData();
                column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
                column.setColLength(rs.getInt("DATASTRUCTURE_LENGTH"));
                column.setStartPosition(recPosition);
                column.setEndPosition(recPosition + (rs.getInt("DATASTRUCTURE_LENGTH") - 1));
                recPosition += rs.getInt("DATASTRUCTURE_LENGTH");

                cmds.add(column);
            }

            addToColumnMD(PZConstants.DETAIL_ID, cmds);
            addToColumnMD(PZConstants.COL_IDX, ParserUtils.buidColumnIndexMap(cmds));

            if (cmds.isEmpty()) {
                throw new FileNotFoundException("DATA DEFINITION CAN NOT BE FOUND IN THE DATABASE " + getDataDefinition());
            }

            // read in the fixed length file and construct the DataSet object
            // doFixedLengthFile(dataSourceStream);
            setInitialised(true);
        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected boolean shouldCreateMDFromFile() {
        return true;
    }
}
