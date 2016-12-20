package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.HandHandler;
import com.nisovin.magicspells.util.Util;

// trigger variable option is optional
// if not defined, it will trigger regardless of entity type
// if specified, it should be a comma separated list of entity types to accept
public class RightClickEntityListener extends PassiveListener {

	Map<EntityType, List<PassiveSpell>> types = new HashMap<EntityType, List<PassiveSpell>>();
	List<PassiveSpell> allTypes = new ArrayList<PassiveSpell>();
	
	Map<EntityType, List<PassiveSpell>> typesOffhand = new HashMap<EntityType, List<PassiveSpell>>();
	List<PassiveSpell> allTypesOffhand = new ArrayList<PassiveSpell>();
	
	boolean ignoreCancelled = true;
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		ignoreCancelled = spell.ignoreCancelled();
		
		Map<EntityType, List<PassiveSpell>> typeMapLocal;
		List<PassiveSpell> allTypesLocal;
		
		if (isMainHand(trigger)) {
			typeMapLocal = types;
			allTypesLocal = allTypes;
		} else {
			typeMapLocal = typesOffhand;
			allTypesLocal = allTypesOffhand;
		}
		
		if (var == null || var.isEmpty()) {
			allTypesLocal.add(spell);
		} else {
			String[] split = var.replace(" ", "").toUpperCase().split(",");
			for (String s : split) {
				EntityType t = Util.getEntityType(s);
				if (t != null) {
					List<PassiveSpell> list = typeMapLocal.get(t);
					if (list == null) {
						list = new ArrayList<PassiveSpell>();
						typeMapLocal.put(t, list);
					}
					list.add(spell);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onRightClickEntity(PlayerInteractEntityEvent event) {
		if (ignoreCancelled && event.isCancelled()) return;
		if (!(event.getRightClicked() instanceof LivingEntity)) return;
		
		Map<EntityType, List<PassiveSpell>> typeMapLocal;
		List<PassiveSpell> allTypesLocal;
		
		if (HandHandler.isMainHand(event)) {
			typeMapLocal = types;
			allTypesLocal = allTypes;
		} else {
			typeMapLocal = typesOffhand;
			allTypesLocal = allTypesOffhand;
		}
		
		if (!allTypesLocal.isEmpty()) {
			Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
			for (PassiveSpell spell : allTypesLocal) {
				if (spellbook.hasSpell(spell)) {
					boolean casted = spell.activate(event.getPlayer(), (LivingEntity)event.getRightClicked());
					if (casted && spell.cancelDefaultAction()) {
						event.setCancelled(true);
					}
				}
			}
		}
		if (typeMapLocal.containsKey(event.getRightClicked().getType())) {
			Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
			List<PassiveSpell> list = typeMapLocal.get(event.getRightClicked().getType());
			for (PassiveSpell spell : list) {
				if (spellbook.hasSpell(spell)) {
					boolean casted = spell.activate(event.getPlayer(), (LivingEntity)event.getRightClicked());
					if (casted && spell.cancelDefaultAction()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	public boolean isMainHand(PassiveTrigger trigger) {
		return trigger == PassiveTrigger.RIGHT_CLICK_ENTITY;
	}

	
	
}
