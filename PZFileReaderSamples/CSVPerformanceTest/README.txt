File Type:  Must Be A CSV file

This is meant to be a performance test.  The sample file contains 10 columns and 100,000 rows.


Performance Tests
------------------------------
    Machine Specs:  P4 1.7 GHZ 256MB RAM  | Parse Time: 45-48 secs.
    Machine Specs:  P4 2.9 GHZ 1 GB RAM  | Parse Time: 23-25 secs.

Feel free to post additional performance tests in the SF forums :)


There is a settings.properties file in this directory.  This file has 2 different options:

verbose - can be set to true/false.  If set to true, the data in the file will be ran to the screen
after the parse.  There is a 2 second pause before data start to get spit to the screen.  This is 
to allow the user to see the total time it took to parse the file.

csvFile - set this to the path of the CSV file to be read in.  Feel free to test this against your own
CSV files.


This particular example uses the first record in the file for the column names.  

Depending on the size of the file being parsed, the -Xmx parameter may need to be adjusted.  This
is the max memory setting for the JVM to use.  This parameter is set in the ExecuteSample.bat file
and is currently set to 128mb.  This needs to be adjusted if OutOfMemory errors occur.  


