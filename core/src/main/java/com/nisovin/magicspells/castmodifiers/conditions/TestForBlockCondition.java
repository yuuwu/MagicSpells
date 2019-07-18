package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.util.MagicLocation;
import com.nisovin.magicspells.castmodifiers.Condition;

public class TestForBlockCondition extends Condition {

	MagicLocation location;
	Material blockType;
	
	@Override
	public boolean setVar(String var) {
		try {
			String[] varsplit = var.split("=");
			String[] locsplit = varsplit[0].split(",");
			location = new MagicLocation(locsplit[0], Integer.parseInt(locsplit[1]), Integer.parseInt(locsplit[2]), Integer.parseInt(locsplit[3]));
			blockType = Material.getMaterial(varsplit[1].toUpperCase());
			if (blockType == null || !blockType.isBlock()) return false;
			return true;
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return false;
		}
	}

	@Override
	public boolean check(Player player) {
		Location loc = location.getLocation();
		if (loc == null) return false;
		if (blockType.equals(loc.getBlock().getType())) return true;
		return false;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return check(null);
	}

	@Override
	public boolean check(Player player, Location location) {
		return check(null);
	}

}
