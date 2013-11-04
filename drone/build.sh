#!/bin/sh

#set -x

export ANDROID_SDK_TOOLS_VERSION=22.3
export ANDROID_BUILD_TOOLS_VERSION=19.0.0
export ANDROID_API_LEVEL=19

PATH=$(echo $PATH | sed 's/\/opt\/android-sdk-linux//')

export ANDROID_HOME=$PWD/android-sdk-linux

export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/build-tools

chmod +x update_apt.sh
./update_apt.sh > /dev/null

chmod +x install_sdk.sh
./install_sdk.sh > /dev/null

chmod +x run_emulator.sh
./run_emulator.sh

chmod +x wait_for_emulator.sh
./wait_for_emulator.sh

./gradlew test

