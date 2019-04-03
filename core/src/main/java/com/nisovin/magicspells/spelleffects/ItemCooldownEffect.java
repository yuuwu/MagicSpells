package com.nisovin.magicspells.spelleffects;

import com.nisovin.magicspells.util.TimeUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.Util;

public class ItemCooldownEffect extends SpellEffect {

	ItemStack item;
	int duration;
	
	@Override
	public void loadFromString(String string) {
		super.loadFromString(string);
		String[] split = Util.splitParams(string);
		item = Util.getItemStackFromString(split[0]);
		duration = Integer.parseInt(split[1]);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		item = Util.getItemStackFromString(config.getString("item", "stone"));
		duration = config.getInt("duration", TimeUtil.TICKS_PER_SECOND);
	}
	
	@Override
	protected Runnable playEffectEntity(Entity entity) {
		if (!(entity instanceof Player)) return null;
		// TODO non volatile
		MagicSpells.getVolatileCodeHandler().showItemCooldown((Player)entity, item, duration);
		return null;
	}
	
}
