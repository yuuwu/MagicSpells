package com.nisovin.magicspells.zones;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.ConfigData;

public abstract class NoMagicZone implements Comparable<NoMagicZone> {

	private String id;
	
	@ConfigData(field="priority", dataType="int", defaultValue="0")
	private int priority;
	
	@ConfigData(field="message", dataType="String", defaultValue="You are in a no-magic zone.")
	private String message;
	
	@ConfigData(field="allowed-spells", dataType="String[]")
	private List<String> allowedSpells;
	
	@ConfigData(field="disallowed-spells", dataType="String[]")
	private List<String> disallowedSpells;
	
	@ConfigData(field="allow-all", dataType="boolean", defaultValue="false")
	private boolean allowAll;
	
	@ConfigData(field="disallow-all", dataType="boolean", defaultValue="true")
	private boolean disallowAll;
	
	public final void create(String id, ConfigurationSection config) {
		this.id = id;
		priority = config.getInt("priority", 0);
		message = config.getString("message", "You are in a no-magic zone.");
		allowedSpells = config.getStringList("allowed-spells");
		disallowedSpells = config.getStringList("disallowed-spells");
		allowAll = config.getBoolean("allow-all", false);
		disallowAll = config.getBoolean("disallow-all", true);
		if (allowedSpells != null && allowedSpells.size() == 0) allowedSpells = null;
		if (disallowedSpells != null && disallowedSpells.size() == 0) disallowedSpells = null;
		if (disallowedSpells != null) disallowAll = false;
		if (allowedSpells != null) allowAll = false;
		initialize(config);
	}
	
	public abstract void initialize(ConfigurationSection config);
	
	public final ZoneCheckResult check(Player player, Spell spell) {
		return check(player.getLocation(), spell);
	}
	
	public final ZoneCheckResult check(Location location, Spell spell) {
		if (!inZone(location)) return ZoneCheckResult.IGNORED;
		if (disallowedSpells != null && disallowedSpells.contains(spell.getInternalName())) return ZoneCheckResult.DENY;
		if (allowedSpells != null && allowedSpells.contains(spell.getInternalName())) return ZoneCheckResult.ALLOW;
		if (disallowAll) return ZoneCheckResult.DENY;
		if (allowAll) return ZoneCheckResult.ALLOW;
		return ZoneCheckResult.IGNORED;
	}
	
	public abstract boolean inZone(Location location);
	
	public String getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public int compareTo(NoMagicZone other) {
		if (this.priority < other.priority) return 1;
		if (this.priority > other.priority) return -1;
		return this.id.compareTo(other.id);
	}
	
	public enum ZoneCheckResult {
		
		ALLOW, DENY, IGNORED
		
	}
	
}
