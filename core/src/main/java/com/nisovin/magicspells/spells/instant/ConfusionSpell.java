package com.nisovin.magicspells.spells.instant;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class ConfusionSpell extends InstantSpell {

	private double radius;
	
	public ConfusionSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		radius = getConfigDouble("radius", 10);
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			double castingRange = Math.round(radius * power);
			List<Entity> entities = player.getNearbyEntities(castingRange, castingRange, castingRange);
			List<LivingEntity> monsters = new ArrayList<>();

			for (Entity e : entities) {
				if (!(e instanceof LivingEntity)) continue;
				if (!validTargetList.canTarget(player, e)) continue;
				monsters.add((LivingEntity) e);
			}

			for (int i = 0; i < monsters.size(); i++) {
				int next = i + 1;
				if (next >= monsters.size()) next = 0;
				MagicSpells.getVolatileCodeHandler().setTarget(monsters.get(i), monsters.get(next));
				playSpellEffects(EffectPosition.TARGET, monsters.get(i));
				playSpellEffectsTrail(player.getLocation(), monsters.get(i).getLocation());
			}

			playSpellEffects(EffectPosition.CASTER, player);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

}
