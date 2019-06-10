package com.nisovin.magicspells.spells.buff;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerMoveEvent;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.PlayerNameUtils;

public class StonevisionSpell extends BuffSpell {

	private Map<UUID, TransparentBlockSet> seers;
	private Set<Material> transparentTypes;

	private int range;
	private boolean unobfuscate;

	private Material material;

	public StonevisionSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		range = getConfigInt("range", 4);
		unobfuscate = getConfigBoolean("unobfuscate", false);
		
		transparentTypes = new HashSet<>();

		String replaceMaterialName = getConfigString("material", "BARRIER").toUpperCase();
		material = Material.getMaterial(replaceMaterialName);
		if (material == null || !material.isBlock()) {
			MagicSpells.error("StonevisionSpell '" + internalName + "' has an invalid material defined!");
			material = null;
		}

		List<String> types = getConfigStringList("transparent-types", null);
		if (types != null) {
			for (String str : types) {
				Material material = Material.getMaterial(str.toUpperCase());
				if (material != null && material.isBlock()) transparentTypes.add(material);
			}
		}

		if (transparentTypes.isEmpty()) MagicSpells.error("StonevisionSpell '" + internalName + "' does not define any transparent types");

		seers = new HashMap<>();
	}

	@Override
	public boolean castBuff(LivingEntity entity, float power, String[] args) {
		if (!(entity instanceof Player)) return true;
		seers.put(entity.getUniqueId(), new TransparentBlockSet((Player) entity, range, transparentTypes));
		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return seers.containsKey(entity.getUniqueId());
	}

	@Override
	public void turnOffBuff(LivingEntity entity) {
		TransparentBlockSet t = seers.remove(entity.getUniqueId());
		if (t != null) t.removeTransparency();
	}

	@Override
	protected void turnOff() {
		for (TransparentBlockSet tbs : seers.values()) {
			tbs.removeTransparency();
		}

		seers.clear();
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player pl = event.getPlayer();
		if (!isActive(pl)) return;
		if (isExpired(pl)) {
			turnOff(pl);
			return;
		}

		boolean moved = seers.get(pl.getUniqueId()).moveTransparency();
		if (!moved) return;
		addUseAndChargeCost(pl);
	}

	private class TransparentBlockSet {

		Player player;
		Block center;
		int range;
		Set<Material> types;
		List<Block> blocks;
		Set<Chunk> chunks;

		public TransparentBlockSet(Player player, int range, Set<Material> types) {
			this.player = player;
			this.center = player.getLocation().getBlock();
			this.range = range;
			this.types = types;

			this.blocks = new ArrayList<>();
			if (unobfuscate) this.chunks = new HashSet<>();

			setTransparency();
		}

		public void setTransparency() {
			List<Block> newBlocks = new ArrayList<>();

			// Get blocks to set to transparent
			int px = center.getX();
			int py = center.getY();
			int pz = center.getZ();
			Block block;
			if (!unobfuscate) {
				// Handle normally
				for (int x = px - range; x <= px + range; x++) {
					for (int y = py - range; y <= py + range; y++) {
						for (int z = pz - range; z <= pz + range; z++) {
							block = center.getWorld().getBlockAt(x,y,z);
							if (types.contains(block.getType())) {
								player.sendBlockChange(block.getLocation(), material.createBlockData());
								newBlocks.add(block);
							}
						}
					}
				}
			} else {
				// Unobfuscate everything
				int dx;
				int dy;
				int dz;
				for (int x = px - range - 1; x <= px + range + 1; x++) {
					for (int y = py - range - 1; y <= py + range + 1; y++) {
						for (int z = pz - range - 1; z <= pz + range + 1; z++) {
							dx = Math.abs(x - px);
							dy = Math.abs(y - py);
							dz = Math.abs(z - pz);
							block = center.getWorld().getBlockAt(x, y, z);
							if (types.contains(block.getType()) && dx <= range && dy <= range && dz <= range) {
								player.sendBlockChange(block.getLocation(), material.createBlockData());
								newBlocks.add(block);
							} else if (block.getType() != Material.AIR) {
								player.sendBlockChange(block.getLocation(), block.getType().createBlockData());
							}

							// Save chunk for resending after spell ends
							Chunk c = block.getChunk();
							chunks.add(c);
						}
					}
				}
			}

			// Remove old transparent blocks
			blocks.stream().filter(b -> !newBlocks.contains(b)).forEachOrdered(b -> player.sendBlockChange(b.getLocation(), b.getType().createBlockData()));
			// Update block set
			blocks = newBlocks;
		}

		public boolean moveTransparency() {
			if (player.isDead()) {
				player = PlayerNameUtils.getPlayer(player.getName());
			}
			Location loc = this.player.getLocation();
			if (!center.getWorld().equals(loc.getWorld()) || center.getX() != loc.getBlockX() || center.getY() != loc.getBlockY() || center.getZ() != loc.getBlockZ()) {
				// Moved
				center = loc.getBlock();
				setTransparency();
				return true;
			}
			return false;
		}

		public void removeTransparency() {
			Util.forEachOrdered(blocks, b -> player.sendBlockChange(b.getLocation(), b.getType().createBlockData()));
			blocks = null;
		}

	}

}
