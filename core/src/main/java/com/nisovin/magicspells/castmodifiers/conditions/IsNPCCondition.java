package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class IsNPCCondition extends Condition {

	@Override
	public boolean setVar(String var) {
		return true;
	}

	@Override
	public boolean check(Player player) {
		return player.hasMetadata("NPC");
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return target.hasMetadata("NPC");
	}

	@Override
	public boolean check(Player player, Location location) {
		return false;
	}

}
