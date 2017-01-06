package com.nisovin.magicspells.util;

import org.bukkit.Location;

public class LocationUtil {

	public static boolean isSameWorld(Location loc1, Location loc2) {
		return loc1.getWorld().getName().equals(loc2.getWorld().getName());
	}
	
	public static boolean isntSameWorld(Location loc1, Location loc2) {
		return !isSameWorld(loc1, loc2);
	}
	
}
