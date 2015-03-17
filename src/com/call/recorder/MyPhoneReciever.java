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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;



public class MyPhoneReciever extends BroadcastReceiver {
	
	private String phoneNumber;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Constants.TAG, "MyPhoneReciever.onReceive");
		SharedPreferences settings = context.getSharedPreferences(Constants.LISTEN_ENABLED, 0);
		boolean silent = settings.getBoolean("silentMode", true);
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		
		Log.d(Constants.TAG, "phoneNumber "+((phoneNumber!=null)?phoneNumber:""));
		String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.d(Constants.TAG, "extraState "+((extraState!=null)?extraState:""));

		if (silent && MainActivity.updateExternalStorageState() == Constants.MEDIA_MOUNTED)
		{
			try{
				if (phoneNumber == null)
				{
					if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) 
					{
						if (phoneNumber == null)
							phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
						Log.d(Constants.TAG, "phoneNumber "+((phoneNumber!=null)?phoneNumber:""));
						Intent myIntent = new Intent(context, RecordService.class);
						myIntent.putExtra("commandType", Constants.STATE_CALL_START);
						myIntent.putExtra("phoneNumber",  phoneNumber);
						context.startService(myIntent);
					}
					else if (extraState.equals(TelephonyManager.EXTRA_STATE_IDLE)) 
					{
						Intent myIntent = new Intent(context, RecordService.class);
						myIntent.putExtra("commandType", Constants.STATE_CALL_END);
						context.startService(myIntent);
					}
					else if (extraState.equals(TelephonyManager.EXTRA_STATE_RINGING)) 
					{
						if (phoneNumber == null)
							phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
						Log.d(Constants.TAG, "phoneNumber "+((phoneNumber!=null)?phoneNumber:""));
						Intent myIntent = new Intent(context, RecordService.class);
						myIntent.putExtra("commandType", Constants.STATE_INCOMING_NUMBER);
						myIntent.putExtra("phoneNumber",  phoneNumber);
						context.startService(myIntent);
					}
				}
				else
				{
					Intent myIntent = new Intent(context, RecordService.class);
					myIntent.putExtra("commandType", Constants.STATE_INCOMING_NUMBER);
					myIntent.putExtra("phoneNumber", phoneNumber);
					context.startService(myIntent);
				}
			}catch(Exception e) {
				Log.e(Constants.TAG, "Exception");
				e.printStackTrace();
			}
		}
	}
	
}
