package com.nisovin.magicspells.spelleffects;

public enum EffectPosition {

	/** Can be referenced as: start, startcast **/
	START_CAST(0),
	
	/** Can be referenced as: pos1, position1, caster, actor **/
	CASTER(1),
	
	/** Can be referenced as: pos2, position2, target **/
	TARGET(2),
	
	/** Can be referenced as: line, trail **/
	TRAIL(3),
	
	/** Can be referenced as: disabled **/
	DISABLED(4),
	
	/** Can be referenced as: delayed **/
	DELAYED(5),
	
	/** Can be referenced as: special **/
	SPECIAL(6),
	
	/** Can be referenced as: buff, active **/
	BUFF(7),
	
	/** Can be referenced as: orbit **/
	ORBIT(8),
	
	/** Can be referenced as: reverse_line, reverseline, rline **/
	REVERSE_LINE(9),
	
	
	/** May be referenced as: projectile
	 * Some spells may use this to play an effect on projectile entities **/
	PROJECTILE(10);
	
	private int id;
	private EffectPosition(int num) {
		this.id = num;
	}
	
	public int getId() {
		return id;
	}
	
}
