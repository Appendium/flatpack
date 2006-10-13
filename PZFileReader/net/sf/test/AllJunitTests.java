/*
 * Created on Feb 27, 2006
 */
package net.sf.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author zepernick
 *
 * This class will execute all Junit tests together
 */
public class AllJunitTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test For All PZFileReader Functionality");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(net.sf.test.delim.columnInFile.TestDelimitedColumnNamesInFile.class));
        suite.addTest(new TestSuite(net.sf.test.delim.tab.TestTabDelimited.class));
        //$JUnit-END$
        return suite;
    }
}
