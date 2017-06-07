package com.nisovin.magicspells.teams;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MagicSpellsTeamsCommand implements CommandExecutor {

	private Map<String, TeamsSubCommand> subCommands;
	
	private static void registerSubCommand(Map<String, TeamsSubCommand> cmdMap, TeamsSubCommand subCommand, String... labels) {
		for (String label: labels) {
			cmdMap.put(label.toLowerCase(), subCommand);
		}
	}
	
	public MagicSpellsTeamsCommand(MagicSpellsTeams plugin) {
		subCommands = new HashMap<>();
		
		// magicspellsteams create <name>
		// Creates a team using the default perm structure
		registerSubCommand(subCommands, new CreateTeamSubCommand(plugin), "create", "new", "make");
		
		// magicspellsteams list
		registerSubCommand(subCommands, new ListTeamsSubCommand(plugin), "list");
		
		// magicspellsteams info <name>
		registerSubCommand(subCommands, new TeamInfoSubCommand(plugin), "info", "about");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			TeamsSubCommand sub = subCommands.get(args[0].toLowerCase());
			if (sub != null) return sub.process(sender, args);
		}
		return false;
	}
	
}
