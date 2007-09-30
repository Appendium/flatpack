@echo off
title RunExamples

set CLASSPATH=%CLASSPATH%;./flatpack-@JARFILE@.jar
set CLASSPATH=%CLASSPATH%;./@JARFILEEXAMPLES@
set CLASSPATH=%CLASSPATH%;./jdom.jar
set CLASSPATH=%CLASSPATH%;./jxl.jar
set CLASSPATH=%CLASSPATH%;./slf4j-api-1.1.0-RC1.jar
set CLASSPATH=%CLASSPATH%;./slf4j-simple-1.1.0-RC1.jar
set CLASSPATH=%CLASSPATH%;.

echo %CLASSPATH%

"%JAVA_HOME%\bin\java" -Xmx512m -Xms512m  net.sf.flatpack.examples.Examples

pause