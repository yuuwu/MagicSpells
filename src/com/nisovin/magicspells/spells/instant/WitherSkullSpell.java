package com.nisovin.magicspells.spells.instant;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class WitherSkullSpell extends InstantSpell implements TargetedEntityFromLocationSpell {

	boolean charged;
	double velocity;
	private boolean projectileHasGravity;
	private boolean fire;
	private String skullName;
	private boolean showCustomName;
	
	public WitherSkullSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		charged = getConfigBoolean("charged", false);
		velocity = getConfigFloat("velocity", 2);
		projectileHasGravity = getConfigBoolean("gravity", false);
		fire = getConfigBoolean("fire", false);
		skullName = getConfigString("skull-name", null);
		if (skullName == null || skullName.equals("")) {
			showCustomName = false;
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			WitherSkull skull = player.launchProjectile(WitherSkull.class, player.getLocation().getDirection().multiply(velocity * power));
			skull.setCharged(charged);
			skull.setIsIncendiary(fire);
			if (showCustomName) {
				skull.setCustomName(skullName);
				skull.setCustomNameVisible(true);
			}
			MagicSpells.getVolatileCodeHandler().setGravity(skull, projectileHasGravity);
			playSpellEffects(EffectPosition.PROJECTILE, skull);
			playTrackingLinePatterns(EffectPosition.DYNAMIC_CASTER_PROJECTILE_LINE, player.getLocation(), skull.getLocation(), player, skull);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntityFromLocation(Player caster, Location from, LivingEntity target, float power) {
		Vector v = target.getLocation().toVector().subtract(from.toVector()).normalize();
		v.multiply(velocity * power);
		WitherSkull skull = from.getWorld().spawn(from.clone().setDirection(v), WitherSkull.class);
		skull.setCharged(charged);
		skull.setVelocity(v);
		skull.setDirection(v);
		MagicSpells.getVolatileCodeHandler().setGravity(skull, projectileHasGravity);
		if (caster != null) {
			playSpellEffects(EffectPosition.CASTER, caster);
		} else {
			playSpellEffects(EffectPosition.CASTER, from);
		}
		playSpellEffects(EffectPosition.PROJECTILE, skull);
		playTrackingLinePatterns(EffectPosition.DYNAMIC_CASTER_PROJECTILE_LINE, from, skull.getLocation(), caster, skull);
		
		return true;
	}

	@Override
	public boolean castAtEntityFromLocation(Location from, LivingEntity target, float power) {
		return castAtEntityFromLocation(null, from, target, power);
	}

	
	
}
