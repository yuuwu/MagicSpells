package com.nisovin.magicspells.spelleffects;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

public class ParticlesEffect extends SpellEffect {

	Particle particle;
	Material material;
	BlockData blockData;
	ItemStack itemStack;
	String name = "explode";

	float xSpread = 0.2F;
	float ySpread = 0.2F;
	float zSpread = 0.2F;
	float yOffset = 0F;
	float speed = 0.2F;

	int count = 5;

	boolean block = false;
	boolean item = false;
	boolean none = true;

	@Override
	public void loadFromString(String string) {
		super.loadFromString(string);
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		name = config.getString("particle-name", name);
		material = Material.getMaterial(config.getString("material", ""));
		xSpread = (float) config.getDouble("horiz-spread", xSpread);
		ySpread = (float) config.getDouble("vert-spread", ySpread);
		zSpread = xSpread;
		xSpread = (float) config.getDouble("red", xSpread);
		ySpread = (float) config.getDouble("green", ySpread);
		zSpread = (float) config.getDouble("blue", zSpread);
		yOffset = (float) config.getDouble("y-offset", yOffset);
		speed = (float) config.getDouble("speed", speed);

		count = config.getInt("count", count);

		particle = Particle.valueOf(name.toUpperCase());

		if ((particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST) && material != null && material.isBlock()) {
			block = true;
			blockData = material.createBlockData();
		} else if (particle == Particle.ITEM_CRACK && material != null && material.isItem()) {
			item = true;
			itemStack = new ItemStack(material);
		}
		if (particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST || particle == Particle.ITEM_CRACK) none = false;

	}

	@Override
	public Runnable playEffectLocation(Location location) {
		if (block) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed, blockData);
		else if (item) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed, itemStack);
		else if (none) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed);

		return null;
	}

}
