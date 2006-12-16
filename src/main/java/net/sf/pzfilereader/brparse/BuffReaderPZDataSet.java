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
package net.sf.pzfilereader.brparse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.pzfilereader.DefaultDataSet;
import net.sf.pzfilereader.structure.Row;

public class BuffReaderPZDataSet extends DefaultDataSet{
    private BuffReaderDelimPZParser brDelimPzParser;
    
    private BuffReaderFixedPZParser brFixedPzParser;
    
    public BuffReaderPZDataSet(final Map columnMD2, BuffReaderDelimPZParser brDelimPzParser) {
        super(columnMD2);
        //register the parser with the dataset so we can fetch rows from 
        //the bufferedreader as needed
        this.brDelimPzParser = brDelimPzParser;        
    }
    
    public BuffReaderPZDataSet(final Map columnMD2, BuffReaderFixedPZParser brFixedPzParser) {
        super(columnMD2);
        //register the parser with the dataset so we can fetch rows from 
        //the bufferedreader as needed
        this.brFixedPzParser = brFixedPzParser;        
    }
    
    public boolean next() {
        try {
            Row r = null;
            
            if (brDelimPzParser != null) {
                brDelimPzParser.buildRow(this);
            } else if (brFixedPzParser != null) {
                brFixedPzParser.buildRow(this);
            } else {
                //this should not happen, throw exception
                throw new RuntimeException("No parser available to fetch row");
            }
                
            
            if (r == null) {
                setPointer(-1);
                return false;
            }
                    
            //make sure we have some MD
            if (getColumnMD() == null) {
                //create a new map so the user cannot change the internal 
                //DataSet representation of the MD through the parser
                setColumnMD(new LinkedHashMap(brDelimPzParser.getColumnMD()));
            }   
            
            clearRows();
            addRow(r);
            
            setPointer(0);
            
            return true;
            
        } catch(IOException ex) {
            //TODO real logging here
            ex.printStackTrace();
        }
       
        return false;
    }
}
