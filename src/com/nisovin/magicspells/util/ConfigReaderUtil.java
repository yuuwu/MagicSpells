package com.nisovin.magicspells.util;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigReaderUtil {

	
	public static MagicLocation readLocation(ConfigurationSection section, String path) {
		return readLocation(section, path, "world,0,0,0");
	}
	
	public static MagicLocation readLocation(ConfigurationSection section, String path, String defaultText) {
		String s = section.getString(path, defaultText);
		MagicLocation ret = null;
		try {
			String[] split = s.split(",");
			String world = split[0];
			double x = Double.parseDouble(split[1]);
			double y = Double.parseDouble(split[2]);
			double z = Double.parseDouble(split[3]);
			float yaw = 0;
			float pitch = 0;
			if (split.length > 4) {
				yaw = Float.parseFloat(split[4]);
			}
			if (split.length > 5) {
				pitch = Float.parseFloat(split[5]);
			}
			ret = new MagicLocation(world, x, y, z, yaw, pitch);
		} catch (Exception e) {
			return null;
		}
		return ret;
	}
	
}
