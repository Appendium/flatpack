package net.sf.pzfilereader.brparse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.pzfilereader.DefaultDataSet;
import net.sf.pzfilereader.structure.Row;

public class BuffReaderDelimPZDataSet extends DefaultDataSet{
    private BuffReaderDelimPZParser brpzparser;
    
    public BuffReaderDelimPZDataSet(final Map columnMD2, BuffReaderDelimPZParser brpzparser) {
        super(columnMD2);
        //register the parser with the dataset so we can fetch rows from 
        //the bufferedreader as needed
        this.brpzparser = brpzparser;        
    }
    
    public boolean next() {
        try {
            final Row r = brpzparser.buildRow(this);
            
            if (r == null) {
                setPointer(-1);
                return false;
            }
                    
            //make sure we have some MD
            if (getColumnMD() == null) {
                //create a new map so the user cannot change the internal 
                //DataSet representation of the MD through the parser
                setColumnMD(new LinkedHashMap(brpzparser.getColumnMD()));
            }   
            
            getRows().clear();
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
