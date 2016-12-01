package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;

// no trigger variable is currently used
public class EnterBedListener extends PassiveListener {

	List<PassiveSpell> spells = new ArrayList<PassiveSpell>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		spells.add(spell);
	}
	
	@EventHandler
	public void onDeath(PlayerBedEnterEvent event) {
		Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
		for (PassiveSpell spell : spells) {
			if (spellbook.hasSpell(spell)) {
				spell.activate(event.getPlayer());
			}
		}
	}


}
