package net.sf.flatpack.writer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Assert;

import net.sf.flatpack.InitialisationException;
import net.sf.flatpack.writer.FixedWriterFactory;
import net.sf.flatpack.writer.Writer;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class FixedLengthWriterTest extends PZWriterTestCase {
	public void testWriteFixedLength() throws Exception {
		final StringWriter out = new StringWriter();
		final Writer writer = new FixedWriterFactory(this.getMapping())
				.createWriter(out);

		writer.addRecordEntry("LASTNAME", "DOE") // New fluent
				.addRecordEntry("ADDRESS", "1234 CIRCLE CT") //
				.addRecordEntry("STATE", "OH") //
				.addRecordEntry("ZIP", "44035") //
				.addRecordEntry("FIRSTNAME", "JOHN") //
				.addRecordEntry("CITY", "ELYRIA") //
				.nextRecord() //
				.flush();

		final String expected = this
				.normalizeLineEnding("JOHN                               DOE                                1234 CIRCLE CT                                                                                      ELYRIA                                                                                              OH44035");
		Assert.assertEquals(expected, out.toString());
	}

	public void testWriterWithDifferentFillChar() throws Exception {
		final StringWriter out = new StringWriter();
		final Writer writer = new FixedWriterFactory(this.getMapping(), '.')
				.createWriter(out);

		writer.addRecordEntry("LASTNAME", "DOE");
		writer.addRecordEntry("ADDRESS", "1234 CIRCLE CT");
		writer.addRecordEntry("STATE", "OH");
		writer.addRecordEntry("ZIP", "44035");
		writer.addRecordEntry("FIRSTNAME", "JOHN");
		writer.addRecordEntry("CITY", "ELYRIA");
		writer.nextRecord();
		writer.flush();

		final String expected = this
				.normalizeLineEnding("JOHN...............................DOE................................1234 CIRCLE CT......................................................................................ELYRIA..............................................................................................OH44035");
		Assert.assertEquals(expected, out.toString());
	}

	public void testCreateParserWithMalformedMappingFile() throws Exception {
		try {
			final InputStream mapping = this.getClass().getClassLoader()
					.getResourceAsStream("BrokenMapping.pzmap.xml");
			final InputStreamReader mappingReader = new InputStreamReader(
					mapping);
			new FixedWriterFactory(mappingReader);
			Assert.fail();
		} catch (final InitialisationException ie) {
			// this excecption must occur, mapping xml is invalid
		}
	}

	public void testWriteStringWiderThanColumnDefinition() throws Exception {
		final StringWriter out = new StringWriter();
		final Writer writer = new FixedWriterFactory(this.getMapping())
				.createWriter(out);
		try {
			writer.addRecordEntry("STATE", "THISISTOOLONG");
			Assert.fail("writing entries that are too long should fail");
		} catch (final IllegalArgumentException iae) {
			// expected exception
		}
	}

	public void testWriteNullColumn() throws Exception {
		final StringWriter out = new StringWriter();
		final Writer writer = new FixedWriterFactory(this.getMapping())
				.createWriter(out);

		writer.addRecordEntry("LASTNAME", "DOE");
		writer.addRecordEntry("ADDRESS", "1234 CIRCLE CT");
		writer.addRecordEntry("STATE", "OH");
		writer.addRecordEntry("ZIP", "44035");
		// note that we don't write a firstname
		writer.addRecordEntry("FIRSTNAME", null);
		writer.addRecordEntry("CITY", "ELYRIA");
		writer.nextRecord();
		writer.flush();

		final String expected = this
				.normalizeLineEnding("                                   DOE                                1234 CIRCLE CT                                                                                      ELYRIA                                                                                              OH44035");
		Assert.assertEquals(expected, out.toString());
	}

	public void DONOTtestWriteDifferentRecords() throws Exception {
		final String ls = System.getProperty("line.separator");
		final StringWriter out = new StringWriter();
		final Writer writer = new FixedWriterFactory(
				getMappingDiffRecordTypes()).createWriter(out);
		// writer.setRecordId("header");
		writer.addRecordEntry("recordtype", "H");
		writer.addRecordEntry("headerdata1", "header data");
		writer.nextRecord();

		writer.addRecordEntry("recordtype", "D");
		writer.addRecordEntry("detaildata1", "detail data");
		writer.nextRecord();
		writer.flush();

		final StringBuilder expected = new StringBuilder();
		expected.append("H");
		expected.append("header data         ").append(ls);
		expected.append("D");
		expected.append("detail data         ").append(ls);

		assertEquals("Checking writer for different record types...",
				expected.toString(), out.toString());

	}

	private Reader getMapping() {
		final InputStream mapping = this.getClass().getClassLoader()
				.getResourceAsStream("FixedLength.pzmap.xml");
		return new InputStreamReader(mapping);
	}

	private Reader getMappingDiffRecordTypes() {
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \r\n"
				+ "<!DOCTYPE PZMAP SYSTEM	\"pzfilereader.dtd\" > \r\n"
				+ "	<PZMAP>\r\n"
				+ "		<RECORD id=\"header\" startPosition=\"1\" endPosition=\"1\" indicator=\"H\">"
				+ "			<COLUMN name=\"recordtype\" length=\"1\" /> \r\n"
				+ "			<COLUMN name=\"headerdata1\" length=\"20\" /> \r\n"
				+ "		</RECORD>"
				+ "		<COLUMN name=\"recordtype\" length=\"1\" /> \r\n"
				+ "		<COLUMN name=\"detaildata1\" length=\"20\" /> \r\n"
				+ "	</PZMAP>";

		return new StringReader(xml);
	}
}
