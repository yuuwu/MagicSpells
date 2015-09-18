package com.nisovin.magicspells.spells.buff;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class ReflectSpell extends BuffSpell {

	private HashMap<String, Float> reflectors;
	private HashSet<String> shieldBreakerNames;
	
	String strShieldBrokenSelf;
	String strShieldBrokenTarget;
	float reflectedSpellPowerMultiplier;
	boolean spellPowerAffectsReflectedPower;
	
	public ReflectSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		reflectors = new HashMap<String, Float>();
		shieldBreakerNames = new HashSet<String>();
		shieldBreakerNames.addAll(getConfigStringList("shield-breakers", null));
		reflectedSpellPowerMultiplier = (float) getConfigDouble("reflected-spell-power-multiplier", 1.0);
		spellPowerAffectsReflectedPower = getConfigBoolean("spell-power-affects-reflected-power", false);
	}
	
	@Override
	public boolean castBuff(Player player, float power, String[] args) {
		reflectors.put(player.getName(), power);
		return true;
	}

	@EventHandler
	public void onSpellTarget(SpellTargetEvent event) {
		if (event.isCancelled()) return;
		if (event.getTarget() instanceof Player) {
			Player target = (Player)event.getTarget();
			if (isActive(target)) {
				float power = reflectors.get(target.getName());
				if (shieldBreakerNames.contains(event.getSpell().getInternalName())) {
					turnOffBuff(target);
					return;
				}
				boolean ok = chargeUseCost(target);
				if (ok) {
					event.setTarget(event.getCaster());
					event.setPower(event.getPower()* reflectedSpellPowerMultiplier * (spellPowerAffectsReflectedPower ? power : 1));
					addUse(target);
				}
			}
		}
	}

	@Override
	public void turnOffBuff(Player player) {
		reflectors.remove(player.getName());
	}

	@Override
	protected void turnOff() {
		reflectors.clear();
	}

	@Override
	public boolean isActive(Player player) {
		return reflectors.containsKey(player.getName());
	}

}
