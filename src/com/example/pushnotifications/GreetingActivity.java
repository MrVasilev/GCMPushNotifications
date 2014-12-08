package com.example.pushnotifications;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

public class GreetingActivity extends Activity {

	private TextView messageTextView;
	private ImageView greetImageView;

	private String jsonMessage;
	private String message;
	private String imageUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_greeting);

		messageTextView = (TextView) findViewById(R.id.messageTextView);
		greetImageView = (ImageView) findViewById(R.id.greetImageView);

		Bundle extras = getIntent().getExtras();

		jsonMessage = extras.getString(Constants.KEY_MESSAGE);

		Gson gson = new Gson();
		JsonObject responseJson = new JsonObject();

		responseJson = gson.fromJson(jsonMessage, JsonObject.class);

		message = responseJson.get(Constants.KEY_MESSAGE).getAsString();
		imageUrl = responseJson.get(Constants.KEY_IMAGE_URL).getAsString();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Utils.checkGooglePlayServices(this);

		if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(imageUrl)) {

			messageTextView.setText(message);
			Picasso.with(this).load(imageUrl).into(greetImageView);
		}
	}
}
