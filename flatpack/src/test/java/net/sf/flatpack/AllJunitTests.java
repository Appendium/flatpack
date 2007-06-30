/*
 * Created on Feb 27, 2006
 */
package net.sf.flatpack;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author zepernick
 *
 * This class will execute all Junit tests together
 */
public class AllJunitTests {

    public static Test suite() {
        final TestSuite suite = new TestSuite("Test For All flatpack Functionality");
        // $JUnit-BEGIN$
        suite.addTest(new TestSuite(net.sf.flatpack.columninfile.DelimitedColumnNamesInFileTest.class));
        suite.addTest(new TestSuite(net.sf.flatpack.delim.tab.TabDelimitedTest.class));
        // $JUnit-END$
        return suite;
    }
}
