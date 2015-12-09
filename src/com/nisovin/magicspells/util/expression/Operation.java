package com.nisovin.magicspells.util.expression;

import org.bukkit.entity.Player;

public abstract class Operation {
	
	public abstract Number evaluate(Number arg1, Number arg2);
	
	public Number evaluate(ValueResolver r1, ValueResolver r2, String playerName, Player player) {
		if (r1 == null) {
			throw new NullPointerException("ValueResolver was null");
		}
		
		if (r2 == null && !singleInput()) {
			return r1.resolveValue(playerName, player);
		}
		return evaluate(r1.resolveValue(playerName, player), r2.resolveValue(playerName, player));
	}
	
	public boolean singleInput() {
		return false;
	}
}