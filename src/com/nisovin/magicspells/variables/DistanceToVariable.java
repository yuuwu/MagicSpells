package com.nisovin.magicspells.variables;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.ConfigData;
import com.nisovin.magicspells.util.ConfigReaderUtil;
import com.nisovin.magicspells.util.LocationUtil;
import com.nisovin.magicspells.util.MagicLocation;
import com.nisovin.magicspells.util.PlayerNameUtils;

public class DistanceToVariable extends Variable {

	@ConfigData(field="cross-world", dataType="boolean", defaultValue="false", description="If true, distance will be calculated across worlds.")
	protected boolean crossWorld = false;
	
	@ConfigData(field="target-location", dataType="String", defaultValue="world,0,0,0")
	protected MagicLocation targetLocation;
	
	@ConfigData(field="cross-world-distance-multiplier", dataType="double", defaultValue="1.0", description="When calculating distance between locations between multiple worlds, the distance is multiplied by this value.")
	protected double crossWorldDistanceMultiplier = 1.0;
	
	public DistanceToVariable() {
		super();
	}
	
	@Override
	protected void init() {
		super.init();
		permanent = false;
	}
	
	@Override
	public double getValue(String player) {
		Player p = PlayerNameUtils.getPlayer(player);
		if (p == null) return defaultValue;
		
		Location originLocation = p.getLocation();
		if (originLocation == null) return defaultValue;
		
		Location targetLoc = this.targetLocation.getLocation();
		if (targetLoc == null) return defaultValue;
		
		if (!crossWorld && !LocationUtil.isSameWorld(originLocation, targetLoc)) return defaultValue;
		
		double multiplier = !LocationUtil.isSameWorld(originLocation, targetLoc) ? crossWorldDistanceMultiplier : 1.0;
		targetLoc.setWorld(originLocation.getWorld());
		return calculateReportedDistance(multiplier, originLocation, targetLoc);
	}

	@Override
	public boolean modify(String player, double amount) {
		// No op
		return false;
	}

	@Override
	public void set(String player, double amount) {
		// No op
	}

	@Override
	public void reset(String player) {
		// No op with this
	}
	
	@Override
	public void loadExtraData(ConfigurationSection section) {
		super.loadExtraData(section);
		crossWorld = section.getBoolean("cross-world", false);
		targetLocation = ConfigReaderUtil.readLocation(section, "target-location", "world,0,0,0");
		crossWorldDistanceMultiplier = section.getDouble("cross-world-distance-multiplier", 1.0);
	}
	
	protected double calculateReportedDistance(double multiplier, Location origin, Location target) {
		return target.distance(origin) * multiplier;
	}

}
