package com.nisovin.magicspells.spells.instant;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.LocationUtil;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class RecallSpell extends InstantSpell implements TargetedEntitySpell {

	private double maxRange;

	private boolean useBedLocation;
	private boolean allowCrossWorld;

	private String strNoMark;
	private String strTooFar;
	private String strOtherWorld;
	private String strRecallFailed;
	
	private MarkSpell markSpell;
	private String markSpellName;

	public RecallSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		maxRange = getConfigDouble("max-range", 0);

		useBedLocation = getConfigBoolean("use-bed-location", false);
		allowCrossWorld = getConfigBoolean("allow-cross-world", true);

		strNoMark = getConfigString("str-no-mark", "You have no mark to recall to.");
		strTooFar = getConfigString("str-too-far", "You mark is too far away.");
		strOtherWorld = getConfigString("str-other-world", "Your mark is in another world.");
		strRecallFailed = getConfigString("str-recall-failed", "Could not recall.");
		markSpellName = getConfigString("mark-spell", "mark");
	}
	
	@Override
	public void initialize() {
		super.initialize();

		Spell spell = MagicSpells.getSpellByInternalName(markSpellName);
		if (spell instanceof MarkSpell) markSpell = (MarkSpell) spell;
		else MagicSpells.error("RecallSpell '" + internalName + "' has an invalid mark-spell defined!");
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Location markLocation = null;
			if (args != null && args.length == 1 && player.hasPermission("magicspells.advanced." + internalName)) {
				Player target = PlayerNameUtils.getPlayer(args[0]);				
				if (useBedLocation && target != null) markLocation = target.getBedSpawnLocation();
				else if (markSpell != null) {
					Location loc = markSpell.getEffectiveMark(target != null ? target.getName().toLowerCase() : args[0].toLowerCase());
					if (loc != null) markLocation = loc;
				}
			} else markLocation = getRecallLocation(player);

			if (markLocation == null) {
				sendMessage(strNoMark, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			if (!allowCrossWorld && !LocationUtil.isSameWorld(markLocation, player.getLocation())) {
				sendMessage(strOtherWorld, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			
			if (maxRange > 0 && markLocation.toVector().distanceSquared(player.getLocation().toVector()) > maxRange * maxRange) {
				sendMessage(strTooFar, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			
			Location from = player.getLocation();
			boolean teleported = player.teleport(markLocation);
			if (!teleported) {
				MagicSpells.error("Recall teleport blocked for " + player.getName());
				sendMessage(strRecallFailed, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			playSpellEffects(EffectPosition.CASTER, from);
			playSpellEffects(EffectPosition.TARGET, markLocation);
		}
		return PostCastAction.HANDLE_NORMALLY;
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
	
	private Location getRecallLocation(Player caster) {
		if (useBedLocation) return caster.getBedSpawnLocation();
		if (markSpell == null) return null;
		return markSpell.getEffectiveMark(caster);
	}

}
