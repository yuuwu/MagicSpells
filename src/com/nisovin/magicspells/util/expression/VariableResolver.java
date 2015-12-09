package com.nisovin.magicspells.util.expression;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.variables.Variable;

public class VariableResolver extends ValueResolver {

	private Variable var;
	
	public VariableResolver(String variable) {
		var = MagicSpells.getVariableManager().getVariable(variable);
		if (var == null) {
			throw new NullPointerException("Variable \"" + variable + "\" could not be found");
		}
	}
	
	public VariableResolver(Variable v) {
		this.var = v;
	}
	
	@Override
	public Number resolveValue(String playerName, Player player) {
		if (player != null) {
			return var.getValue(player);
		} else {
			return var.getValue(playerName);
		}
	}
	
}
