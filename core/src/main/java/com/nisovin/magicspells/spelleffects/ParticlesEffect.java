package com.nisovin.magicspells.spelleffects;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;

public class ParticlesEffect extends SpellEffect {

	Particle particle;
	String particleName;

	Material material;
	String materialName;

	BlockData blockData;
	ItemStack itemStack;

	float dustSize;
	int colorRed;
	int colorGreen;
	int colorBlue;
	Color dustColor;
	Particle.DustOptions dustOptions;

	int count;
	float speed;
	float xSpread;
	float ySpread;
	float zSpread;
	float yOffset;

	boolean none = true;
	boolean item = false;
	boolean dust = false;
	boolean block = false;

	@Override
	public void loadFromString(String string) {
		super.loadFromString(string);
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {

		particleName = config.getString("particle-name", "EXPLOSION_NORMAL");
		particle = Util.getParticle(particleName);

		materialName = config.getString("material", "").toUpperCase();
		material = Material.getMaterial(materialName);

		count = config.getInt("count", 5);
		speed = (float) config.getDouble("speed", 0.2F);
		xSpread = (float) config.getDouble("horiz-spread", 0.2F);
		ySpread = (float) config.getDouble("vert-spread", 0.2F);
		zSpread = xSpread;
		xSpread = (float) config.getDouble("x-spread", 0.2F);
		ySpread = (float) config.getDouble("y-spread", 0.2F);
		zSpread = (float) config.getDouble("z-spread", 0.2F);
		yOffset = (float) config.getDouble("y-offset", 0F);

		dustSize = (float) config.getDouble("size", 1);
		colorRed = config.getInt("red", 255);
		colorGreen = config.getInt("green", 0);
		colorBlue = config.getInt("blue", 0);
		dustColor = Color.fromRGB(colorRed, colorGreen, colorBlue);
		dustOptions = new Particle.DustOptions(dustColor, dustSize);

		if ((particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST || particle == Particle.FALLING_DUST) && material != null && material.isBlock()) {
			block = true;
			blockData = material.createBlockData();
			none = false;
		} else if (particle == Particle.ITEM_CRACK && material != null && material.isItem()) {
			item = true;
			itemStack = new ItemStack(material);
			none = false;
		} else if (particle == Particle.REDSTONE && dustOptions != null) {
			dust = true;
			none = false;
		}

		if (particle == null) MagicSpells.error("Wrong particle-name defined! '" + particleName + "'");

		if ((particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST || particle == Particle.FALLING_DUST) && (material == null || !material.isBlock())) {
			particle = null;
			MagicSpells.error("Wrong material defined! '" + materialName + "'");
		}

		if (particle == Particle.ITEM_CRACK && (material == null || !material.isItem())) {
			particle = null;
			MagicSpells.error("Wrong material defined! '" + materialName + "'");
		}
	}

	@Override
	public Runnable playEffectLocation(Location location) {
		if (particle == null) return null;
		else if (block) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed, blockData);
		else if (item) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed, itemStack);
		else if (dust) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed, dustOptions);
		else if (none) location.getWorld().spawnParticle(particle, location.clone().add(0, yOffset, 0), count, xSpread, ySpread, zSpread, speed);

		return null;
	}

}