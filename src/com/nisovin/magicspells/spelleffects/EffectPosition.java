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
	
	/** Can be referenced as: disabled
	 *  Used in:
	 *      - SteedSpell
	 *      - HasteSpell
	 *      - ExternalCommandSpell
	 *      - BuffSpell (and all sub classes)
	 *      - DisguiseSpell
	 **/
	DISABLED(4),
	
	/** Can be referenced as: delayed **/
	DELAYED(5),
	
	/** Can be referenced as: special
	 *  Is used differently based upon each spell.
	 *  Behavior is defined in:
	 *  - MenuSpell
	 *  - FlightPathSpell
	 *  - ProjectileSpell
	 *  - PortalSpell
	 *  - ConjureSpell
	 *  - ConjureFireworkSpell
	 *  - ConjureBookSpell
	 *  - BeamSpell
	 *  - EntombSpell
	 *  - ForcebombSpell
	 *  - HomingMissileSpell
	 *  - HomingArrowSpell
	 *  - BombSpell
	 *  - AreaEffectSpell
	 **/
	SPECIAL(6),
	
	/** Can be referenced as: buff, active 
	 *  Used in:
	 *      - BuffSpell (and all subclasses)
	 *      - StunSpell
	 **/
	BUFF(7),
	
	/** Can be referenced as: orbit
	 *  Used in:
	 *      - BuffSpell (and all subclasses)
	 *      - StunSpell
	 **/
	ORBIT(8),
	
	/** Can be referenced as: reverse_line, reverseline, rline **/
	REVERSE_LINE(9),
	
	
	/** May be referenced as: projectile
	 * Some spells may use this to play an effect on projectile entities.
	 * Currently enabled in:
	 *   - ArrowSpell
	 *   - DestroySpell
	 *   - FireballSpell
	 *   - FreezeSpell
	 *   - HomingArrowSpell
	 *   - ItemProjectileSpell
	 *   - ProjectileSpell
	 *   - SpawnTntSpell
	 *   - ThrowBlockSpell
	 *   - VolleySpell
	 *   - WitherSkullSpell
	 **/
	PROJECTILE(10),
	
	/**
	 * May be referenced as: casterprojectile or casterprojectileline
	 * Currently supported effects:
	 *    - effectlibline
	 * Currently enabled in:
	 *    - ArrowSpell
	 *    - DestroySpell
	 *    - FireballSpell
	 *    - FreezeSpell
	 *    - HomingArrowSpell
	 *    - ItemProjectileSpell
	 *    - ProjectileSpell
	 *    - SpawnTntSpell
	 *    - ThrowBlockSpell
	 *    - VolleySpell
	 *    - WitherSkullSpell
	 */
	DYNAMIC_CASTER_PROJECTILE_LINE(11),
	
	/**
	 * May be referenced as: blockdestroy or blockdestruction
	 * Spells supported in:
	 *     - ThrowBlockSpell
	 *     - SpawnTntSpell
	 *     - MaterializeSpell
	 *     - PulserSpell
	 *     - EntombSpell
	 */
	BLOCK_DESTRUCTION(12);
	//TODO add this effect position to the WallSpell
	
	private int id;
	private EffectPosition(int num) {
		this.id = num;
	}
	
	public int getId() {
		return id;
	}
	
}
