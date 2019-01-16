@echo off
:: /c按键列表 /m提示内容 /d默认选择 /t等待秒数   /d 必须和 /t同时出现
:: choice  /c yn /m "override?" /d n /t 5
:: if %errorlevel%==2 goto done

set APP_HOME=%~dp0

if exist %APP_HOME%\src\CmdHandle.java (
    copy %APP_HOME%\src\* %APP_HOME%\..\app\src\main\java\ret\sec\oxygenauto\daemon\
    cd %APP_HOME%\src\
    del /Q *
    cd %APP_HOME%
) else (
    copy %APP_HOME%\..\app\src\main\java\ret\sec\oxygenauto\daemon\* %APP_HOME%\src\
    cd %APP_HOME%\..\app\src\main\java\ret\sec\oxygenauto\daemon\
    del /Q *
    cd %APP_HOME%
)
