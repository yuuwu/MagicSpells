package com.nisovin.magicspells.spelleffects;

import com.nisovin.magicspells.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;

public class ActionBarTextEffect extends SpellEffect {

	String message = "";
	
	boolean broadcast = false;
	
	@Override
	public void loadFromString(String string) {
		super.loadFromString(string);
		message = ChatColor.translateAlternateColorCodes('&', string);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		message = ChatColor.translateAlternateColorCodes('&', config.getString("message", message));
		broadcast = config.getBoolean("broadcast", broadcast);
	}
	
	@Override
	protected Runnable playEffectEntity(Entity entity) {
		if (broadcast) {
			Util.forEachPlayerOnline(this::send);
		} else if (entity instanceof Player) {
			send((Player) entity);
		}
		return null;
	}
	
	private void send(Player player) {
		// TODO non volatile
		MagicSpells.getVolatileCodeHandler().sendActionBarMessage(player, message);
	}
	
}
