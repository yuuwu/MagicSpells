package com.nisovin.magicspells.spells.instant;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.util.TemporaryBlockSet;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.MagicSpellsBlockPlaceEvent;

public class WallSpell extends InstantSpell implements TargetedLocationSpell {

	private Map<UUID, TemporaryBlockSet> blockSets;
	private List<Material> materials;

	private String strNoTarget;

	private Subspell spellOnBreak;
	private String spellOnBreakName;

	private int yOffset;
	private int distance;
	private int wallWidth;
	private int wallDepth;
	private int wallHeight;
	private int wallDuration;

	private boolean checkPlugins;
	private boolean preventDrops;
	private boolean alwaysOnGround;
	private boolean preventBreaking;
	private boolean checkPluginsPerBlock;

	public WallSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		blockSets = new HashMap<>();
		materials = new ArrayList<>();

		strNoTarget = getConfigString("str-no-target", "Unable to create a wall.");

		List<String> blocks = getConfigStringList("wall-types", null);
		if (blocks != null && !blocks.isEmpty()) {
			for (String s : blocks) {
				Material material = Material.getMaterial(s.toUpperCase());
				if (material == null) continue;
				if (!material.isBlock()) continue;
				materials.add(material);
			}
		}

		spellOnBreakName = getConfigString("spell-on-break", "");

		yOffset = getConfigInt("y-offset", -1);
		distance = getConfigInt("distance", 3);
		wallWidth = getConfigInt("wall-width", 5);
		wallDepth = getConfigInt("wall-depth", 1);
		wallHeight = getConfigInt("wall-height", 3);
		wallDuration = getConfigInt("wall-duration", 15);

		checkPlugins = getConfigBoolean("check-plugins", true);
		preventDrops = getConfigBoolean("prevent-drops", true);
		alwaysOnGround = getConfigBoolean("always-on-ground", false);
		preventBreaking = getConfigBoolean("prevent-breaking", false);
		checkPluginsPerBlock = getConfigBoolean("check-plugins-per-block", checkPlugins);
	}
	
	@Override
	public void initialize() {
		super.initialize();

		spellOnBreak = new Subspell(spellOnBreakName);
		if (!spellOnBreak.process()) {
			if (!spellOnBreakName.isEmpty()) MagicSpells.error("WallSpell '" + internalName + "' has an invalid spell-on-break defined!");
			spellOnBreak = null;
		}

		if ((preventBreaking || preventDrops || spellOnBreak != null) && wallDuration > 0) registerEvents(new BreakListener());
	}

	@Override
	public void turnOff() {
		for (TemporaryBlockSet set : blockSets.values()) {
			set.remove();
		}
		blockSets.clear();
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			if (materials == null || materials.isEmpty()) return PostCastAction.ALREADY_HANDLED;
			Block target = getTargetedBlock(player, distance > 0 && distance < 15 ? distance : 3);
			if (target == null || !BlockUtils.isAir(target.getType())) {
				sendMessage(strNoTarget, player, args);
				return PostCastAction.ALREADY_HANDLED;
			}
			makeWall(player, target.getLocation(), player.getLocation().getDirection(), power);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		makeWall(caster, target, target.getDirection(), power);
		return true;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return false;
	}

	private void makeWall(Player player, Location location, Vector direction, float power) {
		if (blockSets.containsKey(player.getUniqueId())) return;
		if (materials == null || materials.isEmpty()) return;
		if (location == null || direction == null) return;

		Block target = location.getBlock();

		if (checkPlugins) {
			BlockState eventBlockState = target.getState();
			target.setType(materials.get(0), false);
			MagicSpellsBlockPlaceEvent event = new MagicSpellsBlockPlaceEvent(target, eventBlockState, target, player.getEquipment().getItemInMainHand(), player, true);
			EventUtil.call(event);
			BlockUtils.setTypeAndData(target, Material.AIR, Material.AIR.createBlockData(), false);
			if (event.isCancelled()) {
				sendMessage(player, strNoTarget);
				return;
			}
		}

		if (alwaysOnGround) {
			yOffset = 0;
			Block b = target.getRelative(0, -1, 0);
			while (BlockUtils.isAir(b.getType()) && this.yOffset > -5) {
				yOffset--;
				b = b.getRelative(0, -1, 0);
			}
		}

		TemporaryBlockSet blockSet = new TemporaryBlockSet(Material.AIR, materials, checkPluginsPerBlock, player);
		Location loc = target.getLocation();
		Vector dir = direction.clone();
		int wallWidth = Math.round(this.wallWidth * power);
		int wallHeight = Math.round(this.wallHeight * power);
		if (Math.abs(dir.getX()) > Math.abs(dir.getZ())) {
			int depthDir = dir.getX() > 0 ? 1 : -1;
			for (int z = loc.getBlockZ() - (wallWidth / 2); z <= loc.getBlockZ() + (wallWidth / 2); z++) {
				for (int y = loc.getBlockY() + yOffset; y < loc.getBlockY() + wallHeight + yOffset; y++) {
					for (int x = target.getX(); x < target.getX() + wallDepth && x > target.getX() - wallDepth; x += depthDir) {
						blockSet.add(player.getWorld().getBlockAt(x, y, z));
					}
				}
			}
		} else {
			int depthDir = dir.getZ() > 0 ? 1 : -1;
			for (int x = loc.getBlockX() - (wallWidth / 2); x <= loc.getBlockX() + (wallWidth / 2); x++) {
				for (int y = loc.getBlockY() + yOffset; y < loc.getBlockY() + wallHeight + yOffset; y++) {
					for (int z = target.getZ(); z < target.getZ() + wallDepth && z > target.getZ() - wallDepth; z += depthDir) {
						blockSet.add(player.getWorld().getBlockAt(x, y, z));
					}
				}
			}
		}

		if (wallDuration > 0) {
			blockSets.put(player.getUniqueId(), blockSet);
			blockSet.removeAfter(Math.round(wallDuration * power), (TemporaryBlockSet set) -> blockSets.remove(player.getUniqueId()));
		}

		playSpellEffects(EffectPosition.CASTER, player);
	}

	private class BreakListener implements Listener {
		
		@EventHandler(ignoreCancelled=true)
		private void onBlockBreak(BlockBreakEvent event) {
			if (blockSets.isEmpty()) return;
			Block block = event.getBlock();

			Player caster = null;
			TemporaryBlockSet set = null;
			for (TemporaryBlockSet blockSet : blockSets.values()) {
				if (!blockSet.contains(block)) continue;
				set = blockSet;
				event.setCancelled(true);
				if (!preventBreaking) block.setType(Material.AIR);
			}

			for (UUID id : blockSets.keySet()) {
				if (!blockSets.get(id).equals(set)) continue;
				caster = Bukkit.getPlayer(id);
				if (caster == null) return;
				if (!caster.isOnline()) return;
			}

			if (spellOnBreak == null) return;
			if (spellOnBreak.isTargetedEntityFromLocationSpell()) {
				spellOnBreak.castAtEntityFromLocation(caster, block.getLocation().add(0.5, 0, 0.5), event.getPlayer(), 1F);
			} else if (spellOnBreak.isTargetedEntitySpell()) {
				spellOnBreak.castAtEntity(caster, event.getPlayer(), 1F);
			} else if (spellOnBreak.isTargetedLocationSpell()) {
				spellOnBreak.castAtLocation(caster, block.getLocation().add(0.5, 0, 0.5), 1F);
			} else {
				spellOnBreak.cast(caster, 1F);
			}
		}
		
	}
	
}
