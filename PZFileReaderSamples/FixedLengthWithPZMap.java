/*
 * Created on Dec 31, 2004
 *
 */


import java.io.File;

import com.pz.reader.DataSet;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FixedLengthWithPZMap {
    public static void main(String[] args)throws Exception{
        DataSet ds = null;
        String[] colNames = null;
        
        ds = new DataSet(new File("PEOPLE-FixedLength.pzmap.xml"),new File("PEOPLE-FixedLength.txt"));
        
        colNames = ds.getColumns();
        
        while (ds.next()){
            for (int i = 0; i < colNames.length; i++){
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }
        
        //clear out the DataSet object for the JVM to collect
        ds.freeMemory();
        
    }
}
