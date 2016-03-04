package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.castmodifiers.Condition;

public class PlayerCountAbove extends Condition {

	int count;
	
	@Override
	public boolean setVar(String var) {
		try {
			count = Integer.parseInt(var);
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean check(Player player) {
		return Bukkit.getServer().getOnlinePlayers().size() > count;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return Bukkit.getServer().getOnlinePlayers().size() > count;
	}

	@Override
	public boolean check(Player player, Location location) {
		return Bukkit.getServer().getOnlinePlayers().size() > count;
	}

}
