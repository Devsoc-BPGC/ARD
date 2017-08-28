#!/bin/bash
set -e

echo no| android create avd --force -n test -t android-$EMULATOR_API
${ANDROID_HOME}/tools/emulator -avd test -no-skin -no-audio -no-window -netspeed full -netdelay none &
android-wait-for-emulator
adb shell input keyevent 82 &
adb shell settings put global window_animation_scale 0 &
adb shell settings put global transition_animation_scale 0 &
adb shell settings put global animator_duration_scale 0 &
mkdir ./app/debug/logcat -p
adb logcat -c
adb logcat > ./app/debug/logcat/logcat &

