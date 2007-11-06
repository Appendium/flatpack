svn log -v --xml https://flatpack.svn.sourceforge.net/svnroot/flatpack/ > svn.log
mkdir target\docs\statsvn
java -jar c:\java\statsvn\statsvn.jar -xdoc -verbose -config-file ./statsvn.properties -tags "^release_2.2.0|^Root_v2_0_0|^Root_v1_0_5|^Root_V2_1_0|^Root_V2_2_0_0|^3.0.0|^3.1.0|^3.1.1|^3.2.0" -output-dir src\site\statsvn -title FlatPack -exclude "**/SampleCSV.csv|**/qalab.xml" -viewvc http://flatpack.svn.sourceforge.net/viewvc/flatpack/trunk ./svn.log .
