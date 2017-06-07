package com.nisovin.magicspells.util;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;

public class VariableMod {
	
	public enum VariableOwner {
		
		CASTER,
		TARGET
		
	}
	
	public enum Operation {
		
		SET,
		ADD,
		MULTIPLY,
		DIVIDE
		
		;
		
		static Operation fromPrefix(String s) {
			char c = s.charAt(0);
			switch (c) {
			case '=':
				return SET;
			case '+':
				return ADD;
			case '*':
				return MULTIPLY;
			case '/':
				return DIVIDE;
			default:
				return ADD;
			}
		}
		
	}
	
	private VariableOwner variableOwner = null;
	private String modifyingVariableName = null;
	private Operation op = null;
	private double constantModifier;
	private static final Pattern operationMatcher = Pattern.compile("^(=|\\+|\\*|\\/)");
	
	private boolean negate = false;
	
	public VariableMod(String data) {
		this.op = Operation.fromPrefix(data);
		data = operationMatcher.matcher(data).replaceFirst("");
		if (data.startsWith("-")) {
			data = data.substring(1);
			this.negate = true;
		}
		if (!RegexUtil.matches(RegexUtil.DOUBLE_PATTERN, data)) {
			// If it isn't a double, then let's match it as a variable
			if (data.contains(":")) {
				// Then there is an explicit statement of who's variable it is
				String[] dataSplits = data.split(":");
				if (dataSplits[0].toLowerCase().equals("target")) {
					this.variableOwner = VariableOwner.TARGET;
				} else {
					this.variableOwner = VariableOwner.CASTER;
				}
				
			}
		} else {
			this.constantModifier = Double.parseDouble(data);
		}
		
	}
	
	public double getValue(Player caster, Player target) {
		if (this.modifyingVariableName != null) {
			if (this.variableOwner == VariableOwner.CASTER) return MagicSpells.getVariableManager().getValue(this.modifyingVariableName, caster) * (this.negate ? -1 : 1);
			//variable owner == target
			return MagicSpells.getVariableManager().getValue(this.modifyingVariableName, target) * (this.negate ? -1 : 1);
		}
		return this.constantModifier * (this.negate ? -1 : 1);
	}
	
	public boolean isConstantValue() {
		return this.modifyingVariableName == null;
	}
	
	public Operation getOperation() {
		return this.op;
	}
	
	public VariableOwner getVariableOwner() {
		return this.variableOwner;
	}
	
}
