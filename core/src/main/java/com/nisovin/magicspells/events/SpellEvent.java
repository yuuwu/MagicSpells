package com.nisovin.magicspells.events;

import org.bukkit.event.Event;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.Spell;

public abstract class SpellEvent extends Event implements IMagicSpellsCompatEvent {

	protected Spell spell;
	protected Player caster;
	
	public SpellEvent(Spell spell, Player caster) {
		this.spell = spell;
		this.caster = caster;
	}
	
	/**
	 * Gets the spell involved in the event.
	 * @return the spell
	 */
	public Spell getSpell() {
		return spell;
	}
	
	/**
	 * Gets the player casting the spell.
	 * @return the casting player
	 */
	public Player getCaster() {
		return caster;
	}
	
}
