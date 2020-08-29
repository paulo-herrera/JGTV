:: ###########################################
:: #  Executes exportToVTK                   #
:: #  in a DOS (Windows) environment.        #
:: #                                         #
:: # Author: Paulo Herrera                   #
:: # Date:   29/Aug/2020                     #
:: ###########################################

:: @echo off

:: Main directory where the Napa.jar file is installed
set JGTV_HOME="C:/Users/paulo/IdeaProjects/JGTV/out/artifacts/JGTV_jar"

:: Command used to call Java
set JAVA_CMD="E:/jdk/jdk-14.0.1/bin/java"
set JAVA_OPT= -Duser.country=US -Duser.language=EN
:: Additional options
:: -server -Xmx8092m
:: ###################### DO NOT EDIT BELOW ###############

:: Set environment
set JGTV_JAR=%JGTV_HOME%/JGTV.jar
set DRIVER=com.iidp.jgtv.exportToVTK

:: Execute driver with given command line arguments (there is not max. # arguments)
%JAVA_CMD% %JAVA_OPT% -cp %JGTV_JAR%  %DRIVER% %*

:end