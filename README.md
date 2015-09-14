# Continuous audiorecorder

Implementation of the missing feature in Android SDK: audio recorder with a pause. Based on [mp4parser](https://code.google.com/p/mp4parser/).

Build status: [![Build Status](https://drone.io/github.com/lassana/continuous-audiorecorder/status.png)](https://drone.io/github.com/lassana/continuous-audiorecorder/latest)

[Latest sample build](https://drone.io/github.com/lassana/continuous-audiorecorder/files)

## Import

Grab the latest version from Bintray:

    repositories {
        jcenter()
        maven {
            url 'https://dl.bintray.com/lassana/maven/'
        }
    }
    dependencies {
        compile "com.googlecode.mp4parser:isoparser:1.0.2"
        compile "com.github.lassana:continuous-audiorecorder:1.0.0"
    }

or just copy the `recorder` module into your project and add new dependency:

    dependencies {
        compile project(':recorder')
    }

## Usage

Start recording:

    AudioRecorder recorder = AudioRecorder.build(context, filename);

    recorder.start(new AudioRecorder.OnStartListener() {
        @Override
        public void onStarted() {
            // started
        }

        @Override
        public void onException(Exception e) {
            // error
        }
    });

Pause:

    mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
        @Override
        public void onPaused(String activeRecordFileName) {
            // paused
        }

        @Override
        public void onException(Exception e) {
            // error
        }
    });

Use `start()` method to continue recording.

## [License](https://github.com/lassana/continuous-audiorecorder/blob/master/LICENSE)

This project is licensed under the FreeBSD License.
