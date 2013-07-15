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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class RecordService extends Service {

	public static final String LISTEN_ENABLED = "ListenEnabled";
	public static final String FILE_DIRECTORY = "recordedCalls";
	private MediaRecorder recorder = new MediaRecorder();
	private String phoneNumber = null;;
	public static final int STATE_INCOMING_NUMBER = 0;
	public static final int STATE_CALL_START = 1;
	public static final int STATE_CALL_END = 2;
	
	private NotificationManager manger;
	private String myFileName;
	
	
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
		int commandType = intent.getIntExtra("commandType", STATE_INCOMING_NUMBER);
		
		if (commandType == STATE_INCOMING_NUMBER)
		{
			if (phoneNumber == null)
				phoneNumber = intent.getStringExtra("phoneNumber");
		}
		else if (commandType == STATE_CALL_START)
		{
			if (phoneNumber == null)
				phoneNumber = intent.getStringExtra("phoneNumber");
			
			
			try {
				recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				myFileName = getFilename();
				recorder.setOutputFile(myFileName);
			}
			catch (IllegalStateException e) {
				Log.e("Call recorder IllegalStateException: ", "");
				terminateAndEraseFile();
			}
			catch (Exception e) {
				Log.e("Call recorder Exception: ", "");
				terminateAndEraseFile();
			}
			
			OnErrorListener errorListener = new OnErrorListener() {

				public void onError(MediaRecorder arg0, int arg1, int arg2) {
					Log.e("Call recorder OnErrorListener: ", arg1 + "," + arg2);
					arg0.stop();
					arg0.reset();
					arg0.release();
					arg0 = null;
					terminateAndEraseFile();
				}
				
			};
			recorder.setOnErrorListener(errorListener);
			OnInfoListener infoListener = new OnInfoListener() {

				public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
					Log.e("Call recorder OnInfoListener: ", arg1 + "," + arg2);
					arg0.stop();
					arg0.reset();
					arg0.release();
					arg0 = null;
					terminateAndEraseFile();
				}
				
			};
			recorder.setOnInfoListener(infoListener);
			
			
			try {
				recorder.prepare();
				recorder.start();
				Toast toast = Toast.makeText(this, this.getString(R.string.reciever_start_call), Toast.LENGTH_SHORT);
		    	toast.show();
		    	
		    	manger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		    	Notification notification = new Notification(R.drawable.ic_launcher, this.getString(R.string.notification_ticker), System.currentTimeMillis());
		    	notification.flags = Notification.FLAG_NO_CLEAR;
		    	
		    	Intent intent2 = new Intent(this, MainActivity.class);
		    	intent2.putExtra("RecordStatus", true);

		        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent2, 0);
		        notification.setLatestEventInfo(this, this.getString(R.string.notification_title), this.getString(R.string.notification_text), contentIntent);
		        //manger.notify(0, notification);
		        
		        startForeground(1337, notification);
		    	
			} catch (IllegalStateException e) {
				Log.e("Call recorder IllegalStateException: ", "");
				terminateAndEraseFile();
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("Call recorder IOException: ", "");
				terminateAndEraseFile();
				e.printStackTrace();
			}
			catch (Exception e) {
				Log.e("Call recorder Exception: ", "");
				terminateAndEraseFile();
				e.printStackTrace();
			}
			
			
		}
		else if (commandType == STATE_CALL_END)
		{
			try {
				recorder.stop();
				recorder.reset();
				recorder.release();
				recorder = null;
				Toast toast = Toast.makeText(this, this.getString(R.string.reciever_end_call), Toast.LENGTH_SHORT);
		    	toast.show();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			if (manger != null)
				manger.cancel(0);
			stopForeground(true);
			this.stopSelf();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * in case it is impossible to record
	 */
	private void terminateAndEraseFile()
	{
		try {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
			Toast toast = Toast.makeText(this, this.getString(R.string.reciever_end_call), Toast.LENGTH_SHORT);
	    	toast.show();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		File file = new File(myFileName);
		
		if (file.exists()) {
			file.delete();
			
		}
		Toast toast = Toast.makeText(this, this.getString(R.string.record_impossible), Toast.LENGTH_LONG);
    	toast.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * returns absolute file directory
	 * 
	 * @return
	 */
	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, FILE_DIRECTORY);

		if (!file.exists()) {
			file.mkdirs();
		}
		
		String myDate = new String();
		myDate = (String) DateFormat.format("yyyyMMddkkmmss", new Date());

		return (file.getAbsolutePath() + "/d" + myDate + "p" + phoneNumber + ".mp3");
	}

}
