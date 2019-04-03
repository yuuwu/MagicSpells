package com.nisovin.magicspells.spelleffects;

import com.nisovin.magicspells.MagicSpells;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

public class HeartsEffect extends SpellEffect {
	
	private Particle effect = Particle.HEART;
	
	private float yOffset = 2F;
	
	private double range = 32;
	private int count = 4;
	private float speed = .2F;
	private float spreadHoriz = .3F;
	private float spreadVert = .2F;
	
	@Override
	public void loadFromConfig(ConfigurationSection config) {
		// TODO make a config loading schema
	}
	
	@Override
	public Runnable playEffectEntity(Entity entity) {
		if (entity instanceof Tameable) {
			entity.playEffect(EntityEffect.WOLF_HEARTS);
		} else {
			playEffect(entity.getLocation());
		}
		return null;
	}
	
	@Override
	public Runnable playEffectLocation(Location location) {
		//MagicSpells.getVolatileCodeHandler().playParticleEffect(location, "heart", .3F, .2F, .2F, 4, 32, 2F);
		//Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius, float yOffset
		
		//effect.display(null, location.clone().add(0, yOffset, 0), null, range, spreadHoriz, spreadVert, spreadHoriz, speed, count);
		MagicSpells.getInstance().effectManager.display(effect, location.clone().add(0, yOffset, 0), spreadHoriz, spreadVert, spreadHoriz, speed, count, 1.0f, null, null, (byte) 0, range, null);
		//ParticleData data, Location center, Color color, double range, float offsetX, float offsetY, float offsetZ, float speed, int amount
		return null;
	}
	
}
