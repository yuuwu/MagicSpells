package com.nisovin.magicspells.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nisovin.magicspells.Spell;

public class SpellFilter {

	private Set<String> allowedSpells = null;
	private Set<String> blacklistedSpells = null;
	private Set<String> allowedTags = null;
	private Set<String> disallowedTags = null;
	
	private boolean defaultReturn;
	private boolean emptyFilter = false;
	
	public SpellFilter(List<String> allowedSpells, List<String> blacklistedSpells,
			List<String> allowedTags, List<String> disallowedTags) {
		
		//initialize the collections
		if (allowedSpells != null && allowedSpells.size() > 0) {
			this.allowedSpells = new HashSet<String>(allowedSpells);
		}
		if (blacklistedSpells != null && blacklistedSpells.size() > 0) {
			this.blacklistedSpells = new HashSet<String>(blacklistedSpells);
		}
		if (allowedTags != null && allowedTags.size() > 0) {
			this.allowedTags = new HashSet<String>(allowedTags);
		}
		if (disallowedTags != null && disallowedTags.size() > 0) {
			this.disallowedTags = new HashSet<String>(disallowedTags);
		}
		
		//determine the default outcome if nothing catches
		defaultReturn = determineDefaultValue();
	}
	
	private boolean determineDefaultValue() {
		//this means there is a tag whitelist check
		if (allowedTags != null) return false;
		
		//if there is a spell whitelist check
		if (allowedSpells != null) return false;
		
		//this means there is a tag blacklist
		if (disallowedTags != null) return true;
		
		//if there is a spell blacklist
		if (blacklistedSpells != null) return true;
		
		//if all of the collections are null, then there is no filter
		emptyFilter = true;
		return true;
	}
	
	public boolean check(Spell spell) {
		//can't do anything if null anyway
		if (spell == null) return false;
		
		//quick check to exit early if possible
		if (emptyFilter) return true;
		
		//is it whitelisted explicitly?
		if (allowedSpells != null && allowedSpells.contains(spell.getInternalName())) return true;
		
		//is it blacklisted?
		if (blacklistedSpells != null && blacklistedSpells.contains(spell.getInternalName())) return false;
		
		//does it have a blacklisted tag?
		if (disallowedTags != null) {
			for (String tag: disallowedTags) {
				if (spell.hasTag(tag)) return false;
			}
		}
		
		//does it have a whitelisted tag?
		if (allowedTags != null) {
			for (String tag: allowedTags) {
				if (spell.hasTag(tag)) return true;
			}
		}
		
		return defaultReturn;
	}
	
}
