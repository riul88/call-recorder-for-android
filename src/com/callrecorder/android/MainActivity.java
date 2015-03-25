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
package com.callrecorder.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public ListView listView;
	// public ScrollView mScrollView;
	public ScrollView mScrollView2;
	public TextView mTextView;
	private static final int CATEGORY_DETAIL = 1;
	private static final int NO_MEMORY_CARD = 2;
	private static final int TERMS = 3;

	public RadioButton radEnable;
	public RadioButton radDisable;

	private static Resources res;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		res = getResources();

		listView = (ListView) findViewById(R.id.mylist);
		// mScrollView = (ScrollView) findViewById(R.id.ScrollView01);
		mScrollView2 = (ScrollView) findViewById(R.id.ScrollView02);
		mTextView = (TextView) findViewById(R.id.txtNoRecords);

		SharedPreferences settings = this.getSharedPreferences(
				Constants.LISTEN_ENABLED, 0);
		boolean silentMode = settings.getBoolean("silentMode", true);

		if (silentMode)
			showDialog(CATEGORY_DETAIL);

		context = this.getBaseContext();
		// showDialog(TERMS);
	}

	@Override
	protected void onResume() {
		if (updateExternalStorageState() == Constants.MEDIA_MOUNTED) {
			final List<Model> listDir = FileHelper.listFiles(this);

			if (listDir.isEmpty()) {
				mScrollView2.setVisibility(TextView.VISIBLE);
				listView.setVisibility(ScrollView.GONE);
			} else {
				mScrollView2.setVisibility(TextView.GONE);
				listView.setVisibility(ScrollView.VISIBLE);
			}

			final MyCallsAdapter adapter = new MyCallsAdapter(this, listDir);

			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					adapter.showPromotionPieceDialog(listDir.get(position)
							.getCallName(), position);
				}
			});

			adapter.sort(new Comparator<Model>() {
				public int compare(Model arg0, Model arg1) {
					Long date1 = Long.valueOf(arg0.getCallName().substring(1,
							15));
					Long date2 = Long.valueOf(arg1.getCallName().substring(1,
							15));
					return (date1 > date2 ? -1 : (date1 == date2 ? 0 : 1));
				}
			});

			listView.setAdapter(adapter);
		} else if (updateExternalStorageState() == Constants.MEDIA_MOUNTED_READ_ONLY) {
			mScrollView2.setVisibility(TextView.VISIBLE);
			listView.setVisibility(ScrollView.GONE);
			showDialog(NO_MEMORY_CARD);
		} else {
			mScrollView2.setVisibility(TextView.VISIBLE);
			listView.setVisibility(ScrollView.GONE);
			showDialog(NO_MEMORY_CARD);
		}

		super.onResume();
	}

	public static String getDataFromRawFiles(int id) throws IOException {
		InputStream in_s = res.openRawResource(id);

		byte[] b = new byte[in_s.available()];
		in_s.read(b);
		String value = new String(b);

		return value;
	}

	/**
	 * checks if an external memory card is available
	 * 
	 * @return
	 */
	public static int updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return Constants.MEDIA_MOUNTED;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return Constants.MEDIA_MOUNTED_READ_ONLY;
		} else {
			return Constants.NO_MEDIA;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences settings = this.getSharedPreferences(
				Constants.LISTEN_ENABLED, 0);
		boolean silentMode = settings.getBoolean("silentMode", true);

		MenuItem menuDisableRecord = menu.findItem(R.id.menu_Disable_record);
		MenuItem menuEnableRecord = menu.findItem(R.id.menu_Enable_record);

		// silent is disabled, disableRecord item must be disabled
		menuEnableRecord.setEnabled(silentMode);
		menuDisableRecord.setEnabled(!silentMode);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast toast;
		final Activity currentActivity = this;
		switch (item.getItemId()) {
		case R.id.menu_about:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle(R.string.about_title)
					.setMessage(R.string.about_content)
					.setPositiveButton(R.string.about_close_button,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
			break;
		case R.id.menu_Disable_record:
			setSharedPreferences(true);
			toast = Toast.makeText(this,
					this.getString(R.string.menu_record_is_now_disabled),
					Toast.LENGTH_SHORT);
			toast.show();
			break;
		case R.id.menu_Enable_record:
			setSharedPreferences(false);
			// activateNotification();
			toast = Toast.makeText(this,
					this.getString(R.string.menu_record_is_now_enabled),
					Toast.LENGTH_SHORT);
			toast.show();
			break;
		case R.id.menu_see_terms:
			Intent i = new Intent(this.getBaseContext(), TermsActivity.class);
			startActivity(i);
			break;
		case R.id.menu_privacy_policy:
			Intent browserIntent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("http://www.privacychoice.org/policy/mobile?policy=306ef01761f300e3c30ccfc534babf6b"));
			startActivity(browserIntent);
			break;
		case R.id.menu_delete_all:
			AlertDialog.Builder builderDelete = new AlertDialog.Builder(
					MainActivity.this);
			builderDelete
					.setTitle(R.string.dialog_delete_all_title)
					.setMessage(R.string.dialog_delete_all_content)
					.setPositiveButton(R.string.dialog_delete_all_yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									FileHelper
											.deleteAllRecords(currentActivity);
									onResume();
									dialog.cancel();
								}
							})
					.setNegativeButton(R.string.dialog_delete_all_no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// private void activateNotification() {
	// NotificationManager notificationManager = (NotificationManager)
	// getSystemService(NOTIFICATION_SERVICE);
	// Notification notification = new Notification(R.drawable.ic_launcher,
	// "A new notification", System.currentTimeMillis());
	// // Hide the notification after its selected
	// notification.flags |= Notification.FLAG_ONGOING_EVENT;
	// notification.flags |= Notification.FLAG_NO_CLEAR;
	//
	// Intent intent = new Intent(this, MainActivity.class);
	// PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
	// notification.setLatestEventInfo(this, "This is the title",
	// "This is the text", activity);
	// //notification.
	// //notification.number += 1;
	// notificationManager.notify(0, notification);
	// }

	private void setSharedPreferences(boolean silentMode) {
		SharedPreferences settings = this.getSharedPreferences(
				Constants.LISTEN_ENABLED, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("silentMode", silentMode);
		editor.commit();

		Intent myIntent = new Intent(context, RecordService.class);
		myIntent.putExtra("commandType",
				silentMode ? Constants.RECORDING_DISABLED
						: Constants.RECORDING_ENABLED);
		myIntent.putExtra("silentMode", silentMode);
		context.startService(myIntent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CATEGORY_DETAIL:
			LayoutInflater li = LayoutInflater.from(this);
			View categoryDetailView = li.inflate(
					R.layout.startup_dialog_layout, null);

			AlertDialog.Builder categoryDetailBuilder = new AlertDialog.Builder(
					this);
			categoryDetailBuilder.setTitle(this
					.getString(R.string.dialog_welcome_screen));
			categoryDetailBuilder.setView(categoryDetailView);
			AlertDialog categoryDetail = categoryDetailBuilder.create();

			categoryDetail.setButton2("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (radEnable.isChecked())
								setSharedPreferences(false);
							if (radDisable.isChecked())
								setSharedPreferences(true);
						}
					});

			return categoryDetail;
		case NO_MEMORY_CARD:
			li = LayoutInflater.from(this);

			categoryDetailBuilder = new AlertDialog.Builder(this);
			categoryDetailBuilder.setMessage(R.string.dialog_no_memory);
			categoryDetailBuilder.setCancelable(false);
			categoryDetailBuilder.setPositiveButton(
					this.getString(R.string.dialog_close),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			categoryDetail = categoryDetailBuilder.create();

			return categoryDetail;
		case TERMS:
			li = LayoutInflater.from(this);

			categoryDetailBuilder = new AlertDialog.Builder(this);
			categoryDetailBuilder.setMessage(this
					.getString(R.string.dialog_privacy_terms));
			categoryDetailBuilder.setCancelable(false);
			categoryDetailBuilder.setPositiveButton(
					this.getString(R.string.dialog_terms),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent i = new Intent(context, TermsActivity.class);
							startActivity(i);
						}
					});
			categoryDetailBuilder.setNegativeButton(
					this.getString(R.string.dialog_privacy),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent browserIntent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://www.privacychoice.org/policy/mobile?policy=306ef01761f300e3c30ccfc534babf6b"));
							startActivity(browserIntent);
						}
					});
			categoryDetailBuilder.setNeutralButton(
					this.getString(R.string.dialog_close),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			categoryDetail = categoryDetailBuilder.create();

			return categoryDetail;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case CATEGORY_DETAIL:
			AlertDialog categoryDetail = (AlertDialog) dialog;
			radEnable = (RadioButton) categoryDetail
					.findViewById(R.id.radio_Enable_record);
			radDisable = (RadioButton) categoryDetail
					.findViewById(R.id.radio_Disable_record);
			radEnable.setChecked(true);
			break;
		default:
			break;
		}
		super.onPrepareDialog(id, dialog);
	}

}
