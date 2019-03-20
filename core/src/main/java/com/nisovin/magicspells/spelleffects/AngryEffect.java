package com.nisovin.magicspells.spelleffects;

import com.nisovin.magicspells.MagicSpells;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class AngryEffect extends SpellEffect {

	private Particle effect = Particle.VILLAGER_ANGRY;
	
	private double range = 32;
	
	// These are location shifts made to the center point
	private float yOffset = 2;
	
	// These are about how far particles can be from the center
	private float offsetX = 0;
	private float offsetY = 0;
	private float offsetZ = 0;
	
	private float speed = .2F;
	private int count = 1;
	
	@Override
	public void loadFromString(String string) {
		// nope
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		range = config.getDouble("range", range);
	}

	@Override
	public Runnable playEffectLocation(Location location) {
		//MagicSpells.getVolatileCodeHandler().playParticleEffect(location, "angryVillager", 0F, 0F, .2F, 1, 32, 2F);
		//Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius, float yOffset
		
		//ParticleData data, Location center, Color color, double range, float offsetX, float offsetY, float offsetZ, float speed, int amount
		MagicSpells.getInstance().effectManager.display(effect, location.clone().add(0, yOffset, 0), offsetX, offsetY, offsetZ, speed, count, 1.0f, null, null, (byte) 0, range, null);
		//effect.display(null, location.clone().add(xOffset, yOffset, zOffset), null, range, offsetX, offsetY, offsetZ, speed, count);
		return null;
	}
	
}
