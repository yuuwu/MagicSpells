package com.nisovin.magicspells.util;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class HandHandler {

	private static boolean initialized = false;
	private static boolean offhandExists;
	
	public static void initialize() {
		// only initialize once
		if (initialized) return;
		
		try {
			PlayerInteractEvent event = new PlayerInteractEvent(null, Action.LEFT_CLICK_AIR, new ItemStack(Material.STONE, 1), null, BlockFace.UP, EquipmentSlot.OFF_HAND);
			event.getHand();
			offhandExists = true;
		} catch (Throwable t) {
			offhandExists = false;
		}
		initialized = true;
	}
	
	
	public static boolean isMainHand(PlayerInteractEvent event) {
		if (!offhandExists) return true;
		return event.getHand() == EquipmentSlot.HAND;
	}
	
	public static boolean isOffhand(PlayerInteractEvent event) {
		return !isMainHand(event);
	}
	
	public static boolean isMainHand(PlayerInteractEntityEvent event) {
		if (!offhandExists) return true;
		return event.getHand() == EquipmentSlot.HAND;
	}
	
	public static boolean isOffhand(PlayerInteractEntityEvent event) {
		return !isMainHand(event);
	}
	
	
	
}
