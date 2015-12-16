package com.nisovin.magicspells.spelleffects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.ColorUtil;
import com.nisovin.magicspells.util.ConfigData;
import com.nisovin.magicspells.util.EffectPackage;
import com.nisovin.magicspells.util.ParticleNameUtil;

import de.slikey.effectlib.util.ParticleEffect;
import de.slikey.effectlib.util.ParticleEffect.BlockData;
import de.slikey.effectlib.util.ParticleEffect.ItemData;
import de.slikey.effectlib.util.ParticleEffect.ParticleData;

public class ParticlesEffect extends SpellEffect {
	
	ParticleEffect effect;
	String name = "explode";
	float xSpread = 0.2F;
	float ySpread = 0.2F;
	float zSpread = 0.2F;
	float speed = 0.2F;
	int count = 5;
	float yOffset = 0F;
	
	@ConfigData(field="render-distance",
			dataType="int",
			defaultValue="32",
			description="Set to something like 625 for long range rendering.")
	int renderDistance = 32;
	Color color = null;
	ParticleData data = null;

	@Override
	public void loadFromString(String string) {
		if (string != null && !string.isEmpty()) {
			String[] data = string.split(" ");
			if (data.length >= 1) {
				name = data[0];
			}
			if (data.length >= 2) {
				xSpread = Float.parseFloat(data[1]);
				zSpread = xSpread;
			}
			if (data.length >= 3) {
				ySpread = Float.parseFloat(data[2]);
			}
			if (data.length >= 4) {
				speed = Float.parseFloat(data[3]);
			}
			if (data.length >= 5) {
				count = Integer.parseInt(data[4]);
			}
			if (data.length >= 6) {
				yOffset = Float.parseFloat(data[5]);
			}
			if (data.length >= 7) {
				color = ColorUtil.getColorFromHexString(data[6]);
			}
		}
		EffectPackage pkg = ParticleNameUtil.findEffectPackage(name);
		this.data = pkg.data;
		this.effect = pkg.effect;
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		name = config.getString("particle-name", name);
		xSpread = (float)config.getDouble("horiz-spread", xSpread);
		ySpread = (float)config.getDouble("vert-spread", ySpread);
		zSpread = xSpread;
		xSpread = (float)config.getDouble("red", xSpread);
		ySpread = (float)config.getDouble("green", ySpread);
		zSpread = (float)config.getDouble("blue", zSpread);
		speed = (float)config.getDouble("speed", speed);
		count = config.getInt("count", count);
		yOffset = (float)config.getDouble("y-offset", yOffset);
		renderDistance = config.getInt("render-distance", renderDistance);
		color = ColorUtil.getColorFromHexString(config.getString("color", null));
		EffectPackage pkg = ParticleNameUtil.findEffectPackage(name);
		data = pkg.data;
		effect = pkg.effect;
	}
	
	@Override
	public void playEffectLocation(Location location) {
		//ParticleData data, Location center, Color color, double range, float offsetX, float offsetY, float offsetZ, float speed, int amount
		effect.display(data, location.clone().add(0, yOffset, 0), color, renderDistance, xSpread, ySpread, zSpread, speed, count);
		//MagicSpells.getVolatileCodeHandler().playParticleEffect(location, name, xSpread, ySpread, zSpread, speed, count, renderDistance, yOffset);
	}
	
}
