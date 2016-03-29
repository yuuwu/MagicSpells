package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class EffectLibLineEffect extends EffectLibEffect {
	
	@Override
	public Runnable playEffect(Location location1, Location location2) {
		manager.start(className, effectLibSection, location1, location2, null, null, null);
		return null;
	}
	
	@Override
	public void playTrackingLinePatterns(Location origin, Location target, Entity originEntity,
			Entity targetEntity) {
		manager.start(className, effectLibSection, origin, target, originEntity, targetEntity, null);
	}
	
}
