package net.sf.flatpack.brparse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import net.sf.flatpack.CsvParserFactory;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.Parser;

class BuffReaderDataSetTest {
    private static final String CSV = "col1,col2" + System.getProperty("line.separator") + "val1,val2";

    @Test
    void testCsv() {
        final Parser parser = CsvParserFactory.newForwardParser(new StringReader(CSV));
        final DataSet ds = parser.parse();
        assertThat(ds.getErrorCount()).isEqualTo(0);
        assertThat(ds.next()).isTrue();
        assertThat(ds.getString("col1")).isEqualToIgnoringCase("val1");
        assertThat(ds.getString("col2")).isEqualToIgnoringCase("val2");
    }
}
