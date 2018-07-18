/**
 *
 */
package net.sf.flatpack.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

/**
 * @author Benoit Xhenseval
 *
 */
public class MetaData {
    private List<ColumnMetaData> columnsNames;
    private Map columnIndexMap;
    private final Map<String, XMLRecordElement> xmlRecordElements;

    public MetaData(final Map fullMapFromPZParser) {
        columnsNames = (List<ColumnMetaData>) fullMapFromPZParser.get(FPConstants.DETAIL_ID);
        columnIndexMap = (Map) fullMapFromPZParser.get(FPConstants.COL_IDX);
        xmlRecordElements = fullMapFromPZParser;
    }

    public MetaData(final List<ColumnMetaData> columnNames, final Map columnIndexMap) {
        this.columnsNames = Collections.unmodifiableList(columnNames);
        this.columnIndexMap = Collections.unmodifiableMap(columnIndexMap);
        this.xmlRecordElements = new HashMap();
    }

    MetaData(final List<ColumnMetaData> columnNames, final Map columnIndexMap, final Map<String, XMLRecordElement> xmlRecordElements) {
        this.columnsNames = Collections.unmodifiableList(columnNames);
        this.columnIndexMap = columnIndexMap;
        this.xmlRecordElements = xmlRecordElements;
    }

    public List<ColumnMetaData> getColumnsNames() {
        return columnsNames;
    }

    public Map getColumnIndexMap() {
        return columnIndexMap;
    }

    public void setColumnIndexMap(final Map columnIndexMap) {
        this.columnIndexMap = columnIndexMap;
    }

    public void setColumnsNames(final List<ColumnMetaData> columnsNames) {
        this.columnsNames = Collections.unmodifiableList(columnsNames);
    }

    public boolean isAnyRecordFormatSpecified() {
        return xmlRecordElements != null && !xmlRecordElements.isEmpty();
    }

    public Iterator<Entry<String, XMLRecordElement>> xmlRecordIterator() {
        return xmlRecordElements.entrySet().iterator();
    }

    public List<ColumnMetaData> getListColumnsForRecord(final String key) {
        return xmlRecordElements.get(key).getColumns();
    }

    public int getColumnIndex(final String key, final String columnName) {
        int idx = -1;
        if (key != null && !key.equals(FPConstants.DETAIL_ID) && !key.equals(FPConstants.COL_IDX)) {
            idx = xmlRecordElements.get(key).getColumnIndex(columnName);
        } else if (key == null || key.equals(FPConstants.DETAIL_ID)) {
            final Integer i = (Integer) columnIndexMap.get(columnName);
            if (i != null) { // happens when the col name does not exist in the
                // mapping
                idx = i.intValue();
            }
        }
        return idx;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Col Names:").append(columnsNames).append(System.lineSeparator());
        buf.append("Col Index Map:").append(columnIndexMap).append(System.lineSeparator());
        buf.append("XML Record Elements:").append(xmlRecordElements).append(System.lineSeparator());
        return buf.toString();
    }
}
