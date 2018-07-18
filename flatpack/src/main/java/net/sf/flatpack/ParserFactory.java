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
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;

/**
 * Factory definitions for creating a PZParser (delimiter or fixed length). The
 * creation of a parser will not start the parsing. It should not fail either
 * (unless DB issues etc).
 *
 * @author Benoit Xhenseval
 */
public interface ParserFactory {
    /**
     * Constructs a new DataSet using the database table file layout method.
     * This is used for a FIXED LENGTH text file.
     *
     * The user is responsible for closing the DB connection.
     *
     * @param con -
     *            Connection to database with DATAFILE and DATASTRUCTURE tables,
     *            user is responsible for closing it.
     * @param dataSource -
     *            Fixed length file to read from
     * @param dataDefinition -
     *            Name of dataDefinition in the DATAFILE table DATAFILE_DESC
     *            column
     * @deprecated Please use the newFixedLengthParser(Connection, Reader, String).  The File can be
     *            wrapped in a "new FileReader(File)"
     * @return PZParser
     */
    @Deprecated
    Parser newFixedLengthParser(Connection con, File dataSource, String dataDefinition);

    /**
     * Constructs a new DataSet using the database table file layout method.
     * This is used for a FIXED LENGTH text file.
     *
     * The user is responsible for closing the DB connection and InputStream.
     *
     * @param con -
     *            Connection to database with DATAFILE and DATASTRUCTURE tables,
     *            user is responsible for closing it.
     * @param dataSourceStream -
     *            text file datasource InputStream to read from
     * @param dataDefinition -
     *            Name of dataDefinition in the DATAFILE table DATAFILE_DESC
     *            column
     * @deprecated Please use the newFixedLengthParser(Connection, Reader, String).  The InputStream can be
     *            wrapped in a "new InputStreamReader(InputStream)"
     * @return PZParser
     */
    @Deprecated
    Parser newFixedLengthParser(Connection con, InputStream dataSourceStream, String dataDefinition);

    /**
     * Constructs a new DataSet using the PZMAP XML file layout method. This is
     * used for a FIXED LENGTH text file.
     *
     * @param pzmapXML -
     *            Reference to the xml file holding the pzmap
     * @param dataSource -
     *            Delimited file to read from
     * @deprecated Please use the newFixedLengthParser(Reader, Reader).  The File can be
     *            wrapped in a "new FileReader(InputStream)"
     * @return PZParser
     */
    @Deprecated
    Parser newFixedLengthParser(File pzmapXML, File dataSource);

    /**
     * New constructor based on InputStream. Constructs a new DataSet using the
     * PZMAP XML file layout method. This is used for a FIXED LENGTH text file.
     *
     * The user is responsible for closing the InputStreams.
     *
     * @param pzmapXMLStream -
     *            Reference to the xml file InputStream holding the pzmap, user
     *            must close them after use.
     * @param dataSourceStream -
     *            Delimited file InputStream to read from, user must close them
     *            after use.
     * @deprecated Please use the newFixedLengthParser(Reader, Reader).  The InputStream can be
     *            wrapped in a "new InputStreamReader(InputStream)"
     * @return PZParser
     */
    @Deprecated
    Parser newFixedLengthParser(InputStream pzmapXMLStream, InputStream dataSourceStream);

    /**
     * Constructs a new DataSet using the database table file layout method.
     * This is used for a FIXED LENGTH text file.
     *
     * The user is responsible for closing the DB connection.
     *
     * @param con -
     *            Connection to database with DATAFILE and DATASTRUCTURE tables,
     *            user is responsible for closing it.
     * @param dataSource -
     *            Fixed length file to read from
     * @param dataDefinition -
     *            Name of dataDefinition in the DATAFILE table DATAFILE_DESC
     *            column
     * @return PZParser
     */
    Parser newFixedLengthParser(Connection con, Reader dataSource, String dataDefinition);

    /**
     * New constructor based on Reader. Constructs a new DataSet using the
     * PZMAP XML file layout method. This is used for a FIXED LENGTH text file.
     *
     * The user is responsible for closing the Readers.
     *
     * @param pzmapXMLStream -
     *            Reference to the xml Reader holding the pzmap, user
     *            must close them after use.
     * @param dataSource -
     *            Delimited file Reader to read from, user must close them
     *            after use.
     * @return PZParser
     */
    Parser newFixedLengthParser(Reader pzmapXMLStream, Reader dataSource);

    //
    //
    // ------------------------------------------ DELIMITED -----------
    //
    //

    /**
     * Constructs a new DataSet using the
     * database table file layout method. This is used for a DELIMITED text
     * file. esacpe sequence reference: \n newline <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * The user is responsible for closing the DB connection and InputStream.
     *
     * @param con -
     *            Connection to database with DATAFILE and DATASTRUCTURE tables,
     *            user must close it when done.
     * @param dataSource -
     *            text file datasource InputStream to read from, user must close
     *            it when done.
     * @param dataDefinition -
     *            Name of dataDefinition in the DATAFILE table DATAFILE_DESC
     *            column
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @param ignoreFirstRecord -
     *            skips the first line that contains data in the file
     * @deprecated Please use the newDelimitedParser(Connection, Reader, String, char, char, boolean).
     *            The InputStream can be wrapped in a "new InputStreamReader(InputStream)"
     * @return PZParser
     */
    @Deprecated
    Parser newDelimitedParser(Connection con, InputStream dataSource, String dataDefinition, char delimiter, char qualifier,
            boolean ignoreFirstRecord);

