package com.pz.reader.examples.csvheaderandtrailer;
/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import com.pz.reader.DataSet;
import com.pz.reader.ordering.OrderBy;
import com.pz.reader.ordering.OrderColumn;
import com.pz.reader.xml.PZMapParser;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSVHeaderAndTrailer {
    public static void main(String[] args)throws Exception{
        DataSet ds = null;
        String[] colNames = null;

        //turn on the debuging for the XML parser
       // PZMapParser.setDebug(true);

        //delimited by a comma
        //text qualified by double quotes
        //ignore first record
        ds = new DataSet(new File("PEOPLE-Delimited.pzmap.xml"),new File("PEOPLE-CommaDelimitedWithQualifier.txt"),",","\"",true, false);


        while (ds.next()){

            if (ds.isRecordID("header")){
                System.out.println(">>>>>>Found Header Record");
                System.out.println("COLUMN NAME: RECORDINDICATOR VALUE: " + ds.getString("RECORDINDICATOR"));
                System.out.println("COLUMN NAME: HEADERDATA VALUE: " + ds.getString("HEADERDATA"));
                System.out.println("===========================================================================");
                continue;
            }

            if (ds.isRecordID("trailer")){
                System.out.println(">>>>>>Found Trailer Record");
                System.out.println("COLUMN NAME: RECORDINDICATOR VALUE: " + ds.getString("RECORDINDICATOR"));
                System.out.println("COLUMN NAME: TRAILERDATA VALUE: " + ds.getString("TRAILERDATA"));
                System.out.println("===========================================================================");
                continue;
            }

            System.out.println("COLUMN NAME: FIRSTNAME VALUE: " + ds.getString("FIRSTNAME"));
            System.out.println("COLUMN NAME: LASTNAME VALUE: " + ds.getString("LASTNAME"));
            System.out.println("COLUMN NAME: ADDRESS VALUE: " + ds.getString("ADDRESS"));
            System.out.println("COLUMN NAME: CITY VALUE: " + ds.getString("CITY"));
            System.out.println("COLUMN NAME: STATE VALUE: " + ds.getString("STATE"));
            System.out.println("COLUMN NAME: ZIP VALUE: " + ds.getString("ZIP"));
            System.out.println("===========================================================================");
        }


        if (ds.getErrors() != null && ds.getErrors().size() > 0){
            System.out.println("FOUND ERRORS IN FILE");
        }

        //clear out the DataSet object for the JVM to collect
        ds.freeMemory();

    }
}
