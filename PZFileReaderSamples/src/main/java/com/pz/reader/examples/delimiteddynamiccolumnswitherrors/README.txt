File Type:  Comma Delimted With Text Qualified By "'s

This sample contains errors in the PEOPLE-CommaDelimitedWithQualifier.txt
2 Lines in the file contain more columns then we have declared in the 
PEOPLE-Delimited.pzmap.xml
This demonstrates how to check for errors that happened while reading the file.


This sample dynamically reads the column names from the DataSet and evaluates 
the values of each column.  Making use of the getColumns() method.

This sample also re-orders the file by CITY ASC, LASTNAME DESC


There have also been " 's and , 's added to the text on one of the records
to show the parsing abilities of the API.


