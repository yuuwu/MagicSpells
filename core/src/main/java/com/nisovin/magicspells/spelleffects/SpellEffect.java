package com.nisovin.magicspells.spelleffects;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.castmodifiers.ModifierSet;

public abstract class SpellEffect {

	int delay;
	double chance;
	double zOffset;
	double heightOffset;
	double forwardOffset;
	
	// for line
	double distanceBetween;

	// for buff/orbit
	float orbitRadius;
	float orbitYOffset;
	float horizOffset;
	float horizExpandRadius;
	float vertExpandRadius;
	float ticksPerSecond;
	float distancePerTick;
	float secondsPerRevolution;

	int tickInterval;
	int ticksPerRevolution;
	int horizExpandDelay;
	int vertExpandDelay;
	int effectInterval = TimeUtil.TICKS_PER_SECOND;

	boolean counterClockwise;
	
	ModifierSet modifiers;
	Random random = new Random();
	
	int taskId = -1;
	
	public void loadFromString(String string) {
		MagicSpells.plugin.getLogger().warning("Warning: single line effects are being removed, usage encountered: " + string);
	}
	
	public final void loadFromConfiguration(ConfigurationSection config) {
		delay = config.getInt("delay", 0);
		chance = config.getDouble("chance", -1) / 100;
		zOffset = config.getDouble("z-offset", 0);
		heightOffset = config.getDouble("height-offset", 0);
		forwardOffset = config.getDouble("forward-offset", 0);

		distanceBetween = config.getDouble("distance-between", 1);

		orbitRadius = (float) config.getDouble("orbit-radius", 1F);
		orbitYOffset = (float) config.getDouble("orbit-y-offset", 0F);
		horizOffset = (float) config.getDouble("orbit-horiz-offset", 0F);
		horizExpandRadius = (float) config.getDouble("orbit-horiz-expand-radius", 0);
		vertExpandRadius = (float) config.getDouble("orbit-vert-expand-radius", 0);
		ticksPerSecond = 20F / (float) tickInterval;
		distancePerTick = 6.28F / (ticksPerSecond * secondsPerRevolution);
		secondsPerRevolution = (float) config.getDouble("orbit-seconds-per-revolution", 3);

		tickInterval = config.getInt("orbit-tick-interval", 2);
		ticksPerRevolution = Math.round(ticksPerSecond * secondsPerRevolution);
		horizExpandDelay = config.getInt("orbit-horiz-expand-delay", 0);
		vertExpandDelay = config.getInt("orbit-vert-expand-delay", 0);
		effectInterval = config.getInt("effect-interval", effectInterval);

		counterClockwise = config.getBoolean("orbit-counter-clockwise", false);
		
		List<String> list = config.getStringList("modifiers");
		if (list != null) modifiers = new ModifierSet(list);
		
		loadFromConfig(config);
	}
	
	protected abstract void loadFromConfig(ConfigurationSection config);
	
	/**
	 * Plays an effect on the specified entity.
	 * @param entity the entity to play the effect on
	 * @param param the parameter specified in the spell config (can be ignored)
	 */
	public Runnable playEffect(final Entity entity) {
		if (chance > 0 && chance < 1 && random.nextDouble() > chance) return null;
		if (delay <= 0) return playEffectEntity(entity);
		MagicSpells.scheduleDelayedTask(() -> playEffectEntity(entity), delay);
		return null;
	}
	
	protected Runnable playEffectEntity(Entity entity) {
		return playEffectLocationReal(entity == null ? null : entity.getLocation());
	}
	
	/**
	 * Plays an effect at the specified location.
	 * @param location location to play the effect at
	 * @param param the parameter specified in the spell config (can be ignored)
	 */
	public final Runnable playEffect(final Location location) {
		if (chance > 0 && chance < 1 && random.nextDouble() > chance) return null;
		if (delay <= 0) return playEffectLocationReal(location);
		MagicSpells.scheduleDelayedTask(() -> playEffectLocationReal(location), delay);
		return null;
	}
	
