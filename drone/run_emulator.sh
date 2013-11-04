#!/bin/sh

echo no | android create avd --force -n test -t android-19 --abi armeabi-v7a
emulator -avd test -no-skin -no-window &
