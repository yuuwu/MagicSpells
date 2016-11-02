package com.nisovin.magicspells.variables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nisovin.magicspells.variables.meta.CoordPitchVariable;
import com.nisovin.magicspells.variables.meta.CoordXVariable;
import com.nisovin.magicspells.variables.meta.CoordYVariable;
import com.nisovin.magicspells.variables.meta.CoordYawVariable;
import com.nisovin.magicspells.variables.meta.CoordZVariable;

public class SpecialVariables {

	private static Map<String, Variable> specialVariables;
	
	static {
		specialVariables = new HashMap<String, Variable>();
		specialVariables.put("meta_location_x", new CoordXVariable());
		specialVariables.put("meta_location_y", new CoordYVariable());
		specialVariables.put("meta_location_z", new CoordZVariable());
		specialVariables.put("meta_location_pitch", new CoordPitchVariable());
		specialVariables.put("meta_location_yaw", new CoordYawVariable());
	}
	
	public static Map<String, Variable> getSpecialVariables() {
		return Collections.unmodifiableMap(specialVariables);
	}
	
}
