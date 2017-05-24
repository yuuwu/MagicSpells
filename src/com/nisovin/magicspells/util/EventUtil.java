package com.nisovin.magicspells.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

// TODO add a utility to wrap operations in anticheat systems exemption
public class EventUtil {

	public static void call(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}
	
}
