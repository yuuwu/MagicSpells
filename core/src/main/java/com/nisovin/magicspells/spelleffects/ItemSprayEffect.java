package com.nisovin.magicspells.spelleffects;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.materials.MagicMaterial;
import com.nisovin.magicspells.materials.MagicUnknownMaterial;

public class ItemSprayEffect extends SpellEffect {

	MagicMaterial mat;
	
	int num = 15;
	
	int duration = 10;
	
	float force = 1.0F;

	@Override
	public void loadFromString(String string) {
		if (string != null) {
			String[] data = string.split(" ");
			Material type = Material.LEGACY_REDSTONE;
			short dura = 0;
			if (data.length >= 1) {
				if (data[0].contains(":")) {
					try {
						String[] typeData = data[0].split(":");
						type = Material.matchMaterial(typeData[0], true);
						dura = Short.parseShort(typeData[1]);
					} catch (NumberFormatException e) {
						DebugHandler.debugNumberFormat(e);
					}
				} else {
					try {
						type = Material.matchMaterial(data[0], true);
					} catch (NumberFormatException e) {
						DebugHandler.debugNumberFormat(e);
					}
				}
			}
			mat = new MagicUnknownMaterial(type, dura);
			if (data.length >= 2) {
				try {
					num = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					DebugHandler.debugNumberFormat(e);
				}
			}
			if (data.length >= 3) {
				try {
					duration = Integer.parseInt(data[2]);
				} catch (NumberFormatException e) {
					DebugHandler.debugNumberFormat(e);
				}
			}
			if (data.length >= 4) {
				try {
					force = Float.parseFloat(data[3]);
				} catch (NumberFormatException e) {
					DebugHandler.debugNumberFormat(e);
				}
			}
		}
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		mat = MagicSpells.getItemNameResolver().resolveItem(config.getString("type", "redstone"));
		num = config.getInt("quantity", num);
		duration = config.getInt("duration", duration);
		force = (float)config.getDouble("force", force);
	}
	
	@Override
	public Runnable playEffectLocation(Location location) {
		if (mat == null) return null;
		
		// Spawn items
		Random rand = new Random();
		Location loc = location.clone().add(0, 1, 0);
		final Item[] items = new Item[num];
		for (int i = 0; i < num; i++) {
			items[i] = loc.getWorld().dropItem(loc, mat.toItemStack(0));
			items[i].setVelocity(new Vector((rand.nextDouble() - .5) * force, (rand.nextDouble() - .5) * force, (rand.nextDouble() - .5) * force));
			items[i].setPickupDelay(duration << 1);
		}
		
		// Schedule item deletion
		MagicSpells.scheduleDelayedTask(() -> Arrays.stream(items).forEach(Item::remove), duration);
		return null;
	}
	
}
