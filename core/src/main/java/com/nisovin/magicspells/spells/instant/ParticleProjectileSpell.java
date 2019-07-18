package com.nisovin.magicspells.spells.instant;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.BoundingBox;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.ValidTargetList;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.castmodifiers.ModifierSet;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;

public class ParticleProjectileSpell extends InstantSpell implements TargetedLocationSpell, TargetedEntitySpell, TargetedEntityFromLocationSpell {

	private static Set<ProjectileTracker> trackerSet;

	private ParticleProjectileSpell thisSpell;

	private Random rand;

	private float targetYOffset;
	private float startXOffset;
	private float startYOffset;
	private float startZOffset;
	private Vector relativeOffset;

	private float acceleration;
	private int accelerationDelay;
	private float projectileTurn;
	private float projectileVelocity;
	private float projectileVertOffset;
	private float projectileHorizOffset;
	private float projectileVertSpread;
	private float projectileHorizSpread;
	private float projectileVertGravity;
	private float projectileHorizGravity;

	private int tickInterval;
	private float ticksPerSecond;
	private int spellInterval;
	private int intermediateEffects;
	private int specialEffectInterval;

	private int maxEntitiesHit;
	private float hitRadius;
	private float verticalHitRadius;

	private int maxDuration;
	private int maxDistanceSquared;

	private boolean hugSurface;
	private float heightFromSurface;

	private boolean controllable;
	private boolean changePitch;
	private boolean hitSelf;
	private boolean hitGround;
	private boolean hitPlayers;
	private boolean hitAirAtEnd;
	private boolean hitAirDuring;
	private boolean hitNonPlayers;
	private boolean hitAirAfterDuration;
	private boolean stopOnHitEntity;
	private boolean stopOnHitGround;
	private boolean stopOnModifierFail;
	private boolean allowCasterInteract;
	private boolean powerAffectsVelocity;

	private ValidTargetList targetList;
	private ModifierSet projModifiers;
	private List<String> projModifiersStrings;
	private List<String> interactions;
	private Map<String, Subspell> interactionSpells;

	private Subspell airSpell;
	private Subspell selfSpell;
	private Subspell tickSpell;
	private Subspell entitySpell;
	private Subspell groundSpell;
	private Subspell durationSpell;
	private Subspell modifierSpell;
	private String airSpellName;
	private String selfSpellName;
	private String tickSpellName;
	private String entitySpellName;
	private String groundSpellName;
	private String durationSpellName;
	private String modifierSpellName;

	private Subspell defaultSpell;
	private String defaultSpellName;

	public ParticleProjectileSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		trackerSet = new HashSet<>();
		thisSpell = this;

		rand = new Random();

		// Compatibility with start-forward-offset
		float startForwardOffset = getConfigFloat("start-forward-offset", 1F);
		startXOffset = getConfigFloat("start-x-offset", 1F);
		if (startForwardOffset != 1F) startXOffset = startForwardOffset;
		startYOffset = getConfigFloat("start-y-offset", 1F);
		startZOffset = getConfigFloat("start-z-offset", 0F);
		targetYOffset = getConfigFloat("target-y-offset", 0F);

		// If relative-offset contains different values than the offsets above, override them
		relativeOffset = getConfigVector("relative-offset", "1,1,0");
		if (relativeOffset.getX() != 1F) startXOffset = (float) relativeOffset.getX();
		if (relativeOffset.getY() != 1F) startYOffset = (float) relativeOffset.getY();
		if (relativeOffset.getZ() != 0F) startZOffset = (float) relativeOffset.getZ();

		acceleration = getConfigFloat("projectile-acceleration", 0F);
		accelerationDelay = getConfigInt("projectile-acceleration-delay", 0);

		projectileTurn = getConfigFloat("projectile-turn", 0);
		projectileVelocity = getConfigFloat("projectile-velocity", 10F);
		projectileVertOffset = getConfigFloat("projectile-vert-offset", 0F);
		projectileHorizOffset = getConfigFloat("projectile-horiz-offset", 0F);
		float projectileGravity = getConfigFloat("projectile-gravity", 0F);
		projectileVertGravity = getConfigFloat("projectile-vert-gravity", projectileGravity);
		projectileHorizGravity = getConfigFloat("projectile-horiz-gravity", 0F);
		float projectileSpread = getConfigFloat("projectile-spread", 0F);
		projectileVertSpread = getConfigFloat("projectile-vertical-spread", projectileSpread);
		projectileHorizSpread = getConfigFloat("projectile-horizontal-spread", projectileSpread);

