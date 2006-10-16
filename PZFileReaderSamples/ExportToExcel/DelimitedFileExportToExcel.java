/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import com.pz.reader.DataSet;
import com.pz.reader.ordering.OrderBy;
import com.pz.reader.ordering.OrderColumn;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DelimitedFileExportToExcel {
    public static void main(String[] args)throws Exception{
        DataSet ds = null;
        OrderBy orderby = null;
        
        
        //delimited by a comma
        //text qualified by double quotes
        //ignore first record
        ds = new DataSet(new File("PEOPLE-Delimited.pzmap.xml"),new File("PEOPLE-CommaDelimitedWithQualifier.txt"),",","\"",true, false);
        
        //re order the data set by last name
        orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY",false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME",true));
        ds.orderRows(orderby);
        
             
        if (ds.getErrors() != null && ds.getErrors().size() > 0){
            System.out.println("FOUND ERRORS IN FILE");
        }
        
        
        //lets write this file out to excel :)
        ds.writeToExcel(new File("MyExcelExport.xls"));
        
        
        
        //clear out the DataSet object for the JVM to collect
        ds.freeMemory();
        
    }
}
