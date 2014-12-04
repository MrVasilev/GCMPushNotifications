package com.example.pushnotifications;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class ResultActivity extends Activity {

	private TextView messageTextView;
	private TextView welcomeTextView;

	private String registeredEmail;
	private String receivedMessage;

	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		messageTextView = (TextView) findViewById(R.id.messageTextView);
		welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);

		sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

		registeredEmail = sharedPreferences.getString(Constants.KEY_EMAIL, "");
		receivedMessage = getIntent().getExtras().getString(Constants.KEY_MESSAGE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Utils.checkGooglePlayServices(this);

		welcomeTextView.setText("Hello,\n" + registeredEmail);

		if (!TextUtils.isEmpty(receivedMessage)) {
			messageTextView.setText(receivedMessage);
		}
	}

}