	private Runnable playEffectLocationReal(Location location) {
		if (location == null) return playEffectLocation(null);
		Location loc = location.clone();
		if (zOffset != 0) {
			Vector locDirection = loc.getDirection().normalize();
			Vector horizOffset = new Vector(-locDirection.getZ(), 0.0, locDirection.getX()).normalize();
			loc.add(horizOffset.multiply(zOffset)).getBlock().getLocation();
		}
		if (heightOffset != 0) loc.setY(loc.getY() + heightOffset);
		if (forwardOffset != 0) loc.add(loc.getDirection().setY(0).normalize().multiply(forwardOffset));
		return playEffectLocation(loc);
	}
	
	protected Runnable playEffectLocation(Location location) {
		//expect to be overridden
		return null;
	}
	
	/**
	 * Plays an effect between two locations (such as a smoke trail type effect).
	 * @param location1 the starting location
	 * @param location2 the ending location
	 * @param param the parameter specified in the spell config (can be ignored)
	 */
	public Runnable playEffect(Location location1, Location location2) {
		Location loc1 = location1.clone();
		Location loc2 = location2.clone();
		//double localHeightOffset = heightOffsetExpression.resolveValue(null, null, location1, location2).doubleValue();
		//double localForwardOffset = forwardOffsetExpression.resolveValue(null, null, location1, location2).doubleValue();
		int c = (int) Math.ceil(loc1.distance(loc2) / distanceBetween) - 1;
		if (c <= 0) return null;
		Vector v = loc2.toVector().subtract(loc1.toVector()).normalize().multiply(distanceBetween);
		Location l = loc1.clone();
		if (heightOffset != 0) l.setY(l.getY() + heightOffset);
		
		for (int i = 0; i < c; i++) {
			l.add(v);
			playEffect(l);
		}
		return null;
	}
	
	public void playEffectWhileActiveOnEntity(final Entity entity, final SpellEffectActiveChecker checker) {
		new EffectTracker(entity, checker);
	}
	
	public OrbitTracker playEffectWhileActiveOrbit(final Entity entity, final SpellEffectActiveChecker checker) {
		return new OrbitTracker(entity, checker);
	}
	
	@FunctionalInterface
	public interface SpellEffectActiveChecker {
		boolean isActive(Entity entity);
	}

	class EffectTracker implements Runnable {

		Entity entity;
		SpellEffectActiveChecker checker;
		int effectTrackerTaskId;

		public EffectTracker(Entity entity, SpellEffectActiveChecker checker) {
			this.entity = entity;
			this.checker = checker;
			this.effectTrackerTaskId = MagicSpells.scheduleRepeatingTask(this, 0, effectInterval);
		}

		@Override
		public void run() {
			if (!entity.isValid() || !checker.isActive(entity)) {
				stop();
				return;
			}
			
			if (entity instanceof Player && !modifiers.check((Player) entity)) return;
			
			playEffect(entity);

		}

		public void stop() {
			MagicSpells.cancelTask(effectTrackerTaskId);
			entity = null;
		}

	}

	class OrbitTracker implements Runnable {
		
		Entity entity;
		SpellEffectActiveChecker checker;
		Vector currentPosition;
		int orbitTrackerTaskId;
		int repeatingHorizTaskId;
		int repeatingVertTaskId;
		float orbRadius;
		float orbHeight;
		
		int counter = 0;
		
		public OrbitTracker(Entity entity, SpellEffectActiveChecker checker) {
			this.entity = entity;
			this.checker = checker;
			this.currentPosition = entity.getLocation().getDirection().setY(0);
			Util.rotateVector(this.currentPosition, horizOffset);
			this.orbRadius = orbitRadius;
			this.orbHeight = orbitYOffset;
			this.orbitTrackerTaskId = MagicSpells.scheduleRepeatingTask(this, 0, tickInterval);
			if (horizExpandDelay > 0 && horizExpandRadius != 0) {
				this.repeatingHorizTaskId = MagicSpells.scheduleRepeatingTask(() -> this.orbRadius += horizExpandRadius, horizExpandDelay, horizExpandDelay);
			}
			if (vertExpandDelay > 0 && vertExpandRadius != 0) {
				this.repeatingVertTaskId = MagicSpells.scheduleRepeatingTask(() -> this.orbHeight += vertExpandRadius, vertExpandDelay, vertExpandDelay);
			}
		}
		
