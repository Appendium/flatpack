File Type:  Must Be A CSV file

This is meant to be a performance test for large files.  This will make use of the new LargeDataSet class introduced
in 2.2.  A println will be run to the screen for every 2500 records parsed & looped through in the file.

This class differs from the DataSet class where the file is no longer read into memory.  

There is a settings.properties file in this directory. 

csvFile - set this to the path of the CSV file to be read in.  Feel free to test this against your own
CSV files.


This particular example uses the first record in the file for the column names.  

Depending on the size of the file being parsed, the -Xmx parameter may need to be adjusted.  This
is the max memory setting for the JVM to use.  This parameter is set in the ExecuteSample.bat file
and is currently set to 128mb.  This needs to be adjusted if OutOfMemory errors occur.  


