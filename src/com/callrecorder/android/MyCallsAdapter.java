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

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyCallsAdapter extends ArrayAdapter<Model> {

	private final Context context;
	private List<Model> list;

	public MyCallsAdapter(Context context, List<Model> list) {
		super(context, R.layout.rowlayout, list);
		this.list = list;
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater
				.inflate(R.layout.rowlayout, parent, false);
		final TextView textView = (TextView) rowView
				.findViewById(R.id.label_list);
		final TextView textView2 = (TextView) rowView
				.findViewById(R.id.label_list_2);
		// final ImageView imgDelete =
		// (ImageView)rowView.findViewById(R.id.img_delete);
		String getCallName = list.get(position).getCallName();
		String myDateStr = getCallName.substring(1, 15);
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyyMMddkkmmss");

		Date dateObj = new Date();
		try {
			dateObj = curFormater.parse(myDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		textView2.setText(DateFormat.getDateInstance().format(dateObj) + " "
				+ DateFormat.getTimeInstance().format(dateObj));
		String myPhone = getCallName.substring(16, getCallName.length() - 4);

		if (!myPhone.matches("^[\\d]{1,}$")) {
			myPhone = context.getString(R.string.withheld_number);
		} else if (list.get(position).getUserNameFromContact() != myPhone) {
			myPhone = list.get(position).getUserNameFromContact();
		}

		textView.setText(myPhone);

		return rowView;
	}

	/**
	 * shows dialog of promotion tools
	 */
	public void showPromotionPieceDialog(final String fileName,
			final int position) {
		final CharSequence[] items = {
				context.getString(R.string.confirm_play),
				context.getString(R.string.confirm_send),
				context.getString(R.string.options_delete) };

		new AlertDialog.Builder(context).setTitle(R.string.options_title)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							// startPlay(fileName);
							startPlayExternal(fileName);
						} else if (item == 1) {
							sendMail(fileName);
						} else if (item == 2) {
							DeleteRecord(fileName, position);
						}
					}
				}).show();
	}

	void DeleteRecord(final String fileName, final int position) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.confirm_delete_title)
				.setMessage(R.string.confirm_delete_text)
				.setPositiveButton(R.string.confirm_delete_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String filepath = FileHelper.getFilePath()
										+ "/" + Constants.FILE_DIRECTORY;
								File file = new File(filepath, fileName);

								if (file.exists()) {
									file.delete();
									list.remove(position);
									notifyDataSetChanged();
								}

								filepath = context.getFilesDir()
										.getAbsolutePath()
										+ "/"
										+ Constants.FILE_DIRECTORY;
								file = new File(filepath, fileName);

								if (file.exists()) {
									file.delete();
									list.remove(position);
									notifyDataSetChanged();
								}
							}
						})
				.setNegativeButton(R.string.confirm_delete_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	void sendMail(String fileName) {
		String filepath = FileHelper.getFilePath() + "/"
				+ Constants.FILE_DIRECTORY;
		File file = new File(filepath, fileName);

		Intent sendIntent;

		sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT,
				context.getString(R.string.sendMail_subject));
		sendIntent.putExtra(Intent.EXTRA_TEXT,
				context.getString(R.string.sendMail_body));
		if (file.exists())
			sendIntent.putExtra(
					Intent.EXTRA_STREAM,
					Uri.parse("file://" + FileHelper.getFilePath() + "/"
							+ Constants.FILE_DIRECTORY + "/" + fileName));
		else
			sendIntent.putExtra(
					Intent.EXTRA_STREAM,
					Uri.parse("file://"
							+ context.getFilesDir().getAbsolutePath() + "/"
							+ Constants.FILE_DIRECTORY + "/" + fileName));
		sendIntent.setType("audio/3gpp");

		context.startActivity(Intent.createChooser(sendIntent,
				context.getString(R.string.send_mail)));
	}

	void startPlayExternal(String charSequence) {
		String filepath = FileHelper.getFilePath() + "/"
				+ Constants.FILE_DIRECTORY;
		File file = new File(filepath, charSequence);
		Uri intentUri;

		if (file.exists())
			intentUri = Uri.parse("file://" + FileHelper.getFilePath() + "/"
					+ Constants.FILE_DIRECTORY + "/" + charSequence);
		else
			intentUri = Uri.parse("file://"
					+ context.getFilesDir().getAbsolutePath() + "/"
					+ Constants.FILE_DIRECTORY + "/" + charSequence);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(intentUri, "audio/3gpp");
		context.startActivity(intent);
	}

	public void removeFromList(int position) {
		list.remove(position);
	}
}
