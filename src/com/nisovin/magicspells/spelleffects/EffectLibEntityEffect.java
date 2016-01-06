package com.nisovin.magicspells.spelleffects;

import org.bukkit.entity.Entity;

public class EffectLibEntityEffect extends EffectLibEffect {

//	protected Map<UUID, Effect> entityEffectMap = new HashMap<UUID, Effect>();
//	protected Map<String, Effect> playerEffectMap = new HashMap<String, Effect>();
	
	//protected Map<String, Effect> idEffectMap = new HashMap<String, Effect>();
	
	
	@Override
	protected Runnable playEffectEntity(final Entity e) {
		return manager.start(className, effectLibSection, e);
	}

}
