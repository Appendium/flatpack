/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import com.pz.reader.LargeDataSet;
import com.pz.reader.ordering.OrderBy;
import com.pz.reader.ordering.OrderColumn;
import java.io.FileInputStream;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DelimitedWithPZMap {
    public static void main(String[] args)throws Exception{
        LargeDataSet ds = null;
        String[] colNames = null;
        OrderBy orderby = null;
	FileInputStream pzmap = null;
	FileInputStream fileToParse = null;
        
        

	pzmap = new FileInputStream(new File("PEOPLE-Delimited.pzmap.xml"));
	fileToParse = new FileInputStream(new File("PEOPLE-CommaDelimitedWithQualifier.txt"));
        //delimited by a comma
        //text qualified by double quotes
        //ignore first record
        ds = new LargeDataSet(pzmap,fileToParse,",","\"",true, false);
        
        
        colNames = ds.getColumns();
        
        while (ds.next()){
            for (int i = 0; i < colNames.length; i++){
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }
        
        
        if (ds.getErrors() != null && ds.getErrors().size() > 0){
            System.out.println("FOUND ERRORS IN FILE");
        }
        
        //clear out the DataSet object for the JVM to collect
        ds.freeMemory();
        
    }
}
