package com.nisovin.magicspells.spelleffects;

import org.bukkit.entity.Entity;

public class EffectLibEntityEffect extends EffectLibEffect {
	
	@Override
	protected void playEffectEntity(Entity e) {
		manager.start(className, effectLibSection, e);
	}

}
