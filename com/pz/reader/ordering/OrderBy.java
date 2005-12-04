/*
Copyright 2005 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/
/*
 * Created on Nov 23, 2004
 */
package com.pz.reader.ordering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

import com.pz.reader.structure.Row;
import com.pz.reader.util.ParserUtils;

/**
 * @author paul zepernick
 *
 * resorts an array of objects.  Arrays get sorted by
 * OrderElements.  Right now, this class will only handle string comparisons.
 * 
 * @version 2.0
 */
public class OrderBy implements Comparator{
    /**collection of order elements to sort by*/
    private ArrayList orderbys = new ArrayList();
    /**column meta data*/
    private Vector columnMD = null;
    
    
    /**
     * Adds an order element to the sort.
     * 
     * @param oc - OrderColumn
     */
    public void addOrderColumn(OrderColumn oc){
       orderbys.add(oc); 
    }
    
    
	/**
	 * over ridden from the Comparator class.
	 * 
	 * Performs the sort
	 * 
	 * @return int
	 */
    public int compare(Object arg0, Object arg1){
        OrderColumn oc = null;
        Comparable comp0 = null;
        Comparable comp1 = null;
        Row row0 =  null;
        Row row1 = null;
        int result = 0;
        
        row0 = (Row)arg0;
        row1 = (Row)arg1;
        
        for (int i = 0; i < orderbys.size(); i++){
            oc = (OrderColumn)orderbys.get(i);
           // System.out.println(">>SORT COLUMN: " + oc.getColumnName());

           // System.out.println("COLUMN0 VALUE: " + row0.getValue(oc.getColumnName()));
           // System.out.println("COLUMN1 VALUE: " + row1.getValue(oc.getColumnName()));
            
            //convert to one type of case so the comparator does not take case into account when sorting
            
            comp0 = (Comparable)row0.getValue(ParserUtils.findColumn(oc.getColumnName(),columnMD)).toLowerCase();
            comp1 = (Comparable)row1.getValue(ParserUtils.findColumn(oc.getColumnName(),columnMD)).toLowerCase();
            
            if (comp0 == null){
                comp0 = (Comparable) new String("");
            }
            if (comp1 == null){
                comp1 = (Comparable) new String("");
            }
            
            //multiply by the sort indicator to get a ASC or DESC result
            result = comp0.compareTo(comp1) * oc.getSortIndicator(); 
            
            //if it is = 0 then the primary sort is done, and it can start the
            //secondary sorts
            if (result != 0){
                return result;
            }
            
        }
        
        return 0;
    }
    
   
    
    /**
     * @param columnMD The columnMD to set.
     */
    public void setColumnMD(Vector columnMD) {
        this.columnMD = columnMD;
    }
}
