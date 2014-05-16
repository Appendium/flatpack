/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting FlatPack.
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
package net.sf.flatpack.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.flatpack.Parser;
import net.sf.flatpack.converter.Converter;
import net.sf.flatpack.converter.FPConvertException;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.xml.MetaData;
import net.sf.flatpack.xml.XMLRecordElement;

/**
 *  Static utilities that are used to perform parsing in the
 *         DataSet class These can also be used for low level parsing, if not
 *         wishing to use the DataSet class.
 *
 * @author Paul Zepernick
 * @author Benoit Xhenseval
 */
public final class ParserUtils {
    private ParserUtils() {
    }

    /**
     * Returns an ArrayList of items in a delimited string. If there is no
     * qualifier around the text, the qualifier parameter can be left null, or
     * empty. There should not be any line breaks in the string. Each line of
     * the file should be passed in individually.
     * Elements which are not qualified will have leading and trailing white
     * space removed.  This includes unqualified elements, which may be
     * contained in an unqualified parse: "data",  data  ,"data"
     *
     * Special thanks to Benoit for contributing this much improved speedy parser :0)
     *
     * @author Benoit Xhenseval
     * @param line -
     *            String of data to be parsed
     * @param delimiter -
     *            Delimiter seperating each element
     * @param qualifier -
     *            qualifier which is surrounding the text
     * @param initialSize -
     *            intial capacity of the List size
     * @return List
     */
    public static List splitLine(final String line, final char delimiter, final char qualifier, final int initialSize) {
        final List list = new ArrayList(initialSize);

        if (delimiter == 0) {
            list.add(line);
            return list;
        } else if (line == null) {
            return list;
        }

        String trimmedLine;
        if (delimiter == '\t' || delimiter == ' ') {
            //skip the trim for these delimiters, doing the trim will mess up the parse
            //on empty records which contain just the delimiter
            trimmedLine = line;
        } else {
            trimmedLine = line.trim();
        }

        final int size = trimmedLine.length();

        if (size == 0) {
            list.add("");
            return list;
        }

        boolean insideQualifier = false;
        char previousChar = 0;
        int startBlock = 0;
        int endBlock = 0;
        boolean blockWasInQualifier = false;

        final String doubleQualifier = "" + qualifier + qualifier;
        for (int i = 0; i < size; i++) {

            final char currentChar = trimmedLine.charAt(i);
            if (currentChar != delimiter && currentChar != qualifier) {
                previousChar = currentChar;
                endBlock = i + 1;
                continue;
            }

            if (currentChar == delimiter) {
                // we've found the delimiter (eg ,)
                if (!insideQualifier) {
                    String trimmed = trimmedLine.substring(startBlock, endBlock > startBlock ? endBlock : startBlock + 1);
                    if (!blockWasInQualifier) {
                        trimmed = trimmed.trim();
                    } else {
                        //need to run the qualifier replace when it was in qualifier
                        trimmed = trimmed.replaceAll(doubleQualifier, String.valueOf(qualifier));
                    }

                    if (trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
                        list.add("");
                    } else {
                        list.add(trimmed);
                    }
                    blockWasInQualifier = false;
                    startBlock = i + 1;
                }
            } else if (currentChar == qualifier) {
                if (!insideQualifier && previousChar != qualifier) {
                    if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
                        insideQualifier = true;
                        startBlock = i + 1;
                    } else {
                        endBlock = i + 1;
                    }
                } else {
                    if (i + 1 < size && delimiter != ' ') {
                        //this is used to allow unescaped qualifiers to be contained within the element
                        //do not run this check is a space is being used as a delimiter
                        //we don't want to trim the delimiter off
                        //loop until we find a char that is not a space, or we reach the end of the line.
                        int start = i + 1;
                        char charToCheck = trimmedLine.charAt(start);
                        while (charToCheck == ' ') {
                            start++;
                            if (start == size) {
                                break;
                            }
                            charToCheck = trimmedLine.charAt(start);
                        }

                        if (charToCheck != delimiter) {
                            previousChar = currentChar;
                            endBlock = i + 1;
                            continue;
                        }

                    }
                    insideQualifier = false;
                    blockWasInQualifier = true;
                    endBlock = i;
                    // last column (e.g. finishes with ")
                    if (i == size - 1) {
                        String str = trimmedLine.substring(startBlock, size - 1);
                        str = str.replaceAll(doubleQualifier, String.valueOf(qualifier));
                        list.add(str);
                        startBlock = i + 1;
                    }
                }
            }
            previousChar = currentChar;
        }

