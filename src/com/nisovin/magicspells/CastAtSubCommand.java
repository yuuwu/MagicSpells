package com.nisovin.magicspells;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class CastAtSubCommand {

	//handles the /c castat command
	public static boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
		// begin /c castat handling
		
		if (args.length == 3 || args.length == 4) {
			// /c castat <spell> <player name> [power]
			Spell spell = MagicSpells.getSpellByInGameName(args[1]);
			Player target = Bukkit.getServer().getPlayer(args[2]);
			TargetedEntitySpell tes = null;
			if (spell instanceof TargetedEntitySpell) {
				tes = (TargetedEntitySpell)spell;
			} else  {
				sender.sendMessage("You did not specify a targeted entity spell");
				return true;
			}
			if (target == null) {
				sender.sendMessage("Could not find player:" + args[2]);
				return true;
			} else {
				float cPower = 1;
				if (args.length == 4) {
					cPower = Float.parseFloat(args[3]);
				}
				tes.castAtEntity(target, cPower);
				return true;
			}
		}
		
		// end /c castat handling
		return true;
	}
	
}
