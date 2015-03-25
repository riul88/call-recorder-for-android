package com.callrecorder.android;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TermsActivity extends Activity {

	public TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_layout);

		mTextView = (TextView) findViewById(R.id.txtTerms2);

		try {
			mTextView.setText(MainActivity.getDataFromRawFiles(R.raw.terms));
		} catch (IOException e) {

		}
	}

}
