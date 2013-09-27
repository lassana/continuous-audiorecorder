# Continuous audiorecorder

Implementation of the missing feature in Android SDK: audio recorder with pause feature.

## Import

Just copy `recorder` module into your project and add dependence:

    dependencies {
        compile project(':recorder')
    }

## Usage

Start record:

    AudioRecorder recorder = AudioRecorder.build(activity, filename);

    recorder.start(new AudioRecorder.OnStartListener() {
        @Override
        public void onStarted() {
            // started
        }

        @Override
        public void onError(Throwable th) {
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
        public void onError(Throwable th) {
            // error
        }
    });

Use `start()` method to continue recording.

## [License](https://github.com/lassana/continuous-audiorecorder/blob/master/LICENSE)

This project is licensed under the FreeBSD License.
