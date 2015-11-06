#!/bin/sh

set -x

export ANDROID_SDK_TOOLS_VERSION=24.3.4

PATH=$(echo $PATH | sed 's/\/opt\/android-sdk-linux//')

export ANDROID_HOME=$PWD/android-sdk-linux

wget https://dl.google.com/android/android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz -nv
tar xzf android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz

export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/build-tools

android list sdk --extended --all
echo yes | android update sdk --no-ui --all --force --filter tools,platform-tools,build-tools-23.0.2,android-23,extra-android-m2repository,extra-android-support

./gradlew assembleDebug

