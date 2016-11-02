package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.castmodifiers.IModifier;
import com.nisovin.magicspells.events.MagicSpellsGenericPlayerEvent;
import com.nisovin.magicspells.events.ManaChangeEvent;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;

public class SpellTagCondition extends Condition implements IModifier {

	private String tag;
	
	@Override
	public boolean apply(SpellCastEvent event) {
		if (event.getSpell().getTags() != null) {
			return event.getSpell().getTags().contains(tag);
		}
		return false;
	}

	@Override
	public boolean apply(ManaChangeEvent event) {
		return false;
	}

	@Override
	public boolean apply(SpellTargetEvent event) {
		if (event.getSpell().getTags() != null) {
			return event.getSpell().getTags().contains(tag);
		}
		return false;
	}

	@Override
	public boolean apply(SpellTargetLocationEvent event) {
		if (event.getSpell().getTags() != null) {
			return event.getSpell().getTags().contains(tag);
		}
		return false;
	}

	@Override
	public boolean apply(MagicSpellsGenericPlayerEvent event) {
		return false;
	}

	@Override
	public boolean setVar(String var) {
		if (var != null) {
			tag = var.trim();
			return true;
		}
		return false;
	}

	@Override
	public boolean check(Player player) {
		return false;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return false;
	}

	@Override
	public boolean check(Player player, Location location) {
		return false;
	}

}
