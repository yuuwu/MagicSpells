package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.castmodifiers.Condition;

public class LookingAtBlockCondition extends Condition {

	Material blockType;
	int dist = 4;
	
	@Override
	public boolean setVar(String var) {
		try {
			String[] varsplit = var.split(",");
			blockType = Material.getMaterial(varsplit[0].toUpperCase());
			if (blockType == null || !blockType.isBlock()) return false;
			if (varsplit.length > 1) {
				dist = Integer.parseInt(varsplit[1]);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean check(Player player) {
		return check(player, player);
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		Block block = BlockUtils.getTargetBlock(null, target, dist);
		return blockType.equals(block.getType());
	}

	@Override
	public boolean check(Player player, Location location) {
		return check(player);
	}
	
}
