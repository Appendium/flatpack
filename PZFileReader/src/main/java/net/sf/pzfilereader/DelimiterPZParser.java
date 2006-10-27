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
public class DelimiterPZParser extends AbstractDelimiterPZParser {
    private InputStream pzmapXMLStream = null;

    private File pzmapXML = null;

    public DelimiterPZParser(File pzmapXML, File dataSource, char delimiter, char qualifier, boolean ignoreFirstRecord) {
        super(dataSource, delimiter, qualifier, ignoreFirstRecord);
        this.pzmapXML = pzmapXML;
    }

    public DelimiterPZParser(InputStream pzmapXMLStream, InputStream dataSourceStream, char delimiter, char qualifier,
            boolean ignoreFirstRecord) {
        super(dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
        this.pzmapXMLStream = pzmapXMLStream;
    }

    public DelimiterPZParser(File dataSource, char delimiter, char qualifier, boolean ignoreFirstRecord) {
        super(dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    public DelimiterPZParser(InputStream dataSourceStream, char delimiter, char qualifier, boolean ignoreFirstRecord) {
        super(dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    protected void init() throws InitialisationException {
        try {
            if (pzmapXMLStream != null) {
                setColumnMD(PZMapParser.parse(pzmapXMLStream));
            } else if (pzmapXML != null) {
                InputStream stream = ParserUtils.createInputStream(pzmapXML);
                try {
                    setColumnMD(PZMapParser.parse(stream));
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
            setInitialised(true);
        } catch (JDOMException e) {
            throw new InitialisationException(e);
        } catch (IOException e) {
            throw new InitialisationException(e);
        }
    }

    protected boolean shouldCreateMDFromFile() {
        return pzmapXML == null && pzmapXMLStream == null;
    }
}