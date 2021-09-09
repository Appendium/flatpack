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

/**
 * This class holds errors that occurred while parsing or processing a data file.
 *
 * @author Paul Zepernick
 * @version 2.0
 */
public class DataError {
    private final String errorDesc;
    private final int lineNo;
    private final int errorLevel;
    private final String rawData;
    private final String lastColumnName;
    private final String lastColumnValue;

    /**
     *
     * @param errorDesc
     *          Text description of the error that occurred
     * @param lineNo
     *          Line number in the data file the error occurred on
     * @param errorLevel
     *          Level of the error (1=warning, 2=moderate, 3=severe)
     */
    public DataError(final String errorDesc, final int lineNo, final int errorLevel) {
        this(errorDesc, lineNo, errorLevel, null, null, null);
    }

    /**
     *
     * @param errorDesc
     *          Text description of the error that occurred
     * @param lineNo
     *          Line number in the data file the error occurred on
     * @param errorLevel
     *          Level of the error (1=warning, 2=moderate, 3=severe)
     * @param rawData
     *          String of data which the parse failed on
     */
    public DataError(final String errorDesc, final int lineNo, final int errorLevel, final String rawData) {
        this(errorDesc, lineNo, errorLevel, rawData, null, null);
    }

    /**
     *
     * @param errorDesc
     *          Text description of the error that occurred
     * @param lineNo
     *          Line number in the data file the error occurred on
     * @param errorLevel
     *          Level of the error (1=warning, 2=moderate, 3=severe)
     * @param rawData
     *          String of data which the parse failed on
     */
    public DataError(final String errorDesc, final int lineNo, final int errorLevel, final String rawData, final String lastColumnName,
            final String lastColumnValue) {
        this.errorDesc = errorDesc;
        this.lineNo = lineNo;
        this.errorLevel = errorLevel;
        this.rawData = rawData;
        this.lastColumnName = lastColumnName;
        this.lastColumnValue = lastColumnValue;
    }

    /**
     * Returns the errorDesc.
     *
     * @return String
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * Returns the errorLevel.
     *
     * @return int
     */
    public int getErrorLevel() {
        return errorLevel;
    }

    /**
     * Returns the lineNo.
     *
     * @return int
     */
    public int getLineNo() {
        return lineNo;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Line:").append(lineNo).append(" Level:").append(errorLevel).append(" Desc:").append(errorDesc)
                .append(System.getProperty("line.separator"));
        if (rawData != null) {
            buf.append("RawData:").append(rawData).append(System.lineSeparator());
        }
        if (lastColumnName != null) {
            buf.append("LastColName:").append(lastColumnName).append(" Value:").append(lastColumnValue).append(System.lineSeparator());
        }
        return buf.toString();
    }

    public String getLastColumnName() {
        return lastColumnName;
    }

    public String getLastColumnValue() {
        return lastColumnValue;
    }

    /**
     * Option must be set on parser, otherwise this is
     * null by default
     *
     * @return the rawData
     */
    public String getRawData() {
        return rawData;
    }
}
