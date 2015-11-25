package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import com.nisovin.magicspells.MagicSpells;

import de.slikey.effectlib.EffectManager;

public class EffectLibEffect extends SpellEffect {

	ConfigurationSection effectLibSection;
	EffectManager manager = MagicSpells.plugin.effectManager;
	String className;
	
	@Override
	public void loadFromString(String string) {
		//TODO make a string loading schema
	}

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		effectLibSection = config.getConfigurationSection("effectlib");
		className = effectLibSection.getString("class");
	}

	@Override
	protected void playEffectLocation(Location location) {
		manager.start(className, effectLibSection, location);
	}
}
