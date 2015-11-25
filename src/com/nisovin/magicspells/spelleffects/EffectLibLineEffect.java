package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;

public class EffectLibLineEffect extends EffectLibEffect {

	@Override
	public void playEffect(Location location1, Location location2) {
		manager.start(className, effectLibSection, location1, location2, null, null, null);
	}
	
}
