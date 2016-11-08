package com.nisovin.magicspells.factions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.nisovin.magicspells.castmodifiers.ProxyCondition;
import com.nisovin.magicspells.events.MagicSpellsLoadedEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;

public class MagicSpellsFactions extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		registerCustomConditions();
	}
	
	@EventHandler
	public void onSpellTarget(SpellTargetEvent event) {
		if (event.getCaster() == null) return;
		if (!(event.getTarget() instanceof Player)) return;
		
		boolean beneficial = event.getSpell().isBeneficial();
		MPlayer caster = MPlayer.get(event.getCaster());
		MPlayer target = MPlayer.get(event.getTarget());
		
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(event.getCaster().getLocation()));
		Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getTarget().getLocation()));
		
		Rel rel = caster.getRelationTo(target);
		
		//make only check relations if friendly fire is disabled
		if ((faction == null || !faction.getFlag(MFlag.ID_FRIENDLYFIRE)) || (targetFaction == null || !targetFaction.getFlag(MFlag.ID_FRIENDLYFIRE))) {
			if (rel.isFriend() && !beneficial) {
				event.setCancelled(true);
			} else if (!rel.isFriend() && beneficial) {
				event.setCancelled(true);
			}
		}
		
		if (faction != null && !faction.getFlag(MFlag.ID_PVP)) {
			event.setCancelled(true);
		}
		
		if (targetFaction != null && !targetFaction.getFlag(MFlag.ID_PVP)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMagicSpellsLoad(MagicSpellsLoadedEvent event) {
		registerCustomConditions();
	}
	
	public void registerCustomConditions() {
		ProxyCondition.loadBackends(FactionsConditions.conditions);
	}
	
}
