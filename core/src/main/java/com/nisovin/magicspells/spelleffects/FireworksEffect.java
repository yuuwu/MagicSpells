package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;

public class FireworksEffect extends SpellEffect {

	int type;
	int flightDuration;

	boolean trail;
	boolean flicker;

	int[] colors = new int[] { 0xFF0000 };
	int[] fadeColors = new int[] { 0xFF0000 };

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		type = config.getInt("type", 0);
		flightDuration = config.getInt("flight", 0);

		trail = config.getBoolean("trail", false);
		flicker = config.getBoolean("flicker", false);

		String[] c = config.getString("colors", "FF0000").replace(" ", "").split(",");
		if (c.length > 0) {
			colors = new int[c.length];
			for (int i = 0; i < colors.length; i++) {
				try {
					colors[i] = Integer.parseInt(c[i], 16);
				} catch (NumberFormatException e) {
					colors[i] = 0;
				}
			}
		}

		String[] fc = config.getString("fade-colors", "").replace(" ", "").split(",");
		if (fc.length > 0) {
			fadeColors = new int[fc.length];
			for (int i = 0; i < fadeColors.length; i++) {
				try {
					fadeColors[i] = Integer.parseInt(fc[i], 16);
				} catch (NumberFormatException e) {
					fadeColors[i] = 0;
				}
			}
		}
	}

	@Override
	public Runnable playEffectLocation(Location location) {
		MagicSpells.getVolatileCodeHandler().createFireworksExplosion(location, flicker, trail, type, colors, fadeColors, flightDuration);
		return null;
	}

}
