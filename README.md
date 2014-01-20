# Continuous audiorecorder

Implementation of the missing feature in Android SDK: audio recorder with a pause.

Build status: [![Build Status](https://drone.io/github.com/lassana/continuous-audiorecorder/status.png)](https://drone.io/github.com/lassana/continuous-audiorecorder/latest)

[Latest sample build](https://drone.io/github.com/lassana/continuous-audiorecorder/files)

## Import

Just copy `recorder` module into your project and add dependence:

    dependencies {
        compile project(':recorder')
    }

## Usage

Start record:

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
