package com.nisovin.magicspells.spells.targeted;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class OffsetLocationSpell extends TargetedSpell implements TargetedLocationSpell {

	private Vector relativeOffset;
	private Vector absoluteOffset;
	
	private Subspell spellToCast;
	private String spellToCastName;
	
	public OffsetLocationSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		relativeOffset = getConfigVector("relative-offset", "0,0,0");
		absoluteOffset = getConfigVector("absolute-offset", "0,0,0");
		
		spellToCastName = getConfigString("spell", "");
	}
	
	@Override
	public void initialize() {
		super.initialize();

		spellToCast = new Subspell(spellToCastName);
		if (!spellToCast.process() || !spellToCast.isTargetedLocationSpell()) {
			MagicSpells.error("OffsetLocationSpell '" + internalName + "' has an invalid spell defined!");
			spellToCast = null;
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Location baseTargetLocation;
			TargetInfo<LivingEntity> entityTargetInfo = getTargetedEntity(player, power);
			if (entityTargetInfo != null && entityTargetInfo.getTarget() != null) baseTargetLocation = entityTargetInfo.getTarget().getLocation();
			else baseTargetLocation = getTargetedBlock(player, power).getLocation();
			if (baseTargetLocation == null) return noTarget(player);
			Location loc = Util.applyOffsets(baseTargetLocation, relativeOffset, absoluteOffset);
			if (loc == null) return PostCastAction.ALREADY_HANDLED;

			if (spellToCast != null) spellToCast.castAtLocation(player, loc, power);
			playSpellEffects(player, loc);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		if (spellToCast != null) spellToCast.castAtLocation(caster, Util.applyOffsets(target, relativeOffset, absoluteOffset), power);
		return true;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return castAtLocation(null, target, power);
	}
	
}
