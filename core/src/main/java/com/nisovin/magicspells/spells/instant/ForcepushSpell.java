package com.nisovin.magicspells.spells.instant;

import java.util.List;

import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class ForcepushSpell extends InstantSpell {

	private float force;
	private float radius;
	private float yForce;
	private float maxYForce;

	private boolean addVelocityInstead;
	
	public ForcepushSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		force = getConfigFloat("pushback-force", 30) / 10F;
		radius = getConfigFloat("radius", 3F);
		yForce = getConfigFloat("additional-vertical-force", 15) / 10F;
		maxYForce = getConfigFloat("max-vertical-force", 20) / 10F;

		addVelocityInstead = getConfigBoolean("add-velocity-instead", false);
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			knockback(player, power);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	private void knockback(Player player, float basePower) {
		List<Entity> entities = player.getNearbyEntities(radius, radius, radius);
		Vector e;
		Vector v;
		Vector p = player.getLocation().toVector();
		for (Entity entity : entities) {
			if (!(entity instanceof LivingEntity)) continue;
			if (!validTargetList.canTarget(player, entity)) continue;

			LivingEntity target = (LivingEntity) entity;
			float power = basePower;
			SpellTargetEvent event = new SpellTargetEvent(this, player, target, power);
			EventUtil.call(event);
			if (event.isCancelled()) continue;

			target = event.getTarget();
			power = event.getPower();
			
			e = target.getLocation().toVector();
			v = e.subtract(p).normalize().multiply(force * power);

			if (force != 0) v.setY(v.getY() + (yForce * power));
			else v.setY(yForce * power);
			if (v.getY() > (maxYForce)) v.setY(maxYForce);

			if (addVelocityInstead) target.setVelocity(target.getVelocity().add(v));
			else target.setVelocity(v);

			playSpellEffects(EffectPosition.TARGET, target);
			playSpellEffectsTrail(player.getLocation(), target.getLocation());
		}
		playSpellEffects(EffectPosition.CASTER, player);
	}

}
