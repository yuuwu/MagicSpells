package com.nisovin.magicspells.teams;

import org.bukkit.command.CommandSender;

public interface TeamsSubCommand {

	boolean process(CommandSender sender, String[] args);
	
}
