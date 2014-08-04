package net.sf.flatpack.brparse;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import net.sf.flatpack.InitialisationException;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.ParserUtils;

/**
 *
 *
 * @author Paul Zepernick
 */
public class DBBuffReaderDelimParser extends BuffReaderDelimParser implements InterfaceBuffReaderParse {

    private final Connection con;

    /**
     * @param con
     * @param dataSourceReader
     * @param dataDefinition
     * @param delimiter
     * @param qualifier
     * @param ignoreFirstRecord
     */
    public DBBuffReaderDelimParser(final Connection con, final Reader dataSourceReader, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceReader, delimiter, qualifier, ignoreFirstRecord);
        setDataDefinition(dataDefinition);
        this.con = con;
    }

    @Override
    protected void init() {
        try {
            final List<ColumnMetaData> cmds = ParserUtils.buildMDFromSQLTable(con, getDataDefinition(), this);
            addToMetaData(cmds);

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

    @Override
    protected boolean shouldCreateMDFromFile() {
        // The MetaData should always be pulled from the DB for this implementation
        return false;
    }
}
