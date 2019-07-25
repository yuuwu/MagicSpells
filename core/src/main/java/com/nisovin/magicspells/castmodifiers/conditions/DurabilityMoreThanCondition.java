package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class DurabilityMoreThanCondition extends Condition {

	private int slot;
	private int durability;

	@Override
	public boolean setVar(String var) {
		try {
			String[] data = var.split(":");
			durability = Integer.parseInt(data[1]);
			switch(data[0].toLowerCase()) {
				case "helm":
					slot = 0;
					break;
				case "chestplate":
					slot = 1;
					break;
				case "leggings":
					slot = 2;
					break;
				case "boots":
					slot = 3;
					break;
				case "offhand":
					slot = 4;
					break;
				default:
					slot = -1;
					break;
			}
			return true;
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return false;
		}
	}

	@Override
	public boolean check(Player player) {
		ItemStack item = null;
		switch (slot) {
			case -1:
				item = player.getEquipment().getItemInMainHand();
				break;
			case 0:
				item = player.getInventory().getHelmet();
				break;
			case 1:
				item = player.getInventory().getChestplate();
				break;
			case 2:
				item = player.getInventory().getLeggings();
				break;
			case 3:
				item = player.getInventory().getBoots();
				break;
			case 4:
				item = player.getInventory().getItemInOffHand();
				break;
		}
		if (item == null) return false;
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		if (!(meta instanceof Damageable)) return false;

		int max = item.getType().getMaxDurability();
		if (max > 0) return max - ((Damageable) meta).getDamage() > durability;
		return false;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return target instanceof Player && check((Player) target);
	}

	@Override
	public boolean check(Player player, Location location) {
		return false;
	}

}
