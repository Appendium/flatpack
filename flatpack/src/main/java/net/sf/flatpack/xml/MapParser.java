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
package net.sf.flatpack.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.flatpack.Parser;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

/**
 * @author zepernick
 *
 * Parses a PZmap definition XML file
 */
public final class MapParser {
    private static boolean showDebug = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(MapParser.class);

    /**
     * Constructor
     */
    private MapParser() {
    }

    /**
     * Method based on InputStream. Reads the XMLDocument for a PZMetaData
     * file from an InputStream, WebStart compatible. Parses the XML file, and
     * returns a Map containing Lists of ColumnMetaData.
     *
     * @param xmlStream
     * @return Map &lt;records&gt; with their corresponding
     * @throws IOException
     * @throws JDOMException
     * @deprecated please use parse(Reader)
     */
    @Deprecated
    public static Map parse(final InputStream xmlStream) throws JDOMException, IOException {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(xmlStream);
            return parse(isr, null);
        } finally {
            if (isr != null) {
                isr.close();
            }
        }
    }

    /**
     * New method based on Reader. Reads the XMLDocument for a PZMetaData
     * file from an InputStream, WebStart compatible. Parses the XML file, and
     * returns a Map containing Lists of ColumnMetaData.
     *
     * @param xmlStreamReader
     * @param pzparser
     *          Can be null.  Allows additional opts to be set during the XML map read
     * @return Map &lt;records&gt; with their corresponding
     * @throws IOException
     * @throws JDOMException
     */
    public static Map<String, Object> parse(final Reader xmlStreamReader, final Parser pzparser) throws JDOMException, IOException {
        if (xmlStreamReader == null) {
            throw new NullPointerException("XML Reader Is Not Allowed To Be Null...");
        }
        final SAXBuilder builder = new SAXBuilder();
        builder.setValidation(true);
        // handle the ability to pull DTD from Jar if needed
        builder.setEntityResolver(new ResolveLocalDTD());

        // JDOM started to blow up on the parse if the system id param was not
        // specified
        // not sure why this started to happen now. Was not making to
        // EntityResolver to pull
        // dtd out of the jar if needed
        final Document document = builder.build(xmlStreamReader, "file:///");

        final Element root = document.getRootElement();

        // lets first get all of the columns that are declared directly under
        // the PZMAP
        List<ColumnMetaData> columns = getColumnChildren(root);
        final Map<String, Object> mdIndex = new LinkedHashMap<String, Object>(); // retain the same order
        // specified in the mapping
        mdIndex.put(FPConstants.DETAIL_ID, columns); // always force detail
        // to the top of
        // the map no matter what
        mdIndex.put(FPConstants.COL_IDX, ParserUtils.buidColumnIndexMap(columns, pzparser));

        // get all of the "record" elements and the columns under them
        final Iterator<Element> recordDescriptors = root.getChildren("RECORD").iterator();
        while (recordDescriptors.hasNext()) {
            final Element xmlElement = recordDescriptors.next();

            if (xmlElement.getAttributeValue("id").equals(FPConstants.DETAIL_ID)) {
                // make sure the id attribute does not have a value of "detail" this
                // is the harcoded
                // value we are using to mark columns specified outside of a
                // <RECORD> element
                throw new IllegalArgumentException("The ID 'detail' on the <RECORD> element is reserved, please select another id");
            }

            columns = getColumnChildren(xmlElement);
            final XMLRecordElement xmlre = new XMLRecordElement();
            xmlre.setColumns(columns, pzparser);
            xmlre.setIndicator(xmlElement.getAttributeValue("indicator"));
            xmlre.setElementNumber(convertAttributeToInt(xmlElement.getAttribute("elementNumber")));
            xmlre.setStartPosition(convertAttributeToInt(xmlElement.getAttribute("startPosition")));
            xmlre.setEndPositition(convertAttributeToInt(xmlElement.getAttribute("endPosition")));
            xmlre.setElementCount(convertAttributeToInt(xmlElement.getAttribute("elementCount")));
            mdIndex.put(xmlElement.getAttributeValue("id"), xmlre);
            // make a column index for non detail records
            mdIndex.put(FPConstants.COL_IDX + "_" + xmlElement.getAttributeValue("id"), ParserUtils.buidColumnIndexMap(columns, pzparser));
        }

        if (showDebug) {
            setShowDebug(mdIndex);
        }

        return mdIndex;
    }

    // helper to convert to integer
    private static int convertAttributeToInt(final Attribute attribute) {
        if (attribute == null) {
            return 0;
        }

        try {
            return attribute.getIntValue();
        } catch (final Exception ignore) {
            return 0;
        }
    }

    // helper to retrieve the "COLUMN" elements from the given parent
    private static List<ColumnMetaData> getColumnChildren(final Element parent) {
        final List<ColumnMetaData> columnResults = new ArrayList<ColumnMetaData>();
        final Set<String> columnNames = new HashSet<String>();
        final Iterator<Element> xmlChildren = parent.getChildren("COLUMN").iterator();

        while (xmlChildren.hasNext()) {
            final ColumnMetaData cmd = new ColumnMetaData();
            final Element xmlColumn = xmlChildren.next();

            // make sure the name attribute is present on the column
            final String columnName = xmlColumn.getAttributeValue("name");
            if (columnName == null) {
                throw new IllegalArgumentException("Name attribute is required on the column tag!");
            }

            // make sure the names in columnInfo are unique
            if (columnNames.contains(columnName)) {
                throw new IllegalArgumentException("Duplicate name column '" + columnName + "'");
            }

            cmd.setColName(columnName);
            columnNames.add(columnName);

            // check to see if the column length can be set
            if (xmlColumn.getAttributeValue("length") != null) {
                try {
                    cmd.setColLength(Integer.parseInt(xmlColumn.getAttributeValue("length")));
                } catch (final Exception ex) {
                    throw new IllegalArgumentException(
                            "LENGTH ATTRIBUTE ON COLUMN ELEMENT MUST BE AN INTEGER.  GOT: " + xmlColumn.getAttributeValue("length"), ex);
                }
            }
            columnResults.add(cmd);
        }

        return columnResults;
    }

    /**
     * If set to true, debug information for the map file will be thrown to the
     * console after the parse is finished
     *
     * @param b
     */
    public static void setDebug(final boolean b) {
        showDebug = b;
    }

    private static void setShowDebug(final Map<String, Object> xmlResults) {
        for (final Entry<String, Object> entry : xmlResults.entrySet()) {
            XMLRecordElement xmlrecEle = null;
            final String recordID = entry.getKey();
            List<ColumnMetaData> columns = null;
            if (recordID.equals(FPConstants.DETAIL_ID)) {
                columns = (List<ColumnMetaData>) entry.getValue();
            } else {
                xmlrecEle = (XMLRecordElement) entry.getValue();
                columns = xmlrecEle.getColumns();
            }

            LOGGER.debug(">>>>Column MD Id: " + recordID);
            if (xmlrecEle != null) {
                LOGGER.debug("Start Position: " + xmlrecEle.getStartPosition() + " " + "End Position: " + xmlrecEle.getEndPositition() + " "
                        + "Element Number: " + xmlrecEle.getElementNumber() + " " + "Indicator: " + xmlrecEle.getIndicator());
            }
            for (final ColumnMetaData cmd : columns) {
                LOGGER.debug("Column Name: " + cmd.getColName() + " LENGTH: " + cmd.getColLength());

            }
        }
    }

    /**
     * New method based on Reader. Reads the XMLDocument for a PZMetaData
     * file from an InputStream, WebStart compatible. Parses the XML file, and
     * returns a Map containing Lists of ColumnMetaData.
     *
     * @param xmlStreamReader
     * @param pzparser
     *          Can be null.  Allows additional opts to be set during the XML map read
     * @return Map &lt;records&gt; with their corresponding
     * @throws IOException
     * @throws JDOMException
     */
    public static MetaData parseMap(final Reader xmlStreamReader, final Parser pzparser) throws JDOMException, IOException {
        final Map map = parse(xmlStreamReader, pzparser);

        final List<ColumnMetaData> col = (List<ColumnMetaData>) map.get(FPConstants.DETAIL_ID);
        map.remove(FPConstants.DETAIL_ID);

        final Map m = (Map) map.get(FPConstants.COL_IDX);
        map.remove(FPConstants.COL_IDX);

        // loop through the map and remove anything else that is an index of FPConstancts.COL_IDX + _
        // these were put in for the writer.
        // TODO maybe these shoudld be thrown into the MetaData instead of just discarded, but they are unused
        // in the Reader the moment. This parseMap is not utilized in the writer so it is safe to remove them here
        final Iterator entrySetIt = map.entrySet().iterator();
        while (entrySetIt.hasNext()) {
            final Entry e = (Entry) entrySetIt.next();
            if (((String) e.getKey()).startsWith(FPConstants.COL_IDX + "_")) {
                entrySetIt.remove();
            }
        }

        return new MetaData(col, m, map);
    }
}
