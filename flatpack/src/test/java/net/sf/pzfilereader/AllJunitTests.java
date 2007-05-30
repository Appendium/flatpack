/*
 * Created on Feb 27, 2006
 */
package net.sf.pzfilereader;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author zepernick
 * 
 * This class will execute all Junit tests together
 */
public class AllJunitTests {

    public static Test suite() {
        final TestSuite suite = new TestSuite("Test For All PZFileReader Functionality");
        // $JUnit-BEGIN$
        suite.addTest(new TestSuite(net.sf.pzfilereader.columninfile.DelimitedColumnNamesInFileTest.class));
        suite.addTest(new TestSuite(net.sf.pzfilereader.delim.tab.TabDelimitedTest.class));
        // $JUnit-END$
        return suite;
    }
}
