package com.nisovin.magicspells.util;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;

public class LocationUtil {

	public static boolean isSameWorld(Object loc1, Object loc2) {
		World world1 = getWorld(loc1);
		if (world1 == null) return false;
		World world2 = getWorld(loc2);
		if (world2 == null) return false;
		return world1.equals(world2);
	}
	
	public static boolean isSameBlock(Object loc1, Object loc2) {
		Location location1 = getLocation(loc1);
		if (location1 == null) return false;
		Location location2 = getLocation(loc2);
		if (location2 == null) return false;
		if (!Objects.equals(location1.getWorld(), location2.getWorld())) return false;
		if (location1.getBlockX() != location2.getBlockX()) return false;
		if (location1.getBlockY() != location2.getBlockY()) return false;
		if (location1.getBlockZ() != location2.getBlockZ()) return false;
		return true;
	}
	
	public static boolean isSameChunk(Object one, Object two) {
		Location location1 = getLocation(one);
		if (location1 == null) return false;
		Location location2 = getLocation(two);
		if (location2 == null) return false;
		if (location1.getBlockX() >> 4 != location2.getBlockX() >> 4) return false;
		if (location1.getBlockY() >> 4 != location2.getBlockY() >> 4) return false;
		if (location1.getBlockZ() >> 4 != location2.getBlockZ() >> 4) return false;
		return Objects.equals(location1.getWorld(), location2.getWorld());
	}
	
	// This should redirect to other internal methods depending on what the type of object is
	public static Location getLocation(Object object) {
		if (object == null) return null;
		
		// Handle as Location
		if (object instanceof Location) return (Location) object;
		
		// Handle as Entity
		if (object instanceof Entity) return ((Entity) object).getLocation();
		
		// Handle as Block
		if (object instanceof Block) return ((Block) object).getLocation();
		
		// Handle as MagicLocation
		if (object instanceof MagicLocation) return ((MagicLocation) object).getLocation();
		
		// Handle as BlockCommandSender
		if (object instanceof BlockCommandSender) return getLocation(((BlockCommandSender) object).getBlock());
		
		return null;
	}
	
	// This should redirect to other internal methods depending on what the type of object is
	public static World getWorld(Object object) {
		if (object == null) return null;
		
		// Handle as World
		if (object instanceof World) return (World) object;
		
		// Handle as Location
		if (object instanceof Location) return ((Location) object).getWorld();
		
		// Handle as String
		if (object instanceof String) return Bukkit.getServer().getWorld((String) object);
		
		// Handle as Entity
		if (object instanceof Entity) return ((Entity) object).getWorld();
		
		// Handle as Block
		if (object instanceof Block) return ((Block) object).getWorld();
		
		// Handle as MagicLocation
		if (object instanceof MagicLocation) return ((MagicLocation) object).getLocation().getWorld();
		
		// Handle as BlockCommandSender
		if (object instanceof BlockCommandSender) return getWorld(((BlockCommandSender) object).getBlock());
		
		return null;
	}
	
}
