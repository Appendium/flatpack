/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed 
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 CONDITIONS OF ANY KIND, either express or implied. See the License for 
 the specific language governing permissions and limitations under the License.  
 */
package net.sf.pzfilereader.ordering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sf.pzfilereader.structure.Row;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

/**
 * @author paul zepernick
 * 
 * resorts an array of objects. Arrays get sorted by OrderElements. Right now,
 * this class will only handle string comparisons.
 * 
 * @version 2.0
 */
public class OrderBy implements Comparator {
    /** collection of order elements to sort by */
    private final ArrayList orderbys = new ArrayList();

    /** column meta data */
    private List columnMD = null;

    /**
     * Adds an order element to the sort.
     * 
     * @param oc -
     *            OrderColumn
     */
    public void addOrderColumn(final OrderColumn oc) {
        orderbys.add(oc);
    }

    /**
     * overridden from the Comparator class.
     * 
     * Performs the sort
     * 
     * @return int
     */
    public int compare(final Object arg0, final Object arg1) {
        final Row row0 = (Row) arg0;
        final Row row1 = (Row) arg1;

        for (int i = 0; i < orderbys.size(); i++) {
            final OrderColumn oc = (OrderColumn) orderbys.get(i);
            // null indicates "detail" record which is what the parser assigns
            // to <column> 's setup outside of <record> elements
            final String mdkey0 = row0.getMdkey() == null ? PZConstants.DETAIL_ID : row0.getMdkey();
            final String mdkey1 = row1.getMdkey() == null ? PZConstants.DETAIL_ID : row1.getMdkey();

            // shift all non detail records to the bottom of the DataSet
            if (!mdkey0.equals(PZConstants.DETAIL_ID) && !mdkey1.equals(PZConstants.DETAIL_ID)) {
                // keep headers / trailers in the same order at the bottom of
                // the DataSet
                return 0;
            } else if (!mdkey0.equals(PZConstants.DETAIL_ID)) {
                return 1;
            } else if (!mdkey1.equals(PZConstants.DETAIL_ID)) {
                return 0;
            }

            // convert to one type of case so the comparator does not take case
            // into account when sorting
            final Comparable comp0 = row0.getValue(ParserUtils.findColumn(oc.getColumnName(), columnMD)).toLowerCase();
            final Comparable comp1 = row1.getValue(ParserUtils.findColumn(oc.getColumnName(), columnMD)).toLowerCase();

            // + BX will never be equal to null.
            // if (comp0 == null) {
            // comp0 = new String("");
            // }
            // if (comp1 == null) {
            // comp1 = new String("");
            // }

            // multiply by the sort indicator to get a ASC or DESC result
            final int result = comp0.compareTo(comp1) * oc.getSortIndicator();

            // if it is = 0 then the primary sort is done, and it can start the
            // secondary sorts
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    /**
     * @param columnMD
     *            The columnMD to set.
     */
    public void setColumnMD(final List columnMD) {
        this.columnMD = columnMD;
    }
}
