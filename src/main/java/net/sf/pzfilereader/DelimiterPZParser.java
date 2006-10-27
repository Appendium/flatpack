/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting PZFileReader.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 74 2006-10-24 22:19:05Z benoitx $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

    public DelimiterPZParser(final File pzmapXML, final File dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(dataSource, delimiter, qualifier, ignoreFirstRecord);
        this.pzmapXML = pzmapXML;
    }

    public DelimiterPZParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
        this.pzmapXMLStream = pzmapXMLStream;
    }

    public DelimiterPZParser(final File dataSource, final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    public DelimiterPZParser(final InputStream dataSourceStream, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    protected void init() throws InitialisationException {
        try {
            if (pzmapXMLStream != null) {
                setColumnMD(PZMapParser.parse(pzmapXMLStream));
            } else if (pzmapXML != null) {
                final InputStream stream = ParserUtils.createInputStream(pzmapXML);
                try {
                    setColumnMD(PZMapParser.parse(stream));
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
            setInitialised(true);
        } catch (final JDOMException e) {
            throw new InitialisationException(e);
        } catch (final IOException e) {
            throw new InitialisationException(e);
        }
    }

    protected boolean shouldCreateMDFromFile() {
        return pzmapXML == null && pzmapXMLStream == null;
    }
}