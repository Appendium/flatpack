/*
Copyright 2006 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/
package com.pz.reader.util;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.pz.reader.DataSet;

/**
 * @author Paul Zepernick
 *
 * Converts a DataSet object into an excel spreadsheet
 */
public class ExcelTransformer {
    
    private DataSet ds;
    private File xlsFile;
    
    
    /**
     * Constructs a new Excel transformer
     * 
     * @param ds DataSet to convert
     * @param xlsFile Excel file to be created
     */
    public ExcelTransformer(DataSet ds, File xlsFile){
        this.ds = ds;
        this.xlsFile = xlsFile;
    }
    
    /**
     * Writes the Excel file to disk
     * 
     * @throws Exception
     */
    public void writeExcelFile() throws Exception{
        WritableWorkbook excelWrkBook = null;
        WritableSheet wrkSheet = null;
        WritableFont times10ptBold = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD);
        WritableCellFormat cellFormat = null;
        Label xlsTextLbl = null;
        int curDsPointer = 0;
        
        try{
            String[] columnNames = ds.getColumns();
            //get the current position of the DataSet.  We have to go to the top to do this write, 
            //and we will put the pionter back where it was after we are done
            curDsPointer = ds.getIndex();
            ds.goTop();
            
            excelWrkBook = Workbook.createWorkbook(xlsFile);
            wrkSheet = excelWrkBook.createSheet("results", 0);            
            
            
            //write the column headings in the spreadsheet
            cellFormat = new WritableCellFormat(times10ptBold);
            for(int i = 0; i < columnNames.length; i++){
                xlsTextLbl = new Label(i, 0, columnNames[i], cellFormat);
                wrkSheet.addCell(xlsTextLbl);
            }
            
            
            cellFormat = new WritableCellFormat(times10pt);
            int row = 1; 
            while (ds.next()){
                for (int i = 0; i < columnNames.length; i ++){
                    xlsTextLbl = new Label(i, row, ds.getString(columnNames[i]), cellFormat);
                    wrkSheet.addCell(xlsTextLbl);                    
                    
                }
                
                row ++;
            }
            
            excelWrkBook.write();
            
        }finally{
            if (curDsPointer > -1)ds.absolute(curDsPointer);
            if (excelWrkBook != null) excelWrkBook.close();            
        }
        
    }
    

}
