@echo off
set APP_HOME=%~dp0
del daemon.dex
rem javac -Xlint:deprecation ^
javac   ^
    -bootclasspath %APP_HOME%/lib/android.jar; ^
    -encoding utf-8 -source 1.7 -target 1.7 -d "./output" ^
    -cp "%APP_HOME%/lib/okhttp-3.11.0.jar;%APP_HOME%/lib/mockwebserver-3.11.0.jar;%APP_HOME%/lib/okio-1.14.0.jar;%APP_HOME%/lib/my-mockwebserver-3.11.0.jar" ^
    ./src/*.java

rem move /Y .\output\*.class ret\sec\oxygenauto
rem cd output

dx --dex --output %APP_HOME%/daemon.dex ^
    --core-library %APP_HOME%/output ^
    %APP_HOME%/lib/animal-sniffer-annotations-1.10.jar; ^
    %APP_HOME%/lib/okhttp-3.11.0.jar; ^
    %APP_HOME%/lib/okio-1.14.0.jar; ^
    %APP_HOME%/lib/my-mockwebserver-3.11.0.jar
