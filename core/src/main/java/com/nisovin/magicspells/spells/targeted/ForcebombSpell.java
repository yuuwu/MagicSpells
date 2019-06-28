package com.nisovin.magicspells.spells.targeted;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;

public class ForcebombSpell extends TargetedSpell implements TargetedLocationSpell {

	private float force;
	private float yForce;
	private float yOffset;
	private float maxYForce;

	private double radiusSquared;

	private boolean callTargetEvents;
	private boolean addVelocityInstead;
	
	public ForcebombSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		force = getConfigFloat("pushback-force", 30) / 10.0F;
		yForce = getConfigFloat("additional-vertical-force", 15) / 10.0F;
		yOffset = getConfigFloat("y-offset", 0F);
		maxYForce = getConfigFloat("max-vertical-force", 20) / 10.0F;

		radiusSquared = getConfigDouble("radius", 3);
		radiusSquared *= radiusSquared;

		callTargetEvents = getConfigBoolean("call-target-events", true);
		addVelocityInstead = getConfigBoolean("add-velocity-instead", false);
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Block block = getTargetedBlock(player, power);
			if (block != null && block.getType() != Material.AIR) {
				SpellTargetLocationEvent event = new SpellTargetLocationEvent(this, player, block.getLocation(), power);
				EventUtil.call(event);
				if (event.isCancelled()) block = null;
				else {
					block = event.getTargetLocation().getBlock();
					power = event.getPower();
				}
			}

			if (block == null || BlockUtils.isAir(block.getType())) return noTarget(player);
			knockback(player, block.getLocation().add(0.5, 0, 0.5), power);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		knockback(caster, target, power);
		return true;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		knockback(null, target, power);
		return true;
	}
	
	private void knockback(Player player, Location location, float basePower) {
		location = location.clone().add(0D, yOffset, 0D);
		Collection<Entity> entities = location.getWorld().getEntitiesByClasses(LivingEntity.class);

		Vector e;
		Vector v;
		Vector t = location.toVector();
		for (Entity entity : entities) {
			if (player == null && !validTargetList.canTarget(entity)) continue;
			if (player != null && !validTargetList.canTarget(player, entity)) continue;
			if (entity.getLocation().distanceSquared(location) > radiusSquared) continue;

			float power = basePower;
			if (callTargetEvents && player != null) {
				SpellTargetEvent event = new SpellTargetEvent(this, player, (LivingEntity) entity, power);
				EventUtil.call(event);
				if (event.isCancelled()) continue;
				power = event.getPower();
			}

			e = entity.getLocation().toVector();
			v = e.subtract(t).normalize().multiply(force * power);

			if (force != 0) v.setY(v.getY() * (yForce * power));
			else v.setY(yForce * power);
			if (v.getY() > maxYForce) v.setY(maxYForce);

			if (addVelocityInstead) entity.setVelocity(entity.getVelocity().add(v));
			else entity.setVelocity(v);

			if (player != null) playSpellEffectsTrail(player.getLocation(), entity.getLocation());
			playSpellEffects(EffectPosition.TARGET, entity);
		}

		playSpellEffects(EffectPosition.SPECIAL, location);
		if (player != null) playSpellEffects(EffectPosition.CASTER, player);
	}

}
