This utility can be used to create a sample CSV file for testing the parser.  In the 
ExecuterSample.bat file, there are 2 parameters.  

param1 = # cols to make | param2 = #rows to make

The following line would create a csv file with 10 columns and 100000 rows:

java -cp ./ CSVTestFileCreator 10 100000