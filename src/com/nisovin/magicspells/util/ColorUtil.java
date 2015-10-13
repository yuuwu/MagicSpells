package com.nisovin.magicspells.util;

import org.bukkit.Color;

public class ColorUtil {
	
	public static Color getColorFromHexString(String hex) {
		String working = hex;
		working = working.replace("#", "");
		try {
		int value = Integer.parseInt(working, 16);
		return Color.fromRGB(value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
