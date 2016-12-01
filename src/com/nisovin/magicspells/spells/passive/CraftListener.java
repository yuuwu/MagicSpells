package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.materials.MagicItemWithNameMaterial;
import com.nisovin.magicspells.materials.MagicMaterial;
import com.nisovin.magicspells.spells.PassiveSpell;

// optional trigger variable of a comma separated list of items to accept as the result
public class CraftListener extends PassiveListener {

	Set<Material> materials = new HashSet<Material>();
	Map<MagicMaterial, List<PassiveSpell>> types = new HashMap<MagicMaterial, List<PassiveSpell>>();
	List<PassiveSpell> allTypes = new ArrayList<PassiveSpell>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			allTypes.add(spell);
		} else {
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
					List<PassiveSpell> list = types.get(mat);
					if (list == null) {
						list = new ArrayList<PassiveSpell>();
						types.put(mat, list);
					}
					list.add(spell);
					materials.add(mat.getMaterial());
				}
			}	
		}		
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (event.getCurrentItem() == null && event.getCurrentItem().getType() == Material.AIR) return;
		Player player = (Player)event.getWhoClicked();
		
		if (allTypes.size() > 0) {
			Spellbook spellbook = MagicSpells.getSpellbook(player);
			for (PassiveSpell spell : allTypes) {
				if (spell.ignoreCancelled() || !event.isCancelled()) {
					if (spellbook.hasSpell(spell)) {
						boolean casted = spell.activate(player);
						if (casted && spell.cancelDefaultAction()) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
		
		if (types.size() > 0) {
			List<PassiveSpell> list = getSpells(event.getCurrentItem());
			if (list != null) {
				Spellbook spellbook = MagicSpells.getSpellbook(player);
				for (PassiveSpell spell : list) {
					if (spell.ignoreCancelled() || !event.isCancelled()) {
						if (spellbook.hasSpell(spell)) {
							boolean casted = spell.activate(player);
							if (casted && spell.cancelDefaultAction()) {
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	private List<PassiveSpell> getSpells(ItemStack item) {
		if (materials.contains(item.getType())) {
			for (MagicMaterial m : types.keySet()) {
				if (m.equals(item)) {
					return types.get(m);
				}
			}
		}
		return null;
	}

}
