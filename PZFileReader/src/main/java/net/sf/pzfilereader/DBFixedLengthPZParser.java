/**
 * 
 */
package net.sf.pzfilereader;

import java.io.File;
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
public class DBFixedLengthPZParser extends AbstractFixedLengthPZParser {

    private Connection con;

    public DBFixedLengthPZParser(final Connection con, final InputStream dataSourceStream, final String dataDefinition) {
        super(dataSourceStream, dataDefinition);
        this.con = con;
    }

    public DBFixedLengthPZParser(Connection con, File dataSource, String dataDefinition) {
        super(dataSource, dataDefinition);
        this.con = con;
    }

    protected void init() {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {
            final String sql = "SELECT * FROM DATAFILE INNER JOIN DATASTRUCTURE ON "
                    + "DATAFILE.DATAFILE_NO = DATASTRUCTURE.DATAFILE_NO " + "WHERE DATAFILE.DATAFILE_DESC = '"
                    + getDataDefinition() + "' " + "ORDER BY DATASTRUCTURE_COL_ORDER";

            stmt = con.prepareStatement(sql); // always use PreparedStatement
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
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
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
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public IDataSet doParse() {
        return null;
    }
}
