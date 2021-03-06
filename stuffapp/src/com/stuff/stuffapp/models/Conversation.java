package com.stuff.stuffapp.models;

import java.io.Serializable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Conversation")
public class Conversation extends ParseObject implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8930877670482943104L;
	public static final String ATTR_USER_ONE = "userOne";
	public static final String ATTR_USER_TWO = "userTwo";
	public static final String ATTR_ITEM = "item";
	public static final String ATTR_RECENT_REPLY = "recentReply";

	public Conversation() {
		super();
	}
	
	
	public ParseUser getUserOne() {
		return getParseUser(ATTR_USER_ONE);
	}

	public void setUserOne(ParseUser user) {
		put(ATTR_USER_ONE, user);
	}
	
	public ParseUser getUserTwo() {
		return getParseUser(ATTR_USER_TWO);
	}

	public void setUserTwo(ParseUser user) {
		put(ATTR_USER_TWO, user);
	}

	public Item getItem() {
		return (Item) getParseObject(ATTR_ITEM);
	}

	public void setItem(Item item) {
		put(ATTR_ITEM, item);
	}
	
	public ConversationReply getRecentReply() {
		return (ConversationReply) getParseObject(ATTR_RECENT_REPLY);
	}

	public void setRecentReply(ConversationReply conversationReply) {
		put(ATTR_RECENT_REPLY, conversationReply);
	}
}
