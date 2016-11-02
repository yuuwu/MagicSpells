package com.nisovin.magicspells.variables.meta;

import com.nisovin.magicspells.variables.Variable;

public abstract class MetaVariable extends Variable {

	public MetaVariable() {
		permanent = false;
		bossBar = null;
		expBar = false;
		objective = null;
		minValue = Double.MIN_VALUE;
	}
	
	@Override
	public boolean modify(String player, double amount) {
		//no op
		return false;
	}

	@Override
	public void set(String player, double amount) {
		//no op
	}
	
	@Override
	public void reset(String player) {
		//no op
	}

}
