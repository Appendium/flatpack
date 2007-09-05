package net.sf.flatpack.writer;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public class AllTests {

    public static Test suite() {
        final TestSuite suite = new TestSuite("Test for net.sf.flatpack.writer");
        //$JUnit-BEGIN$
        suite.addTestSuite(DelimiterWriterTestCase.class);
        suite.addTestSuite(FixedLengthWriterTestCase.class);
        //$JUnit-END$
        return suite;
    }

}
