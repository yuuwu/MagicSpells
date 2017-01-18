package com.nisovin.magicspells.util.prompt;

import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;

public class MagicRegexPrompt extends RegexPrompt {
	
	private boolean saveToVariable;
	private String variableName;
	
	private String promptText;
	
	public MagicRegexPrompt(String pattern) {
		super(pattern);
	}
	
	public MagicRegexPrompt(Pattern pattern) {
		super(pattern);
	}

	@Override
	public String getPromptText(ConversationContext paramConversationContext) {
		return promptText;
	}

	@Override
	protected Prompt acceptValidatedInput(
			ConversationContext paramConversationContext, String paramString) {
		String playerName = null;
		Conversable who = ConversationContextUtil.getConversable(paramConversationContext.getAllSessionData());
		if (who != null && who instanceof Player) {
			playerName = ((Player)who).getName();
		}
		
		if (saveToVariable) {
			MagicSpells.getVariableManager().set(variableName, playerName, paramString);
		}
		return Prompt.END_OF_CONVERSATION;
	}
	
	
	public static MagicRegexPrompt fromConfigSection(ConfigurationSection section) {
		// handle the regex
		String regexp = section.getString("regexp", null);
		if (regexp == null || regexp.isEmpty()) return null;
		MagicRegexPrompt ret = new MagicRegexPrompt(regexp);
		
		// handle the variable name
		String variableName = section.getString("variable-name", null);
		
		ret.variableName = variableName;
		
		ret.saveToVariable = MagicSpells.getVariableManager().getVariable(variableName) != null;
		
		String promptText = section.getString("prompt-text", "");
		ret.promptText = promptText;
		
		return ret;
	}

}
