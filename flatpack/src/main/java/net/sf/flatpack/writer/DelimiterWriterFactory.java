package net.sf.flatpack.writer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.xml.XMLRecordElement;

import org.jdom.JDOMException;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class DelimiterWriterFactory extends AbstractWriterFactory {
    public static final char DEFAULT_DELIMITER = ';';
    public static final char DEFAULT_QUALIFIER = '"';

    private final char delimiter;
    private final char qualifier;
    

    public DelimiterWriterFactory(final char delimiter, final char qualifier) {
        super();
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public DelimiterWriterFactory(final Reader mappingSrc) throws IOException, JDOMException {
        this(mappingSrc, DEFAULT_DELIMITER);
    }

    public DelimiterWriterFactory(final Reader mappingSrc, final char delimiter) throws IOException, JDOMException {
        this(mappingSrc, delimiter, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(final Reader mappingSrc, final char delimiter, final char qualifier) throws IOException {
        super(mappingSrc);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public DelimiterWriterFactory(final Map mapping) {
        this(mapping, DEFAULT_DELIMITER, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(final Map mapping, final char delimiter) {
        this(mapping, delimiter, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(final Map mapping, final char delimiter, final char qualifier) {
        super(mapping);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public char getQualifier() {
        return qualifier;
    }
    

	public Writer createWriter(final java.io.Writer out) throws IOException {
    	return createWriter(out, new DelimiterWriterOptions());
    
    }
    
    /**
     * Create an instance of the DelimterWriter
     * 
     * @param out
     * @param delimiterWriterOptions
     * 			Options for Writing
     * @return {@link DelimiterWriter}
     * @throws IOException
     */
    public Writer createWriter(final java.io.Writer out, final DelimiterWriterOptions delimiterWriterOptions) throws IOException {
   		return new DelimiterWriter(this.getColumnMapping(), out, delimiter, qualifier, delimiterWriterOptions);
    }

	/**
	 * Add column titles for mapping. This can be done in lieu of using an XML Mapping. This needs to be done before calling createWriter()
	 * 
	 * @param columnTitle
	 * @param recordId
	 */
    public void addColumnTitle(final String columnTitle, final String recordId) {
    	final String colIdxKey = FPConstants.DETAIL_ID.equals(recordId) ? FPConstants.COL_IDX : FPConstants.COL_IDX + "_" + recordId;
        final Map columnMapping = this.getColumnMapping();
        List columnMetaDatas = null;
        if (FPConstants.DETAIL_ID.equals(recordId)) {
        	//the detail record will always be there.  It is being setup by the AbstractWriter if it 
        	//is not coming off of the XML
        	columnMetaDatas = (List) columnMapping.get(FPConstants.DETAIL_ID);
        } else {
        	//this has a XMLRecord in the map, for anything other than the detail id
        	XMLRecordElement xmlRec = (XMLRecordElement)columnMapping.get(recordId);
        	if (xmlRec == null) {
        		columnMetaDatas = new ArrayList();
        		xmlRec = new XMLRecordElement();
        		xmlRec.setColumns(columnMetaDatas, null);
        		columnMapping.put(recordId, xmlRec);
        	} else {
        		columnMetaDatas = xmlRec.getColumns();
        	}
        }
        
        Map columnIndices = (Map) columnMapping.get(colIdxKey);
        if (columnIndices == null) {
        	columnIndices = new HashMap();
        	columnMapping.put(recordId, columnIndices);
        }

        final ColumnMetaData metaData = new ColumnMetaData();
        metaData.setColName(columnTitle);
        columnMetaDatas.add(metaData);

        final Integer columnIndex = Integer.valueOf(columnMetaDatas.indexOf(metaData));
        columnIndices.put(columnTitle, columnIndex);
    }
    
    /**
     * Add column titles for mapping.  This can be done in lieu of using an XML Mapping.  This needs to be done before calling createWriter()
     * 
     * @param string
     */
	public void addColumnTitle(final String string) {
		addColumnTitle(string, FPConstants.DETAIL_ID);
	}



}
