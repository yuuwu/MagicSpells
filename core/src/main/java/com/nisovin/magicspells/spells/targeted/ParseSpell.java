package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class ParseSpell extends TargetedSpell implements TargetedEntitySpell {

	private String parseTo;
	private String expectedValue;
	private String parseToVariable;
	private String variableToParse;

	public ParseSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		parseTo = getConfigString("parse-to", "");
		expectedValue = getConfigString("expected-value", "");
		parseToVariable = getConfigString("parse-to-variable", "");
		variableToParse = getConfigString("variable-to-parse", "");
	}

	@Override
	public void initialize() {
		super.initialize();

		if (variableToParse.isEmpty()) {
			MagicSpells.error("You must define a variable to parse for ParseSpell");
			return;
		}

		if (expectedValue.isEmpty()) {
			MagicSpells.error("You must define an expected variable for ParseSpell");
			return;
		}

		if (parseToVariable.isEmpty()) MagicSpells.error("You must define a variable to parse to for ParseSpell");
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<Player> targetInfo = getTargetedPlayer(player, power);
			if (targetInfo == null) return noTarget(player);
			Player target = targetInfo.getTarget();
			if (target == null) return noTarget(player);

			parse(target);
			playSpellEffects(player, target);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		playSpellEffects(caster, target);
		parse(target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		playSpellEffects(EffectPosition.TARGET, target);
		parse(target);
		return true;
	}

	private void parse(LivingEntity target) {
		if (!(target instanceof Player)) return;

		String receivedValue = MagicSpells.getVariableManager().getStringValue(variableToParse, (Player) target);
		if (!receivedValue.equalsIgnoreCase(expectedValue)) return;

		MagicSpells.getVariableManager().set(parseToVariable, (Player) target, parseTo);
	}

}
