package com.nisovin.magicspells.variables;

import java.util.HashMap;
import java.util.Map;

public enum VariableType {
	
	PLAYER(new String[]{ "player" }) {
		@Override
		public Variable newInstance() {
			return new PlayerVariable();
		}
	},
	
	GLOBAL(new String[]{ "global" }) {
		@Override
		public Variable newInstance() {
			return new GlobalVariable();
		}
	},
	
	DISTANCE_TO(new String[]{ "distancetolocation" }) {
		@Override
		public Variable newInstance() {
			return new DistanceToVariable();
		}
	},
	
	
	DISTANCE_TO_SQUARED(new String[]{ "squareddistancetolocation" }) {
		@Override
		public Variable newInstance() {
			return new DistanceToSquaredVariable();
		}
	},
	
	PLAYER_STRING(new String[] { "playerstring" }) {

		@Override
		public Variable newInstance() {
			return new PlayerStringVariable();
		}
		
	}
	;
	
	private String[] names;
	
	private VariableType(String[] names) {
		this.names = names;
	}
	
	public abstract Variable newInstance();
	
	private static Map<String, VariableType> nameMap;
	private static boolean initialized = false;
	
	public static void initialize() {
		if (initialized) return;
		nameMap = new HashMap<String, VariableType>();
		for (VariableType type : VariableType.values()) {
			for (String name: type.names) {
				nameMap.put(name.toLowerCase(), type);
			}
		}
		initialized = true;
	}
	
	public static VariableType getType(String name) {
		if (!initialized) initialize();
		
		VariableType ret = nameMap.get(name.toLowerCase());
		if (ret == null) ret = VariableType.GLOBAL;
		
		return ret;
	}
}
