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
package net.sf.pzfilereader.parserutils;

import net.sf.pzfilereader.util.ParserUtils;
import junit.framework.TestCase;

/**
 * Tests the functionality of the left trim in the ParserUtils
 *  
 * @author paul zepernick
 */
public class LTrimTest extends TestCase{
    
    
    
    /**
     * Make sure all spaces are properly removed from the front of the String
     *
     */
    public void testTrimLeadingSpaces(){
        final String testS = "     RemoveAllSpacesFromMe";        
        assertEquals(true , ParserUtils.lTrim(testS).indexOf(" ") == -1);        
    }
    
    /**
     * Make sure all spaces are properly removed from the front of the String
     * leaving the trailing spaces at the end
     *
     */
    public void testTrimLeadingSpacesWithTrailingSpaces(){
        final String testS = "     RemoveAllSpacesFromMe     ";
        final String tResult = ParserUtils.lTrim(testS);
        assertEquals(true , !tResult.startsWith(" ") && tResult.endsWith(" "));        
    }
    
    
    /**
     * Make sure all TAB chars are properly removed from the front of the String
     * leaving the trailing spaces at the end
     *
     */
    public void testTrimLeadingTabs(){
        final String testS = "\t\t\tRemoveAllSpacesFromMe     ";
        final String tResult = ParserUtils.lTrim(testS);
        assertEquals(true , !tResult.startsWith("\t") && tResult.endsWith(" "));        
    }
    
    
    /**
     * Make sure all TAB chars are properly removed from the front of the String
     * leaving the trailing spaces at the end
     *
     */
    public void testKeepLeadingTabs(){
        final String testS = "     \t\t\tRemoveAllSpacesFromMe     ";
        final String tResult = ParserUtils.lTrimKeepTabs(testS);
        assertEquals(true , !tResult.startsWith("\t") && tResult.endsWith(" "));        
    }
    
    
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(LTrimTest.class);
    }
}
