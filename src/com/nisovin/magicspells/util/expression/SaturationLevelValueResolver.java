package com.nisovin.magicspells.util.expression;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SaturationLevelValueResolver extends ValueResolver {

	@Override
	public Number resolveValue(String playerName, Player player) {
		if (player != null) {
			return player.getSaturation();
		}
		Player p = Bukkit.getServer().getPlayer(playerName);
		if (p != null) {
			return p.getSaturation();
		}
		return 0;
	}

}
