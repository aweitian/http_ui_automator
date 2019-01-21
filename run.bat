@echo off
adb root
adb push daemon.dex /data/data
adb shell app_process -Djava.class.path=/data/data/daemon.dex  / ret.sec.oxygenauto.daemon.Daemon 3 50 5 3820





