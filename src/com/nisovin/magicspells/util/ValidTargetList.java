package com.nisovin.magicspells.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;

public class ValidTargetList {
	
	public static enum TargetingElement {
		
		TARGET_SELF,
		TARGET_PLAYERS,
		TARGET_INVISIBLES,
		TARGET_NONPLAYERS,
		TARGET_MONSTERS,
		TARGET_ANIMALS,
		TARGET_NONLIVING_ENTITIES
		
	}
	
	boolean targetSelf = false;
	boolean targetPlayers = false;
	boolean targetInvisibles = false;
	boolean targetNonPlayers = false;
	boolean targetMonsters = false;
	boolean targetAnimals = false;
	boolean targetNonLivingEntities = false; // this will be kept as false for now during restructuring
	Set<EntityType> types = new HashSet<EntityType>();
	
	public ValidTargetList(Spell spell, String list) {
		if (list != null) {
			String[] ss = list.replace(" ", "").split(",");
			init(spell, Arrays.asList(ss));
		}
	}
	
	public void enforce(TargetingElement element, boolean value) {
		switch (element) {
		case TARGET_SELF:
			targetSelf = value;
			break;
		case TARGET_ANIMALS:
			targetAnimals = value;
			break;
		case TARGET_INVISIBLES:
			targetInvisibles = value;
			break;
		case TARGET_MONSTERS:
			targetMonsters = value;
			break;
		case TARGET_NONLIVING_ENTITIES:
			targetNonLivingEntities = value;
			break;
		case TARGET_NONPLAYERS:
			targetNonPlayers = value;
			break;
		case TARGET_PLAYERS:
			targetPlayers = value;
			break;
		}
	}
	
	public void enforce(TargetingElement[] elements, boolean value) {
		for (TargetingElement e : elements) {
			enforce(e, value);
		}
	}
	
	public ValidTargetList(Spell spell, List<String> list) {
		if (list != null) {
			init(spell, list);
		}
	}
	
	void init(Spell spell, List<String> list) {
		for (String s : list) {
			s = s.trim();
			if (s.equalsIgnoreCase("self") || s.equalsIgnoreCase("caster")) {
				targetSelf = true;
			} else if (s.equalsIgnoreCase("players") || s.equalsIgnoreCase("player")) {
				targetPlayers = true;
			} else if (s.equalsIgnoreCase("invisible") || s.equalsIgnoreCase("invisibles")) {
				targetInvisibles = true;
			} else if (s.equalsIgnoreCase("nonplayers") || s.equalsIgnoreCase("nonplayer")) {
				targetNonPlayers = true;
			} else if (s.equalsIgnoreCase("monsters") || s.equalsIgnoreCase("monster")) {
				targetMonsters = true;
			} else if (s.equalsIgnoreCase("animals") || s.equalsIgnoreCase("animal")) {
				targetAnimals = true;
			} else {
				EntityType type = Util.getEntityType(s);
				if (type != null) {
					types.add(type);
				} else {
					MagicSpells.error("Invalid target type '" + s + "' on spell '" + spell.getInternalName() + "'");
				}
			}
		}
	}
	
	public ValidTargetList(boolean targetPlayers, boolean targetNonPlayers) {
		this.targetPlayers = targetPlayers;
		this.targetNonPlayers = targetNonPlayers;
	}
	
	public boolean canTarget(Player caster, Entity target) {
		return canTarget(caster, target, targetPlayers);
	}
	
	public boolean canTarget(Player caster, Entity target, boolean targetPlayers) {
		if (!(target instanceof LivingEntity) && !targetNonLivingEntities) return false;
		boolean targetIsPlayer = target instanceof Player;
		if (targetIsPlayer && ((Player)target).getGameMode() == GameMode.CREATIVE) return false;
		if (targetSelf && target.equals(caster)) return true;
		if (!targetSelf && target.equals(caster)) return false;
		if (!targetInvisibles && targetIsPlayer && !caster.canSee((Player)target)) return false;
		if (targetPlayers && targetIsPlayer) return true;
		if (targetNonPlayers && !targetIsPlayer) return true;
		if (targetMonsters && target instanceof Monster) return true;
		if (targetAnimals && target instanceof Animals) return true;
		if (types.contains(target.getType())) return true;
		return false;
	}
	
	public boolean canTarget(Entity target) {
		if (!(target instanceof LivingEntity) && !targetNonLivingEntities) return false;
		boolean targetIsPlayer = target instanceof Player;
		if (targetIsPlayer && ((Player)target).getGameMode() == GameMode.CREATIVE) return false;
		if (targetPlayers && targetIsPlayer) return true;
		if (targetNonPlayers && !targetIsPlayer) return true;
		if (targetMonsters && target instanceof Monster) return true;
		if (targetAnimals && target instanceof Animals) return true;
		if (types.contains(target.getType())) return true;
		return false;
	}
	
	public List<LivingEntity> filterTargetListCastingAsLivingEntities(Player caster, List<Entity> targets) {
		return filterTargetListCastingAsLivingEntities(caster, targets, targetPlayers);
	}
	
	public List<LivingEntity> filterTargetListCastingAsLivingEntities(Player caster, List<Entity> targets, boolean targetPlayers) {
		List<LivingEntity> realTargets = new ArrayList<LivingEntity>();
		for (Entity e : targets) {
			if (canTarget(caster, e, targetPlayers)) {
				realTargets.add((LivingEntity)e);
			}
		}
		return realTargets;
	}
	
	public boolean canTargetPlayers() {
		return targetPlayers;
	}
	
	public boolean canTargetNonLivingEntities() {
		return targetNonLivingEntities;
	}
	
	@Override
	public String toString() {
		return "ValidTargetList:["
			+ "targetSelf=" + targetSelf
			+ ",targetPlayers=" + targetPlayers
			+ ",targetInvisibles=" + targetInvisibles
			+ ",targetNonPlayers=" + targetNonPlayers
			+ ",targetMonsters=" + targetMonsters
			+ ",targetAnimals=" + targetAnimals
			+ ",types=" + types
			+ ",targetNonLivingEntities=" + targetNonLivingEntities
			+ "]";
	}
	
}
