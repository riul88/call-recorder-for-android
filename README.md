# call-recorder-for-android
Base code automatically exported from code.google.com/p/call-recorder-for-android

Adding some functionality to performed some validations that help properly dispose the MediaRecorder and avoid app breaking. Moved default folder location to SD card but allowing to see and delete existing files to allow backwards compatibility.

Changed to use base Android 2.3.3 (API 10)

Tested on Android 4.1.2 device

Note: For some reason Android will not properly clean references to MediaRecorder, that will stop the application to use MediaRecorder again until phone is rebooted.

This android application allows you to record all incoming and outgoing calls from your phone. All your recorded calls are saved in 3gp files and can be sent from the application.

The main application screen contains a list of all calls with details of phone numbers, date and time of a call. By selecting one of the items the application will provide you with 3 options: erase record, send record and play record.

In order to allow or disallow recordings open the main menu and choose enable/disable recordings.

Your 3gp files are located in the folder "recordedCalls" in your SD card or phone storage, you can transfer your recorded calls to your computer by conecting your phone to it with a USB cable. (Some operative systems might require a driver to properly navigate your phone files)

The recording does not have a limit of time so be careful not to run out of space after a few long calls.

