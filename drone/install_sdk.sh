#!/bin/sh

# Download and unpack Android SDK
wget https://dl.google.com/android/android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz -nv
tar xzf android-sdk_r$ANDROID_SDK_TOOLS_VERSION-linux.tgz

# --filter for 'build-tools' not working, I don't know why. Installing by number ('2') works fine.
echo yes | android update sdk --no-ui --force --filter build-tools-$ANDROID_BUILD_TOOLS_VERSION,tool,android-$ANDROID_API_LEVEL,sysimg-$ANDROID_API_LEVEL,platform-tool,extra
