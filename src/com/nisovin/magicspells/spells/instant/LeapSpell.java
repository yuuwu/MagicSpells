package com.nisovin.magicspells.spells.instant;

import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.MagicConfig;

// TODO allow this to cast a spell when the caster lands
// TODO add a condition to see if someone is leaping
public class LeapSpell extends InstantSpell {
	
	private double forwardVelocity;
	private double upwardVelocity;
	private boolean cancelDamage;
	private boolean clientOnly;
	
	private HashSet<Player> jumping;
	
	public LeapSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		this.forwardVelocity = getConfigInt("forward-velocity", 40) / 10D;
		this.upwardVelocity = getConfigInt("upward-velocity", 15) / 10D;
		this.cancelDamage = getConfigBoolean("cancel-damage", true);
		this.clientOnly = getConfigBoolean("client-only", false);
		
		if (this.cancelDamage) this.jumping = new HashSet<>();
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Vector v = player.getLocation().getDirection();
			v.setY(0).normalize().multiply(this.forwardVelocity * power).setY(this.upwardVelocity * power);
			if (this.clientOnly) {
				MagicSpells.getVolatileCodeHandler().setClientVelocity(player, v);
			} else {
				player.setVelocity(v);
			}
			if (this.cancelDamage) this.jumping.add(player);
			playSpellEffects(EffectPosition.CASTER, player);
		}
		
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (!this.cancelDamage) return;
		if (event.getCause() != DamageCause.FALL) return;
		
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		if (!this.jumping.contains(entity)) return;
		event.setCancelled(true);
		this.jumping.remove(entity);
		playSpellEffects(EffectPosition.TARGET, entity.getLocation());
	}

}
