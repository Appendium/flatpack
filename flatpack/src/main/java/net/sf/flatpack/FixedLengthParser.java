/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting FlatPack.
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
package net.sf.flatpack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import net.sf.flatpack.xml.MapParser;

import org.jdom.JDOMException;

/**
 * @author xhensevb
 * @author zepernick
 *
 */
public class FixedLengthParser extends AbstractFixedLengthParser {
    private InputStream pzmapXMLStream;

    private File pzmapXML;

    private Reader pzmapReader;

    //this InputStream and file can be removed after support for
    //file and inputstream is removed from the parserfactory.  The
    //methods have been deprecated..pz
    private InputStream dataSourceStream = null;

    private File dataSource = null;

    public FixedLengthParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream) {
        super(null);
        this.pzmapXMLStream = pzmapXMLStream;
        this.dataSourceStream = dataSourceStream;
    }

    public FixedLengthParser(final File pzmapXML, final File dataSource) {
        super(null);
        this.pzmapXML = pzmapXML;
        this.dataSource = dataSource;
    }

    public FixedLengthParser(final Reader pzmapReader, final Reader dataSourceReader) {
        super(dataSourceReader);
        this.pzmapReader = pzmapReader;
    }

    protected void init() {
        try {
            //check to see if the user is using a File or InputStream.  This is
            //here for backwards compatability
            if (dataSourceStream != null) {
                final Reader r = new InputStreamReader(dataSourceStream);
                setDataSourceReader(r);
                addToCloseReaderList(r);
            } else if (dataSource != null) {
                final Reader r = new FileReader(dataSource);
                setDataSourceReader(r);
                addToCloseReaderList(r);
            }

            boolean closeMapReader = false;
            if (pzmapXML != null) {
                this.pzmapReader = new FileReader(pzmapXML);
                closeMapReader = true;
            } else if (pzmapXMLStream != null) {
                this.pzmapReader = new InputStreamReader(pzmapXMLStream);
                closeMapReader = true;
            }

            try {
                //                setColumnMD(PZMapParser.parse(this.pzmapReader, this));
                setPzMetaData(MapParser.parseMap(this.pzmapReader, this));
            } finally {
                if (closeMapReader) {
                    //only close the reader if it is one we created
                    //otherwise we will let the user handle it
                    this.pzmapReader.close();
                }
            }

            //  setInitialised(true);
        } catch (final JDOMException e) {
            throw new InitialisationException(e);
        } catch (final IOException e) {
            throw new InitialisationException(e);
        }
    }
}
