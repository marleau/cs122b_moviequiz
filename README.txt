Package name: com.cs122b.group10.moviequiz

To check if device is attached:

cd /usr/local/Cellar/android-sdk/r15/tools
./emulator -avd cs122b
adb kill-server
adb start-server

To build:

cd path/to/project
android update project --path .
ant debug

To uninstall:

adb shell
cd data/app
ls
rm

To install:

adb install path/to/.apk/file
