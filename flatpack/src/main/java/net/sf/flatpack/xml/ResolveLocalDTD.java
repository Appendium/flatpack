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
package net.sf.flatpack.xml;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolves a local copy of the DTD instead of having to pull
 * over the internet from the SF site
 *
 * @author Paul Zepernick
 */
public final class ResolveLocalDTD implements EntityResolver {

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     *      java.lang.String)
     */
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        if (!systemId.toLowerCase(Locale.getDefault()).startsWith("http://")) {
            final URL resource = getClass().getResource("flatpack.dtd");

            if (resource != null) {
                return new InputSource(resource.openStream());
            } else {
                //should probably not happen, this may indicate that the dtd has been
                //removed from the jar for some reason
                throw new IOException("could not load dtd resource from jar!!");
            }
        }

        // must be pulling from the web, stick with default implementation
        return null;
    }
}
