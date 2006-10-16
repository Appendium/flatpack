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
package com.pz.reader.xml;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.pz.reader.structure.ColumnMetaData;
import com.pz.reader.util.ParserUtils;

/**
 * @author zepernick
 * 
 * Parses a PZmap definition XML file
 */
public final class PZMapParser {

    private static boolean SHOW_DEBUG = false;

    /**
     * Constructor
     * 
     * @param XMLDocument -
     *            xml file to be parsed
     */
    private PZMapParser() {
    }

    /**
     * Reads the XMLDocument for a PZMap file. Parses the XML file, and returns
     * a List of ColumnMetaData.
     * 
     * @param xmlFile
     *            XML file
     * @return List of ColumnMetaData
     * @throws Exception
     * @deprecated
     */
    public static Map parse(final File xmlFile) throws Exception {
        Map mdIndex = null;
        final InputStream xmlStream = ParserUtils.createInputStream(xmlFile);
        mdIndex = parse(xmlStream);
        if (mdIndex == null) {
            mdIndex = new LinkedHashMap();
        }
        return mdIndex;
    }

    /**
     * TODO New method based on InputStream. Reads the XMLDocument for a PZMap
     * file from an InputStream, WebStart combatible. Parses the XML file, and
     * returns a Map containing Lists of ColumnMetaData.
     * 
     * @param xmlStream
     * @return
     * @throws Exception
     */
    public static Map parse(final InputStream xmlStream) throws Exception {
        Document document = null;
        SAXBuilder builder = null;
        Element root = null;
        List columns = null;
        Element xmlElement = null;
        final Map mdIndex = new LinkedHashMap(); // retain the same order
                                                    // specified in the mapping

        builder = new SAXBuilder();
        builder.setValidation(true);
        // handle the ability to pull DTD from Jar if needed
        builder.setEntityResolver(new ResolveLocalDTD());
        document = builder.build(xmlStream);

        root = document.getRootElement();

        // lets first get all of the columns that are declared directly under
        // the PZMAP
        columns = getColumnChildren(root);
        mdIndex.put("detail", columns); // always force detail to the top of the
                                        // map no matter what

        // get all of the "record" elements and the columns under them
        final Iterator recordDescriptors = root.getChildren("RECORD").iterator();
        while (recordDescriptors.hasNext()) {
            xmlElement = (Element) recordDescriptors.next();

            // make sure the id attribute does not have a value of "detail" this
            // is the harcoded
            // value we are using to mark columns specified outside of a
            // <RECORD> element
            if (xmlElement.getAttributeValue("id").equals("detail")) {
                throw new Exception("The ID 'detail' on the <RECORD> element is reserved, please select another id");
            }

            columns = getColumnChildren(xmlElement);
            final XMLRecordElement xmlre = new XMLRecordElement();
            xmlre.setColumns(columns);
            xmlre.setIndicator(xmlElement.getAttributeValue("indicator"));
            xmlre.setElementNumber(convertAttributeToInt(xmlElement.getAttribute("elementNumber")));
            xmlre.setStartPosition(convertAttributeToInt(xmlElement.getAttribute("startPosition")));
            xmlre.setEndPositition(convertAttributeToInt(xmlElement.getAttribute("endPosition")));
            mdIndex.put(xmlElement.getAttributeValue("id"), xmlre);
        }

        if (SHOW_DEBUG) {
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
        }

        return 0;

    }

    // helper to retrieve the "COLUMN" elements from the given parent
    private static List getColumnChildren(final Element parent) throws Exception {
        final List columnResults = new ArrayList();
        final Iterator xmlChildren = parent.getChildren("COLUMN").iterator();
        Element xmlColumn = null;
        ColumnMetaData cmd = null;

        while (xmlChildren.hasNext()) {
            cmd = new ColumnMetaData();
            xmlColumn = (Element) xmlChildren.next();

            // make sure the name attribute is present on the column
            if (xmlColumn.getAttributeValue("name") == null) {
                throw new Exception("Name attribute is required on the column tag!");
            }

            cmd.setColName(xmlColumn.getAttributeValue("name"));

            // check to see if the column length can be set
            if (xmlColumn.getAttributeValue("length") != null) {
                try {
                    cmd.setColLength(Integer.parseInt(xmlColumn.getAttributeValue("length")));
                } catch (final Exception ex) {
                    throw new Exception("LENGTH ATTRIBUTE ON COLUMN ELEMENT MUST BE AN INTEGER.  GOT: "
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
        SHOW_DEBUG = b;
    }

    private static void showDebug(final Map xmlResults) {
        Iterator columns = null;
        final Iterator mapIt = xmlResults.keySet().iterator();
        XMLRecordElement xmlrecEle = null;
        while (mapIt.hasNext()) {
            xmlrecEle = null;
            final String recordID = (String) mapIt.next();
            if (recordID.equals("detail")) {
                columns = ((List) xmlResults.get(recordID)).iterator();
            } else {
                xmlrecEle = (XMLRecordElement) xmlResults.get(recordID);
                columns = xmlrecEle.getColumns().iterator();
            }

            System.out.println(">>>>Column MD Id: " + recordID);
            if (xmlrecEle != null) {
                System.out.println("Start Position: " + xmlrecEle.getStartPosition() + " " + "End Position: "
                        + xmlrecEle.getEndPositition() + " " + "Element Number: " + xmlrecEle.getElementNumber() + " "
                        + "Indicator: " + xmlrecEle.getIndicator());
            }
            while (columns.hasNext()) {
                final ColumnMetaData cmd = (ColumnMetaData) columns.next();
                System.out.println("          Column Name: " + cmd.getColName() + " LENGTH: " + cmd.getColLength());

            }

        }
    }

}
