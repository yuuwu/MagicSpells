package com.nisovin.magicspells.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.slikey.effectlib.util.ReflectionUtils;

public class SoundUtils {

	public static void playSound(Player player, String sound, float volume,
			float pitch) {
		Location loc = player.getLocation();
		Object packet = getSoundPacket(sound, loc, volume, pitch);
		// PacketPlayOutNamedSoundEffect packet = new
		// PacketPlayOutNamedSoundEffect(sound, loc.getX(), loc.getY(),
		// loc.getZ(), volume, pitch);

		sendPacket(player, packet);
		//((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	
	
	public static void makeSound(Location location, String soundName, float volume, float pitch) {
		//get the world's handle
		Object worldHandle = getCraftWorldHandle(location);
		try {
			//call makeSound(location.getX(), location.getY(), location.getZ(), sound, volume, pitch);
			ReflectionUtils.invokeMethod(worldHandle, "makeSound", location.getX(), location.getY(), location.getZ(), soundName, volume, pitch);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	private static Object getCraftWorldHandle(Location loc) {
		try {
			ReflectionUtils.invokeMethod(loc.getWorld(), "getHandle");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Object getSoundPacket(String name, Location loc,
			float volume, float pitch) {
		try {
			return de.slikey.effectlib.util.ReflectionUtils
					.instantiateObject(
							"PacketPlayOutNamedSoundEffect",
							de.slikey.effectlib.util.ReflectionUtils.PackageType.MINECRAFT_SERVER,
							name, loc.getX(), loc.getY(), loc.getZ(), volume,
							pitch);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void sendPacket(Player player, Object packet) {
		// get the craftplayer
		try {
			// get the craftplayer handle
			Object handle = ReflectionUtils.invokeMethod(player, "getHandle");

			// get the player connection
			Object connection = ReflectionUtils.getValue(handle,
					handle.getClass(), false, "playerConnection");

			// send the packet
			ReflectionUtils.invokeMethod(connection, "sendPacket", packet);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

}
