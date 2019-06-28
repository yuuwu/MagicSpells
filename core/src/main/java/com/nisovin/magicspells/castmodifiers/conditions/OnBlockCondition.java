package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class OnBlockCondition extends Condition {

	Set<Material> materials;
	Material material;

	@Override
	public boolean setVar(String var) {
		if (var.contains(",")) {
			materials = new HashSet<>();
			String[] split = var.split(",");
			for (String s : split) {
				Material mat = Material.getMaterial(s.toUpperCase());
				if (mat == null) return false;
				if (!mat.isBlock()) return false;
				materials.add(mat);
			}
			return true;
		}

		material = Material.getMaterial(var.toUpperCase());
		if (material == null) return false;
		if (!material.isBlock()) return false;
		return true;
	}

	@Override
	public boolean check(Player player) {
		return check(player, player);
	}
	
	@Override
	public boolean check(Player player, LivingEntity target) {
		Block block = target.getLocation().subtract(0, 1, 0).getBlock();
		if (material != null) return material.equals(block.getType());
		if (!materials.contains(block.getType())) return false;

		for (Material mat : materials) {
			if (mat.equals(block.getType())) return true;
		}

		return false;
	}
	
	@Override
	public boolean check(Player player, Location location) {
		return false;
	}

}
