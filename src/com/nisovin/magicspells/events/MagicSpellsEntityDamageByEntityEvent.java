package com.nisovin.magicspells.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MagicSpellsEntityDamageByEntityEvent extends
		EntityDamageByEntityEvent implements IMagicSpellsCompatEvent {

	public MagicSpellsEntityDamageByEntityEvent(Entity damager, Entity damagee,
			DamageCause cause, double damage) {
		super(damager, damagee, cause, damage);
	}

}
