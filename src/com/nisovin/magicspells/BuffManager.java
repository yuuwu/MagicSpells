package com.nisovin.magicspells;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.zones.NoMagicZoneManager;

public class BuffManager {
	
	HashMap<String,HashSet<BuffSpell>> activeBuffs;
	int taskId = -1;
	
	public BuffManager(int interval) {
		activeBuffs = new HashMap<>();
		if (interval > 0) taskId = MagicSpells.scheduleRepeatingTask(new Monitor(), interval, interval);
	}
	
	public void addBuff(Player player, BuffSpell spell) {
		HashSet<BuffSpell> buffs = activeBuffs.get(player.getName());
		if (buffs == null) {
			buffs = new HashSet<>();
			activeBuffs.put(player.getName(), buffs);
		}
		buffs.add(spell);
	}
	
	public void removeBuff(Player player, BuffSpell spell) {
		HashSet<BuffSpell> buffs = activeBuffs.get(player.getName());
		if (buffs == null) return;
		buffs.remove(spell);
		if (buffs.isEmpty()) activeBuffs.remove(player.getName());
	}
	
	public HashSet<BuffSpell> getActiveBuffs(Player player) {
		return activeBuffs.get(player.getName());
	}
	
	public void turnOff() {
		if (taskId > 0) Bukkit.getScheduler().cancelTask(taskId);
		activeBuffs.clear();
		activeBuffs = null;
	}
	
	class Monitor implements Runnable {
		
		@Override
		public void run() {
			NoMagicZoneManager noMagicZones = MagicSpells.getNoMagicZoneManager();
			if (noMagicZones != null) {
				for (String playerName : activeBuffs.keySet()) {
					Player p = PlayerNameUtils.getPlayerExact(playerName);
					if (p == null) continue;
					HashSet<BuffSpell> buffs = new HashSet<>(activeBuffs.get(playerName));
					for (BuffSpell spell : buffs) {
						if (noMagicZones.willFizzle(p, spell)) spell.turnOff(p);
					}
				}
			} else {
				Bukkit.getScheduler().cancelTask(taskId);
				taskId = -1;
			}
		}
		
	}
	
}
