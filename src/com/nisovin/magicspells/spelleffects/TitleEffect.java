package com.nisovin.magicspells.spelleffects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ConfigData;
/**
 * class TitleEffect<p>
 * Configuration fields:<br>
 * <ul>
 * <li>title</li>
 * <li>subtitle</li>
 * <li>fade-in</li>
 * <li>stay</li>
 * <li>fade-out</li>
 * <li>broadcast</li>
 * </ul>
 */
public class TitleEffect extends SpellEffect {

	@ConfigData(field="title", dataType="String", defaultValue="")
	String title = null;
	
	@ConfigData(field="subtitle", dataType="String", defaultValue="")
	String subtitle = null;
	
	@ConfigData(field="fade-in", dataType="int", defaultValue="10")
	int fadeIn = 10;
	
	@ConfigData(field="stay", dataType="int", defaultValue="40")
	int stay = 40;
	
	@ConfigData(field="fade-out", dataType="int", defaultValue="10")
	int fadeOut = 10;
	
	@ConfigData(field="broadcast", dataType="boolean", defaultValue="false")
	boolean broadcast = false;
	
	@Override
	public void loadFromString(String string) {
		//No string format
	}

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
			for (Player player : Bukkit.getOnlinePlayers()) {
				MagicSpells.getVolatileCodeHandler().sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
				//TODO use a non volatile handler for this
			}
		} else if (entity != null && entity instanceof Player) {
			MagicSpells.getVolatileCodeHandler().sendTitleToPlayer((Player)entity, title, subtitle, fadeIn, stay, fadeOut);
			//TODO use a non volatile handler for this
		}
		return null;
	}

}
