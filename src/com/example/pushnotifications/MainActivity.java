package com.example.pushnotifications;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pushnotifications.ServerAPI.SuccessListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {

	private EditText emailEditText;

	private SharedPreferences sharedPreferences;

	private String registrationId;

	private boolean isGooglePlayServicesAvailable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		emailEditText = (EditText) findViewById(R.id.emailEditText);

		sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

		registrationId = sharedPreferences.getString(Constants.KEY_REGISTRATION_ID, "");

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
		intent.putExtra(Constants.KEY_REGISTRATION_ID, registrationId);
		startActivity(intent);
		finish();
	}

	private void storeRegistrationIdInSharedPref(String registrationId, String email) {

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.KEY_REGISTRATION_ID, registrationId);
		editor.putString(Constants.KEY_EMAIL, email);
		editor.commit();

		storeRegistrationIdInServer();
	}

	private void storeRegistrationIdInServer() {

		ServerAPI serverAPI = ServerAPI.getInstance(this);

		serverAPI.setMethod(Constants.METHOD_JUST_REGISTER).setRegId(registrationId)
				.addOnSuccess(new SuccessListener() {

					@Override
					public void onSuccess() {

						openResultScreen();
					}
				}).execute();
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

				Toast.makeText(getApplicationContext(),
						getString(R.string.success_registration_message) + "\n" + result, Toast.LENGTH_LONG).show();
			} else {

				Toast.makeText(getApplicationContext(),
						getString(R.string.failed_registration_message) + "\n" + result, Toast.LENGTH_LONG).show();
			}
		}
	}
}
