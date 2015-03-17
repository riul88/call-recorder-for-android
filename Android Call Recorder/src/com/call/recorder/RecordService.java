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
import android.util.Log;
import android.widget.Toast;

public class RecordService extends Service {

	private MediaRecorder recorder = null;
	private String phoneNumber = null;

	private NotificationManager manager;
	private String fileName;
    private boolean foregroundStarted = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(Constants.TAG, "RecordService.onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(Constants.TAG, "RecordService.onStartCommand");
		int commandType = intent.getIntExtra("commandType", Constants.STATE_INCOMING_NUMBER);
		
		if (commandType == Constants.STATE_INCOMING_NUMBER) {
			Log.d(Constants.TAG, "RecordService STATE_INCOMING_NUMBER");
			if (phoneNumber == null)
				phoneNumber = intent.getStringExtra("phoneNumber");

            if(!foregroundStarted) {
                if(recorder == null) {
                    recorder = new MediaRecorder();
                }

                manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification(R.drawable.ic_launcher, this.getString(R.string.notification_ticker), System.currentTimeMillis());
                notification.flags = Notification.FLAG_NO_CLEAR;

                Intent intent2 = new Intent(this, MainActivity.class);
                intent2.putExtra("RecordStatus", true);

                PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent2, 0);
                notification.setLatestEventInfo(this, this.getString(R.string.notification_title), this.getString(R.string.notification_text), contentIntent);
                manager.notify(0, notification);

                startForeground(1337, notification);
                foregroundStarted = true;
            }
		} else if (commandType == Constants.STATE_CALL_START && recorder != null) {
            boolean recorderStarted = false;
			Log.d(Constants.TAG, "RecordService STATE_CALL_START");
			if (phoneNumber == null)
				phoneNumber = intent.getStringExtra("phoneNumber");

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
                recorderStarted = true;
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

			if(recorderStarted) {
				Toast toast = Toast.makeText(this, this.getString(R.string.reciever_start_call), Toast.LENGTH_SHORT);
		    	toast.show();

			} else {
				Toast toast = Toast.makeText(this, this.getString(R.string.record_impossible), Toast.LENGTH_LONG);
		    	toast.show();
			}
		} else if (commandType == Constants.STATE_CALL_END) {
			Log.d(Constants.TAG, "RecordService STATE_CALL_END");
			stopAndReleaseRecorder();
            finishService();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * in case it is impossible to record
	 */
	private void terminateAndEraseFile() {
        Log.d(Constants.TAG, "terminateAndEraseFile");
		stopAndReleaseRecorder();
        deleteFile();
        finishService();
	}

    private void finishService(){
    	if (manager != null)
			manager.cancel(0);
        if(foregroundStarted) {
            stopForeground(true);
            foregroundStarted = false;
        }
        this.stopSelf();
    }

    private void deleteFile(){
        FileHelper.deleteFile(fileName);
        fileName = null;
    }

	private void stopAndReleaseRecorder() {
        if(recorder == null) return;
        Log.d(Constants.TAG,"stopAndReleaseRecorder");
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
		super.onDestroy();
	}
	
}
