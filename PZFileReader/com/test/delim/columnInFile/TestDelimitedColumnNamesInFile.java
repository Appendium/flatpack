/*
 * Created on Feb 26, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.test.delim.columnInFile;

import com.pz.reader.DataSet;

import junit.framework.TestCase;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestDelimitedColumnNamesInFile extends TestCase {
    public TestDelimitedColumnNamesInFile(
            String name) {
            super(name);
        }
    
    	//tests to make sure we have 0 errors
        public void testErrorCount() {
            DataSet ds = null;
            
            try{
                DelimitedColumnNamesInFile testDelimted = new DelimitedColumnNamesInFile();
            
                ds = testDelimted.getDsForTest();
                              
                //check that we had no errors
                assertEquals(0, ds.getErrors().size());
        
            
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                if (ds != null) ds.freeMemory();
            }
        }
        
        //test to make sure we parsed the correct number
        //of rows in the file
        public void testRowCount(){
            DataSet ds = null;
            
            try{
                DelimitedColumnNamesInFile testDelimted = new DelimitedColumnNamesInFile();
            
                ds = testDelimted.getDsForTest();
                
                //check that we parsed in the right amount of rows
                assertEquals(6, ds.rows.size());
                              
        
            
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                if (ds != null) ds.freeMemory();
            }
        }
        
        
        //test to make sure we have the right number of column names from the file
        public void testColumnNameCount(){
            DataSet ds = null;
            
            try{
                DelimitedColumnNamesInFile testDelimted = new DelimitedColumnNamesInFile();
            
                ds = testDelimted.getDsForTest();
                
                //check that we parsed in the right amount of column names
                assertEquals(6, ds.getColumns().length);
                              
        
            
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                if (ds != null) ds.freeMemory();
            }
        }
        
        
        public static void main(String[] args) {
            junit.textui.TestRunner.run(
                TestDelimitedColumnNamesInFile.class);
        }
}
