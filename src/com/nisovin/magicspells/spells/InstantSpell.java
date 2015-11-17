package com.nisovin.magicspells.spells;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.ConfigData;
import com.nisovin.magicspells.util.MagicConfig;

public abstract class InstantSpell extends Spell {
	
	@ConfigData(field="can-cast-with-item", dataType="boolean", defaultValue="true")
	private boolean castWithItem;
	
	@ConfigData(field="can-cast-with-command", dataType="boolean", defaultValue="true")
	private boolean castByCommand;
	
	public InstantSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		castWithItem = getConfigBoolean("can-cast-with-item", true);
		castByCommand = getConfigBoolean("can-cast-by-command", true);
	}
	
	@Override
	public boolean canCastWithItem() {
		return castWithItem;
	}
	
	@Override
	public boolean canCastByCommand() {
		return castByCommand;
	}
}
