package com.nisovin.magicspells;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.Spell.SpellCastState;
import com.nisovin.magicspells.Spell.PostCastAction;
import com.nisovin.magicspells.Spell.SpellCastResult;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.util.CastUtil.CastMode;
import com.nisovin.magicspells.events.SpellCastedEvent;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;

public class Subspell {

	private static Random random = new Random();
	
	private Spell spell;
	private String spellName;
	private CastMode mode = CastMode.PARTIAL;

	private int delay = 0;
	private float subPower = 1F;
	private double chance = -1D;
	
	private boolean isTargetedEntity = false;
	private boolean isTargetedLocation = false;
	private boolean isTargetedEntityFromLocation = false;

	// spellName(mode=hard|h|full|f|partial|p|direct|d;power=[subpower];delay=[delay];chance=[chance])
	public Subspell(String data) {
		String[] split = data.split("\\(", 2);
		
		spellName = split[0].trim();
		
		if (split.length > 1) {
			split[1] = split[1].trim();
			if (split[1].endsWith(")")) split[1] = split[1].substring(0, split[1].length() - 1);
			String[] args = Util.splitParams(split[1]);

			for (String arg : args) {
				if (!arg.contains("=")) continue;

				String[] castArguments = arg.split(";");
				for (String castArgument : castArguments) {
					String[] keyValue = castArgument.split("=");
					switch(keyValue[0].toLowerCase()) {
						case "mode":
							mode = Util.getCastMode(keyValue[1]);
							break;
						case "power":
							try {
								subPower = Float.parseFloat(keyValue[1]);
							} catch (NumberFormatException e) {
								DebugHandler.debugNumberFormat(e);
							}
							break;
						case "delay":
							try {
								delay = Integer.parseInt(keyValue[1]);
							} catch (NumberFormatException e) {
								DebugHandler.debugNumberFormat(e);
							}
							break;
						case "chance":
							try {
								chance = Double.parseDouble(keyValue[1]) / 100D;
							} catch (NumberFormatException e) {
								DebugHandler.debugNumberFormat(e);
							}
							break;
					}
				}

			}
		}
	}
	
	public boolean process() {
		spell = MagicSpells.getSpellByInternalName(spellName);
		if (spell != null) {
			isTargetedEntity = spell instanceof TargetedEntitySpell;
			isTargetedLocation = spell instanceof TargetedLocationSpell;
			isTargetedEntityFromLocation = spell instanceof TargetedEntityFromLocationSpell;
		}
		return spell != null;
	}
	
	public Spell getSpell() {
		return spell;
	}
	
	public boolean isTargetedEntitySpell() {
		return isTargetedEntity;
	}
	
	public boolean isTargetedLocationSpell() {
		return isTargetedLocation;
	}
	
	public boolean isTargetedEntityFromLocationSpell() {
		return isTargetedEntityFromLocation;
	}
	
