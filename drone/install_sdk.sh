#!/bin/sh

# Download and unpack Android SDK
wget https://dl.google.com/android/android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz -nv
tar xzf android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz

# --filter for 'build-tools' not working, I don't know why. Installing by number ('2') works fine.
echo yes | android update sdk --no-ui --force --filter 2,tool,android-$ANDROID_API_LEVEL,platform-tool,extra > /dev/null

