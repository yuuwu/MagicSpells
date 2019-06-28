package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Slime;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.VariableMod;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class SlimeSizeSpell extends TargetedSpell implements TargetedEntitySpell {
	
	private VariableMod variableMod;

	private int minSize;
	private int maxSize;
	
	private static ValidTargetChecker isSlimeChecker = (LivingEntity entity) -> entity instanceof Slime;
	
	public SlimeSizeSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		variableMod = new VariableMod(getConfigString("size", "=5"));
		
		minSize = getConfigInt("min-size", 0);
		maxSize = getConfigInt("max-size", 20);

		if (minSize < 0) minSize = 0;
		if (maxSize < minSize) maxSize = minSize;
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<LivingEntity> targetInfo = getTargetedEntity(player, power);
			if (targetInfo == null) return noTarget(player);

			LivingEntity targetEntity = targetInfo.getTarget();
			setSize(player, targetEntity);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		setSize(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		setSize(null, target);
		return true;
	}
	
	@Override
	public ValidTargetChecker getValidTargetChecker() {
		return isSlimeChecker;
	}

	private void setSize(Player caster, LivingEntity target) {
		if (!(target instanceof Slime)) return;

		Slime slime = (Slime) target;
		double rawOutputValue = variableMod.getValue(caster, null, slime.getSize());
		int finalSize = Util.clampValue(minSize, maxSize, (int) rawOutputValue);
		slime.setSize(finalSize);
	}

}
