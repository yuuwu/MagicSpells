package com.nisovin.magicspells.variables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nisovin.magicspells.variables.meta.BedCoordXVariable;
import com.nisovin.magicspells.variables.meta.BedCoordYVariable;
import com.nisovin.magicspells.variables.meta.BedCoordZVariable;
import com.nisovin.magicspells.variables.meta.CompassTargetXVariable;
import com.nisovin.magicspells.variables.meta.CompassTargetYVariable;
import com.nisovin.magicspells.variables.meta.CompassTargetZVariable;
import com.nisovin.magicspells.variables.meta.CoordPitchVariable;
import com.nisovin.magicspells.variables.meta.CoordXVariable;
import com.nisovin.magicspells.variables.meta.CoordYVariable;
import com.nisovin.magicspells.variables.meta.CoordYawVariable;
import com.nisovin.magicspells.variables.meta.CoordZVariable;
import com.nisovin.magicspells.variables.meta.EntityIDVariable;
import com.nisovin.magicspells.variables.meta.ExperienceLevelVariable;
import com.nisovin.magicspells.variables.meta.ExperienceVariable;
import com.nisovin.magicspells.variables.meta.FallDistanceVariable;
import com.nisovin.magicspells.variables.meta.FireTicksVariable;
import com.nisovin.magicspells.variables.meta.FlySpeedVariable;
import com.nisovin.magicspells.variables.meta.FoodLevelVariable;
import com.nisovin.magicspells.variables.meta.HealthScaleVariable;
import com.nisovin.magicspells.variables.meta.LastDamageVariable;
import com.nisovin.magicspells.variables.meta.MaxHealthVariable;
import com.nisovin.magicspells.variables.meta.MaximumAirVariable;
import com.nisovin.magicspells.variables.meta.MaximumNoDamageTicksVariable;
import com.nisovin.magicspells.variables.meta.NoDamageTicksVariable;
import com.nisovin.magicspells.variables.meta.PlayersOnlineVariable;
import com.nisovin.magicspells.variables.meta.RemainingAirVariable;
import com.nisovin.magicspells.variables.meta.SaturationVariable;
import com.nisovin.magicspells.variables.meta.SleepTicksVariable;
import com.nisovin.magicspells.variables.meta.VelocityXVariable;
import com.nisovin.magicspells.variables.meta.VelocityYVariable;
import com.nisovin.magicspells.variables.meta.VelocityZVariable;
import com.nisovin.magicspells.variables.meta.WalkSpeedVariable;

// TODO add meta_attribute_base_attack
// TODO add meta_attribute_knockback_resistance
// TODO add meta_attribute_luck
// TODO add meta_attribute_attack_speed
// TODO add meta_attribute_armor
// TODO add meta_attribute_armor_toughness
public class SpecialVariables {

	private static Map<String, Variable> specialVariables;
	
	static {
		specialVariables = new HashMap<>();
		specialVariables.put("meta_location_x", new CoordXVariable());
		specialVariables.put("meta_location_y", new CoordYVariable());
		specialVariables.put("meta_location_z", new CoordZVariable());
		specialVariables.put("meta_location_pitch", new CoordPitchVariable());
		specialVariables.put("meta_location_yaw", new CoordYawVariable());
		specialVariables.put("meta_saturation", new SaturationVariable());
		specialVariables.put("meta_experience_level", new ExperienceLevelVariable());
		specialVariables.put("meta_experience_points", new ExperienceVariable());
		specialVariables.put("meta_remaining_air", new RemainingAirVariable());
		specialVariables.put("meta_max_air", new MaximumAirVariable());
		specialVariables.put("meta_fly_speed", new FlySpeedVariable());
		specialVariables.put("meta_walk_speed", new WalkSpeedVariable());
		specialVariables.put("meta_food_level", new FoodLevelVariable());
		specialVariables.put("meta_entity_id", new EntityIDVariable());
		specialVariables.put("meta_fire_ticks", new FireTicksVariable());
		specialVariables.put("meta_fall_distance", new FallDistanceVariable());
		specialVariables.put("meta_players_online", new PlayersOnlineVariable());
		specialVariables.put("meta_max_health", new MaxHealthVariable());
		specialVariables.put("meta_health_scale", new HealthScaleVariable());
		specialVariables.put("meta_compass_target_x", new CompassTargetXVariable());
		specialVariables.put("meta_compass_target_y", new CompassTargetYVariable());
		specialVariables.put("meta_compass_target_z", new CompassTargetZVariable());
		specialVariables.put("meta_velocity_x", new VelocityXVariable());
		specialVariables.put("meta_velocity_y", new VelocityYVariable());
		specialVariables.put("meta_velocity_z", new VelocityZVariable());
		specialVariables.put("meta_no_damage_ticks", new NoDamageTicksVariable());
		specialVariables.put("meta_max_no_damage_ticks", new MaximumNoDamageTicksVariable());
		specialVariables.put("meta_last_damage", new LastDamageVariable());
		specialVariables.put("meta_sleep_ticks", new SleepTicksVariable());
		specialVariables.put("meta_bed_location_x", new BedCoordXVariable());
		specialVariables.put("meta_bed_location_y", new BedCoordYVariable());
		specialVariables.put("meta_bed_location_z", new BedCoordZVariable());
	}
	
	public static Map<String, Variable> getSpecialVariables() {
		return Collections.unmodifiableMap(specialVariables);
	}
	
}
