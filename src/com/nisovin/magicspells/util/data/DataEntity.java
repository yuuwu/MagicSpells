package com.nisovin.magicspells.util.data;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DataEntity {
	
	private static Map<String, Function<Entity, String>> dataElements = new HashMap<>();
	
	static {
		dataElements.put("uuid", entity -> entity.getUniqueId().toString());
		dataElements.put("name", Entity::getName);
		dataElements.put("customname", Entity::getCustomName);
		dataElements.put("entitytype", entity -> entity.getType().name());
	}
	
	public static Function<Entity, String> getDataFunction(String elementId) {
		return dataElements.get(elementId);
	}
	
}
