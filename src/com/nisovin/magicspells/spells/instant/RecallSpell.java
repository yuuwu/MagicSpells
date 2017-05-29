package com.nisovin.magicspells.spells.instant;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.util.LocationUtil;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.PlayerNameUtils;

// advanced perm is for teleporting to other player's recall points
public class RecallSpell extends InstantSpell implements TargetedEntitySpell {
	
	private String markSpellName;
	private boolean allowCrossWorld;
	private int maxRange;
	private boolean useBedLocation;
	private String strNoMark;
	private String strOtherWorld;
	private String strTooFar;
	private String strRecallFailed;
	
	private MarkSpell markSpell;

	public RecallSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		markSpellName = getConfigString("mark-spell", "mark");
		allowCrossWorld = getConfigBoolean("allow-cross-world", true);
		maxRange = getConfigInt("max-range", 0);
		useBedLocation = getConfigBoolean("use-bed-location", false);
		strNoMark = getConfigString("str-no-mark", "You have no mark to recall to.");
		strOtherWorld = getConfigString("str-other-world", "Your mark is in another world.");
		strTooFar = getConfigString("str-too-far", "You mark is too far away.");
		strRecallFailed = getConfigString("str-recall-failed", "Could not recall.");
	}
	
	@Override
	public void initialize() {
		super.initialize();
		Spell spell = MagicSpells.getSpellByInternalName(markSpellName);
		if (spell != null && spell instanceof MarkSpell) {
			markSpell = (MarkSpell)spell;
		} else {
			MagicSpells.error("Failed to get marks list for '" + internalName + "' spell");
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Location markLocation = null;
			if (args != null && args.length == 1 && player.hasPermission("magicspells.advanced." + internalName)) {
				Player target = PlayerNameUtils.getPlayer(args[0]);				
				if (useBedLocation) {
					if (target != null) markLocation = target.getBedSpawnLocation();
				} else if (markSpell != null) {
					Location loc = markSpell.getEffectiveMark(target != null ? target.getName().toLowerCase() : args[0].toLowerCase());
					if (loc != null) markLocation = loc;
				}
			} else {
				markLocation = getRecallLocation(player);
			}
			if (markLocation == null) {
				sendMessage(strNoMark, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			if (!allowCrossWorld && !LocationUtil.isSameWorld(markLocation, player.getLocation())) {
				// can't cross worlds
				sendMessage(strOtherWorld, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			if (maxRange > 0 && markLocation.toVector().distanceSquared(player.getLocation().toVector()) > maxRange * maxRange) {
				// too far
				sendMessage(strTooFar, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			
			// all good!
			Location from = player.getLocation();
			boolean teleported = player.teleport(markLocation);
			if (teleported) {
				playSpellEffects(EffectPosition.CASTER, from);
				playSpellEffects(EffectPosition.TARGET, markLocation);
			} else {
				// fail -- teleport prevented
				MagicSpells.error("Recall teleport blocked for " + player.getName());
				sendMessage(strRecallFailed, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	Location getRecallLocation(Player caster) {
		if (useBedLocation) return caster.getBedSpawnLocation();
		if (markSpell == null) return null;
		Location loc = markSpell.getEffectiveMark(caster);
		return loc;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		Location mark = getRecallLocation(caster);
		if (mark == null) return false;
		target.teleport(mark);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return false;
	}

}
