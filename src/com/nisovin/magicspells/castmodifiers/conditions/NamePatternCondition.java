package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.castmodifiers.Condition;

public class NamePatternCondition extends Condition {

	String rawPattern;
	
	@Override
	public boolean setVar(String var) {
		if (var == null || var.isEmpty()) return false;
		rawPattern = var;
		// note, currently won't translate the & to the color code,
		// this will need to be done through regex unicode format 
		return true;
	}

	@Override
	public boolean check(Player player) {
		return (Pattern.matches(rawPattern, player.getName()) || Pattern.matches(rawPattern, player.getDisplayName()));
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		if (target instanceof Player) {
			return check((Player)target);
		} else {
			String n = target.getCustomName();
			if (n != null && !n.isEmpty()) {
				return Pattern.matches(rawPattern, n);
			}
			return false;
		}
	}

	@Override
	public boolean check(Player player, Location location) {
		return false;
	}

}
