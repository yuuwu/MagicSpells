package com.nisovin.magicspells.zones;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.compat.CompatBasics;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class NoMagicZoneWorldGuard extends NoMagicZone {

	private String worldName;
	private String regionName;
	private ProtectedRegion region;

	@Override
	public void initialize(ConfigurationSection config) {
		worldName = config.getString("world", "");
		regionName = config.getString("region", "");
	}

	@Override
	public boolean inZone(Location location) {
		// Check world
		if (!worldName.equals(location.getWorld().getName())) return false;

		// Get region, if necessary
		if (region == null) {
			WorldGuardPlugin worldGuard = null;
			if (CompatBasics.pluginEnabled("WorldGuard")) worldGuard = (WorldGuardPlugin)CompatBasics.getPlugin("WorldGuard");
			if (worldGuard != null) {
				World w = Bukkit.getServer().getWorld(worldName);
				if (w != null) {
					RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
					if (rm != null) region = rm.getRegion(regionName);
				}
			}
		}

		// Check if contains
		if (region != null) {
			return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}

		MagicSpells.error("Failed to access WorldGuard region '" + regionName + '\'');
		return false;
	}

}
