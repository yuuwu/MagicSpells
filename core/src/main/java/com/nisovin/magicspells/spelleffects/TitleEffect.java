package com.nisovin.magicspells.spelleffects;

import com.nisovin.magicspells.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;

public class TitleEffect extends SpellEffect {

	String title = null;
	
	String subtitle = null;
	
	int fadeIn = 10;
	
	int stay = 40;
	
	int fadeOut = 10;
	
	boolean broadcast = false;
	
	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		title = config.getString("title", title);
		if (title != null) title = ChatColor.translateAlternateColorCodes('&', title);
		subtitle = config.getString("subtitle", subtitle);
		if (subtitle != null) subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
		fadeIn = config.getInt("fade-in", fadeIn);
		stay = config.getInt("stay", stay);
		fadeOut = config.getInt("fade-out", fadeOut);
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
		MagicSpells.getVolatileCodeHandler().sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
	}

}
