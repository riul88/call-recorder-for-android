# call-recorder-for-android
Base code automatically exported from code.google.com/p/call-recorder-for-android

Adding some functionality to performed some validations that help properly dispose the MediaRecorder and avoid app breaking. Moved default folder location to SD card but allowing to see and delete existing files to allow backwards compatibility.

Changed to use base Android 2.3.3 (API 10)

Tested on Android 4.1.2 device

For some reason Android will not properly clean references to MediaRecorder, that will stop the application to use MediaRecorder again until phone is rebooted.

