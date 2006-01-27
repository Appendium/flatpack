/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
 */
/*
 * Created on Dec 31, 2004
 */
package com.pz.reader.xml;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
public class PZMapParser {

	/**
	 * Constructor
	 * 
	 * @param XMLDocument -
	 *            xml file to be parsed
	 */
	private PZMapParser() {
	}

	/**
	 * Reads the XMLDocument for a PZMap file.
	 * Parses the XML file, and returns a List of ColumnMetaData.
	 * @param xmlFile XML file
	 * @return List of ColumnMetaData
	 * @throws Exception
     * @deprecated
	 */
	public static List parse(File xmlFile) throws Exception {
		List columnDescriptors = null;
		InputStream xmlStream = ParserUtils.createInputStream(xmlFile);
		columnDescriptors = parse(xmlStream);
		if (columnDescriptors == null) {
			columnDescriptors = new ArrayList();
		}
		return columnDescriptors;
	}

	/**
     * TODO New method based on InputStream.
	 * Reads the XMLDocument for a PZMap file from an InputStream, WebStart combatible.
	 * Parses the XML file, and returns a List of ColumnMetaData.
	 * @param xmlStream
	 * @return
	 * @throws Exception
	 */
	public static List parse(InputStream xmlStream) throws Exception {
		ArrayList columnDescriptors = new ArrayList();
		Document document = null;
		SAXBuilder builder = null;
		Element root = null;
		List columns = null;
		Element column = null;
		ColumnMetaData columnObj = null;

		builder = new SAXBuilder();
		builder.setValidation(true);
		document = builder.build(xmlStream);

		root = document.getRootElement();
		columns = root.getChildren();

		for (int i = 0; i < columns.size(); i++) {
			columnObj = new ColumnMetaData();
			column = (Element) columns.get(i);

			// make sure the name attribute is present on the column
			if (column.getAttributeValue("name") == null) {
				throw new Exception(
						"Name attribute is required on the column tag!");
			}

			columnObj.setColName(column.getAttributeValue("name"));

			// check to see if the column length can be set
			if (column.getAttributeValue("length") != null) {
				try {
					columnObj.setColLength(Integer.parseInt(column
							.getAttributeValue("length")));
				} catch (Exception ex) {
					throw new Exception(
							"LENGTH ATTRIBUTE ON COLUMN ELEMENT MUST BE AN INTEGER.  GOT: "
									+ column.getAttributeValue("length"));
				}
			}

			// System.out.println("Column Name: " +
			// column.getAttributeValue("name") + " LENGTH: " +
			// column.getAttributeValue("length"));

			columnDescriptors.add(columnObj);

		}
		return columnDescriptors;
	}
}
