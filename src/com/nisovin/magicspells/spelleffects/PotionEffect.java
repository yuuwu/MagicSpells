package com.nisovin.magicspells.spelleffects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ConfigData;

/**
 * public class PotionEffect<p>
 * Configuration fields:<br>
 * <ul>
 * <li>color: the color of the potion particles in rgb hex code.</li>
 * <li>duration: a base 10 integer representing how long the particles should play for.</li>
 * </ul>
 */
public class PotionEffect extends SpellEffect {
	
	@ConfigData(field="color", dataType="String", defaultValue="", description="Color of the potion effect using hexidecimal color format")
	int color = 0xFF0000;
	
	@ConfigData(field="duration", dataType="int", defaultValue="30")
	int duration = 30;
	
	@Override
	public void loadFromString(String string) {
		if (string != null && !string.isEmpty()) {
			String[] data = string.split(" ");
			try {
				color = Integer.parseInt(data[0], 16);
			} catch (NumberFormatException e) {
				DebugHandler.debugNumberFormat(e);
			}
			if (data.length > 1) {
				try {
					duration = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					DebugHandler.debugNumberFormat(e);
				}
			}
		}
	}
	
	@Override
	public void loadFromConfig(ConfigurationSection config) {
		String c = config.getString("color", "");
		if (!c.isEmpty()) {
			try {
				color = Integer.parseInt(c, 16);
			} catch (NumberFormatException e) {
				DebugHandler.debugNumberFormat(e);
			}
		}
		duration = config.getInt("duration", duration);
	}

	@Override
	public Runnable playEffectEntity(Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)entity;
			MagicSpells.getVolatileCodeHandler().addPotionGraphicalEffect(le, color, duration);
			//TODO use a non volatile handler for this
		}
		return null;
	}
	
}
