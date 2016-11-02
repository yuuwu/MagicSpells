package com.nisovin.magicspells.factions.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.MPlayer;
import com.nisovin.magicspells.castmodifiers.Condition;

public class HasFactionCondition extends Condition {

	@Override
	public boolean setVar(String var) {
		return true;
	}

	@Override
	public boolean check(Player player) {
		if (player != null) {
			return MPlayer.get(player).hasFaction();
		}
		return false;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		if (target != null && target instanceof Player) {
			return MPlayer.get((Player)target).hasFaction();
		}
		return false;
	}

	@Override
	public boolean check(Player player, Location location) {
		return false;
	}

}
