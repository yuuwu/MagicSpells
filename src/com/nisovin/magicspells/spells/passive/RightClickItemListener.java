package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.materials.MagicItemWithNameMaterial;
import com.nisovin.magicspells.materials.MagicMaterial;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.HandHandler;
import com.nisovin.magicspells.util.OverridePriority;

// trigger variable of a comma separated list of items to accept
public class RightClickItemListener extends PassiveListener {

	Set<Material> materials = new HashSet<Material>();
	Map<MagicMaterial, List<PassiveSpell>> types = new LinkedHashMap<MagicMaterial, List<PassiveSpell>>();
	
	Set<Material> materialsOffhand = new HashSet<Material>();
	Map<MagicMaterial, List<PassiveSpell>> typesOffhand = new LinkedHashMap<MagicMaterial, List<PassiveSpell>>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		Set<Material> materialSetAddTo = null;
		Map<MagicMaterial, List<PassiveSpell>> typesMapAddTo = null;
		if (isMainHand(trigger)) {
			materialSetAddTo = materials;
			typesMapAddTo = types;
		} else {
			materialSetAddTo = materialsOffhand;
			typesMapAddTo = typesOffhand;
		}
		
		String[] split = var.split(",");
		for (String s : split) {
			s = s.trim();
			MagicMaterial mat = null;
			if (s.contains("|")) {
				String[] stuff = s.split("\\|");
				mat = MagicSpells.getItemNameResolver().resolveItem(stuff[0]);
				if (mat != null) {
					mat = new MagicItemWithNameMaterial(mat, stuff[1]);
				}
			} else {
				mat = MagicSpells.getItemNameResolver().resolveItem(s);
			}
			if (mat != null) {
				List<PassiveSpell> list = typesMapAddTo.get(mat);
				if (list == null) {
					list = new ArrayList<PassiveSpell>();
					typesMapAddTo.put(mat, list);
				}
				list.add(spell);
				materialSetAddTo.add(mat.getMaterial());
			}
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!event.hasItem()) return;
		
		ItemStack item = event.getItem();
		if (item == null || item.getType() == Material.AIR) return;
		List<PassiveSpell> list = getSpells(item, HandHandler.isMainHand(event));
		if (list != null) {
			Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
			for (PassiveSpell spell : list) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (spellbook.hasSpell(spell, false)) {
					boolean casted = spell.activate(event.getPlayer());
					if (PassiveListener.cancelDefaultAction(spell, casted)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	private List<PassiveSpell> getSpells(ItemStack item, boolean mainHand) {
		Set<Material> materialSet;
		Map<MagicMaterial, List<PassiveSpell>> spellMap;
		if (mainHand) {
			materialSet = materials;
			spellMap = types;
		} else {
			materialSet = materialsOffhand;
			spellMap = typesOffhand;
		}
		
		
		if (materialSet.contains(item.getType())) {
			for (MagicMaterial m : spellMap.keySet()) {
				if (m.equals(item)) {
					return spellMap.get(m);
				}
			}
		}
		return null;
	}
	
	private boolean isMainHand(PassiveTrigger trigger) {
		return PassiveTrigger.RIGHT_CLICK.contains(trigger);
	}

}
