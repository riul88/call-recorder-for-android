/*
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;

public class FileHelper {
	/**
	 * returns absolute file directory
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getFilename(String phoneNumber) throws Exception {
		String filepath = null;
		String myDate = null;
		File file = null;
		if (phoneNumber == null)
			throw new Exception("Phone number can't be empty");
		try {
			filepath = getFilePath();

			file = new File(filepath, Constants.FILE_DIRECTORY);

			if (!file.exists()) {
				file.mkdirs();
			}

			myDate = (String) DateFormat.format("yyyyMMddkkmmss", new Date());

			// Clean characters in file name
			phoneNumber = phoneNumber.replaceAll("[\\*\\+-]", "");
			if (phoneNumber.length() > 10) {
				phoneNumber.substring(phoneNumber.length() - 10,
						phoneNumber.length());
			}
		} catch (Exception e) {
			Log.e(Constants.TAG, "Exception " + phoneNumber);
			e.printStackTrace();
		}

		return (file.getAbsolutePath() + "/d" + myDate + "p" + phoneNumber + ".3gp");
	}

	public static String getFilePath() {
		// TODO: Change to user selected directory
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static void deleteAllRecords(Activity caller) {
		String filepath = getFilePath() + "/" + Constants.FILE_DIRECTORY;
		File file = new File(filepath);

		String listOfFileNames[] = file.list();

		for (int i = 0; i < listOfFileNames.length; i++) {
			File file2 = new File(filepath, listOfFileNames[i]);
			if (file2.exists()) {
				file2.delete();
			}
		}

		filepath = caller.getFilesDir().getAbsolutePath() + "/"
				+ Constants.FILE_DIRECTORY;
		file = new File(filepath);

		String listOfFileNames2[] = file.list();

		for (int i = 0; i < listOfFileNames2.length; i++) {
			File file2 = new File(filepath, listOfFileNames2[i]);
			if (file2.exists()) {
				file2.delete();
			}
		}
	}

	/**
	 * Obtains the contact list for the currently selected account.
	 * 
	 * @return A cursor for for accessing the contact list.
	 */
	public static String getContactName(String phoneNum, Activity caller) {
		String res = phoneNum;
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		String selection = null;
		String[] selectionArgs = null;
		Cursor names = caller.getContentResolver().query(uri, projection,
				selection, selectionArgs, null);

		int indexName = names
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = names
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		if (names.getCount() > 0) {
			names.moveToFirst();
			do {
				String name = names.getString(indexName);
				String number = names.getString(indexNumber).replaceAll(
						"[\\*\\+-]", "");

				if (number.compareTo(phoneNum) == 0) {
					res = name;
					break;
				}
			} while (names.moveToNext());
		}

		return res;
	}

	/**
	 * Fetches list of previous recordings
	 * 
	 * @param f
	 * @return
	 */
	public static List<Model> listDir2(File f, Activity caller) {
		File[] files = f.listFiles();
		List<Model> fileList = new ArrayList<Model>();
		for (File file : files) {
			if (!file.getName().matches(Constants.FILE_NAME_PATTERN)) {
				Log.d(Constants.TAG, String.format(
						"'%s' didn't match the file name pattern",
						file.getName()));
				continue;
			}

			Model mModel = new Model(file.getName());
			String phoneNum = mModel.getCallName().substring(16,
					mModel.getCallName().length() - 4);
			mModel.setUserNameFromContact(getContactName(phoneNum, caller));
			fileList.add(mModel);
		}

		Collections.sort(fileList);
		Collections.sort(fileList, Collections.reverseOrder());

		return fileList;
	}

	public static List<Model> listFiles(Activity caller) {
		String filepath = FileHelper.getFilePath();
		final File file = new File(filepath, Constants.FILE_DIRECTORY);

		if (!file.exists()) {
			file.mkdirs();
		}

		final List<Model> listDir = FileHelper.listDir2(file, caller);

		filepath = caller.getFilesDir().getAbsolutePath();
		final File file2 = new File(filepath, Constants.FILE_DIRECTORY);

		if (!file2.exists()) {
			file2.mkdirs();
		}

		final List<Model> listDir2 = FileHelper.listDir2(file2, caller);

		listDir.addAll(listDir2);

		return listDir;
	}

	public static void deleteFile(String fileName) {
		if (fileName == null)
			return;
		Log.d(Constants.TAG, "FileHelper deleteFile " + fileName);
		try {
			File file = new File(fileName);

			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			Log.e(Constants.TAG, "Exception");
			e.printStackTrace();
		}
	}
}
