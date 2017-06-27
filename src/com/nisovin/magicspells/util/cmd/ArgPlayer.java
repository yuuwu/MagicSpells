package com.nisovin.magicspells.util.cmd;

import com.nisovin.magicspells.exception.MagicException;
import org.bukkit.entity.Player;

public class ArgPlayer extends Arg<Player> {
	
	public ArgPlayer(String name) {
		super(name);
	}
	
	public ArgPlayer(String name, Player defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	protected Player readValueInner(String input) throws MagicException {
		
		return null;
	}
	
}
