@echo off
title RunExamples

set CLASSPATH=%CLASSPATH%;./pzfilereader-@JARFILE@.jar
set CLASSPATH=%CLASSPATH%;./@JARFILEEXAMPLES@
set CLASSPATH=%CLASSPATH%;./jdom.jar
set CLASSPATH=%CLASSPATH%;./jxl.jar
set CLASSPATH=%CLASSPATH%;.

echo %CLASSPATH%

"%JAVA_HOME%\bin\java" -Xmx512m -Xms512m  com.pz.reader.examples.Examples

pause