package com.example.pushnotifications;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerAPI {

	private static final String SERVER_METHOD_JUST_REGISTER = "/gcm/gcm.php?shareRegId=true";
	private static final String SERVER_METHOD_INSERT_USER = "/gcmwebapp/insert_user.php";

	private String appServerUrl = "http://10.0.4.58:8787";

	private Activity activity;

	private static ServerAPI instance;

	private AsyncHttpClient asyncHttpClient;
	private RequestParams requestParams;

	private ProgressDialog progressDialog;

	private SuccessListener successListener;
	private FailureListener failureListener;

	public interface SuccessListener {

		void onSuccess();
	}

	public interface FailureListener {

		void onFailure();
	}

	private ServerAPI(Activity activity) {
		this.activity = activity;
		this.asyncHttpClient = new AsyncHttpClient();
		this.requestParams = new RequestParams();
		this.progressDialog = new ProgressDialog(activity);
		this.progressDialog.setTitle(activity.getString(R.string.please_wait_message));
	}

	public static ServerAPI getInstance(Activity activity) {

		if (instance == null)
			instance = new ServerAPI(activity);

		return instance;
	}

	public ServerAPI setMethod(String method) {

		if (!TextUtils.isEmpty(method)) {

			switch (method) {

			case Constants.METHOD_JUST_REGISTER:
				appServerUrl += SERVER_METHOD_JUST_REGISTER;
				break;

			case Constants.METHOD_INSERT_USER:
				appServerUrl += SERVER_METHOD_INSERT_USER;
				break;

			default:
				break;
			}
		}

		return instance;
	}

	public ServerAPI setEmail(String email) {

		if (!TextUtils.isEmpty(email)) {

			requestParams.put(Constants.KEY_EMAIL, email);
		}

		return instance;
	}

	public ServerAPI setRegId(String registrationId) {

		if (!TextUtils.isEmpty(registrationId)) {

			requestParams.put(Constants.KEY_REGISTRATION_ID, registrationId);
		}

		return instance;
	}

	public ServerAPI addOnSuccess(SuccessListener successListener) {

		this.successListener = successListener;

		return instance;
	}

	public ServerAPI addOnFailure(FailureListener failureListener) {

		this.failureListener = failureListener;

		return instance;
	}

	public void execute() {

		progressDialog.show();

		asyncHttpClient.post(appServerUrl, requestParams, new AsyncHttpResponseHandler() {

			@Override
			@Deprecated
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);

				progressDialog.dismiss();

				Toast.makeText(activity, activity.getString(R.string.web_app_success_message), Toast.LENGTH_LONG)
						.show();

				if (successListener != null)
					successListener.onSuccess();
			}

			@Override
			@Deprecated
			public void onFailure(int statusCode, Throwable error, String content) {
				super.onFailure(statusCode, error, content);

				progressDialog.dismiss();

				if (failureListener != null) {

					failureListener.onFailure();

				} else {

					// When Http response code is '404'
					if (statusCode == 404) {
						Toast.makeText(activity,
								activity.getString(R.string.web_app_not_found_error) + error.getMessage(),
								Toast.LENGTH_LONG).show();
					}
					// When Http response code is '500'
					else if (statusCode == 500) {
						Toast.makeText(activity, activity.getString(R.string.web_app_500_error) + error.getMessage(),
								Toast.LENGTH_LONG).show();
					}
					// When Http response code other than 404, 500
					else {
						Toast.makeText(activity,
								activity.getString(R.string.web_app_unexpected_error) + error.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}
}
