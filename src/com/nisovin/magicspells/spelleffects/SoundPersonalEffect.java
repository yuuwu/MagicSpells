package com.nisovin.magicspells.spelleffects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ConfigData;

/**
 * public class SoundPersonalEffect<p>
 * Configuration fields:<br>
 * <ul>
 * <li>sound</li>
 * <li>volume</li>
 * <li>pitch</li>
 * </ul>
 */
public class SoundPersonalEffect extends SpellEffect {
	
	@ConfigData(field="sound", dataType="String", defaultValue="random.pop")
	String sound = "random.pop";
	
	@ConfigData(field="volume", dataType="double", defaultValue="1.0")
	float volume = 1.0F;
	
	@ConfigData(field="pitch", dataType="double", defaultValue="1.0")
	float pitch = 1.0F;

	@Override
	public void loadFromString(String string) {
		if (string != null && string.length() > 0) {
			String[] data = string.split(" ");
			sound = data[0];
			if (data.length > 1) {
				volume = Float.parseFloat(data[1]);
			}
			if (data.length > 2) {
				pitch = Float.parseFloat(data[2]);
			}
		}
		if (sound.equals("random.wood_click")) {
			sound = "random.wood click";
		} else if (sound.equals("mob.ghast.affectionate_scream")) {
			sound = "mob.ghast.affectionate scream";
		}
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		sound = config.getString("sound", sound);
		volume = (float)config.getDouble("volume", volume);
		pitch = (float)config.getDouble("pitch", pitch);
	}

	@Override
	public void playEffectEntity(Entity entity) {
		if (entity instanceof Player) {
			MagicSpells.getVolatileCodeHandler().playSound((Player)entity, sound, volume, pitch);
			//SoundUtils.playSound((Player) entity, sound, volume, pitch);
		}
	}
	
}