		tickInterval = getConfigInt("tick-interval", 2);
		ticksPerSecond = 20F / (float) tickInterval;
		spellInterval = getConfigInt("spell-interval", 20);
		intermediateEffects = getConfigInt("intermediate-effects", 0);
		specialEffectInterval = getConfigInt("special-effect-interval", 1);

		maxDistanceSquared = getConfigInt("max-distance", 15);
		maxDistanceSquared *= maxDistanceSquared;
		maxDuration = (int) (getConfigInt("max-duration", 0) * TimeUtil.MILLISECONDS_PER_SECOND);
		hitRadius = getConfigFloat("hit-radius", 1.5F);
		maxEntitiesHit = getConfigInt("max-entities-hit", 0);
		verticalHitRadius = getConfigFloat("vertical-hit-radius", hitRadius);

		hugSurface = getConfigBoolean("hug-surface", false);
		if (hugSurface) heightFromSurface = getConfigFloat("height-from-surface", 0.6F);

		controllable = getConfigBoolean("controllable", false);
		changePitch = getConfigBoolean("change-pitch", true);
		hitSelf = getConfigBoolean("hit-self", false);
		hitGround = getConfigBoolean("hit-ground", true);
		hitPlayers = getConfigBoolean("hit-players", false);
		hitAirAtEnd = getConfigBoolean("hit-air-at-end", false);
		hitAirDuring = getConfigBoolean("hit-air-during", false);
		hitNonPlayers = getConfigBoolean("hit-non-players", true);
		hitAirAfterDuration = getConfigBoolean("hit-air-after-duration", false);
		stopOnHitGround = getConfigBoolean("stop-on-hit-ground", true);
		stopOnHitEntity = getConfigBoolean("stop-on-hit-entity", true);
		stopOnModifierFail = getConfigBoolean("stop-on-modifier-fail", true);
		allowCasterInteract = getConfigBoolean("allow-caster-interact", true);
		powerAffectsVelocity = getConfigBoolean("power-affects-velocity", true);
		if (stopOnHitEntity) maxEntitiesHit = 1;

		// Target List
		targetList = new ValidTargetList(this, getConfigStringList("can-target", null));
		if (hitSelf) targetList.enforce(ValidTargetList.TargetingElement.TARGET_SELF, true);
		if (hitPlayers) targetList.enforce(ValidTargetList.TargetingElement.TARGET_PLAYERS, true);
		if (hitNonPlayers) targetList.enforce(ValidTargetList.TargetingElement.TARGET_NONPLAYERS, true);
		projModifiersStrings = getConfigStringList("projectile-modifiers", null);
		interactions = getConfigStringList("interactions", null);
		interactionSpells = new HashMap<>();

