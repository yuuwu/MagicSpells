package com.nisovin.magicspells.spelleffects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ColorUtil;

class ParticleCloudEffect extends SpellEffect {

	Particle particle;
	String particleName;

	Material material;
	String materialName;

	BlockData blockData;
	ItemStack itemStack;

	float dustSize;
	String colorHex;
	Color dustColor;
	DustOptions dustOptions;

	boolean none = true;
	boolean item = false;
	boolean dust = false;
	boolean block = false;

	int color = 0xFF0000;
	int duration = 60;

	float radius = 5f;
	float radiusPerTick = 0f;
	float yOffset = 0F;

	@Override
	public void loadFromConfig(ConfigurationSection config) {

		particleName = config.getString("particle-name", "EXPLOSION_NORMAL");
		particle = Util.getParticle(particleName);

		materialName = config.getString("material", "").toUpperCase();
		material = Material.getMaterial(materialName);

		dustSize = (float) config.getDouble("size", 1);
		colorHex = config.getString("color", "FF0000");
		dustColor = ColorUtil.getColorFromHexString(colorHex);
		if (dustColor != null) dustOptions = new DustOptions(dustColor, dustSize);

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

		if (particle == Particle.REDSTONE && dustColor == null) {
			particle = null;
			MagicSpells.error("Wrong color defined! '" + colorHex + "'");
		}

		radius = (float) config.getDouble("radius", radius);
		radiusPerTick = (float) config.getDouble("radius-per-tick", radiusPerTick);
		duration = config.getInt("duration", duration);
		color = config.getInt("color", color);
		yOffset = (float) config.getDouble("y-offset", yOffset);
	}

	@Override
	public Runnable playEffectLocation(Location location) {
		if (particle == null) return null;
		AreaEffectCloud cloud = location.getWorld().spawn(location.clone().add(0, yOffset, 0), AreaEffectCloud.class);
		if (block) cloud.setParticle(particle, blockData);
		else if (item) cloud.setParticle(particle, itemStack);
		else if (dust) cloud.setParticle(particle, dustOptions);
		else if (none) cloud.setParticle(particle);

		cloud.setRadius(radius);
		cloud.setRadiusPerTick(radiusPerTick);
		cloud.setDuration(duration);
		cloud.setColor(Color.fromRGB(color));
		return null;
	}

}
