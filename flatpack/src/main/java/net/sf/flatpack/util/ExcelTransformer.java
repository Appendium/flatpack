/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed 
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 CONDITIONS OF ANY KIND, either express or implied. See the License for 
 the specific language governing permissions and limitations under the License.  
 */
package net.sf.flatpack.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.flatpack.DataSet;

/**
 * Converts a DataSet object into an excel spreadsheet.
 * 
 * Only detail records will be contained in the export.
 * Header / Trailer records defined by <record> tags in the 
 * pzmap will be ignored on the export.
 * 
 * @author Paul Zepernick
 */
public class ExcelTransformer {

    private DataSet ds;

    private File xlsFile;

    private String[] exportOnlyColumns;

    private String[] excludeFromExportColumns;

    /**
     * Constructs a new Excel transformer
     * 
     * @param ds
     *            DataSet to convert
     * @param xlsFile
     *            Excel file to be created
     */
    public ExcelTransformer(final DataSet ds, final File xlsFile) {
        this.ds = ds;
        this.xlsFile = xlsFile;
    }

    /**
     * Writes the Excel file to disk
     * 
     * @throws IOException
     * @thorws WriteException
     */
    public void writeExcelFile() throws IOException, WriteException {
        WritableWorkbook excelWrkBook = null;
        int curDsPointer = 0;

        try {
            final String[] columnNames = ds.getColumns();
            final List exportOnlyColumnsList = exportOnlyColumns != null ? Arrays.asList(exportOnlyColumns) : null;

            final List excludeFromExportColumnsList = excludeFromExportColumns != null ? Arrays.asList(excludeFromExportColumns) : null;
            // get the current position of the DataSet. We have to go to the top
            // to do this write,
            // and we will put the pionter back where it was after we are done
            curDsPointer = ds.getIndex();
            ds.goTop();

            excelWrkBook = Workbook.createWorkbook(xlsFile);
            final WritableSheet wrkSheet = excelWrkBook.createSheet("results", 0);

            final WritableFont times10ptBold = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);
            final WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD);
            // write the column headings in the spreadsheet
            WritableCellFormat cellFormat = new WritableCellFormat(times10ptBold);
            int colOffset = 0;
            for (int i = 0; i < columnNames.length; i++) {
                if (exportOnlyColumnsList != null && !exportOnlyColumnsList.contains(columnNames[i])) {
                    colOffset++;
                    continue;
                } else if (excludeFromExportColumnsList != null && excludeFromExportColumnsList.contains(columnNames[i])) {
                    colOffset++;
                    continue;
                }
                final Label xlsTextLbl = new Label(i - colOffset, 0, columnNames[i], cellFormat);
                wrkSheet.addCell(xlsTextLbl);
            }

            cellFormat = new WritableCellFormat(times10pt);
            int row = 1;
            while (ds.next()) {
                if (!ds.isRecordID(PZConstants.DETAIL_ID)) {
                    continue;
                }

                colOffset = 0;
                for (int i = 0; i < columnNames.length; i++) {
                    if (exportOnlyColumnsList != null && !exportOnlyColumnsList.contains(columnNames[i])) {
                        colOffset++;
                        continue;
                    } else if (excludeFromExportColumnsList != null && excludeFromExportColumnsList.contains(columnNames[i])) {
                        colOffset++;
                        continue;
                    }

                    final Label xlsTextLbl = new Label(i - colOffset, row, ds.getString(columnNames[i]), cellFormat);
                    wrkSheet.addCell(xlsTextLbl);
                }

                row++;
            }

            excelWrkBook.write();

        } finally {
            if (curDsPointer > -1) {
                ds.absolute(curDsPointer);
            }
            if (excelWrkBook != null) {
                excelWrkBook.close();
            }
        }

    }

    /**
     * The columns names contained in the array will be igored if
     * setExportOnlyColumns() is called.  
     * 
     * Any columns names contained in this list will be excluded from
     * the export in Excel.
     * 
     * @param excludeFromExportColumns the excludeFromExportColumns to set
     */
    public void setExcludeFromExportColumns(final String[] excludeFromExportColumns) {
        this.excludeFromExportColumns = excludeFromExportColumns;
    }

    /**
     * When set, only columns contained in the String[] will
     * be exported out to Excel.
     * 
     * @param exportOnlyColumns the exportOnlyColumns to set
     */
    public void setExportOnlyColumns(final String[] exportOnlyColumns) {
        this.exportOnlyColumns = exportOnlyColumns;
    }

}
