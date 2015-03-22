# call-recorder-for-android

This android application allows you to record all incoming and outgoing calls from your phone. All your recorded calls are saved in 3gp files and can be sent from the application.

You can enable/disable the recording during the call, or leave it enabled to record all the calls.

The main application screen contains a list of all calls with details of phone numbers, date and time of a call. By selecting one of the items the application will provide you with 3 options: erase record, send record and play record.

In order to allow or disallow recordings open the main menu and choose enable/disable recordings.

Your 3gp files are located in the folder "recordedCalls" in your SD card or phone storage, you can transfer your recorded calls to your computer by conecting your phone to it with a USB cable. (Some operative systems might require a driver to properly navigate your phone files)

The recording does not have a limit of time so be careful not to run out of space after a few long calls.

Direct download of the apk: https://github.com/riul88/call-recorder-for-android/blob/master/bin/Android%20Call%20Recorder.apk

Compatible with Android 2.2 (API 8) or above

Tested on Android 4.1.2, 4.2.2 and 4.4.2

Some devices with Android 4.4.2 and Android 5 are not able to start recording

This repository is maintained by Raul Robledo, the base code was developed by Kobi Krasnoff, and automatically exported from code.google.com/p/call-recorder-for-android into this repository.

Note: For some reason Android will not properly clean references to MediaRecorder, that will stop the application to use MediaRecorder again until phone is rebooted.

Change log:
2015-03-21
- RecordService flow updated to allow stop and start recording during call
- Added authors and contributors files

2015-03-18
- Removed unneeded Internet permission

2015-03-17
- Fixed random application breaking bug caused by Service was called with null intent
- Fixed application breaking bug when other files exist on the recordedCalls folder
- Added functionality to stop recording when recording is disabled on main screen
- Adjusted to build with API 8 for Android 2.2 support
- Adjusted EN and ES translations
- Changed recording item options order to have delete as the last one
- Fixed mime type in play and email functionality
- Removed unneeded files from bin directory

2015-03-16:
- Project files moved out of the 'Android Call Recorder' folder to have direct access to project
- Fixed bug on deleteFile
- Fixed bug on stopAndReleaseRecorder
- Adjusted finishService functionality to properly dispose notifications
- Added TAG to constants
- Added functionality to use foreground when call is started to avoid service to be killed due to lack of resources
- Adjusted functionality to dispose MediaRecorder
- Adjusted functionality to delete file if MediaRecorder failed to stop

2015-03-15:
- Code re-factoring to use Constants and FileHelper
- Validations added to avoid starting or stopping recorder when not required
- Adjusted cleanup functionality to properly dispose recorder
- Moved target API to 10
- Added label to indicate possible issue with existing recorder

