package com.example.pushnotifications;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText emailEditText;

	private SharedPreferences sharedPreferences;

	private String registrationId;

	private ProgressDialog progressDialog;

	private boolean isGooglePlayServicesAvailable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		emailEditText = (EditText) findViewById(R.id.emailEditText);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.please_wait_message));

		sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

		registrationId = sharedPreferences.getString(Constants.REGISTRATION_ID, "");

		if (!TextUtils.isEmpty(registrationId)) {

			openResultScreen();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		isGooglePlayServicesAvailable = Utils.checkGooglePlayServices(this);
	}

	public void onRegisterClick(View view) {

		if (view.getId() == R.id.registerButton) {

			String enteredEmail = emailEditText.getText().toString().trim();

			if (!TextUtils.isEmpty(enteredEmail) && Utils.validateEmail(enteredEmail)) {

				if (isGooglePlayServicesAvailable) {

					RegisterUserTask registerUserTask = new RegisterUserTask();
					registerUserTask.execute(enteredEmail);
				} else {
					Toast.makeText(this, getString(R.string.not_available_gps), Toast.LENGTH_SHORT).show();
				}

			} else {

				Toast.makeText(this, getString(R.string.not_valid_email_message), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void openResultScreen() {

		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(Constants.REGISTRATION_ID, registrationId);
		startActivity(intent);
		finish();
	}

	private void storeRegistrationIdInSharedPref(String registrationId, String email) {

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.REGISTRATION_ID, registrationId);
		editor.putString(Constants.KEY_EMAIL, email);
		editor.commit();

		storeRegistrationIdInServer();
	}

	private void storeRegistrationIdInServer() {

		progressDialog.show();

		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		RequestParams requestParams = new RequestParams();

		requestParams.put(Constants.REGISTRATION_ID, registrationId);

		asyncHttpClient.setTimeout(10000);

		asyncHttpClient.post(Constants.APP_SERVER_URL, requestParams, new AsyncHttpResponseHandler() {

			@Override
			@Deprecated
			public void onSuccess(int statusCode, Header[] headers, String content) {
				super.onSuccess(statusCode, headers, content);

				progressDialog.dismiss();

				Toast.makeText(getApplicationContext(), "Registration Id shared successfully with Web App",
						Toast.LENGTH_LONG).show();

				openResultScreen();
			}

			@Override
			@Deprecated
			public void onFailure(int statusCode, Header[] headers, Throwable error, String content) {
				super.onFailure(statusCode, headers, error, content);

				progressDialog.dismiss();

				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(getApplicationContext(), "Requested resource not found" + error.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							getApplicationContext(),
							"Unexpected Error occcured! Device might not be connected to Internet or remote server is not up and running!"
									+ error.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private class RegisterUserTask extends AsyncTask<String, Void, String> {

		private String email;

		@Override
		protected String doInBackground(String... params) {

			email = params[0];
			String message;

			try {

				GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(getApplicationContext());

				registrationId = googleCloudMessaging.register(Constants.GOOGLE_PROJECT_ID);

				message = "Registration ID : " + registrationId;

			} catch (IOException e) {
				message = "ERROR : " + e.getMessage();
			}

			return message;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (!TextUtils.isEmpty(registrationId)) {

				storeRegistrationIdInSharedPref(registrationId, email);

				Toast.makeText(getApplicationContext(), "Registered with GCM Server successfully.\n" + result,
						Toast.LENGTH_LONG).show();
			} else {

				Toast.makeText(getApplicationContext(), "Registration ID Creation Failed.\n" + result,
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
