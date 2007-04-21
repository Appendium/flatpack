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
package net.sf.pzfilereader.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.pzfilereader.PZParser;
import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zepernick
 * 
 * Parses a PZmap definition XML file
 */
public final class PZMapParser {
    private static boolean showDebug = false;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PZMapParser.class);
    /**
     * Constructor
     * 
     * @param XMLDocument -
     *            xml file to be parsed
     */
    private PZMapParser() {
    }

    /**
     * Method based on InputStream. Reads the XMLDocument for a PZMap
     * file from an InputStream, WebStart combatible. Parses the XML file, and
     * returns a Map containing Lists of ColumnMetaData.
     * 
     * @param xmlStream
     * @return Map <records> with their corrisponding
     * @throws IOException
     * @throws JDOMException
     * @deprecated please use parse(Reader)
     */
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
     * New method based on Reader. Reads the XMLDocument for a PZMap
     * file from an InputStream, WebStart combatible. Parses the XML file, and
     * returns a Map containing Lists of ColumnMetaData.
     * 
     * @param xmlStreamReader
     * @param pzparser
     *          Can be null.  Allows additional opts to be set durring the XML map read
     * @return Map <records> with their corrisponding
     * @throws IOException
     * @throws JDOMException
     */
    public static Map parse(final Reader xmlStreamReader, final PZParser pzparser) throws JDOMException, IOException {
        //use for debug when JDOM complains about the xml
        /* final BufferedReader br = new BufferedReader(xmlStreamReader);
        final FileWriter fw = new FileWriter("c:/test.pz");
        final PrintWriter out = new PrintWriter(fw);
        String line = null;
        while ((line = br.readLine()) != null) {
            out.println(line);
        }
        out.flush();
        fw.close();
        br.close();*/
        
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
        List columns = getColumnChildren(root);
        final Map mdIndex = new LinkedHashMap(); // retain the same order
        // specified in the mapping
        mdIndex.put(PZConstants.DETAIL_ID, columns); // always force detail
        // to the top of
        // the map no matter what
        mdIndex.put(PZConstants.COL_IDX, ParserUtils.buidColumnIndexMap(columns, pzparser));

        // get all of the "record" elements and the columns under them
        final Iterator recordDescriptors = root.getChildren("RECORD").iterator();
        while (recordDescriptors.hasNext()) {
            final Element xmlElement = (Element) recordDescriptors.next();

            if (xmlElement.getAttributeValue("id").equals(PZConstants.DETAIL_ID)) {
                // make sure the id attribute does not have a value of "detail" this
                // is the harcoded
                // value we are using to mark columns specified outside of a
                // <RECORD> element
                throw new IllegalArgumentException(
                        "The ID 'detail' on the <RECORD> element is reserved, please select another id");
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
        }

        if (showDebug) {
            showDebug(mdIndex);
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
        } catch (final Exception ex) {
            return 0;
        }
    }

    // helper to retrieve the "COLUMN" elements from the given parent
    private static List getColumnChildren(final Element parent) {
        final List columnResults = new ArrayList();
        final Iterator xmlChildren = parent.getChildren("COLUMN").iterator();

        while (xmlChildren.hasNext()) {
            final ColumnMetaData cmd = new ColumnMetaData();
            final Element xmlColumn = (Element) xmlChildren.next();

            // make sure the name attribute is present on the column
            if (xmlColumn.getAttributeValue("name") == null) {
                throw new IllegalArgumentException("Name attribute is required on the column tag!");
            }

            cmd.setColName(xmlColumn.getAttributeValue("name"));

            // check to see if the column length can be set
            if (xmlColumn.getAttributeValue("length") != null) {
                try {
                    cmd.setColLength(Integer.parseInt(xmlColumn.getAttributeValue("length")));
                } catch (final Exception ex) {
                    throw new IllegalArgumentException("LENGTH ATTRIBUTE ON COLUMN ELEMENT MUST BE AN INTEGER.  GOT: "
                            + xmlColumn.getAttributeValue("length"));
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
    
    //TODO convert to logger instead of system out
    private static void showDebug(final Map xmlResults) {
        final Iterator mapIt = xmlResults.entrySet().iterator();
        while (mapIt.hasNext()) {
            XMLRecordElement xmlrecEle = null;
            final Entry entry = (Entry)mapIt.next();
            final String recordID = (String) entry.getKey();
            Iterator columns = null;
            if (recordID.equals(PZConstants.DETAIL_ID)) {
                columns = ((List) entry.getValue()).iterator();
            } else {
                xmlrecEle = (XMLRecordElement) entry.getValue();
                columns = xmlrecEle.getColumns().iterator();
            }

            LOGGER.debug(">>>>Column MD Id: " + recordID);
            if (xmlrecEle != null) {
                LOGGER.debug("Start Position: " + xmlrecEle.getStartPosition() + " " + "End Position: "
                        + xmlrecEle.getEndPositition() + " " + "Element Number: " + xmlrecEle.getElementNumber() + " "
                        + "Indicator: " + xmlrecEle.getIndicator());
            }
            while (columns.hasNext()) {
                final ColumnMetaData cmd = (ColumnMetaData) columns.next();
                LOGGER.debug("Column Name: " + cmd.getColName() + " LENGTH: " + cmd.getColLength());

            }
        }
    }
}
