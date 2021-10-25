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
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(MapParser.class);
    private static final String COLUMN = "COLUMN";
    private static final String LENGTH = "length";
    private static boolean showDebug = false;

    /**
     * Constructor
     */
    private MapParser() {
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
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static Map<String, Object> parse(final Reader xmlStreamReader, final Parser pzparser)
            throws IOException, ParserConfigurationException, SAXException {
        if (xmlStreamReader == null) {
            throw new NullPointerException("XML Reader Is Not Allowed To Be Null...");
        }
        final Map<String, Object> mdIndex = new LinkedHashMap<>(); // retain the same order

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new ResolveLocalDTD());
        final org.w3c.dom.Document document = builder.parse(new InputSource(xmlStreamReader));
        final NodeList nodeList = document.getDocumentElement().getChildNodes();

        final List<ColumnMetaData> columnResults = parseColumnElements(nodeList);

        mdIndex.put(FPConstants.DETAIL_ID, columnResults); // always force detail
        // to the top of
        // the map no matter what
        mdIndex.put(FPConstants.COL_IDX, ParserUtils.buidColumnIndexMap(columnResults, pzparser));

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);

            final String nodeName = node.getNodeName();

            if ("RECORD".equalsIgnoreCase(nodeName)) {
                final NamedNodeMap attributes = node.getAttributes();
                final Node id = attributes.getNamedItem("id");
                if (id != null && FPConstants.DETAIL_ID.equalsIgnoreCase(id.getTextContent())) {
                    // make sure the id attribute does not have a value of "detail" this
                    // is the harcoded
                    // value we are using to mark columns specified outside of a
                    // <RECORD> element
                    throw new IllegalArgumentException("The ID 'detail' on the <RECORD> element is reserved, please select another id");
                }

                final List<ColumnMetaData> columns = parseColumnElements(node.getChildNodes());
                final XMLRecordElement xmlre = new XMLRecordElement();
                xmlre.setColumns(columns, pzparser);
                xmlre.setIndicator(getAttributeValue(attributes, "indicator"));
                xmlre.setElementNumber(convertAttributeToInt(getAttributeValue(attributes, "elementNumber")));
                xmlre.setStartPosition(convertAttributeToInt(getAttributeValue(attributes, "startPosition")));
                xmlre.setEndPositition(convertAttributeToInt(getAttributeValue(attributes, "endPosition")));
                xmlre.setElementCount(convertAttributeToInt(getAttributeValue(attributes, "elementCount")));
                mdIndex.put(getAttributeValue(attributes, "id"), xmlre);
                // make a column index for non detail records
                mdIndex.put(FPConstants.COL_IDX + "_" + getAttributeValue(attributes, "id"), ParserUtils.buidColumnIndexMap(columns, pzparser));

            }
        }
        if (showDebug) {
            setShowDebug(mdIndex);
        }
        return mdIndex;
    }

    private static String getAttributeValue(final NamedNodeMap attributes, final String attributeName) {
        final Node namedItem = attributes.getNamedItem(attributeName);
        return namedItem != null ? namedItem.getTextContent() : null;
    }

    private static List<ColumnMetaData> parseColumnElements(final NodeList nodeList) {
        final Set<String> columnNames = new HashSet<>();
        final List<ColumnMetaData> columnResults = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);

            final String nodeName = node.getNodeName();

            if (COLUMN.equalsIgnoreCase(nodeName)) {
                final NamedNodeMap attributes = node.getAttributes();
                final ColumnMetaData cmd = new ColumnMetaData();

                // make sure the name attribute is present on the column
                final String columnName = attributes.getNamedItem("name").getTextContent();
                if (columnName == null) {
                    throw new IllegalArgumentException("Name attribute is required on the column tag!");
                } else if (columnNames.contains(columnName)) {
                    // make sure the names in columnInfo are unique
                    throw new IllegalArgumentException("Duplicate name column '" + columnName + "'");
                }

                cmd.setColName(columnName);
                columnNames.add(columnName);

                // check to see if the column length can be set
                if (attributes.getNamedItem(LENGTH) != null) {
                    try {
                        cmd.setColLength(Integer.parseInt(attributes.getNamedItem(LENGTH).getTextContent()));
                    } catch (final Exception ex) {
                        throw new IllegalArgumentException(
                                "LENGTH ATTRIBUTE ON COLUMN ELEMENT MUST BE AN INTEGER.  GOT: " + attributes.getNamedItem(LENGTH).getTextContent(),
                                ex);
                    }
                }
                columnResults.add(cmd);
            }
        }
        return columnResults;
    }

    // helper to convert to integer
    private static int convertAttributeToInt(final String attribute) {
        if (attribute == null) {
            return 0;
        }

        try {
            return Integer.parseInt(attribute);
        } catch (final Exception ignore) {
            return 0;
        }
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

            LOGGER.debug(">>>>Column MD Id:{}", recordID);
            if (xmlrecEle != null) {
                LOGGER.debug("Start Position: {} End Postion: {} Element Number: {} Indicator{}", xmlrecEle.getStartPosition(),
                        xmlrecEle.getEndPositition(), xmlrecEle.getElementNumber(), xmlrecEle.getIndicator());
            }
            for (final ColumnMetaData cmd : columns) {
                LOGGER.debug("Column Name: {} LENGTH: {}", cmd.getColName(), cmd.getColLength());

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
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static MetaData parseMap(final Reader xmlStreamReader, final Parser pzparser)
            throws IOException, ParserConfigurationException, SAXException {
        final Map map = parse(xmlStreamReader, pzparser);

        final List<ColumnMetaData> col = (List<ColumnMetaData>) map.get(FPConstants.DETAIL_ID);
        map.remove(FPConstants.DETAIL_ID);

        final Map m = (Map) map.get(FPConstants.COL_IDX);
        map.remove(FPConstants.COL_IDX);

        // loop through the map and remove anything else that is an index of FPConstancts.COL_IDX + _
        // these were put in for the writer.
        // TODO maybe these should be thrown into the MetaData instead of just discarded, but they are unused
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