        if (startBlock < size) {
            String str = trimmedLine.substring(startBlock, size);
            str = str.replaceAll(doubleQualifier, String.valueOf(qualifier));
            if (blockWasInQualifier) {
                if (str.charAt(str.length() - 1) == qualifier) {
                    list.add(str.substring(0, str.length() - 1));
                } else {
                    list.add(str);
                }
            } else {
                list.add(str.trim());
            }
        } else if (trimmedLine.charAt(size - 1) == delimiter) {
            list.add("");
        }

        return list;
    }

    /**
     * reads from the specified point in the line and returns how many chars to
     * the specified delimiter
     *
     * @param line
     * @param start
     * @param delimiter
     * @return int
     */
    public static int getDelimiterOffset(final String line, final int start, final char delimiter) {
        int idx = line.indexOf(delimiter, start);
        if (idx >= 0) {
            idx -= start - 1;
        }
        return idx;
    }

    /**
     * Removes empty space from the beginning of a string
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String lTrim(final String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value;
        int offset = 0;
        final int maxLength = value.length();
        while (offset < maxLength && (value.charAt(offset) == ' ' || value.charAt(offset) == '\t')) {
            offset++;
        }

        if (offset > 0) {
            trimmed = value.substring(offset);
        }

        return trimmed;
    }

    /**
     * Removes empty space from the beginning of a string, except for tabs
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String lTrimKeepTabs(final String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value;
        int offset = 0;
        final int maxLength = value.length();
        while (offset < maxLength && value.charAt(offset) == ' ') {
            offset++;
        }

        if (offset > 0) {
            trimmed = value.substring(offset);
        }

        return trimmed;
    }

    /**
     * Removes empty space from the end of a string
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String rTrim(final String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value;
        int offset = value.length() - 1;
        while (offset > -1 && (value.charAt(offset) == ' ' || value.charAt(offset) == '\t')) {
            offset--;
        }

        if (offset < value.length() - 1) {
            trimmed = value.substring(0, offset + 1);
        }

        return trimmed;
    }

    /**
     * Will return a null if the String is empty returns the
     * trimmed string otherwise.
     *
     * @param value
     *          to be trimmed
     * @return String
     */
    public static String trimToNull(final String value) {
        if (value == null) {
            return null;
        }

        final String ret = value.trim();

        return ret.length() == 0 ? null : ret;

    }

    /**
     * Removes a single string character from a given string
     *
     * @param theChar -
     *            string char
     * @param theString -
     *            string to search
     * @return String
     */
    public static String removeChar(final char theChar, final String theString) {
        final StringBuffer s = new StringBuffer();
        for (int i = 0; i < theString.length(); i++) {
            final char currentChar = theString.charAt(i);
            if (currentChar != theChar) {
                s.append(currentChar);
            }
        }

        return s.toString();
    }

    //    /**
    //     * Returns a list of ColumnMetaData objects. This is for use with delimited
    //     * files. The first line of the file which contains data will be used as the
    //     * column names
    //     *
    //     * @param line
    //     * @param delimiter
    //     * @param qualifier
    //     * @exception Exception
    //     * @return ArrayList - ColumnMetaData
    //     * @deprecated Use getColumnMDFromFile(String, char, char, PZParser)
    //     */
    //    public static Map getColumnMDFromFile(final String line, final char delimiter, final char qualifier) {
    //        return getColumnMDFromFile(line, delimiter, qualifier, null);
    //    }

    //    /**
    //     * Returns a list of ColumnMetaData objects. This is for use with delimited
    //     * files. The first line of the file which contains data will be used as the
    //     * column names
    //     *
    //     * @param line
    //     * @param delimiter
    //     * @param qualifier
    //     * @param p
    //     *          PZParser used to specify additional option when working with the ColumnMetaData. Can be null
    //     * @return ArrayList - ColumnMetaData
    //     * @deprecated use the getPZMetaDataFromFile
    //     */
    //    public static Map getColumnMDFromFile(final String line, final char delimiter, final char qualifier, final Parser p) {
    //        List lineData = null;
    //        final List results = new ArrayList();
    //        final Map columnMD = new LinkedHashMap();
    //
    //        lineData = splitLine(line, delimiter, qualifier, FPConstants.SPLITLINE_SIZE_INIT);
    //        for (int i = 0; i < lineData.size(); i++) {
    //            final ColumnMetaData cmd = new ColumnMetaData();
    //            cmd.setColName((String) lineData.get(i));
    //            results.add(cmd);
    //        }
    //
    //        columnMD.put(FPConstants.DETAIL_ID, results);
    //        columnMD.put(FPConstants.COL_IDX, buidColumnIndexMap(results, p));
    //
    //        return columnMD;
    //    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited
     * files. The first line of the file which contains data will be used as the
     * column names
     *
     * @param line
     * @param delimiter
     * @param qualifier
     * @param p
     *          PZParser used to specify additional option when working with the ColumnMetaData. Can be null
     * @return PZMetaData
     */
    public static MetaData getPZMetaDataFromFile(final String line, final char delimiter, final char qualifier, final Parser p) {
        List lineData = null;
        final List results = new ArrayList();
        final Set dupCheck = new HashSet();

        lineData = splitLine(line, delimiter, qualifier, FPConstants.SPLITLINE_SIZE_INIT);
        for (int i = 0; i < lineData.size(); i++) {
            final ColumnMetaData cmd = new ColumnMetaData();
            cmd.setColName((String) lineData.get(i));
            if (dupCheck.contains(cmd.getColName())) {
                throw new FPException("Duplicate Column Name In File: " + cmd.getColName());
            }
            results.add(cmd);
            dupCheck.add(cmd.getColName());
        }

        return new MetaData(results, buidColumnIndexMap(results, p));
    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited
     * files. The first line of the file which contains data will be used as the
     * column names
     *
     * @param theFile
     * @param delimiter
     * @param qualifier
     * @exception FileNotFoundException
     * @exception IOException
     * @return ArrayList - ColumnMetaData
     */
    public static List getColumnMDFromFile(final File theFile, final String delimiter, final String qualifier) throws IOException {
        BufferedReader br = null;
        FileReader fr = null;
        String line = null;
        List lineData = null;
        final List results = new ArrayList();

        try {
            fr = new FileReader(theFile);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                lineData = splitLine(line, delimiter.charAt(0), qualifier.charAt(0), FPConstants.SPLITLINE_SIZE_INIT);
                for (int i = 0; i < lineData.size(); i++) {
                    final ColumnMetaData cmd = new ColumnMetaData();
                    cmd.setColName((String) lineData.get(i));
                    results.add(cmd);
                }
                break;
            }
        } finally {
            if (lineData != null) {
                lineData.clear();
            }
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
        return results;
    }

    //    /**
    //     * @param columnName
    //     * @param columnMD -
    //     *            vector of ColumnMetaData objects
    //     * @return int - position of the column in the file
    //     * @throws NoSuchElementException
    //     * @deprecated surely not...
    //     */
    //    public static int findColumn(final String columnName, final List columnMD) {
    //        for (int i = 0; i < columnMD.size(); i++) {
    //            final ColumnMetaData cmd = (ColumnMetaData) columnMD.get(i);
    //            if (cmd.getColName().equalsIgnoreCase(columnName)) {
    //                return i;
    //            }
    //        }
    //
    //        throw new NoSuchElementException("Column Name: " + columnName + " does not exist");
    //    }

    /**
     * Determines if the given line is the first part of a multiline record.  It does this by verifying that the
     * qualifer on the last element is not closed
     *
     * @param chrArry -
     *            char data of the line
     * @param delimiter -
     *            delimiter being used
     * @param qualifier -
     *            qualifier being used
     * @return boolean
     */
    public static boolean isMultiLine(final char[] chrArry, final char delimiter, final char qualifier) {

        // check if the last char is the qualifier, if so then this a good
        // chance it is not multiline
        if (chrArry[chrArry.length - 1] != qualifier) {
            // could be a potential line break
            boolean qualiFound = false;
            for (int i = chrArry.length - 1; i >= 0; i--) {
            	//System.out.println(chrArry[i]);
                if (chrArry[i] == ' ') {
                    continue;
                }
                
                // check to see if we can find a qualifier followed by a
                // delimiter
                // remember we are working are way backwards on the line
                if (qualiFound) {
                    if (chrArry[i] == delimiter) {
                        return true;
                    }
                    //guard against multiple qualifiers in the sequence [ ,""We  ]
                    qualiFound = chrArry[i] == qualifier;
                } else if (chrArry[i] == delimiter) {
                    // if we have a delimiter followed by a qualifier, then we
                    // have moved on to a new element and this could not be multiline.
                    // start a new loop here in case there is
                    // space between the delimiter and qualifier
                    for (int j = i - 1; j >= 0; j--) {
                        if (chrArry[j] == ' ') {
                            continue;
                        } else if (chrArry[j] == qualifier) {
                            return false;
                        }
                        break;
                    }

                } else if (chrArry[i] == qualifier) {
                    qualiFound = true;
                }
            }
        } else {
            // we have determined that the last char on the line is a qualifier.
            // This most likely means
            // that this is not multiline, however we must account for the
            // following scenario
            // data,data,"
            // data
            // /data"
            for (int i = chrArry.length - 1; i >= 0; i--) {
                if (i == chrArry.length - 1 || chrArry[i] == ' ') {
                    // skip the first char, or any spaces we come across between
                    // the delimiter and qualifier
                    continue;
                }
                if (chrArry[i] == delimiter) {
                    return true;
                }
                break;
            }
        }

        return false;
    }

    //    /**
    //     * Returns a map with the MD id's and their record lengths. This is used for
    //     * fixed length parsing
    //     *
    //     * @param columnMD
    //     * @return Map
    //     * @deprecated use PZMetaData
    //     */
    //    public static Map calculateRecordLengths(final Map columnMD) {
    //        final Map recordLengths = new HashMap();
    //        List cmds = null;
    //
    //        final Iterator columnMDIt = columnMD.entrySet().iterator();
    //        while (columnMDIt.hasNext()) {
    //            final Entry entry = (Entry) columnMDIt.next();
    //            if (entry.getKey().equals(FPConstants.DETAIL_ID) || entry.getKey().equals(FPConstants.COL_IDX)) {
    //                cmds = (List) columnMD.get(FPConstants.DETAIL_ID);
    //            } else {
    //                cmds = ((XMLRecordElement) entry.getValue()).getColumns();
    //            }
    //
    //            int recordLength = 0;
    //            for (int i = 0; i < cmds.size(); i++) {
    //                recordLength += ((ColumnMetaData) cmds.get(i)).getColLength();
    //            }
    //
    //            recordLengths.put(entry.getKey(), new Integer(recordLength));
    //
    //        }
    //
    //        return recordLengths;
    //
    //    }

    public static Map calculateRecordLengths(final MetaData columnMD) {
        final Map recordLengths = new HashMap();
        List cmds = null;

        // first the basic columns
        int recordLength = 0;
        for (final Iterator i = columnMD.getColumnsNames().iterator(); i.hasNext();) {
            recordLength += ((ColumnMetaData) i.next()).getColLength();
        }

        recordLengths.put(FPConstants.DETAIL_ID, Integer.valueOf(recordLength));

        final Iterator columnMDIt = columnMD.xmlRecordIterator();
        while (columnMDIt.hasNext()) {
            final Entry entry = (Entry) columnMDIt.next();
            cmds = ((XMLRecordElement) entry.getValue()).getColumns();

            recordLength = 0;
            for (int i = 0; i < cmds.size(); i++) {
                recordLength += ((ColumnMetaData) cmds.get(i)).getColLength();
            }

            recordLengths.put(entry.getKey(), Integer.valueOf(recordLength));

        }

        return recordLengths;

    }

    //    /**
    //     * Returns the key to the list of ColumnMetaData objects. Returns the
    //     * correct MetaData per the mapping file and the data contained on the line
    //     *
    //     *
    //     * @param columnMD
    //     * @param lineElements
    //     * @return List - ColumMetaData
    //     * @deprecated use the PZMetaData
    //     */
    //    public static String getCMDKeyForDelimitedFile(final Map columnMD, final List lineElements) {
    //        if (columnMD.size() == 1) {
    //            // no <RECORD> elments were specifed for this parse, just return the
    //            // detail id
    //            return FPConstants.DETAIL_ID;
    //        }
    //        final Iterator mapEntries = columnMD.entrySet().iterator();
    //        // loop through the XMLRecordElement objects and see if we need a
    //        // different MD object
    //        while (mapEntries.hasNext()) {
    //            final Entry entry = (Entry) mapEntries.next();
    //            if (entry.getKey().equals(FPConstants.DETAIL_ID) || entry.getKey().equals(FPConstants.COL_IDX)) {
    //                continue; // skip this key will be assumed if none of the
    //                // others match
    //            }
    //            final XMLRecordElement recordXMLElement = (XMLRecordElement) entry.getValue();
    //
    //            if (recordXMLElement.getElementCount() > 0 && recordXMLElement.getElementCount() == lineElements.size()) {
    //                //determing which <record> mapping to use by the number of elements
    //                //contained on the line
    //                return (String) entry.getKey();
    //            } else if (recordXMLElement.getElementNumber() > lineElements.size()) {
    //                // make sure the element referenced in the mapping exists
    //                continue;
    //            }
    //
    //            final String lineElement = (String) lineElements.get(recordXMLElement.getElementNumber() - 1);
    //            if (lineElement.equals(recordXMLElement.getIndicator())) {
    //                // we found the MD object we want to return
    //                return (String) entry.getKey();
    //            }
    //
    //        }
    //
    //        // must be a detail line
    //        return FPConstants.DETAIL_ID;
    //    }

    public static String getCMDKeyForDelimitedFile(final MetaData columnMD, final List lineElements) {
        if (!columnMD.isAnyRecordFormatSpecified()) {
            // no <RECORD> elments were specifed for this parse, just return the
            // detail id
            return FPConstants.DETAIL_ID;
        }
        final Iterator mapEntries = columnMD.xmlRecordIterator();//getColumnsNames().iterator();
        // loop through the XMLRecordElement objects and see if we need a
        // different MD object
        while (mapEntries.hasNext()) {
            final Entry entry = (Entry) mapEntries.next();
            //            if (entry.getKey().equals(PZConstants.DETAIL_ID) || entry.getKey().equals(PZConstants.COL_IDX)) {
            //                continue; // skip this key will be assumed if none of the
            //                // others match
            //            }
            final XMLRecordElement recordXMLElement = (XMLRecordElement) entry.getValue();

            if (recordXMLElement.getElementCount() > 0 && recordXMLElement.getElementCount() == lineElements.size()) {
                //determing which <record> mapping to use by the number of elements
                //contained on the line
                return (String) entry.getKey();
            } else if (recordXMLElement.getElementNumber() > lineElements.size()) {
                // make sure the element referenced in the mapping exists
                continue;
            }

            final String lineElement = (String) lineElements.get(recordXMLElement.getElementNumber() - 1);
            if (lineElement.equals(recordXMLElement.getIndicator())) {
                // we found the MD object we want to return
                return (String) entry.getKey();
            }

        }

        // must be a detail line
        return FPConstants.DETAIL_ID;
    }

    //    /**
    //     * Returns a list of ColumMetaData objects for the given key
    //     *
    //     * @param key
    //     * @param columnMD
    //     * @return List
    //     * @deprecated use the PZMetaData
    //     */
    //    public static List getColumnMetaData(final String key, final Map columnMD) {
    //        if (key == null || key.equals(FPConstants.DETAIL_ID) || key.equals(FPConstants.COL_IDX)) {
    //            return (List) columnMD.get(FPConstants.DETAIL_ID);
    //        }
    //
    //        return ((XMLRecordElement) columnMD.get(key)).getColumns();
    //    }

    public static List getColumnMetaData(final String key, final MetaData columnMD) {
        if (key == null || key.equals(FPConstants.DETAIL_ID) || key.equals(FPConstants.COL_IDX)) {
            return columnMD.getColumnsNames();
        }

        return columnMD.getListColumnsForRecord(key);
    }

    //    /**
    //     * Use this method to find the index of a column.
    //     *
    //     * @author Benoit Xhenseval
    //     * @author Paul Zepernick
    //     * @param key
    //     * @param columnMD
    //     * @param colName
    //     * @param p
    //     *          Can be null.  Used to specify potential options on how the column should be retrieved
    //     * @return -1 if it does not find it
    //     * @deprecated use PZMetaData
    //     */
    //    public static int getColumnIndex(final String key, final Map columnMD, final String colName, final Parser p) {
    //        int idx = -1;
    //        String column = colName;
    //        if (p != null && !p.isColumnNamesCaseSensitive()) {
    //            column = colName.toLowerCase(Locale.getDefault());
    //        }
    //        if (key != null && !key.equals(FPConstants.DETAIL_ID) && !key.equals(FPConstants.COL_IDX)) {
    //            idx = ((XMLRecordElement) columnMD.get(key)).getColumnIndex(column);
    //        } else if (key == null || key.equals(FPConstants.DETAIL_ID)) {
    //            final Map map = (Map) columnMD.get(FPConstants.COL_IDX);
    //            final Integer i = (Integer) map.get(column);
    //            if (i != null) { //happens when the col name does not exist in the mapping
    //                idx = i.intValue();
    //            }
    //        }
    //
    //        if (idx < 0) {
    //            throw new NoSuchElementException("Column " + colName + " does not exist, check case/spelling. key:" + key);
    //        }
    //        return idx;
    //    }

    public static int getColumnIndex(final String key, final MetaData columnMD, final String colName, final boolean columNameCaseSensitive) {
        int idx = -1;
        String column = colName;
        if (!columNameCaseSensitive) {
            column = colName.toLowerCase(Locale.getDefault());
        }
        idx = columnMD.getColumnIndex(key, column);

        //        if (key != null && !key.equals(PZConstants.DETAIL_ID) && !key.equals(PZConstants.COL_IDX)) {
        //            idx = ((XMLRecordElement) columnMD.get(key)).getColumnIndex(column);
        //        } else if (key == null || key.equals(PZConstants.DETAIL_ID)) {
        //            final Map map = (Map) columnMD.get(PZConstants.COL_IDX);
        //            final Integer i = (Integer) map.get(column);
        //            if (i != null) { //happens when the col name does not exist in the mapping
        //                idx = i.intValue();
        //            }
        //        }

        if (idx < 0) {
            throw new NoSuchElementException("Column [" + column + "] does not exist, check case/spelling. key:" + key);
        }
        return idx;
    }

    /**
     * Use this method to find the index of a column.
     *
     * @author Benoit Xhenseval
     * @param key
     * @param columnMD
     * @param colName
     * @return -1 if it does not find it
     * @deprecated use getColumnIndex(String, Map, String, PZParser)
     */
    //    public static int getColumnIndex(final String key, final Map columnMD, final String colName) {
    //        return getColumnIndex(key, columnMD, colName, null);
    //    }

    /**
     * Create an InputStream based on a File.
     *
     * @param file
     *            The file.
     * @return the InputStream.
     * @throws FileNotFoundException
     */
    public static InputStream createInputStream(final File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("null not allowed");
        }
        return new FileInputStream(file);
    }

    /**
     * Closes the given reader
     *
     * @param reader
     *
     */
    public static void closeReader(final Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (final IOException ex) {
            throw new FPException(ex);
        }
    }

    /**
     * Closes the given reader
     *
     * @param reader
     *
     */
    public static void closeReader(final InputStream reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (final IOException ex) {
            throw new FPException(ex);
        }
    }

    /**
     * <p>
     * Returns padding using the specified delimiter repeated to a given length.
     * </p>
     *
     * <pre>
     *                PZStringUtils.padding(0, 'e')  = &quot;&quot;
     *                PZStringUtils.padding(3, 'e')  = &quot;eee&quot;
     *                PZStringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * <p>
     * Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
     * Supplementary Characters</a> as they require a pair of <code>char</code>s
     * to be represented. If you are needing to support full I18N of your
     * applications consider using {@link #repeat(String, int)} instead.
     * </p>
     *
     * @param repeat
     *            number of times to repeat delim
     * @param padChar
     *            character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException
     *             if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    public static String padding(final int repeat, final char padChar) {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    /**
     * Build a map of name/position based on a list of ColumnMetaData.
     *
     * @author Benoit Xhenseval
     * @author Paul Zepernick
     * @param columns
     * @param p
     *         Reference to Parser which can provide additional options on how the
     *         map should be build.  This can be NULL.
     * @return a new Map
     */
    public static Map buidColumnIndexMap(final List columns, final Parser p) {
        Map map = null;
        if (columns != null && !columns.isEmpty()) {
            map = new HashMap();
            int idx = 0;
            for (final Iterator it = columns.iterator(); it.hasNext(); idx++) {
                final ColumnMetaData meta = (ColumnMetaData) it.next();
                String colName = meta.getColName();
                if (p != null && !p.isColumnNamesCaseSensitive()) {
                    //user has selected to make column names case sensitive
                    //on lookups
                    colName = colName.toLowerCase(Locale.getDefault());
                }
                map.put(colName, Integer.valueOf(idx));
            }
        }
        return map;
    }

    /**
     * Build a map of name/position based on a list of ColumnMetaData.
     *
     * @author Benoit Xhenseval
     * @param columns
     * @return a new Map
     * @deprecated Please use buildColumnIndexMap(List, PZParser)
     */
    //    public static Map buidColumnIndexMap(final List columns) {
    //        return buidColumnIndexMap(columns, null);
    //    }

    /**
     * Removes chars from the String that could not
     * be parsed into a Long value
     *
     *      PZStringUtils.stripNonLongChars("1000.25") = "1000"
     *
     * Method will truncate everything to the right of the decimal
     * place when encountered.
     *
     * @param value
     * @return String
     */
    public static String stripNonLongChars(final String value) {
        final StringBuffer newString = new StringBuffer();

        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c == '.') {
                //stop if we hit a decimal point
                break;
            } else if (c >= '0' && c <= '9' || c == '-') {
                newString.append(c);
            }
        }
        // check to make sure we do not have a single length string with
        // just a minus sign
        final int sLen = newString.length();
        final String s = newString.toString();
        if (sLen == 0 || (sLen == 1 && "-".equals(s))) {
            return "0";
        }

        return newString.toString();
    }

    /**
     * Removes chars from the String that could not
     * be parsed into a Double value
     *
     * @param value
     * @return String
     */
    public static String stripNonDoubleChars(final String value) {
        final StringBuffer newString = new StringBuffer();

        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c >= '0' && c <= '9' || c == '-' || c == '.') {
                newString.append(c);
            }
        }
        final int sLen = newString.length();
        final String s = newString.toString();
        if (sLen == 0 || (sLen == 1 && (".".equals(s) || "-".equals(s)))) {
            return "0";
        }

        return newString.toString();
    }

    /**
     * Retrieves the conversion table for use with the getObject()
     * method in IDataSet
     *
     * @throws IOException
     * @return Properties
     *              Properties contained in the pzconvert.properties file
     */
    public static Properties loadConvertProperties() throws IOException {
        final Properties pzConvertProps = new Properties();
        final URL url = ParserUtils.class.getClassLoader().getResource("fpconvert.properties");
        pzConvertProps.load(url.openStream());

        return pzConvertProps;
    }

    /**
     * Checks a list of <String> elements to see if every element
     * in the list is empty.
     * 
     * @param l
     *          List of <String>
     * @return boolean
     *              true when all elements are empty
     */
    public static boolean isListElementsEmpty(final List l) {
        final Iterator it = l.iterator();
        while (it.hasNext()) {
            final String s = (String) it.next();
            if (s != null && s.trim().length() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a String value to the appropriate Object via
     * the correct net.sf.flatpack.converter.PZConverter implementation
     *
     * @param classXref
     *             Properties holding class cross reference
     * @param value
     *             Value to be converted to the Object
     * @param typeToReturn
     *             Type of object to be returned
     * @throws PZConvertExeption
     * @return Object
     */
    public static Object runPzConverter(final Properties classXref, final String value, final Class typeToReturn) {
        final String sConverter = classXref.getProperty(typeToReturn.getName());
        if (sConverter == null) {
            throw new FPConvertException(typeToReturn.getName() + " is not registered in pzconvert.properties");
        }
        try {
            final Converter pzconverter = (Converter) Class.forName(sConverter).newInstance();
            return pzconverter.convertValue(value);
        } catch (final IllegalAccessException ex) {
            throw new FPConvertException(ex);
        } catch (final InstantiationException ex) {
            throw new FPConvertException(ex);
        } catch (final ClassNotFoundException ex) {
            throw new FPConvertException(ex);
        }
    }

    
    /**
     * Returns a definition of pz column metadata from a given
     * pz datastructure held in an SQL database
     *
     * @param con
     *          Database connection containing the Datafile and Datastructure
     *          tables
     * @param dataDefinition
     *          Name of the data definition stored in the Datafile table
     * @throws SQLException
     * @return List
     */
    public static List buildMDFromSQLTable(final Connection con, final String dataDefinition) throws SQLException {
        return buildMDFromSQLTable(con, dataDefinition, null);
    }
    
    
    /**
     * Returns a definition of pz column metadata from a given
     * pz datastructure held in an SQL database
     *
     * @param con
     *          Database connection containing the Datafile and Datastructure
     *          tables
     * @param dataDefinition
     *          Name of the data definition stored in the Datafile table
     * @param parser
     * 			Instance of the parser being used for the file.  It will be checked to get the table names 
     * 			for the DATASTRUCTURE table and DATAFILE table.
     * @throws SQLException
     * @return List
     */
    public static List buildMDFromSQLTable(final Connection con, final String dataDefinition, final Parser parser) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        final List cmds = new ArrayList();
        String dfTbl = parser != null ? parser.getDataFileTable() : "DATAFILE";
        String dsTbl = parser != null ? parser.getDataStructureTable() : "DATASTRUCTURE";
        try {
        	final StringBuffer sqlSb = new StringBuffer();
        	
        	sqlSb.append("SELECT * FROM ").append(dfTbl).append(" INNER JOIN ").append(dsTbl).append(" ON ").append(dfTbl).append(".DATAFILE_NO = ").append(dsTbl).append(".DATAFILE_NO " +
                            "WHERE DATAFILE_DESC = ? ORDER BY DATASTRUCTURE_COL_ORDER");

            stmt = con.prepareStatement(sqlSb.toString()); // always use PreparedStatement
            // as the DB can do clever things.
            stmt.setString(1, dataDefinition);
            rs = stmt.executeQuery();

            int recPosition = 1;
            // put array of columns together. These will be used to put together
            // the dataset when reading in the file
            while (rs.next()) {

                final ColumnMetaData column = new ColumnMetaData();
                column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
                column.setColLength(rs.getInt("DATASTRUCTURE_LENGTH"));
                column.setStartPosition(recPosition);
                column.setEndPosition(recPosition + (rs.getInt("DATASTRUCTURE_LENGTH") - 1));
                recPosition += rs.getInt("DATASTRUCTURE_LENGTH");

                cmds.add(column);
            }
            
            if (cmds.isEmpty()) {
            	throw new FPException("Data File Key [" + dataDefinition + "] Is Not In The database OR No Columns Specified In Table");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

        return cmds;
    }

    //LEAVE AS A REFERENCE FOR POSSIBLE LATER USE
    /* public static List splitLineWithBuf(String line, final char delimiter, char qualifier, int initialSize) {
     List list = new ArrayList(initialSize);

     if (delimiter == 0) {
     list.add(line);
     return list;
     } else if (line == null) {
     return list;
     }

     final String trimmedLine = line.trim();
     int size = trimmedLine.length();

     if (size == 0) {
     list.add("");
     return list;
     }

     boolean insideQualifier = false;
     char previousChar = 0;
     boolean blockWasInQualifier = false;
     StringBuffer buf = new StringBuffer(32);

     // final String doubleQualifier = String.valueOf(qualifier) +
     // String.valueOf(qualifier);
     for (int i = 0; i < size; i++) {
     final char currentChar = trimmedLine.charAt(i);
     if (currentChar != delimiter && currentChar != qualifier) {
     previousChar = currentChar;
     if (' ' != currentChar || insideQualifier || buf.length() > 0) {
     buf.append(currentChar);
     }
     continue;
     }

     if (currentChar == delimiter) {
     // we've found the delimiter (eg ,)
     if (!insideQualifier) {
     // String trimmed = trimmedLine.substring(startBlock,
     // endBlock > startBlock ? endBlock : startBlock + 1);
     String trimmed = buf.toString();
     if (!blockWasInQualifier) {
     trimmed = trimmed.trim();
     // trimmed = trimmed.replaceAll(doubleQualifier,
     // String.valueOf(qualifier));
     }

     if (trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
     list.add("");
     } else {
     list.add(trimmed);
     }
     blockWasInQualifier = false;
     buf.delete(0, buf.length());
     } else if (buf.length() != 1 || buf.charAt(0) != qualifier) {
     buf.append(currentChar);
     } else {
     buf.delete(0, buf.length());
     insideQualifier = false;
     list.add("");
     }
     } else if (currentChar == qualifier) {
     if (!insideQualifier && previousChar != qualifier) {
     if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
     insideQualifier = true;
     int l = buf.length();
     if (l > 0) {
     buf.delete(0, l); // just entered a
     // qualifier, remove
     // whatever was
     }
     } else {
     buf.append(currentChar);
     }
     } else {
     insideQualifier = false;
     blockWasInQualifier = true;
     if (previousChar == qualifier) {
     buf.append(qualifier);
     insideQualifier = true;
     previousChar = 0;
     continue;
     }
     // last column (e.g. finishes with ")
     if (i == size - 1) {
     // list.add(trimmedLine.substring(startBlock, size -
     // 1));
     list.add(buf.toString());
     buf.delete(0, buf.length());
     }
     }
     }
     previousChar = currentChar;
     }

     if (buf.length() > 0) {
     list.add(buf.toString().trim());
     } else if (trimmedLine.charAt(size - 1) == delimiter) {
     list.add("");
     }

     return list;
     }*/
}
