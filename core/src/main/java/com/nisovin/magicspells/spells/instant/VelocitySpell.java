package com.nisovin.magicspells.spells.instant;

import org.bukkit.util.Vector;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class VelocitySpell extends InstantSpell {
	
	private double speed;
	private boolean addVelocityInstead;
	
	public VelocitySpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		speed = getConfigFloat("speed", 40) / 10F;

		addVelocityInstead = getConfigBoolean("add-velocity-instead", false);
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Vector v = player.getEyeLocation().getDirection().normalize().multiply(speed * power);
			if (!addVelocityInstead) player.setVelocity(v);
			else player.setVelocity(player.getVelocity().add(v));
			playSpellEffects(EffectPosition.CASTER, player);
		}
		
		return PostCastAction.HANDLE_NORMALLY;
	}
	
}
