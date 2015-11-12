package com.nisovin.magicspells.spelleffects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.DebugHandler;

/**
 * class SoundEffect<p>
 * Configuration fields:<br>
 * <ul>
 * <li>dir</li>
 * </ul>
 */

class SmokeEffect extends SpellEffect {

	int dir = 4;
	
	@Override
	public void loadFromString(String string) {
		if (string != null && !string.isEmpty()) {
			try {
				dir = Integer.parseInt(string);
			} catch (NumberFormatException e) {
				DebugHandler.debugNumberFormat(e);
			}
		}
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		dir = config.getInt("dir", dir);
	}

	@Override
	public void playEffectLocation(Location location) {
		location.getWorld().playEffect(location, Effect.SMOKE, dir);
	}

}
