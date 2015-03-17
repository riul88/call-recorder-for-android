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

public class Constants {
    public static final String TAG = "Call recorder: ";

	public static final String FILE_DIRECTORY = "recordedCalls";
	public static final String LISTEN_ENABLED = "ListenEnabled";

	public static final int MEDIA_MOUNTED = 0;
	public static final int MEDIA_MOUNTED_READ_ONLY = 1;
	public static final int NO_MEDIA = 2;

	public static final int STATE_INCOMING_NUMBER = 0;
	public static final int STATE_CALL_START = 1;
	public static final int STATE_CALL_END = 2;
	public static final int EXTRA_INCOMING_NUMBER = 3;

}
