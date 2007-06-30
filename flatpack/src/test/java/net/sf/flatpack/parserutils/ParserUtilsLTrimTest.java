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
package net.sf.flatpack.parserutils;

import junit.framework.TestCase;
import net.sf.flatpack.util.ParserUtils;

/**
 * Tests the functionality of the left trim in the ParserUtils
 *  
 * @author paul zepernick
 */
public class ParserUtilsLTrimTest extends TestCase {
    /**
     * Make sure all spaces are properly removed from the front of the String
     *
     */
    public void testTrimLeadingSpaces() {
        final String testS = "     RemoveAll SpacesFromMe";
        assertEquals("RemoveAll SpacesFromMe", ParserUtils.lTrim(testS));
    }

    /**
     * Make sure all spaces are properly removed from the front of the String
     * leaving the trailing spaces at the end
     *
     */
    public void testTrimLeadingSpacesWithTrailingSpaces() {
        final String testS = "     RemoveAll SpacesFromMe     ";
        final String tResult = ParserUtils.lTrim(testS);
        assertEquals("RemoveAll SpacesFromMe     ", tResult);
    }

    /**
     * Make sure all TAB chars are properly removed from the front of the String
     * leaving the trailing spaces at the end
     *
     */
    public void testTrimLeadingTabs() {
        final String testS = "\t\t\tRemoveAll SpacesFromMe     ";
        final String tResult = ParserUtils.lTrim(testS);
        assertEquals("RemoveAll SpacesFromMe     ", tResult);
    }

    /**
     * Make sure all TAB chars are properly removed from the front of the String
     * leaving the trailing spaces at the end
     *
     */
    public void testKeepLeadingTabs() {
        final String testS = "     \t\t\tRemoveAll SpacesFromMe     ";
        final String tResult = ParserUtils.lTrimKeepTabs(testS);
        assertEquals("\t\t\tRemoveAll SpacesFromMe     ", tResult);
    }

    /**
     * Ensure that spaces and tabs in the middle of the string will
     * not be removed.
     */
    public void testWithTabsInMiddleAndEnd() {
        assertEquals("RemoveAll \tSpaces \t\t", ParserUtils.lTrim("\t \t RemoveAll \tSpaces \t\t"));
        assertEquals("\t \t RemoveAll \tSpaces \t\t ", ParserUtils.lTrimKeepTabs(" \t \t RemoveAll \tSpaces \t\t "));
        assertEquals("\t \t RemoveAll \tSpaces \t\t", ParserUtils.lTrimKeepTabs("\t \t RemoveAll \tSpaces \t\t"));
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsLTrimTest.class);
    }
}
