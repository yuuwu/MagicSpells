package com.nisovin.magicspells.util.prompt;

import java.util.Map;

import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class ConversationContextUtil {

	public static final String CONVERSABLE_KEY = "magicspells.conversable";
	
	
	public static final Conversable getConversable(ConversationContext context) {
		return getConversable(context.getAllSessionData());
	}
	
	public static final Conversable getConversable(Map<Object, Object> context) {
		return (Conversable)context.get(CONVERSABLE_KEY);
	}
	
	public static final void setconversable(ConversationContext context, Conversable conversable) {
		setConversable(context.getAllSessionData(), conversable);
	}
	
	public static final void setConversable(Map<Object, Object> context, Conversable conversable) {
		context.put(CONVERSABLE_KEY, conversable);
	}
	
}
