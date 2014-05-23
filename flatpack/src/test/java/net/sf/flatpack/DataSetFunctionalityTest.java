package net.sf.flatpack;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.flatpack.util.FPConstants;
import junit.framework.TestCase;

/**
 * Test methods in the DataSet
 * 
 * @author Paul Zepernick
 */
public class DataSetFunctionalityTest extends TestCase {

	public void testContains() {
		DataSet ds;
		final String cols = "column1,column2,column3\r\n value1,value2,value3";
		final Parser p = DefaultParserFactory.getInstance().newDelimitedParser(
				new StringReader(cols), ',', FPConstants.NO_QUALIFIER);
		ds = p.parse();
		ds.next();
		assertEquals("column should NOT be found...", false,
				ds.contains("shouldnotcontain"));

		assertEquals("column should be found...", true, ds.contains("column1"));
	}

	public void testContainsForStream() {
		final String cols = "column1,column2,column3\r\n value1,value2,value3";
		final Parser p = DefaultParserFactory.getInstance().newDelimitedParser(
				new StringReader(cols), ',', FPConstants.NO_QUALIFIER);
		StreamingDataSet ds = p.parseAsStream();
		assertTrue(ds.next());
		assertEquals("column should NOT be found...", false, ds.getRecord()
				.contains("shouldnotcontain"));
		assertEquals("column should be found...", true, ds.getRecord()
				.contains("column1"));
	}

	public void testContainsWithStream() {
		final String cols = "column1,column2,column3\r\nvalue1,value2,value3\r\nvalue1a,value2a,value3a";
		final Parser p = DefaultParserFactory.newCsvParser(new StringReader(
				cols));
		List<Record> collect = p.stream().collect(Collectors.toList());
		assertEquals("Size", 2, collect.size());
		assertEquals("column should NOT be found...", false, collect.get(0).contains("shouldnotcontain"));
		assertEquals("column should be found...", true, collect.get(0).contains("column1"));
		assertEquals("column should NOT be found...", false, collect.get(0).contains("shouldnotcontain"));
		assertEquals("column should be found...", true, collect.get(0).contains("column1"));
		assertEquals("column should be found val1...", "value1", collect.get(0).getString("column1"));
		assertEquals("column should be found 1a...", "value1a", collect.get(1).getString("column1"));
//		p.stream().forEach(
//				t -> {
//					assertEquals("column should NOT be found...", false,
//							t.contains("shouldnotcontain"));
//					assertEquals("column should be found...", true,
//							t.contains("column1"));
//				});
	}
}
