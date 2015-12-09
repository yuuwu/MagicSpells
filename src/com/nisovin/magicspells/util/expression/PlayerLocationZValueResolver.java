package com.nisovin.magicspells.util.expression;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerLocationZValueResolver extends ValueResolver {
	
	@Override
	public Number resolveValue(String playerName, Player player) {
		if (player != null) {
			return player.getLocation().getZ();
		}
		Player p = Bukkit.getServer().getPlayer(playerName);
		if (p != null) {
			return p.getLocation().getZ();
		}
		return 0;
	}

}
