echo off
javac -encoding utf-8 -source 1.7 -target 1.7 -d "./output" ^
    -cp "%CD%/lib/android.jar;%CD%/lib/okhttp-3.11.0.jar;%CD%/lib/mockwebserver-3.11.0.jar;%CD%/lib/okio-1.14.0.jar;%CD%/lib/my-mockwebserver-3.11.0.jar" ^
    ./src/*.java

move /Y .\output\*.class ret\sec\oxygenauto

dx --dex --output daemon.dex ret\sec\oxygenauto\*.class ^
    %CD%/lib/animal-sniffer-annotations-1.10.jar; ^
    %CD%/lib/okhttp-3.11.0.jar; ^
    %CD%/lib/okio-1.14.0.jar; ^
    %CD%/lib/my-mockwebserver-3.11.0.jar

