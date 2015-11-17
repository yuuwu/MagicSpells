package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import de.slikey.effectlib.util.ParticleEffect;

public class AngryEffect extends SpellEffect {

	private ParticleEffect effect = ParticleEffect.VILLAGER_ANGRY;
	
	private double range = 32;
	
	//these are location shifts made to the center point
	private float xOffset = 0;
	private float yOffset = 2;
	private float zOffset = 0;
	
	//these are about how far particles can be from the center
	private float offsetX = 0;
	private float offsetY = 0;
	private float offsetZ = 0;
	
	private float speed = .2F;
	private int count = 1;
	
	@Override
	public void loadFromString(String string) {
		//TODO make a string loading schema
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		//TODO make a config loading schema
	}

	@Override
	public void playEffectLocation(Location location) {
		//MagicSpells.getVolatileCodeHandler().playParticleEffect(location, "angryVillager", 0F, 0F, .2F, 1, 32, 2F);
		//Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius, float yOffset
		
		//ParticleData data, Location center, Color color, double range, float offsetX, float offsetY, float offsetZ, float speed, int amount
		effect.display(null, location.add(xOffset, yOffset, zOffset), null, range, offsetX, offsetY, offsetZ, speed, count);
		
		
	}
	
}
