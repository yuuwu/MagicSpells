package com.nisovin.magicspells.spells.buff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.Util;

public class MinionSpell extends BuffSpell {
	
	private EntityType[] creatureTypes;
	private int[] chances;
	private boolean preventCombust;
	private boolean targetPlayers;
	private boolean gravity;
	
	private HashMap<String,LivingEntity> minions;
	private HashMap<String,LivingEntity> targets;
	private Random random;
	
	public MinionSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		gravity = getConfigBoolean("gravity", true);
		
		// Formatted as <entity type> <chance>
		List<String> c = getConfigStringList("mob-chances", null);
		if (c == null) c = new ArrayList<>();
		if (c.isEmpty()) {
			c.add("Zombie 100");
		}
		creatureTypes = new EntityType[c.size()];
		chances = new int[c.size()];
		for (int i = 0; i < c.size(); i++) {
			String[] data = c.get(i).split(" ");
			EntityType creatureType = Util.getEntityType(data[0]);
			int chance = 0;
			if (creatureType != null) {
				try {
					chance = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					// No op
				}
			}
			creatureTypes[i] = creatureType;
			chances[i] = chance;
		}
		preventCombust = getConfigBoolean("prevent-sun-burn", true);
		targetPlayers = getConfigBoolean("target-players", false);
		
		minions = new HashMap<>();
		targets = new HashMap<>();
		random = new Random();
	}
	
	@Override
	public boolean castBuff(Player player, float power, String[] args) {
		EntityType creatureType = null;
		int num = random.nextInt(100);
		int n = 0;
		for (int i = 0; i < creatureTypes.length; i++) {
			if (num < chances[i] + n) {
				creatureType = creatureTypes[i];
				break;
			} else {
				n += chances[i];
			}
		}
		if (creatureType != null) {
			// Get spawn location
			Location loc;
			loc = player.getLocation();
			loc.setX(loc.getX() - 1);
			
			// Spawn creature
			LivingEntity minion = (LivingEntity)player.getWorld().spawnEntity(loc, creatureType);
			MagicSpells.getVolatileCodeHandler().setGravity(minion, gravity);
			if (minion instanceof Creature) {
				minions.put(player.getName(), minion);
				targets.put(player.getName(), null);
			} else {
				minion.remove();
				MagicSpells.error("Cannot summon a non-creature with the minion spell!");
				return false;
			}
		} else {
			// Fail -- no creature found
			return false;
		}
		return true;
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (!event.isCancelled() && !minions.isEmpty()) {	
			if (event.getTarget() != null && event.getTarget() instanceof Player) {
				// A monster is trying to target a player
				Player player = (Player)event.getTarget();
				LivingEntity minion = minions.get(player.getName());
				if (minion != null && minion.getEntityId() == event.getEntity().getEntityId()) {
					// The targeted player owns the minion
					if (isExpired(player)) {
						// Spell is expired
						turnOff(player);
						return;
					}
					// Check if the player has a current target
					LivingEntity target = targets.get(player.getName());
					if (target != null) {
						// Player has a target
						if (target.isDead()) {
							// The target is dead, so remove that target
							targets.put(player.getName(), null);
							event.setCancelled(true);
						} else {
							// Send the minion after the player's target
							event.setTarget(target);
							MagicSpells.getVolatileCodeHandler().setTarget(minion, target);
							addUse(player);
							chargeUseCost(player);
						}
					} else {
						// Player doesn't have a target, so just order the minion to follow
						event.setCancelled(true);
						double distSq = minion.getLocation().toVector().distanceSquared(player.getLocation().toVector());
						if (distSq > 3 * 3) {
							// Minion is too far, tell him to move closer
							MagicSpells.getVolatileCodeHandler().entityPathTo(minion, player);
						} 
					}
				} else if (!targetPlayers && minions.containsValue(event.getEntity())) {
					// Player doesn't own minion, but it is an owned minion and pvp is off, so cancel
					event.setCancelled(true);
				}
			} else if (event.getReason() == TargetReason.FORGOT_TARGET && minions.containsValue(event.getEntity())) {
				// Forgetting target but it's a minion, don't let them do that! (probably a spider going passive)
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!minions.containsValue(event.getEntity())) return;
		event.setDroppedExp(0);
		event.getDrops().clear();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;
		EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent)event;
		Player p = null;
		if (evt.getDamager() instanceof Player) {
			p = (Player)evt.getDamager();
		} else if (evt.getDamager() instanceof Projectile && ((Projectile)evt.getDamager()).getShooter() instanceof Player) {
			p = (Player)((Projectile)evt.getDamager()).getShooter();
		}
		if (p != null) {
			if (minions.containsKey(p.getName())) {
				if (isExpired(p)) {
					turnOff(p);
					return;
				}
				LivingEntity target = (LivingEntity)event.getEntity();
				MagicSpells.getVolatileCodeHandler().setTarget(minions.get(p.getName()), target);
				targets.put(p.getName(), target);
				addUse(p);
				chargeUseCost(p);
			}
		}
	}	

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		if (!preventCombust) return;
		if (event.isCancelled()) return;
		if (!minions.containsValue(event.getEntity())) return;
		event.setCancelled(true);
	}
	
	@Override
	public void turnOffBuff(Player player) {
		LivingEntity minion = minions.remove(player.getName());
		if (minion != null && !minion.isDead()) minion.setHealth(0);
		targets.remove(player.getName());
	}
	
	@Override
	protected void turnOff() {
		Util.forEachValueOrdered(minions, minion -> minion.setHealth(0));
		minions.clear();
		targets.clear();
	}

	@Override
	public boolean isActive(Player player) {
		return minions.containsKey(player.getName());
	}
	
}
