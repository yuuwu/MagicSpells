package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class ModifyCooldownSpell extends TargetedSpell implements TargetedEntitySpell {

	private List<Spell> spells;
	private List<String> spellNames;
	
	private float seconds;
	private float multiplier;
	
	public ModifyCooldownSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		spellNames = getConfigStringList("spells", null);

		seconds = getConfigFloat("seconds", 1F);
		multiplier = getConfigFloat("multiplier", 0F);
	}
	
	@Override
	public void initialize() {
		spells = new ArrayList<>();

		if (spellNames == null) {
			MagicSpells.error("ModifyCooldownSpell '" + internalName + "' has no spells defined!");
			return;
		}

		for (String spellName : spellNames) {
			Spell spell = MagicSpells.getSpellByInternalName(spellName);
			if (spell == null) {
				MagicSpells.error("ModifyCooldownSpell '" + internalName + "' has an invalid spell defined '" + spellName + '\'');
				continue;
			}
			spells.add(spell);
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<Player> target = getTargetedPlayer(player, power);
			if (target == null) return noTarget(player);
			modifyCooldowns(target.getTarget(), target.getPower());
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		if (!(target instanceof Player)) return false;
		modifyCooldowns((Player)target, power);
		playSpellEffects(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (!(target instanceof Player)) return false;
		modifyCooldowns((Player)target, power);
		playSpellEffects(EffectPosition.TARGET, target);
		return true;
	}

	private void modifyCooldowns(Player player, float power) {
		float sec = seconds * power;
		float mult = multiplier * (1F / power);

		for (Spell spell : spells) {
			float cd = spell.getCooldown(player);
			if (cd <= 0) continue;

			cd -= sec;
			if (mult > 0) cd *= mult;
			if (cd < 0) cd = 0;
			spell.setCooldown(player, cd, false);
		}
	}

}
