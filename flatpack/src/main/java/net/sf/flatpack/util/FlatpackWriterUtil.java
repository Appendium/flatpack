/**
 *
 */
package net.sf.flatpack.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;

/**
 * Helper class to create ColumnMetaData and Mapping for the Writer Factory.
 * @author Benoit Xhenseval
 */
public final class FlatpackWriterUtil {
    private FlatpackWriterUtil() {
    }

    /**
     * Creates a Mapping for a WriterFactory for the given list of columns.
     * @param colsAsCsv comma-separated column names
     * @return a map to be used in, for instance, DelimiterWriterFactory
     */
    public static Map<String, Object> buildParametersForColumns(final String colsAsCsv) {
        final Map<String, Object> mapping = new HashMap<>();
        mapping.put(FPConstants.DETAIL_ID, buildColumns(colsAsCsv));
        return mapping;
    }

    /**
     * Create a new list of ColumnMetaData based on a CSV list of column titles.
     * @param colsAsCsv
     * @return new list
     */
    public static List<ColumnMetaData> buildColumns(final String colsAsCsv) {
        final List<ColumnMetaData> listCol = new ArrayList<>();
        buildColumns(listCol, colsAsCsv);
        return listCol;
    }

    public static void buildColumns(final List<ColumnMetaData> listCol, final Collection<String> cols) {
        if (cols != null) {
            cols.forEach(s -> listCol.add(new ColumnMetaData(s)));
        }
    }

    public static void buildColumns(final List<ColumnMetaData> listCol, final String cols) {
        if (cols != null) {
            for (final String s : cols.split(",")) {
                listCol.add(new ColumnMetaData(s));
            }
        }
    }

    public static void buildColumns(final List<ColumnMetaData> listCol, final String... cols) {
        if (cols != null) {
            for (final String s : cols) {
                listCol.add(new ColumnMetaData(s));
            }
        }
    }
}
