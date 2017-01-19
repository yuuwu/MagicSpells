package com.nisovin.magicspells.util.prompt;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

public class MagicEnumSetPrompt extends FixedSetPrompt {

	private String promptText;
	
	private MagicPromptResponder responder;
	
	public MagicEnumSetPrompt(List<String> options) {
		super();
		super.fixedSet = new ArrayList<String>(options);
	}
	
	public MagicEnumSetPrompt(String... options) {
		super(options);
	}
	
	@Override
	public String getPromptText(ConversationContext context) {
		return promptText;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		return responder.acceptValidatedInput(context, input);
	}
	
	
	
	
	
	
	public static MagicEnumSetPrompt fromConfigSection(ConfigurationSection section) {
		// get the options
		String enumClassName = section.getString("enum-class");
		Class<? extends Enum> enumClass;
		try {
			enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		Enum[] values =  enumClass.getEnumConstants();
		if (values == null || values.length == 0) return null;
		List<String> parsedValues = new ArrayList<String>();
		for (Enum e: values) {
			parsedValues.add(e.name());
		}
		MagicEnumSetPrompt ret = new MagicEnumSetPrompt(parsedValues);
		
		ret.responder = new MagicPromptResponder(section);
		
		String promptText = section.getString("prompt-text", "");
		ret.promptText = promptText;
		
		return ret;
	}
}
