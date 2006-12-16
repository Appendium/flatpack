/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting PZFileReader.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 74 2006-10-24 22:19:05Z benoitx $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.pzfilereader.ordering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
public class OrderBy implements Comparator, Serializable {
    private static final long serialVersionUID = 5622406168247149895L;

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
        int result = 0;

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
            } else if (!mdkey0.equals(PZConstants.DETAIL_ID) || !mdkey1.equals(PZConstants.DETAIL_ID)) {
                return !mdkey0.equals(PZConstants.DETAIL_ID) ? 1 : 0;
            }

            // convert to one type of case so the comparator does not take case
            // into account when sorting
            final Comparable comp0 = row0.getValue(ParserUtils.findColumn(
                    oc.getColumnName(), columnMD)).toLowerCase(Locale.getDefault());
            final Comparable comp1 = row1.getValue(ParserUtils.findColumn(
                    oc.getColumnName(), columnMD)).toLowerCase(Locale.getDefault());

            // multiply by the sort indicator to get a ASC or DESC result
            result = comp0.compareTo(comp1) * oc.getSortIndicator();

            // if it is = 0 then the primary sort is done, and it can start the
            // secondary sorts
            if (result != 0) {
                break;
            }
        }

        return result;
    }

    /**
     * @param columnMD
     *            The columnMD to set.
     */
    public void setColumnMD(final List columnMD) {
        this.columnMD = columnMD;
    }
}
