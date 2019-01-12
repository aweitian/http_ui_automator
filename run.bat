@echo off
adb root
adb push daemon.dex /data/data
adb shell app_process -Djava.class.path=/data/data/daemon.dex  / ret.sec.oxygenauto.Daemon 1515





