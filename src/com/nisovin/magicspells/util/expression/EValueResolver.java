package com.nisovin.magicspells.util.expression;

import org.bukkit.entity.Player;

public class EValueResolver extends ValueResolver {

	@Override
	public Number resolveValue(String playerName, Player player) {
		return Math.E;
	}

}
