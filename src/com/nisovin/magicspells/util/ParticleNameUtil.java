package com.nisovin.magicspells.util;

import de.slikey.effectlib.util.ParticleEffect;

public class ParticleNameUtil {
	public static ParticleEffect findEffect(String name) {
		ParticleEffect effect = null;
		effect = ParticleEffect.fromName(name);
		if (effect == null) {
			throw new NullPointerException("No particle could be found from: \"" + name + "\"");
		}
		return effect;
	}
}
