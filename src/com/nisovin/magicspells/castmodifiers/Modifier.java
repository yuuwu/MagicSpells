package com.nisovin.magicspells.castmodifiers;


import org.bukkit.entity.Player;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.events.MagicSpellsGenericPlayerEvent;
import com.nisovin.magicspells.events.ManaChangeEvent;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;
import com.nisovin.magicspells.util.Util;

public class Modifier implements IModifier {

	boolean negated = false;
	Condition condition;
	ModifierType type;
	String modifierVar;
	float modifierVarFloat;
	int modifierVarInt;
	String modifierVarString;
	String strModifierFailed = null;
	Object customActionData = null;
	
	//is this a condition that will want to access the events directly?
	boolean alertCondition = false;
	
	public static Modifier factory(String s) {
		Modifier m = new Modifier();
		String[] s1 = s.split("\\$\\$");
		String[] data = Util.splitParams(s1[0].trim());
		if (data.length < 2) return null;
				
		// get condition
		if (data[0].startsWith("!")) {
			m.negated = true;
			data[0] = data[0].substring(1);
		}
		m.condition = Condition.getConditionByName(data[0]);
		if (m.condition == null) return null;
		
		// get type and vars
		m.type = getTypeByName(data[1]);
		if (m.type == null && data.length > 2) {
			boolean varok = m.condition.setVar(data[1]);
			if (!varok) return null;
			m.type = getTypeByName(data[2]);
			if (data.length > 3) {
				m.modifierVar = data[3];
			}
		} else if (data.length > 2) {
			m.modifierVar = data[2];
		}
		
		// check type
		if (m.type == null) return null;
		
		// process modifiervar
		try {
			if (m.type.usesModifierFloat()) {
				m.modifierVarFloat = Float.parseFloat(m.modifierVar);
			} else if (m.type.usesModifierInt()) {
				m.modifierVarInt = Integer.parseInt(m.modifierVar);
			} else if (m.type.usesCustomData()) {
				m.customActionData = m.type.buildCustomActionData(m.modifierVar);
				if (m.customActionData == null) return null;
			}
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return null;
		}
		
		// check for failed string
		if (s1.length > 1) {
			m.strModifierFailed = s1[1].trim();
		}
		
		//check for the alert condition
		if (m.condition instanceof IModifier) {
			m.alertCondition = true;
		}
		
		// done
		return m;
	}
	
	@Override
	public boolean apply(SpellCastEvent event) {
		Player player = event.getCaster();
		boolean check;
		if (alertCondition) {
			check = ((IModifier)condition).apply(event);
		} else {
			check = condition.check(player);
		}
		if (negated) check = !check;
		return type.apply(event, check, modifierVar, modifierVarFloat, modifierVarInt, customActionData);
	}
	
	@Override
	public boolean apply(ManaChangeEvent event) {
		Player player = event.getPlayer();
		boolean check;
		if (alertCondition) {
			check = ((IModifier)condition).apply(event);
		} else {
			check = condition.check(player);
		}
		if (negated) check = !check;
		return type.apply(event, check, modifierVar, modifierVarFloat, modifierVarInt, customActionData);
	}
	
	@Override
	public boolean apply(SpellTargetEvent event) {
		Player player = event.getCaster();
		
		boolean check;
		if (alertCondition) {
			check = ((IModifier)condition).apply(event);
		} else {
			check = condition.check(player, event.getTarget());
		}
		
		if (negated) check = !check;
		return type.apply(event, check, modifierVar, modifierVarFloat, modifierVarInt, customActionData);
	}
	
	@Override
	public boolean apply(SpellTargetLocationEvent event) {
		Player player = event.getCaster();
		boolean check;
		if (alertCondition) {
			check = ((IModifier)condition).apply(event);
		} else {
			check = condition.check(player, event.getTargetLocation());
		}
		if (negated) check = !check;
		return type.apply(event, check, modifierVar, modifierVarFloat, modifierVarInt, customActionData);
	}
	
	@Override
	public boolean apply(MagicSpellsGenericPlayerEvent event) {
		boolean check;
		if (alertCondition) {
			check = ((IModifier)condition).check(event.getPlayer());
		} else {
			check = condition.check(event.getPlayer());
		}
		if (negated) check = !check;
		return type.apply(event, check, modifierVar, modifierVarFloat, modifierVarInt, customActionData);
	}
	
	@Override
	public boolean check(Player player) {
		boolean check = condition.check(player);
		if (negated) check = !check;
		if (check == false && type == ModifierType.REQUIRED) {
			return false;
		} else if (check == true && type == ModifierType.DENIED) {
			return false;
		}
		return true;
	}
	
	private static ModifierType getTypeByName(String name) {
		return ModifierType.getModifierTypeByName(name);
	}
}