	public PostCastAction cast(final Player player, final float power) {
		if ((chance > 0 && chance < 1) && random.nextDouble() > chance) return PostCastAction.ALREADY_HANDLED;
		if (delay <= 0) return castReal(player, power);
		MagicSpells.scheduleDelayedTask(() -> castReal(player, power), delay);
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	private PostCastAction castReal(Player player, float power) {
		if ((mode == CastMode.HARD || mode == CastMode.FULL) && player != null) {
			return spell.cast(player, power * subPower, null).action;
		}
		
		if (mode == CastMode.PARTIAL) {
			SpellCastEvent event = new SpellCastEvent(spell, player, SpellCastState.NORMAL, power * subPower, null, 0, null, 0);
			EventUtil.call(event);
			if (!event.isCancelled() && event.getSpellCastState() == SpellCastState.NORMAL) {
				PostCastAction act = spell.castSpell(player, SpellCastState.NORMAL, event.getPower(), null);
				EventUtil.call(new SpellCastedEvent(spell, player, SpellCastState.NORMAL, event.getPower(), null, 0, null, act));
				return act;
			}
			return PostCastAction.ALREADY_HANDLED;
		}
		
		return spell.castSpell(player, SpellCastState.NORMAL, power * subPower, null);
	}
	
	public boolean castAtEntity(final Player player, final LivingEntity target, final float power) {
		if (delay <= 0) return castAtEntityReal(player, target, power);
		MagicSpells.scheduleDelayedTask(() -> castAtEntityReal(player, target, power), delay);
		return true;
	}
	
	private boolean castAtEntityReal(Player player, LivingEntity target, float power) {
		boolean ret = false;

		if (!isTargetedEntity) {
			if (isTargetedLocation) castAtLocationReal(player, target.getLocation(), power);
			return ret;
		}

		if (mode == CastMode.HARD && player != null) {
			SpellCastResult result = spell.cast(player, power, null);
			return result.state == SpellCastState.NORMAL && result.action == PostCastAction.HANDLE_NORMALLY;
		}

		if (mode == CastMode.FULL && player != null) {
			boolean success = false;
			SpellCastEvent spellCast = spell.preCast(player, power * subPower, null);
			if (spellCast != null && spellCast.getSpellCastState() == SpellCastState.NORMAL) {
				success = ((TargetedEntitySpell) spell).castAtEntity(player, target, spellCast.getPower());
				spell.postCast(spellCast, success ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED);
			}
			return success;
		}

		if (mode == CastMode.PARTIAL) {
			SpellCastEvent event = new SpellCastEvent(spell, player, SpellCastState.NORMAL, power * subPower, null, 0, null, 0);
			EventUtil.call(event);
			if (!event.isCancelled() && event.getSpellCastState() == SpellCastState.NORMAL) {
				if (player != null) ret = ((TargetedEntitySpell) spell).castAtEntity(player, target, event.getPower());
				else ret = ((TargetedEntitySpell) spell).castAtEntity(target, event.getPower());
				if (ret) EventUtil.call(new SpellCastedEvent(spell, player, SpellCastState.NORMAL, event.getPower(), null, 0, null, PostCastAction.HANDLE_NORMALLY));
			}
		} else {
			if (player != null) ret = ((TargetedEntitySpell) spell).castAtEntity(player, target, power * subPower);
			else ret = ((TargetedEntitySpell) spell).castAtEntity(target, power * subPower);
		}

		return ret;
	}
	
	public boolean castAtLocation(final Player player, final Location target, final float power) {
		if (delay <= 0) return castAtLocationReal(player, target, power);
		MagicSpells.scheduleDelayedTask(() -> castAtLocationReal(player, target, power), delay);
		return true;
	}
	
	private boolean castAtLocationReal(Player player, Location target, float power) {
		boolean ret = false;

		if (!isTargetedLocation) return ret;

		if (mode == CastMode.HARD && player != null) {
			SpellCastResult result = spell.cast(player, power, null);
			return result.state == SpellCastState.NORMAL && result.action == PostCastAction.HANDLE_NORMALLY;
		}

		if (mode == CastMode.FULL && player != null) {
			boolean success = false;
			SpellCastEvent spellCast = spell.preCast(player, power * subPower, null);
			if (spellCast != null && spellCast.getSpellCastState() == SpellCastState.NORMAL) {
				success = ((TargetedLocationSpell) spell).castAtLocation(player, target, spellCast.getPower());
				spell.postCast(spellCast, success ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED);
			}
			return success;
		}

		if (mode == CastMode.PARTIAL) {
			SpellCastEvent event = new SpellCastEvent(spell, player, SpellCastState.NORMAL, power * subPower, null, 0, null, 0);
			EventUtil.call(event);
			if (!event.isCancelled() && event.getSpellCastState() == SpellCastState.NORMAL) {
				if (player != null) ret = ((TargetedLocationSpell) spell).castAtLocation(player, target, event.getPower());
				else ret = ((TargetedLocationSpell) spell).castAtLocation(target, event.getPower());
				if (ret) EventUtil.call(new SpellCastedEvent(spell, player, SpellCastState.NORMAL, event.getPower(), null, 0, null, PostCastAction.HANDLE_NORMALLY));
			}
		} else {
			if (player != null) ret = ((TargetedLocationSpell) spell).castAtLocation(player, target, power * subPower);
			else ret = ((TargetedLocationSpell) spell).castAtLocation(target, power * subPower);
		}

		return ret;
	}
	
	public boolean castAtEntityFromLocation(final Player player, final Location from, final LivingEntity target, final float power) {
		if (delay <= 0) return castAtEntityFromLocationReal(player, from, target, power);
		MagicSpells.scheduleDelayedTask(() -> castAtEntityFromLocationReal(player, from, target, power), delay);
		return true;
	}
	
	private boolean castAtEntityFromLocationReal(Player player, Location from, LivingEntity target, float power) {
		boolean ret = false;

		if (!isTargetedEntityFromLocation) return ret;

		if (mode == CastMode.HARD && player != null) {
			SpellCastResult result = spell.cast(player, power, MagicSpells.NULL_ARGS);
			return result.state == SpellCastState.NORMAL && result.action == PostCastAction.HANDLE_NORMALLY;
		}

		if (mode == CastMode.FULL && player != null) {
			boolean success = false;
			SpellCastEvent spellCast = spell.preCast(player, power * subPower, MagicSpells.NULL_ARGS);
			if (spellCast != null && spellCast.getSpellCastState() == SpellCastState.NORMAL) {
				success = ((TargetedEntityFromLocationSpell) spell).castAtEntityFromLocation(player, from, target, spellCast.getPower());
				spell.postCast(spellCast, success ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED);
			}
			return success;
		}

		if (mode == CastMode.PARTIAL) {
			SpellCastEvent event = new SpellCastEvent(spell, player, SpellCastState.NORMAL, power * subPower, null, 0, null, 0);
			EventUtil.call(event);
			if (!event.isCancelled() && event.getSpellCastState() == SpellCastState.NORMAL) {
				if (player != null) ret = ((TargetedEntityFromLocationSpell) spell).castAtEntityFromLocation(player, from, target, event.getPower());
				else ret = ((TargetedEntityFromLocationSpell) spell).castAtEntityFromLocation(from, target, event.getPower());
				if (ret) EventUtil.call(new SpellCastedEvent(spell, player, SpellCastState.NORMAL, event.getPower(), null, 0, null, PostCastAction.HANDLE_NORMALLY));
			}
		} else {
			if (player != null) ret = ((TargetedEntityFromLocationSpell) spell).castAtEntityFromLocation(player, from, target, power * subPower);
			else ret = ((TargetedEntityFromLocationSpell) spell).castAtEntityFromLocation(from, target, power * subPower);
		}

		return ret;
	}
	
}
