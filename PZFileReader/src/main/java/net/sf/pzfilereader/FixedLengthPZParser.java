/**
 * 
 */
package net.sf.pzfilereader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sf.pzfilereader.util.ParserUtils;
import net.sf.pzfilereader.xml.PZMapParser;

import org.jdom.JDOMException;

/**
 * @author xhensevb
 * 
 */
public class FixedLengthPZParser extends AbstractFixedLengthPZParser {
    private InputStream pzmapXMLStream;

    private File pzmapXML;

    public FixedLengthPZParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream) {
        super(dataSourceStream);
        this.pzmapXMLStream = pzmapXMLStream;
    }

    public FixedLengthPZParser(File pzmapXML, File dataSource) {
        super(dataSource);
        this.pzmapXML = pzmapXML;
    }

    protected void init() throws InitialisationException {
        try {
            if (pzmapXMLStream != null) {
                setColumnMD(PZMapParser.parse(pzmapXMLStream));
            } else {
                InputStream stream = ParserUtils.createInputStream(pzmapXML);
                try {
                    setColumnMD(PZMapParser.parse(stream));
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
        } catch (JDOMException e) {
            throw new InitialisationException(e);
        } catch (IOException e) {
            throw new InitialisationException(e);
        }
    }
}
