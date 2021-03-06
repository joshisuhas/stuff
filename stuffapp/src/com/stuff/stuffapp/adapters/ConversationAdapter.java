package com.stuff.stuffapp.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.RoundedImageView;
import com.stuff.stuffapp.helpers.ConversationReplyListener;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;

public class ConversationAdapter extends BaseAdapter{
	private Context mContext;
    private Map<String,Bitmap> userImageMap;
    List<ConversationReply> conversationReplies;
    ConversationReplyListener conversationReplyListener;
    
	
	public ConversationAdapter(Context context, final Conversation c,ConversationReplyListener listener) {
		super();
		this.mContext = context;
		userImageMap = new HashMap<String,Bitmap>();
		this.conversationReplyListener = listener;
		 ParseQuery<ConversationReply> query = null;
         //This should never be null?
         if(c != null) {
        	 
        	 if(c.getObjectId() == null) {
        		 try {
					c.fetchIfNeeded();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	 }
         	
         	query = new ParseQuery<ConversationReply>(ConversationReply.class);
         	query.whereEqualTo(ConversationReply.ATTR_CONVERSATION, c);
         	
         } else  {
         	//something is broken handle this case. 
         }
         FindCallback callback = new FindCallbackImpl(this);
         query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
         query.findInBackground(callback);
//         try {
//        	//query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
//			conversationReplies = query.find();
//			conversationReplyListener.conversationRepliesAvailable(conversationReplies);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	} 
	
	
	@Override
	public int getCount() {

		if(conversationReplies != null) {
			return conversationReplies.size();
		}
		
		return 0;
			
	}
	@Override
	public Object getItem(int position) {

		return conversationReplies.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//ConversationReply message = (ConversationReply) this.getItem(position);
		ConversationReply message = conversationReplies.get(position);
		ViewHolder holder;  
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.rivMessageProfilePicture = (RoundedImageView)convertView.findViewById(R.id.rivMessageProfilePicture);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getText());
		
		LayoutParams lpMessage = (LayoutParams)holder.message.getLayoutParams();
		LayoutParams lpRoundedImageView = (LayoutParams)holder.rivMessageProfilePicture.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			lpRoundedImageView.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lpMessage.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{	
			loadProfileImage(message,holder);
						//Check whether message is mine to show my profile picture and align to right
			if(message.isMine())
			{

				lpRoundedImageView.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpRoundedImageView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpMessage.removeRule(RelativeLayout.RIGHT_OF);
				lpMessage.addRule(RelativeLayout.LEFT_OF,R.id.rivMessageProfilePicture);
				
			}
			//If not mine then it is from sender to show their profile picture and align to left
			else
			{
				lpRoundedImageView.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);	
				lpRoundedImageView.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpMessage.removeRule(RelativeLayout.LEFT_OF);
				lpMessage.addRule(RelativeLayout.RIGHT_OF,R.id.rivMessageProfilePicture);
			}
			holder.message.setLayoutParams(lpMessage);
			holder.message.setTextColor(R.color.textColor);	
		}
		convertView.refreshDrawableState();
		return convertView;
	}
	
	
public void addConversationReply (ConversationReply reply) {
	if(conversationReplies != null) {
		this.conversationReplies.add(reply);
		this.notifyDataSetChanged();
	}

}
	
private void loadProfileImage(ConversationReply message, ViewHolder holder) {

				
		ParseUser user = null;
		try {
			user = message.getUser().fetchIfNeeded();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(userImageMap.get(user.getObjectId()) == null) {
			
			ImageLoader imageLoader = ImageLoader.getInstance();
			JSONObject profileData = user.getJSONObject("profile");

			try {
				
				//imageLoader.displayImage("http://graph.facebook.com/" + profileData.get("facebookId").toString() + "/picture?type=normal", holder.rivMessageProfilePicture);
				Bitmap bitmap = imageLoader.loadImageSync("http://graph.facebook.com/" + profileData.get("facebookId").toString() + "/picture?type=normal");
				userImageMap.put(user.getObjectId(), bitmap);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			
		}
		holder.rivMessageProfilePicture.setImageBitmap(userImageMap.get(user.getObjectId()));
		 

		
	}

	private static class ViewHolder
	{
		TextView message;
		RoundedImageView rivMessageProfilePicture;
		
	}
	
	private class FindCallbackImpl extends FindCallback<ConversationReply> {

		private ConversationAdapter adapter = null;
		
		public FindCallbackImpl(ConversationAdapter conversationAdapter) {
			this.adapter = conversationAdapter;
		}
		
		@Override
		public void done(List<ConversationReply> results, ParseException ex) {
			
			if(ex== null) {
				adapter.conversationReplies = results;
				adapter.notifyDataSetChanged();
				if(adapter.conversationReplyListener != null) {
					
					adapter.conversationReplyListener.conversationRepliesAvailable(results);
				}
				
			}
			else {
				ex.printStackTrace();
			}
			
		}
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
		if(conversationReplies !=null) {
			conversationReplies.clear();
			notifyDataSetChanged();
			notifyDataSetInvalidated();
		}
		
		
	}
}




