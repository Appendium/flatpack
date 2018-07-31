package net.sf.flatpack;

import java.io.Reader;

import lombok.experimental.UtilityClass;
import net.sf.flatpack.brparse.BuffReaderParseFactory;

/**
 * Easy way to get a CSV Parser (separator , and qualifier ").
 *
 */
@UtilityClass
public final class CsvParserFactory {
    /**
     * This should be your default mechanism, it does not keep previous records as you stream the results, so
     * it is more memory efficient but the downside is that you cannot reset the parsing or restart it.  It reads
     * a line, parses it and returns a Record or the next entry in the DataSet.
     * @param reader the data source
     * @return a CSV Parser
     */
    public static Parser newForwardParser(Reader reader) {
        return BuffReaderParseFactory.getInstance().newDelimitedParser(reader, ',', '"');
    }

    /**
     * With this Parser, everything is loaded in memory, you can reset the parsing or restart it, etc.
     * @param reader the data source
     * @return a CSV Parser
     */
    public static Parser newInMemoryParser(Reader reader) {
        return DefaultParserFactory.newCsvParser(reader);
    }
}
