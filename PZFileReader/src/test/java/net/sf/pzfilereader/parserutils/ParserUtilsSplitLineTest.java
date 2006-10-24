package net.sf.pzfilereader.parserutils;

import java.util.List;

import net.sf.pzfilereader.util.ParserUtils;
import junit.framework.TestCase;

/**
 * Test the functionality of the splitLine method.  This method returns 
 * a List of Strings. Each element of the list represents a column created
 * by the parser from the delimited String. 
 * 
 * @author zepernick  
 */
public class ParserUtilsSplitLineTest extends TestCase{
    final String[] delimitedDataNoBreaks = {"Column 1","Column 2", "Column 3", "Column 4", "Column 5"};
    final String[] delimitedDataWithBreaks = {"Column 1","Column 2", "Column 3", "Column 4", "Column 5"};
    
    /**
     * Test CSV without any line breaks
     *
     */
    public void testCSVNoLineBreaks(){
        
        final String delimiter = ",";
        final String qualifier = "\"";
        final StringBuffer txtToParse = new StringBuffer();
        for (int i = 0; i < delimitedDataNoBreaks.length; i++){
            if (i > 0){
                txtToParse.append(delimiter);
            }
            txtToParse.append(qualifier + delimitedDataNoBreaks[i] + qualifier);
        }
        
        List splitLineResults = ParserUtils.splitLine(txtToParse.toString(), delimiter, qualifier);
        
        
        //check to make sure we have the same amount of elements which were expected
        assertEquals(delimitedDataNoBreaks.length, splitLineResults.size());
        
        //loop through each value and compare what came back
        for (int i = 0 ; i < delimitedDataNoBreaks.length; i ++){
            assertEquals(delimitedDataNoBreaks[i], (String)splitLineResults.get(i));
        }
        
        
    }
    
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsSplitLineTest.class);
    }
    
}