		// Compatibility
		defaultSpellName = getConfigString("spell", "");
		airSpellName = getConfigString("spell-on-hit-air", defaultSpellName);
		selfSpellName = getConfigString("spell-on-hit-self", defaultSpellName);
		tickSpellName = getConfigString("spell-on-tick", defaultSpellName);
		groundSpellName = getConfigString("spell-on-hit-ground", defaultSpellName);
		entitySpellName = getConfigString("spell-on-hit-entity", defaultSpellName);
		durationSpellName = getConfigString("spell-on-duration-end", defaultSpellName);
		modifierSpellName = getConfigString("spell-on-modifier-fail", defaultSpellName);
	}

	@Override
	public void initialize() {
		super.initialize();

		defaultSpell = new Subspell(defaultSpellName);
		if (!defaultSpell.process()) {
			if (!defaultSpellName.isEmpty()) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell defined!");
			defaultSpell = null;
		}

		airSpell = new Subspell(airSpellName);
		if (!airSpell.process() || !airSpell.isTargetedLocationSpell()) {
			if (!airSpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-hit-air defined!");
			airSpell = null;
		}

		selfSpell = new Subspell(selfSpellName);
		if (!selfSpell.process()) {
			if (!selfSpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-hit-self defined!");
			selfSpell = null;
		}

		tickSpell = new Subspell(tickSpellName);
		if (!tickSpell.process() || !tickSpell.isTargetedLocationSpell()) {
			if (!tickSpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-tick defined!");
			tickSpell = null;
		}

		groundSpell = new Subspell(groundSpellName);
		if (!groundSpell.process() || !groundSpell.isTargetedLocationSpell()) {
			if (!groundSpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-hit-ground defined!");
			groundSpell = null;
		}

		entitySpell = new Subspell(entitySpellName);
		if (!entitySpell.process()) {
			if (!entitySpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-hit-entity defined!");
			entitySpell = null;
		}

		durationSpell = new Subspell(durationSpellName);
		if (!durationSpell.process()) {
			if (!durationSpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-duration-end defined!");
			durationSpell = null;
		}

		modifierSpell = new Subspell(modifierSpellName);
		if (!modifierSpell.process()) {
			if (!modifierSpellName.equals(defaultSpellName)) MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an invalid spell-on-modifier-fail defined!");
			modifierSpell = null;
		}

		if (projModifiersStrings != null && !projModifiersStrings.isEmpty()) {
			projModifiers = new ModifierSet(projModifiersStrings);
			projModifiersStrings = null;
		}

		if (interactions != null && !interactions.isEmpty()) {
			for (String str : interactions) {
				String[] params = str.split(" ");
				if (params[0] == null) continue;
				if (params[0].equalsIgnoreCase(internalName)) {
					MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an interaction with itself!");
					continue;
				}

				Subspell projectile = new Subspell(params[0]);
				if (!projectile.process() || !(projectile.getSpell() instanceof ParticleProjectileSpell)) {
					MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an interaction with '" + params[0] + "' but that's not a valid particle projectile!");
					continue;
				}

				if (params.length == 1) {
					interactionSpells.put(params[0], null);
					continue;
				}

				if (params.length <= 1) continue;
				if (params[1] == null) continue;
				Subspell collisionSpell = new Subspell(params[1]);
				if (!collisionSpell.process() || !collisionSpell.isTargetedLocationSpell()) {
					MagicSpells.error("ParticleProjectileSpell '" + internalName + "' has an interaction with '" + params[0] + "' and their spell on collision '" + params[1] + "' is not a valid spell!");
					continue;
				}
				interactionSpells.put(params[0], collisionSpell);
			}
		}
	}

	@Override
	public void turnOff() {
		trackerSet.clear();
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			new ProjectileTracker(player, player.getLocation(), power);
			playSpellEffects(EffectPosition.CASTER, player);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		new ProjectileTracker(caster, target, power);
		playSpellEffects(EffectPosition.CASTER, caster);
		return true;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		Location targetLoc = target.clone();
		if (Float.isNaN(targetLoc.getPitch())) targetLoc.setPitch(0);
		new ProjectileTracker(null, targetLoc, power);
		return true;
	}

	@Override
	public boolean castAtEntityFromLocation(Player caster, Location from, LivingEntity target, float power) {
		if (!caster.getLocation().getWorld().equals(target.getLocation().getWorld())) return false;
		Location targetLoc = from.clone();
		if (Float.isNaN(targetLoc.getPitch())) targetLoc.setPitch(0);
		new ProjectileTracker(caster, targetLoc, target, power);
		playSpellEffects(from, target);
		return true;
	}

	@Override
	public boolean castAtEntityFromLocation(Location from, LivingEntity target, float power) {
		Location targetLoc = from.clone();
		if (Float.isNaN(targetLoc.getPitch())) targetLoc.setPitch(0);
		new ProjectileTracker(null, targetLoc, target, power);
		playSpellEffects(from, target);
		return true;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		if (!caster.getLocation().getWorld().equals(target.getLocation().getWorld())) return false;
		new ProjectileTracker(caster, caster.getLocation(), target, power);
		playSpellEffects(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return false;
	}

	// TODO move to a separate Java file and use getters for field access
	class ProjectileTracker implements Runnable {

		Player caster;
		float power;
		long startTime;
		Location startLocation;
		Location previousLocation;
		Location currentLocation;
		Vector currentVelocity;
		Vector startDirection;
		int currentX;
		int currentZ;
		int taskId;
		BoundingBox hitBox;
		List<LivingEntity> inRange;
		List<LivingEntity> maxHitLimit;
		Map<LivingEntity, Long> immune;
		ValidTargetChecker entitySpellChecker;
		ProjectileTracker tracker;
		ParticleProjectileSpell projectileSpell;

		int counter = 0;

		ProjectileTracker(Player caster, Location from, float power) {
			this.caster = caster;
			this.power = power;
			startTime = System.currentTimeMillis();
			if (!changePitch) from.setPitch(0F);
			startLocation = from.clone();

			// Changing the start location
			startDirection = caster.getLocation().getDirection().normalize();
			Vector horizOffset = new Vector(-startDirection.getZ(), 0.0, startDirection.getX()).normalize();
			startLocation.add(horizOffset.multiply(startZOffset)).getBlock().getLocation();
			startLocation.add(startLocation.getDirection().multiply(startXOffset));
			startLocation.setY(startLocation.getY() + startYOffset);

			previousLocation = startLocation.clone();
			currentLocation = startLocation.clone();
			currentVelocity = from.getDirection();

			init();
		}

		ProjectileTracker(Player caster, Location from, LivingEntity target, float power) {
			this.caster = caster;
			this.power = power;
			startTime = System.currentTimeMillis();
			if (!changePitch) from.setPitch(0F);
			startLocation = from.clone();

			// Changing the target location
			Location targetLoc = target.getLocation().clone();
			targetLoc.add(0, targetYOffset,0);
			Vector dir = targetLoc.clone().subtract(from.clone()).toVector();

			// Changing the start location
			startDirection = dir.clone().normalize();
			Vector horizOffset = new Vector(-startDirection.getZ(), 0.0, startDirection.getX()).normalize();
			startLocation.add(horizOffset.multiply(startZOffset)).getBlock().getLocation();
			startLocation.add(startLocation.getDirection().multiply(startXOffset));
			startLocation.setY(startLocation.getY() + startYOffset);

			dir = targetLoc.clone().subtract(startLocation.clone()).toVector();

			previousLocation = startLocation.clone();
			currentLocation = startLocation.clone();
			currentVelocity = from.setDirection(dir).getDirection();

			init();
		}

		private void init() {
			if (projectileHorizOffset != 0) Util.rotateVector(currentVelocity, projectileHorizOffset);
			if (projectileVertOffset != 0) currentVelocity.add(new Vector(0, projectileVertOffset, 0)).normalize();
			if (projectileVertSpread > 0 || projectileHorizSpread > 0) {
				float rx = -1 + rand.nextFloat() * (1 + 1);
				float ry = -1 + rand.nextFloat() * (1 + 1);
				currentVelocity.add(new Vector(rx * projectileHorizSpread, ry * projectileVertSpread, rx * projectileHorizSpread));
			}
			if (hugSurface) {
				currentLocation.setY(currentLocation.getY() + heightFromSurface);
				currentVelocity.setY(0).normalize();
				currentLocation.setPitch(0);
			}
			if (powerAffectsVelocity) currentVelocity.multiply(power);
			currentVelocity.multiply(projectileVelocity / ticksPerSecond);
			taskId = MagicSpells.scheduleRepeatingTask(this, 0, tickInterval);
			if (targetList.canTargetPlayers() || targetList.canTargetLivingEntities()) {
				inRange = currentLocation.getWorld().getLivingEntities();
				inRange.removeIf(e -> !targetList.canTarget(caster, e));
			}
			immune = new HashMap<>();
			maxHitLimit = new ArrayList<>();
			hitBox = new BoundingBox(currentLocation, hitRadius, verticalHitRadius);
			currentLocation.setDirection(currentVelocity);
			projectileSpell = thisSpell;
			tracker = this;
			trackerSet.add(tracker);
		}

		@Override
		public void run() {
			if (caster != null && !caster.isValid()) {
				stop(true);
				return;
			}

			if (maxDuration > 0 && startTime + maxDuration < System.currentTimeMillis()) {
				if (hitAirAfterDuration && durationSpell != null && durationSpell.isTargetedLocationSpell()) {
					durationSpell.castAtLocation(caster, currentLocation, power);
					playSpellEffects(EffectPosition.TARGET, currentLocation);
				}
				stop(true);
				return;
			}

			if (projModifiers != null && !projModifiers.check(caster)) {
				if (modifierSpell != null) modifierSpell.castAtLocation(caster, currentLocation, power);
				if (stopOnModifierFail) stop(true);
				return;
			}

			if (controllable) {
				currentVelocity = caster.getLocation().getDirection();
				if (hugSurface) currentVelocity.setY(0).normalize();
				currentVelocity.multiply(projectileVelocity / ticksPerSecond);
				currentLocation.setDirection(currentVelocity);
			}

			// Move projectile and apply gravity
			previousLocation = currentLocation.clone();
			currentLocation.add(currentVelocity);

			if (hugSurface && (currentLocation.getBlockX() != currentX || currentLocation.getBlockZ() != currentZ)) {
				Block b = currentLocation.subtract(0, heightFromSurface, 0).getBlock();

				int attempts = 0;
				boolean ok = false;
				while (attempts++ < 10) {
					if (BlockUtils.isPathable(b)) {
						b = b.getRelative(BlockFace.DOWN);
						if (BlockUtils.isPathable(b)) currentLocation.add(0, -1, 0);
						else {
							ok = true;
							break;
						}
					} else {
						b = b.getRelative(BlockFace.UP);
						currentLocation.add(0, 1, 0);
						if (BlockUtils.isPathable(b)) {
							ok = true;
							break;
						}
					}
				}
				if (!ok) {
					stop(true);
					return;
				}

				currentLocation.setY((int) currentLocation.getY() + heightFromSurface);
				currentX = currentLocation.getBlockX();
				currentZ = currentLocation.getBlockZ();

				// Apply vertical gravity
			} else if (projectileVertGravity != 0) currentVelocity.setY(currentVelocity.getY() - (projectileVertGravity / ticksPerSecond));

			// Apply turn
			if (projectileTurn != 0) Util.rotateVector(currentVelocity, projectileTurn);

			// Apply horizontal gravity
			if (projectileHorizGravity != 0) Util.rotateVector(currentVelocity, (projectileHorizGravity / ticksPerSecond) * counter);

			// Rotate effects properly
			currentLocation.setDirection(currentVelocity);

			// Play effects
			if (specialEffectInterval > 0 && counter % specialEffectInterval == 0) playSpellEffects(EffectPosition.SPECIAL, currentLocation);

			// Acceleration
			if (acceleration != 0 && accelerationDelay > 0 && counter % accelerationDelay == 0) currentVelocity.multiply(acceleration);

			// Intermediate effects
			if (intermediateEffects > 0) playIntermediateEffects(previousLocation, currentVelocity);

			counter++;

			// Cast spell mid air
			if (hitAirDuring && counter % spellInterval == 0 && tickSpell != null) {
				tickSpell.castAtLocation(caster, currentLocation.clone(), power);
			}

			if (!BlockUtils.isPathable(currentLocation.getBlock())) {
				if (hitGround && groundSpell != null) {
					Util.setLocationFacingFromVector(previousLocation, currentVelocity);
					groundSpell.castAtLocation(caster, previousLocation, power);
					playSpellEffects(EffectPosition.TARGET, currentLocation);
				}
				if (stopOnHitGround) {
					stop(true);
					return;
				}
			}
			if (currentLocation.distanceSquared(startLocation) >= maxDistanceSquared) {
				if (hitAirAtEnd && airSpell != null) {
					airSpell.castAtLocation(caster, currentLocation.clone(), power);
					playSpellEffects(EffectPosition.TARGET, currentLocation);
				}
				stop(true);
			} else if (inRange != null) {
				hitBox.setCenter(currentLocation);
				for (int i = 0; i < inRange.size(); i++) {
					LivingEntity e = inRange.get(i);
					if (e.isDead()) continue;
					if (!hitBox.contains(e.getLocation().add(0, 0.6, 0))) continue;
					if (entitySpell != null && entitySpell.isTargetedEntitySpell()) {
						entitySpellChecker = entitySpell.getSpell().getValidTargetChecker();
						if (entitySpellChecker != null && !entitySpellChecker.isValidTarget(e)) {
							inRange.remove(i);
							break;
						}
						SpellTargetEvent event = new SpellTargetEvent(thisSpell, caster, e, power);
						EventUtil.call(event);
						if (event.isCancelled()) {
							inRange.remove(i);
							break;
						} else {
							e = event.getTarget();
							power = event.getPower();
						}
						entitySpell.castAtEntity(caster, e, power);
						playSpellEffects(EffectPosition.TARGET, e);
					} else if (entitySpell != null && entitySpell.isTargetedLocationSpell()) {
						entitySpell.castAtLocation(caster, currentLocation.clone(), power);
						playSpellEffects(EffectPosition.TARGET, currentLocation);
					}

					inRange.remove(i);
					maxHitLimit.add(e);
					immune.put(e, System.currentTimeMillis());

					if (maxEntitiesHit > 0 && maxHitLimit.size() >= maxEntitiesHit) stop(true);
					break;
				}

				if (immune != null && !immune.isEmpty()) {
					Iterator<Map.Entry<LivingEntity, Long>> iter = immune.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<LivingEntity, Long> entry = iter.next();
						if (entry.getValue() < System.currentTimeMillis() - (2 * TimeUtil.MILLISECONDS_PER_SECOND)) {
							iter.remove();
							inRange.add(entry.getKey());
						}
					}
				}

				if (projectileSpell.interactions == null || projectileSpell.interactions.isEmpty()) return;
				Set<ProjectileTracker> toRemove = new HashSet<>();
				for (ProjectileTracker collisionTracker : ParticleProjectileSpell.trackerSet) {
					if (collisionTracker == null) continue;
					if (tracker == null) continue;
					if (tracker.caster == null) continue;
					if (collisionTracker.caster == null) continue;
					if (collisionTracker.equals(tracker)) continue;
					if (!interactionSpells.containsKey(collisionTracker.projectileSpell.internalName)) continue;
					if (!collisionTracker.currentLocation.getWorld().equals(tracker.currentLocation.getWorld())) continue;
					if (!collisionTracker.hitBox.contains(tracker.currentLocation) && !tracker.hitBox.contains(collisionTracker.currentLocation)) continue;
					if (!allowCasterInteract && collisionTracker.caster.equals(tracker.caster)) continue;

					Subspell collisionSpell = interactionSpells.get(collisionTracker.projectileSpell.internalName);
					if (collisionSpell == null) {
						toRemove.add(collisionTracker);
						toRemove.add(tracker);
						collisionTracker.stop(false);
						tracker.stop(false);
					} else {
						double x = (tracker.currentLocation.getX() + collisionTracker.currentLocation.getX()) / 2D;
						double y = (tracker.currentLocation.getY() + collisionTracker.currentLocation.getY()) / 2D;
						double z = (tracker.currentLocation.getZ() + collisionTracker.currentLocation.getZ()) / 2D;

						Location middleLoc = new Location(tracker.currentLocation.getWorld(), x, y, z);
						collisionSpell.castAtLocation(tracker.caster, middleLoc, tracker.power);
						toRemove.add(collisionTracker);
						toRemove.add(tracker);
						collisionTracker.stop(false);
						tracker.stop(false);
					}
				}

				trackerSet.removeAll(toRemove);
				toRemove.clear();
			}
		}

		private void playIntermediateEffects(Location old, Vector movement) {
			int divideFactor = intermediateEffects + 1;
			Vector v = movement.clone();
			v.setX(v.getX() / divideFactor);
			v.setY(v.getY() / divideFactor);
			v.setZ(v.getZ() / divideFactor);
			for (int i = 0; i < intermediateEffects; i++) {
				old = old.add(v).setDirection(v);
				if (specialEffectInterval > 0 && counter % specialEffectInterval == 0) playSpellEffects(EffectPosition.SPECIAL, old);
			}
		}

		public void stop(boolean removeTracker) {
			if (removeTracker) trackerSet.remove(tracker);
			playSpellEffects(EffectPosition.DELAYED, currentLocation);
			MagicSpells.cancelTask(taskId);
			caster = null;
			startLocation = null;
			previousLocation = null;
			currentLocation = null;
			currentVelocity = null;
			maxHitLimit.clear();
			maxHitLimit = null;
			immune.clear();
			immune = null;
			if (inRange == null) return;
			inRange.clear();
			inRange = null;
		}

	}

}