    /**
     * New constructor based on Reader. Constructs a new DataSet using the
     * database table file layout method. This is used for a DELIMITED text
     * file. esacpe sequence reference: \n newline <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * The user is responsible for closing the DB connection and InputStream.
     *
     * @param con -
     *            Connection to database with DATAFILE and DATASTRUCTURE tables,
     *            user must close it when done.
     * @param dataSource -
     *            text file datasource InputStream to read from, user must close
     *            it when done.
     * @param dataDefinition -
     *            Name of dataDefinition in the DATAFILE table DATAFILE_DESC
     *            column
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @param ignoreFirstRecord -
     *            skips the first line that contains data in the file
     * @return PZParser
     */
    Parser newDelimitedParser(Connection con, Reader dataSource, String dataDefinition, char delimiter, char qualifier, boolean ignoreFirstRecord);

    /**
     * Constructs a new DataSet using the PZMAP XML file layout method. This is
     * used for a DELIMITED text file. esacpe sequence reference: \n newline
     * <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * @param pzmapXML -
     *            Reference to the xml file holding the pzmap
     * @param dataSource -
     *            text file datasource to read from
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @param ignoreFirstRecord -
     *            skips the first line that contains data in the file
     * @deprecated Please use the newDelimitedParser(Reader, Reader, String, char, char, boolean).
     *            The File can be wrapped in a "new FileReader(File)"
     * @return PZParser
     */
    @Deprecated
    Parser newDelimitedParser(File pzmapXML, File dataSource, char delimiter, char qualifier, boolean ignoreFirstRecord);

    /**
     * Constructs a new DataSet using the PZMAP XML file layout method. This is
     * used for a DELIMITED text file. esacpe sequence reference: \n newline
     * <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * @param pzmapXML -
     *            Reference to the xml file holding the pzmap
     * @param dataSource -
     *            text file datasource to read from
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @param ignoreFirstRecord -
     *            skips the first line that contains data in the file
     * @return PZParser
     */
    Parser newDelimitedParser(Reader pzmapXML, Reader dataSource, char delimiter, char qualifier, boolean ignoreFirstRecord);

    /**
     * New constructor based on InputStream. Constructs a new DataSet using the
     * PZMAP XML file layout method. This is used for a DELIMITED text file.
     * esacpe sequence reference: \n newline <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * The user is responsible for closing the InputStreams.
     *
     * @param pzmapXMLStream -
     *            Reference to the xml file holding the pzmap, user must close
     *            it when done.
     * @param dataSourceStream -
     *            text file datasource InputStream to read from, user must close
     *            it when done.
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @param ignoreFirstRecord -
     *            skips the first line that contains data in the file
     * @deprecated Please use the newDelimitedParser(Reader, Reader, String, char, char, boolean).
     *            The InputStream can be wrapped in a "new InputStreamReader(InputStream)"
     * @return PZParser
     */
    @Deprecated
    Parser newDelimitedParser(InputStream pzmapXMLStream, InputStream dataSourceStream, char delimiter, char qualifier, boolean ignoreFirstRecord);

    /**
     * Constructs a new DataSet using the first line of data found in the text
     * file as the column names. This is used for a DELIMITED text file. esacpe
     * sequence reference: \n newline <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * @param dataSource -
     *            text file datasource to read from
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @deprecated Please use the newDelimitedParser(Reader, char, char, boolean).
     *            The InputStream can be wrapped in a "new FileReader(File)"
     * @return PZParser
     */
    @Deprecated
    Parser newDelimitedParser(File dataSource, char delimiter, char qualifier);

    /**
     * Constructs a new DataSet using the first line of data found in the text
     * file as the column names. This is used for a DELIMITED text file. esacpe
     * sequence reference: \n newline <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * @param dataSource -
     *            text file datasource to read from
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @return PZParser
     */
    Parser newDelimitedParser(Reader dataSource, char delimiter, char qualifier);

    /**
     * Constructs a new DataSet using the first line of data found in the text
     * file as the column names. This is used for a DELIMITED text file. esacpe
     * sequence reference: \n newline <br>
     * \t tab <br>
     * \b backspace <br>
     * \r return <br>
     * \f form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     *
     * The user must close the InputStream when done (after parsing).
     *
     * @param dataSource -
     *            text file InputStream to read from, user must close it when
     *            done.
     * @param delimiter -
     *            Char the file is delimited By
     * @param qualifier -
     *            Char text is qualified by
     * @deprecated Please use the newDelimitedParser(Reader, char, char, boolean).
     *            The InputStream can be wrapped in a "new InputStreamReader(InputStream)"
     * @return PZParser
     */
    @Deprecated
    Parser newDelimitedParser(InputStream dataSource, char delimiter, char qualifier);
}
