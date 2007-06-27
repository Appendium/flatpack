/**
 * 
 */
package net.sf.flatpack.xml;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.util.FPConstants;

/**
 * @author Benoit Xhenseval
 *
 */
public class MetaData {
    private List columnsNames;
    private Map columnIndexMap;
    private Map xmlRecordElements;

    public MetaData(final Map fullMapFromPZParser) {
        columnsNames = (List) fullMapFromPZParser.get(FPConstants.DETAIL_ID);
        columnIndexMap = (Map) fullMapFromPZParser.get(FPConstants.COL_IDX);
        xmlRecordElements = fullMapFromPZParser;
    }

    public MetaData(final List columnNames, final Map columnIndexMap) {
        this.columnsNames = Collections.unmodifiableList(columnNames);
        this.columnIndexMap = Collections.unmodifiableMap(columnIndexMap);
    }

    MetaData(final List columnNames, final Map columnIndexMap, final Map xmlRecordElements) {
        this.columnsNames = Collections.unmodifiableList(columnNames);
        this.columnIndexMap = columnIndexMap;
        this.xmlRecordElements = xmlRecordElements;
    }

    public List getColumnsNames() {
        return columnsNames;
    }

    public Map getColumnIndexMap() {
        return columnIndexMap;
    }

    public void setColumnIndexMap(final Map columnIndexMap) {
        this.columnIndexMap = columnIndexMap;
    }

    public void setColumnsNames(final List columnsNames) {
        this.columnsNames = Collections.unmodifiableList(columnsNames);
    }

    public boolean isAnyRecordFormatSpecified() {
        return xmlRecordElements != null && !xmlRecordElements.isEmpty();
    }

    public Iterator xmlRecordIterator() {
        return xmlRecordElements.entrySet().iterator();
    }

    public List getListColumnsForRecord(final String key) {
        return ((XMLRecordElement) xmlRecordElements.get(key)).getColumns();
    }

    public int getColumnIndex(final String key, final String columnName) {
        int idx = -1;
        if (key != null && !key.equals(FPConstants.DETAIL_ID) && !key.equals(FPConstants.COL_IDX)) {
            idx = ((XMLRecordElement) getListColumnsForRecord(key)).getColumnIndex(columnName);
        } else if (key == null || key.equals(FPConstants.DETAIL_ID)) {
            final Integer i = (Integer) columnIndexMap.get(columnName);
            if (i != null) { //happens when the col name does not exist in the mapping
                idx = i.intValue();
            }
        }
        return idx;
    }
}