		@Override
		public void run() {
			if (!entity.isValid()) {
				stop();
				return;
			}

			if (counter++ % ticksPerRevolution == 0 && !checker.isActive(entity)) {
				stop();
				return;
			}
			
			Location loc = getLocation();
			
			if (entity instanceof Player && !modifiers.check((Player) entity)) return;
			
			playEffect(loc);
			
		}
		
		private Location getLocation() {
			Vector perp;
			if (counterClockwise) perp = new Vector(currentPosition.getZ(), 0, -currentPosition.getX());
			else perp = new Vector(-currentPosition.getZ(), 0, currentPosition.getX());
			currentPosition.add(perp.multiply(distancePerTick)).normalize();
			return entity.getLocation().add(0, orbHeight, 0).add(currentPosition.clone().multiply(orbRadius));
		}
		
		public void stop() {
			MagicSpells.cancelTask(orbitTrackerTaskId);
			MagicSpells.cancelTask(repeatingHorizTaskId);
			MagicSpells.cancelTask(repeatingVertTaskId);
			entity = null;
			currentPosition = null;
		}
		
	}
	
	private static Map<String, Class<? extends SpellEffect>> effects = new HashMap<>();
	
	/**
	 * Gets the GraphicalEffect by the provided name.
	 * @param name the name of the effect
	 * @return
	 */
	public static SpellEffect createNewEffectByName(String name) {
		Class<? extends SpellEffect> clazz = effects.get(name.toLowerCase());
		if (clazz == null) return null;
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return null;
		}
	}
	
	public void playTrackingLinePatterns(Location origin, Location target, Entity originEntity, Entity targetEntity) {
		// no op, effects should override this with their own behavior
	}
	
	/**
	 * Adds an effect with the provided name to the list of available effects.
	 * This will replace an existing effect if the same name is used.
	 * @param name the name of the effect
	 * @param effect the effect to add
	 */
	public static void addEffect(String name, Class<? extends SpellEffect> effect) {
		effects.put(name.toLowerCase(), effect);
	}
	
	static {
		effects.put("actionbartext", ActionBarTextEffect.class);
		effects.put("bossbar", BossBarEffect.class);
		effects.put("broadcast", BroadcastEffect.class);
		effects.put("cloud", CloudEffect.class);
		effects.put("dragondeath", DragonDeathEffect.class);
		effects.put("ender", EnderSignalEffect.class);
		effects.put("explosion", ExplosionEffect.class);
		effects.put("fireworks", FireworksEffect.class);
		effects.put("itemcooldown", ItemCooldownEffect.class);
		effects.put("itemspray", ItemSprayEffect.class);
		effects.put("lightning", LightningEffect.class);
		effects.put("nova", NovaEffect.class);
		effects.put("particles", ParticlesEffect.class);
		effects.put("particlespersonal", ParticlesPersonalEffect.class);
		effects.put("particlecloud", ParticleCloudEffect.class);
		effects.put("potion", PotionEffect.class);
		effects.put("smokeswirl", SmokeSwirlEffect.class);
		effects.put("smoketrail", SmokeTrailEffect.class);
		effects.put("sound", SoundEffect.class);
		effects.put("soundpersonal", SoundPersonalEffect.class);
		effects.put("spawn", MobSpawnerEffect.class);
		effects.put("splash", SplashPotionEffect.class);
		effects.put("title", TitleEffect.class);
		effects.put("effectlib", EffectLibEffect.class);
		effects.put("effectlibline", EffectLibLineEffect.class);
		effects.put("effectlibentity", EffectLibEntityEffect.class);
	}
	
}
