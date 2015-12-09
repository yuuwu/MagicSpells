package com.nisovin.magicspells.util.expression;

import java.util.Random;

import org.bukkit.entity.Player;

public class RandomSignResolver extends ValueResolver {
	
	private static Random rand = new Random();
	
	@Override
	public Number resolveValue(String playerName, Player player) {
		return (rand.nextBoolean() ? 1: -1);
	}

}
