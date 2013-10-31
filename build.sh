#!/bin/sh

set -x

export ANDROID_SDK_TOOLS_VERSION=22.3
export ANDROID_BUILD_TOOLS_VERSION=19
export ANDROID_API_LEVEL=19

PATH=$(echo $PATH | sed 's/\/opt\/android-sdk-linux//')

# Download and unpack Android SDK
wget https://dl.google.com/android/android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz -nv
tar xzf android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz

export ANDROID_HOME=$PWD/android-sdk-linux
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/build-tools

# --filter for 'build-tools' not working, I don't know why. Installing by number ('2') works fine.
echo yes | android update sdk --no-ui --force --filter 2,tool,android-$ANDROID_API_LEVEL,platform-tool,extra > /dev/null

./gradlew assembleDebug
