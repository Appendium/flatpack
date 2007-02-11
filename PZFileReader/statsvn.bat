rem svn log -v --xml https://pzfilereader.svn.sourceforge.net/svnroot/pzfilereader/ > svn.log
rem mkdir target\docs\statsvn
java -jar c:\java\statsvn\statsvn.jar -xdoc -verbose -tags "^release_2.2.0|^Root_v2_0_0|^Root_v1_0_5|^Root_V2_1_0|^Root_V2_2_0_0/" -output-dir src\site\statsvn -title PZFileReader -exclude "**/SampleCSV.csv|**/qalab.xml" -viewvc http://pzfilereader.svn.sourceforge.net/viewvc/pzfilereader/trunk ./svn.log .
