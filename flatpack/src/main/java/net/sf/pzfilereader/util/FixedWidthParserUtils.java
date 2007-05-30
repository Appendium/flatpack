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
package net.sf.pzfilereader.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.xml.PZMetaData;
import net.sf.pzfilereader.xml.XMLRecordElement;

/**
 * Collection of parser utilities related to the parsing of fixed width files.
 * 
 * @author paul zepernick
 */
public final class FixedWidthParserUtils {
    private FixedWidthParserUtils() {
    }

    /**
     * Splits up a fixed width line of text
     * 
     * @param columnMetaData
     *            Collection of ColumnMetaData to parse the line
     * @param lineToParse
     *            Line of text to be parsed against the ColumnMetaData
     * @return List Collection of Strings. Each element representing a column
     */
    public static List splitFixedText(final List columnMetaData, final String lineToParse) {
        final List splitResult = new ArrayList();
        int recPosition = 1;
        for (int i = 0; i < columnMetaData.size(); i++) {
            final ColumnMetaData colMetaDataObj = (ColumnMetaData) columnMetaData.get(i);
            final String tempValue = lineToParse.substring(recPosition - 1, recPosition + colMetaDataObj.getColLength() - 1);
            recPosition += colMetaDataObj.getColLength();
            splitResult.add(tempValue.trim());
        }

        return splitResult;
    }

    /**
     * Returns the key to the list of ColumnMetaData objects. Returns the
     * correct MetaData per the mapping file and the data contained on the line
     * 
     * 
     * @param columnMD
     * @param line
     * @return List - ColumMetaData
     * @deprecated use the PZMetaData
     */
    public static String getCMDKey(final Map columnMD, final String line) {
        if (columnMD.size() == 1) {
            // no <RECORD> elments were specifed for this parse, just return the
            // detail id
            return PZConstants.DETAIL_ID;
        }
        final Iterator mapEntries = columnMD.entrySet().iterator();
        // loop through the XMLRecordElement objects and see if we need a
        // different MD object
        while (mapEntries.hasNext()) {
            final Entry entry = (Entry) mapEntries.next();
            if (entry.getKey().equals(PZConstants.DETAIL_ID) || entry.getKey().equals(PZConstants.COL_IDX)) {
                continue; // skip this key will be assumed if none of the
                // others match
            }
            final XMLRecordElement recordXMLElement = (XMLRecordElement) entry.getValue();

            if (recordXMLElement.getEndPositition() > line.length()) {
                // make sure our substring is not going to fail
                continue;
            }
            final int subfrm = recordXMLElement.getStartPosition() - 1; // convert
            // to 0
            // based
            final int subto = recordXMLElement.getEndPositition();
            if (line.substring(subfrm, subto).equals(recordXMLElement.getIndicator())) {
                // we found the MD object we want to return
                return (String) entry.getKey();
            }

        }

        // must be a detail line
        return PZConstants.DETAIL_ID;

    }

    /**
     * Returns the key to the list of ColumnMetaData objects. Returns the
     * correct MetaData per the mapping file and the data contained on the line
     * 
     * 
     * @param columnMD
     * @param line
     * @return List - ColumMetaData
     */
    public static String getCMDKey(final PZMetaData columnMD, final String line) {
        if (!columnMD.isAnyRecordFormatSpecified()) {
            // no <RECORD> elments were specifed for this parse, just return the
            // detail id
            return PZConstants.DETAIL_ID;
        }
        final Iterator mapEntries = columnMD.xmlRecordIterator();
        // loop through the XMLRecordElement objects and see if we need a
        // different MD object
        while (mapEntries.hasNext()) {
            final Entry entry = (Entry) mapEntries.next();
            //            if (entry.getKey().equals(PZConstants.DETAIL_ID) || entry.getKey().equals(PZConstants.COL_IDX)) {
            //                continue; // skip this key will be assumed if none of the
            // others match
            //            }
            final XMLRecordElement recordXMLElement = (XMLRecordElement) entry.getValue();

            if (recordXMLElement.getEndPositition() > line.length()) {
                // make sure our substring is not going to fail
                continue;
            }
            final int subfrm = recordXMLElement.getStartPosition() - 1; // convert
            // to 0
            // based
            final int subto = recordXMLElement.getEndPositition();
            if (line.substring(subfrm, subto).equals(recordXMLElement.getIndicator())) {
                // we found the MD object we want to return
                return (String) entry.getKey();
            }

        }

        // must be a detail line
        return PZConstants.DETAIL_ID;

    }
}
