package net.sf.flatpack.brparse;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import net.sf.flatpack.InitialisationException;
import net.sf.flatpack.util.ParserUtils;

public class DBBuffReaderFixedParser extends BuffReaderFixedParser implements InterfaceBuffReaderParse {
	private Connection con;

	
    public DBBuffReaderFixedParser(final Connection con, final Reader dataSourceReader, final String dataDefinition) {
        super(dataSourceReader, dataDefinition);  
        this.con = con; 
    }
	
    protected void init() {
        try {
            
            final List cmds = ParserUtils.buildMDFromSQLTable(con, getDataDefinition(), this);
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
    	//The MetaData should always be pulled from the DB for this implementation
        return false;
    }
}
