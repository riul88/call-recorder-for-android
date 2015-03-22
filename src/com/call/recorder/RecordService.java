/*
 *  Copyright 2012 Kobi Krasnoff
 * 
 * This file is part of Call recorder For Android.

    Call recorder For Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Call recorder For Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Call recorder For Android.  If not, see <http://www.gnu.org/licenses/>
 */
package com.call.recorder;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class RecordService extends Service {

	private MediaRecorder recorder = null;
	private String phoneNumber = null;

	private NotificationManager manager;
	private String fileName;
    private boolean onCall = false;
    private boolean recording = false;
    private boolean silentMode = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(Constants.TAG, "RecordService onStartCommand");
		if(intent==null) {
			Log.d(Constants.TAG, "intent is null");
			return super.onStartCommand(intent, flags, startId);
		}
		int commandType = intent.getIntExtra("commandType", 0);
		if(commandType != 0){
			Log.d(Constants.TAG, "RecordService commandType " + commandType);
			if(commandType == Constants.RECORDING_ENABLED){
				Log.d(Constants.TAG, "RecordService RECORDING_ENABLED");
				silentMode = false;
				if(onCall && phoneNumber != null && !recording)
					commandType = Constants.STATE_CALL_START;
			}
			if(commandType == Constants.RECORDING_DISABLED){
				Log.d(Constants.TAG, "RecordService RECORDING_DISABLED");
				silentMode = true;
				if(onCall && phoneNumber != null)
					commandType = Constants.STATE_STOP_RECORDING;
			}
			if (commandType == Constants.STATE_INCOMING_NUMBER) {
				startService();
				Log.d(Constants.TAG, "RecordService STATE_INCOMING_NUMBER");
				if (phoneNumber == null)
					phoneNumber = intent.getStringExtra("phoneNumber");
				
				silentMode = intent.getBooleanExtra("silentMode", true);
			} else if (commandType == Constants.STATE_CALL_START) {
				Log.d(Constants.TAG, "RecordService STATE_CALL_START");
				onCall = true;
				
				if(phoneNumber==null){
					Log.d(Constants.TAG, "RecordService phoneNumber NULL");
				} else {
					Log.d(Constants.TAG, "RecordService phoneNumber " + phoneNumber);
				}
				
				if(!silentMode && phoneNumber != null){
					startRecording(intent);
				}
			} else if (commandType == Constants.STATE_CALL_END) {
				Log.d(Constants.TAG, "RecordService STATE_CALL_END");
				onCall = false;
				phoneNumber = null;
				stopAndReleaseRecorder();
				recording = false;
				finishService();
			} else if (commandType == Constants.STATE_STOP_RECORDING) {
				Log.d(Constants.TAG, "RecordService STATE_STOP_RECORDING");
				stopAndReleaseRecorder();
				recording = false;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * in case it is impossible to record
	 */
	private void terminateAndEraseFile() {
        Log.d(Constants.TAG, "RecordService terminateAndEraseFile");
		stopAndReleaseRecorder();
		recording = false;
        deleteFile();
	}

    private void finishService(){
    	Log.d(Constants.TAG, "RecordService finishService");
    	stopForeground(true);
        
		Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(dialogIntent);
        
        this.stopSelf();
    }

    private void deleteFile(){
    	Log.d(Constants.TAG, "RecordService deleteFile");
        FileHelper.deleteFile(fileName);
        fileName = null;
    }

	private void stopAndReleaseRecorder() {
		Log.d(Constants.TAG, "RecordService stopAndReleaseRecorder");
        if(recorder == null) return;
        boolean recorderStopped = false;

        try{
            recorder.stop();
            recorderStopped = true;
        }catch(IllegalStateException e) {
            Log.e(Constants.TAG,"IllegalStateException");
            deleteFile();
        }catch(RuntimeException e){
            Log.e(Constants.TAG,"RuntimeException");
            deleteFile();
        }
        recorder.reset();
        recorder.release();
        recorder = null;
        if(recorderStopped) {
            Toast toast = Toast.makeText(this, this.getString(R.string.reciever_end_call), Toast.LENGTH_SHORT);
            toast.show();
        }
	}

	@Override
	public void onDestroy() {
		Log.d(Constants.TAG, "RecordService onDestroy");
		stopAndReleaseRecorder();
		finishService();
		super.onDestroy();
	}
	
	private void startRecording(Intent intent){
		Log.d(Constants.TAG, "RecordService startRecording");
		
		recorder = new MediaRecorder();

		try {
			recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			fileName = FileHelper.getFilename(phoneNumber);
			recorder.setOutputFile(fileName);

			OnErrorListener errorListener = new OnErrorListener() {
				public void onError(MediaRecorder arg0, int arg1, int arg2) {
					Log.e(Constants.TAG, "OnErrorListener "+ arg1 + "," + arg2);
					terminateAndEraseFile();
				}
			};
			recorder.setOnErrorListener(errorListener);
			
			OnInfoListener infoListener = new OnInfoListener() {
				public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
					Log.e(Constants.TAG, "OnInfoListener "+ arg1 + "," + arg2);
					terminateAndEraseFile();
				}
			};
			recorder.setOnInfoListener(infoListener);

			recorder.prepare();
			//Sometimes prepare takes some time to complete
			Thread.sleep(2000);
            recorder.start();
            recording = true;
            Log.d(Constants.TAG, "RecordService recorderStarted");
		} catch (IllegalStateException e) {
			Log.e(Constants.TAG, "IllegalStateException");
			e.printStackTrace();
			terminateAndEraseFile();
		} catch (IOException e) {
			Log.e(Constants.TAG, "IOException");
			e.printStackTrace();
			terminateAndEraseFile();
		} catch (Exception e) {
			Log.e(Constants.TAG, "Exception");
			e.printStackTrace();
			terminateAndEraseFile();
		}

		if(recording) {
			Toast toast = Toast.makeText(this, this.getString(R.string.reciever_start_call), Toast.LENGTH_SHORT);
	    	toast.show();
		} else {
			Toast toast = Toast.makeText(this, this.getString(R.string.record_impossible), Toast.LENGTH_LONG);
	    	toast.show();
		}
	}
	
	private void startService(){
		if(manager==null){
	        Intent intent = new Intent(this, MainActivity.class);
	        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
	        
	        Notification notification = new NotificationCompat.Builder(getBaseContext())
		        .setContentTitle(this.getString(R.string.notification_title))
		        .setTicker(this.getString(R.string.notification_ticker))
		        .setContentText(this.getString(R.string.notification_text))
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pendingIntent)
		        .setOngoing(true).getNotification();

	        notification.flags = Notification.FLAG_NO_CLEAR;
	        
	        startForeground(1337, notification);
		}
	}
}
