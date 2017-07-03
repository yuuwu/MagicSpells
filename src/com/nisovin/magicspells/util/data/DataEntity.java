package com.nisovin.magicspells.util.data;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DataEntity {
	
	private static Map<String, Function<Entity, String>> dataElements = new HashMap<>();
	
	static {
		try {
			dataElements.put("uuid", entity -> entity.getUniqueId().toString());
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		
		try {
			dataElements.put("name", entity -> entity.getName());
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		
		try {
			dataElements.put("customname", entity -> entity.getCustomName());
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		
		try {
			dataElements.put("entitytype", entity -> entity.getType().name());
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
	}
	
	public static Function<Entity, String> getDataFunction(String elementId) {
		return dataElements.get(elementId);
	}
	
}
