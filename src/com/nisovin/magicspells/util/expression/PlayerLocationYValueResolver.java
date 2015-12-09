package com.nisovin.magicspells.util.expression;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerLocationYValueResolver extends ValueResolver {
	
	@Override
	public Number resolveValue(String playerName, Player player) {
		if (player != null) {
			return player.getLocation().getY();
		}
		Player p = Bukkit.getServer().getPlayer(playerName);
		if (p != null) {
			return p.getLocation().getY();
		}
		return 0;
	}

}
