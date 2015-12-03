package com.nisovin.magicspells.util;

import org.bukkit.Material;

import de.slikey.effectlib.util.ParticleEffect;
import de.slikey.effectlib.util.ParticleEffect.BlockData;
import de.slikey.effectlib.util.ParticleEffect.ItemData;
import de.slikey.effectlib.util.ParticleEffect.ParticleData;

public class ParticleNameUtil {
	public static ParticleEffect findEffect(String name) {
		ParticleEffect effect = null;
		effect = ParticleEffect.fromName(name);
		if (effect == null) {
			throw new NullPointerException("No particle could be found from: \"" + name + "\"");
		}
		return effect;
	}
	
	public static EffectPackage findEffectPackage(String name) {
		ParticleData data = null;
		ParticleEffect effect = null;
		String[] splits = name.split("_");
		effect = ParticleEffect.fromName(splits[0]);
		
		if (splits.length > 1) {
			Material mat = Material.getMaterial(Integer.parseInt(splits[1]));
			int materialData = 0;
			if (splits.length > 2) {
				materialData = Integer.parseInt(splits[2]);
			}
			if (mat.isBlock()) {
				data = new BlockData(mat, (byte) materialData);
			} else {
				data = new ItemData(mat, (byte) materialData);
			}
		}
		if (effect == null) {
			throw new NullPointerException("No particle could be found from: \"" + splits[0] + "\" + from \"" + name + "\"");
		}
		
		return new EffectPackage(effect, data);
		
	}
}
