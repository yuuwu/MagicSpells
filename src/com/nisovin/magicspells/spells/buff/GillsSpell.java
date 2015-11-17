package com.nisovin.magicspells.spells.buff;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.ConfigData;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.PlayerNameUtils;

public class GillsSpell extends BuffSpell {

	@ConfigData(field="glass-head-effect", dataType="boolean", defaultValue="true")
	private boolean glassHeadEffect;
	
	private HashSet<String> fishes;
	private HashMap<Player,ItemStack> helmets;
	
	public GillsSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		glassHeadEffect = getConfigBoolean("glass-head-effect", true);
		
		fishes = new HashSet<String>();
		if (glassHeadEffect) {
			helmets = new HashMap<Player,ItemStack>();
		}
	}

	@Override
	public boolean castBuff(Player player, float power, String[] args) {
		fishes.add(player.getName());
		if (glassHeadEffect) {
			ItemStack helmet = player.getInventory().getHelmet();
			if (helmet != null && helmet.getType() != Material.AIR) {
				helmets.put(player, helmet);
			}
			player.getInventory().setHelmet(new ItemStack(Material.GLASS, 1));
		}
		return true;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (!event.isCancelled() && event.getEntity() instanceof Player && event.getCause() == DamageCause.DROWNING) {
			Player player = (Player)event.getEntity();
			if (fishes.contains(player.getName())) {
				if (isExpired(player)) {
					turnOff(player);
				} else {
					addUse(player);
					boolean ok = chargeUseCost(player);
					if (ok) {
						event.setCancelled(true);
						player.setRemainingAir(player.getMaximumAir());
					}
				}
			}
		}
	}

	@Override
	public void turnOffBuff(Player player) {
		if (fishes.remove(player.getName())) {
			if (glassHeadEffect) {
				if (helmets.containsKey(player)) {
					if (player.isOnline()) player.getInventory().setHelmet(helmets.get(player));
					helmets.remove(player);
				} else if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.GLASS) {
					if (player.isOnline()) player.getInventory().setHelmet(null);				
				}
			}
		}
	}
	
	@Override
	protected void turnOff() {
		for (String name : fishes) {
			if (glassHeadEffect) {
				Player player = PlayerNameUtils.getPlayerExact(name);
				if (player != null && player.isOnline()) {
					if (helmets.containsKey(player)) {
						player.getInventory().setHelmet(helmets.get(player));
					} else if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.GLASS) {
						player.getInventory().setHelmet(null);
					}
				}
			}
		}
		if (helmets != null) {
			helmets.clear();
		}
		fishes.clear();
	}

	@Override
	public boolean isActive(Player player) {
		return fishes.contains(player.getName());
	}

}
