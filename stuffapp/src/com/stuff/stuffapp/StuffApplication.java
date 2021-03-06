package com.stuff.stuffapp;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.PushService;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.models.Item;
import com.stuff.stuffapp.models.Message;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;


public class StuffApplication extends Application {

	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Item.class);
		ParseObject.registerSubclass(Message.class);
		ParseObject.registerSubclass(Conversation.class);
		ParseObject.registerSubclass(ConversationReply.class);

		// According to parse.com documentation Application ID and Client Key can be public
		Parse.initialize(this, "2vWbCt6aWC1e0pCRk8BxsuXO1XXk9tqpp1ZMvOzr", "zKk8JQ6cHcIiUCx9r5hVlSsvDAgBGVlJeesXeii0");

		ParseFacebookUtils.initialize(getString(R.string.app_id));

		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.defaultDisplayImageOptions(options)
				.build();
		ImageLoader.getInstance().init(config);
		
	    // Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, MainActivity.class);
	}

}
