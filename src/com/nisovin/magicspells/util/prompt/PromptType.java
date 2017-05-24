package com.nisovin.magicspells.util.prompt;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.Prompt;

public enum PromptType {

	REGEX_PROMPT(new String[]{ "regex" }) {

		@Override
		public Prompt constructPrompt(ConfigurationSection section) {
			return MagicRegexPrompt.fromConfigSection(section);
		}
		
	},
	
	FIXED_SET_PROMOT(new String[] { "fixed-set" }) {

		@Override
		public Prompt constructPrompt(ConfigurationSection section) {
			return MagicFixedSetPrompt.fromConfigSection(section);
		}
		
	},
	
	ENUM_SET_PROMPT(new String[] { "enum" }) {

		@Override
		public Prompt constructPrompt(ConfigurationSection section) {
			return MagicEnumSetPrompt.fromConfigSection(section);
		}
		
		@Override
		public void unload() {
			MagicEnumSetPrompt.unload();
		}
		
	}
	
	;
	
	private String[] labels;
	
	private static boolean initialized = false;
	private static Map<String, PromptType> nameMap = null;
	
	private PromptType(String[] names) {
		labels = names;
	}
	
	public abstract Prompt constructPrompt(ConfigurationSection section);
	
	public void unload() {
		// instances may override
	}
	
	public static void unloadDestructPromptData() {
		for (PromptType type: PromptType.values()) {
			type.unload();
		}
	}
	
	private static void initialize() {
		if (initialized) return;
		nameMap = new HashMap<String, PromptType>();
		for (PromptType type: PromptType.values()) {
			for (String name: type.labels) {
				nameMap.put(name.toLowerCase(), type);
			}
		}
		
		initialized = true;
	}
	
	public static PromptType getPromptType(String label) {
		if (!initialized) initialize();
		return nameMap.get(label.toLowerCase());
	}
	
}
