@echo off
set CLASSPATH="%ANDROID_HOME%/platforms/android-27/android.jar;%ANDROID_HOME%/vendor/okhttp-3.11.0.jar;%ANDROID_HOME%/vendor/mockwebserver-3.11.0.jar;%ANDROID_HOME%/vendor/okio-1.14.0.jar"
cd C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver\src\main\java\okhttp3\mockwebserver
javac -source 1.7 -target 1.7 ^
	-d C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver\gen ^
	-cp %CLASSPATH% ^
	Dispatcher.java ^
	MockResponse.java ^
	MockWebServer.java ^
	PushPromise.java ^
	QueueDispatcher.java ^
	RecordedRequest.java ^
	SocketPolicy.java

cd C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver

jar -cvf mockwebserver-3.11.0.jar -C C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver\gen\ .

rem dx --dex --output mockwebserver.dex okhttp3\mockwebserver\*.class %ANDROID_HOME%/vendor/okhttp-3.11.0.jar %ANDROID_HOME%/vendor/mockwebserver-3.11.0.jar %ANDROID_HOME%/vendor/okio-1.14.0.jar
copy C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver\mockwebserver-3.11.0.jar E:\android\sdk\vendor\my-mockwebserver-3.11.0.jar
copy C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver\mockwebserver-3.11.0.jar E:\gitee\OxygenAuto\daemon\lib\my-mockwebserver-3.11.0.jar
cd C:\Users\Administrator\Desktop\okhttp-okhttp_3.11.x\mockwebserver