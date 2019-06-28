package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.BoundingBox;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.castmodifiers.ModifierSet;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;

import org.apache.commons.math3.util.FastMath;

public class AreaEffectSpell extends TargetedSpell implements TargetedLocationSpell {

	private ModifierSet entityTargetModifiers;
	private ModifierSet locationTargetModifiers;

	private List<Subspell> spells;
	private List<String> spellNames;

	private int maxTargets;

	private double cone;
	private double vRadius;
	private double hRadius;

	private boolean pointBlank;
	private boolean failIfNoTargets;
	private boolean spellSourceInCenter;
	
	public AreaEffectSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		List<String> list = getConfigStringList("entity-target-modifiers", null);
		if (list != null) entityTargetModifiers = new ModifierSet(list);
		list = getConfigStringList("location-target-modifiers", null);
		if (list != null) locationTargetModifiers = new ModifierSet(list);

		spellNames = getConfigStringList("spells", null);

		maxTargets = getConfigInt("max-targets", 0);

		cone = getConfigDouble("cone", 0);
		vRadius = getConfigDouble("vertical-radius", 5);
		hRadius = getConfigDouble("horizontal-radius", 10);

		pointBlank = getConfigBoolean("point-blank", true);
		failIfNoTargets = getConfigBoolean("fail-if-no-targets", true);
		spellSourceInCenter = getConfigBoolean("spell-source-in-center", false);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		spells = new ArrayList<>();

		if (spellNames == null || spellNames.isEmpty()) {
			MagicSpells.error("AreaEffectSpell '" + internalName + "' has no spells defined!");
			return;
		}
		
		for (String spellName : spellNames) {
			Subspell spell = new Subspell(spellName);

			if (!spell.process()) {
				MagicSpells.error("AreaEffectSpell '" + internalName + "' attempted to use invalid spell '" + spellName + '\'');
				continue;
			}

			if (!spell.isTargetedLocationSpell() && !spell.isTargetedEntityFromLocationSpell() && !spell.isTargetedEntitySpell()) {
				MagicSpells.error("AreaEffectSpell '" + internalName + "' attempted to use non-targeted spell '" + spellName + '\'');
				continue;
			}

			spells.add(spell);
		}

		spellNames.clear();
		spellNames = null;
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Location loc = null;
			if (pointBlank) loc = player.getLocation();
			else {
				try {
					Block block = getTargetedBlock(player, power);
					if (block != null && !BlockUtils.isAir(block.getType())) loc = block.getLocation();
				} catch (IllegalStateException e) {
					loc = null;
				}
			}

			if (loc == null) return noTarget(player);

			SpellTargetLocationEvent event = new SpellTargetLocationEvent(this, player, loc, power);
			EventUtil.call(event);
			if (locationTargetModifiers != null) locationTargetModifiers.apply(event);
			if (event.isCancelled()) loc = null;
			else {
				loc = event.getTargetLocation();
				power = event.getPower();
			}

			if (loc == null) return noTarget(player);
			
			boolean done = doAoe(player, loc, power);
			
			if (!done && failIfNoTargets) return noTarget(player);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		return doAoe(caster, target, power);
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return doAoe(null, target, power);
	}
	
	private boolean doAoe(Player player, Location location, float basePower) {
		int count = 0;

		Vector vLoc = player != null ? player.getLocation().toVector() : location.toVector();
		Vector facing = player != null ? player.getLocation().getDirection() : location.getDirection();

		BoundingBox box = new BoundingBox(location, hRadius, vRadius);
		List<Entity> entities = new ArrayList<>(location.getWorld().getEntitiesByClasses(LivingEntity.class));
		Collections.shuffle(entities);

		for (Entity e : entities) {
			if (e == null) continue;
			if (!box.contains(e)) continue;
			if (pointBlank && cone > 0) {
				Vector dir = e.getLocation().toVector().subtract(vLoc);
				if (FastMath.toDegrees(FastMath.abs(dir.angle(facing))) > cone) continue;
			}

			LivingEntity target = (LivingEntity) e;
			float power = basePower;

			if (target.isDead()) continue;
			if (player == null && !validTargetList.canTarget(target)) continue;
			if (player != null && !validTargetList.canTarget(player, target)) continue;

			if (player != null) {
				SpellTargetEvent event = new SpellTargetEvent(this, player, target, power);
				EventUtil.call(event);
				if (entityTargetModifiers != null) entityTargetModifiers.apply(event);
				if (event.isCancelled()) continue;

				target = event.getTarget();
				power = event.getPower();
			} else if (entityTargetModifiers != null) {
				SpellTargetEvent event = new SpellTargetEvent(this, player, target, power);
				entityTargetModifiers.apply(event);
				if (event.isCancelled()) continue;
			}

			for (Subspell spell : spells) {
				if (player != null) {
					if (spellSourceInCenter && spell.isTargetedEntityFromLocationSpell()) spell.castAtEntityFromLocation(player, location, target, power);
					else if (spell.isTargetedEntitySpell()) spell.castAtEntity(player, target, power);
					else if (spell.isTargetedLocationSpell()) spell.castAtLocation(player, target.getLocation(), power);
					continue;
				}

				if (spell.isTargetedEntityFromLocationSpell()) spell.castAtEntityFromLocation(null, location, target, power);
				else if (spell.isTargetedEntitySpell()) spell.castAtEntity(null, target, power);
				else if (spell.isTargetedLocationSpell()) spell.castAtLocation(null, target.getLocation(), power);
			}

			playSpellEffects(EffectPosition.TARGET, target);
			if (spellSourceInCenter) playSpellEffectsTrail(location, target.getLocation());
			else if (player != null) playSpellEffectsTrail(player.getLocation(), target.getLocation());

			count++;

			if (maxTargets > 0 && count >= maxTargets) break;
		}

		if (count > 0 || !failIfNoTargets) {
			playSpellEffects(EffectPosition.SPECIAL, location);
			if (player != null) playSpellEffects(EffectPosition.CASTER, player);
		}
		
		return count > 0;
	}
	
}
