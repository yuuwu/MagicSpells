package com.nisovin.magicspells.spells.instant;

import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.events.SpellTargetEvent;
import org.bukkit.Color;
import org.bukkit.block.BlockFace;
import com.nisovin.magicspells.util.BlockUtils;
import org.bukkit.util.EulerAngle;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.ArmorStand;
import java.util.Map;
import org.bukkit.entity.LivingEntity;
import com.nisovin.magicspells.util.BoundingBox;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import org.bukkit.entity.Player;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.EffectPackage;
import com.nisovin.magicspells.util.ParticleNameUtil;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.Spell;
import java.util.List;
import com.nisovin.magicspells.util.MagicConfig;
import org.bukkit.inventory.ItemStack;
import de.slikey.effectlib.util.ParticleEffect;
import java.util.Random;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.util.ValidTargetList;
import org.bukkit.util.Vector;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spells.InstantSpell;

public class ArmorStandProjectile extends InstantSpell implements TargetedLocationSpell
{
    float startXOffset;
    float startYOffset;
    float startZOffset;
    Vector relativeOffset;
    float acceleration;
    int accelerationDelay;
    float projectileVelocity;
    float projectileVertOffset;
    float projectileHorizOffset;
    float projectileSpread;
    float projectileVertGravity;
    float projectileHorizGravity;
    boolean powerAffectsVelocity;
    int tickInterval;
    float ticksPerSecond;
    int specialEffectInterval;
    int spellInterval;
    String particleName;
    int particleCount;
    int renderDistance;
    float particleSpeed;
    float particleXSpread;
    float particleYSpread;
    float particleZSpread;
    int maxEntitiesHit;
    float hitRadius;
    float verticalHitRadius;
    int maxDuration;
    int maxDistanceSquared;
    boolean hugSurface;
    float heightFromSurface;
    boolean hitSelf;
    boolean hitGround;
    boolean hitPlayers;
    boolean hitAirAtEnd;
    boolean hitAirDuring;
    boolean hitNonPlayers;
    boolean hitAirAfterDuration;
    boolean stopOnHitEntity;
    boolean stopOnHitGround;
    ValidTargetList targetList;
    Subspell airSpell;
    Subspell selfSpell;
    Subspell tickSpell;
    Subspell entitySpell;
    Subspell groundSpell;
    Subspell durationSpell;
    String airSpellName;
    String selfSpellName;
    String tickSpellName;
    String entitySpellName;
    String groundSpellName;
    String durationSpellName;
    Subspell defaultSpell;
    String defaultSpellName;
    Random rand;
    ArmorStandProjectile thisSpell;
    ParticleEffect effect;
    ParticleEffect.ParticleData data;
    boolean canRender;
    ItemStack helm;
    boolean small;
    int linger;
    float rotateX;
    float rotateY;
    float rotateZ;
    float spawnRotX;
    float spawnRotY;
    float spawnRotZ;
    
    public ArmorStandProjectile(final MagicConfig config, final String spellName) {
        super(config, spellName);
        this.rand = new Random();
        this.thisSpell = this;
        final float startForwardOffset = this.getConfigFloat("start-forward-offset", 1.0f);
        this.startXOffset = this.getConfigFloat("start-x-offset", 1.0f);
        if (startForwardOffset != 1.0f) {
            this.startXOffset = startForwardOffset;
        }
        this.startYOffset = this.getConfigFloat("start-y-offset", 1.0f);
        this.startZOffset = this.getConfigFloat("start-z-offset", 0.0f);
        this.relativeOffset = this.getConfigVector("relative-offset", "1,1,0");
        if (this.relativeOffset.getX() != 1.0) {
            this.startXOffset = (float)this.relativeOffset.getX();
        }
        if (this.relativeOffset.getY() != 1.0) {
            this.startYOffset = (float)this.relativeOffset.getY();
        }
        if (this.relativeOffset.getZ() != 0.0) {
            this.startZOffset = (float)this.relativeOffset.getZ();
        }
        this.acceleration = this.getConfigFloat("projectile-acceleration", 0.0f);
        this.accelerationDelay = this.getConfigInt("projectile-acceleration-delay", 0);
        this.projectileVelocity = this.getConfigFloat("projectile-velocity", 10.0f);
        this.projectileVertOffset = this.getConfigFloat("projectile-vert-offset", 0.0f);
        this.projectileHorizOffset = this.getConfigFloat("projectile-horiz-offset", 0.0f);
        final float projectileGravity = this.getConfigFloat("projectile-gravity", 0.25f);
        this.projectileVertGravity = this.getConfigFloat("projectile-vert-gravity", projectileGravity);
        this.projectileHorizGravity = this.getConfigFloat("projectile-horiz-gravity", 0.0f);
        this.projectileSpread = this.getConfigFloat("projectile-spread", 0.0f);
        this.powerAffectsVelocity = this.getConfigBoolean("power-affects-velocity", true);
        this.tickInterval = this.getConfigInt("tick-interval", 2);
        this.ticksPerSecond = 20.0f / this.tickInterval;
        this.specialEffectInterval = this.getConfigInt("special-effect-interval", 0);
        this.spellInterval = this.getConfigInt("spell-interval", 20);
        this.particleName = this.getConfigString("particle-name", "reddust");
        this.particleSpeed = this.getConfigFloat("particle-speed", 0.3f);
        this.particleCount = this.getConfigInt("particle-count", 15);
        this.particleXSpread = this.getConfigFloat("particle-horizontal-spread", 0.3f);
        this.particleYSpread = this.getConfigFloat("particle-vertical-spread", 0.3f);
        this.particleZSpread = this.particleXSpread;
        this.particleXSpread = this.getConfigFloat("particle-red", this.particleXSpread);
        this.particleYSpread = this.getConfigFloat("particle-green", this.particleYSpread);
        this.particleZSpread = this.getConfigFloat("particle-blue", this.particleZSpread);
        this.maxDistanceSquared = this.getConfigInt("max-distance", 15);
        this.maxDistanceSquared *= this.maxDistanceSquared;
        this.maxDuration = (int)(this.getConfigInt("max-duration", 0) * 1000L);
        this.hitRadius = this.getConfigFloat("hit-radius", 1.5f);
        this.maxEntitiesHit = this.getConfigInt("max-entities-hit", 0);
        this.verticalHitRadius = this.getConfigFloat("vertical-hit-radius", this.hitRadius);
        this.renderDistance = this.getConfigInt("render-distance", 32);
        this.hugSurface = this.getConfigBoolean("hug-surface", false);
        if (this.hugSurface) {
            this.heightFromSurface = this.getConfigFloat("height-from-surface", 0.6f);
        }
        this.hitSelf = this.getConfigBoolean("hit-self", false);
        this.hitGround = this.getConfigBoolean("hit-ground", true);
        this.hitPlayers = this.getConfigBoolean("hit-players", false);
        this.hitAirAtEnd = this.getConfigBoolean("hit-air-at-end", false);
        this.hitAirDuring = this.getConfigBoolean("hit-air-during", false);
        this.hitNonPlayers = this.getConfigBoolean("hit-non-players", true);
        this.hitAirAfterDuration = this.getConfigBoolean("hit-air-after-duration", false);
        this.stopOnHitGround = this.getConfigBoolean("stop-on-hit-ground", true);
        this.stopOnHitEntity = this.getConfigBoolean("stop-on-hit-entity", true);
        if (this.stopOnHitEntity) {
            this.maxEntitiesHit = 1;
        }
        this.targetList = new ValidTargetList((Spell)this, this.getConfigStringList("can-target", (List)null));
        if (this.hitSelf) {
            this.targetList.enforce(ValidTargetList.TargetingElement.TARGET_SELF, true);
        }
        if (this.hitPlayers) {
            this.targetList.enforce(ValidTargetList.TargetingElement.TARGET_PLAYERS, true);
        }
        if (this.hitNonPlayers) {
            this.targetList.enforce(ValidTargetList.TargetingElement.TARGET_NONPLAYERS, true);
        }
        this.defaultSpellName = this.getConfigString("spell", "explode");
        this.airSpellName = this.getConfigString("spell-on-hit-air", this.defaultSpellName);
        this.selfSpellName = this.getConfigString("spell-on-hit-self", this.defaultSpellName);
        this.tickSpellName = this.getConfigString("spell-on-tick", this.defaultSpellName);
        this.groundSpellName = this.getConfigString("spell-on-hit-ground", this.defaultSpellName);
        this.entitySpellName = this.getConfigString("spell-on-hit-entity", this.defaultSpellName);
        this.durationSpellName = this.getConfigString("spell-on-duration-end", this.defaultSpellName);
        this.helm = Util.getItemStackFromString(this.getConfigString("helmet-type", "iron_sword"));
        this.small = this.getConfigBoolean("small", false);
        this.linger = this.getConfigInt("delay-model-removal", 20);
        this.rotateX = this.getConfigFloat("model-rotate-x", 0.0f);
        this.rotateY = this.getConfigFloat("model-rotate-y", 0.0f);
        this.rotateZ = this.getConfigFloat("model-rotate-z", 0.0f);
        this.spawnRotX = this.getConfigFloat("model-spawn-rotate-x", 0.0f);
        this.spawnRotY = this.getConfigFloat("model-spawn-rotate-y", 0.0f);
        this.spawnRotZ = this.getConfigFloat("model-spawn-rotate-z", 0.0f);
        final EffectPackage pkg = ParticleNameUtil.findEffectPackage(this.particleName);
        this.canRender = pkg.canRender();
        if (this.canRender) {
            this.data = pkg.data;
            this.effect = pkg.effect;
        }
    }
    
    public void initialize() {
        super.initialize();
        this.defaultSpell = new Subspell(this.defaultSpellName);
        if (!this.defaultSpell.process()) {
            MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell defined!");
            this.defaultSpell = null;
        }
        this.airSpell = new Subspell(this.airSpellName);
        if (!this.airSpell.process()) {
            if (!this.airSpellName.equals(this.defaultSpellName)) {
                MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell-on-hit-air defined!");
            }
            this.airSpell = null;
        }
        this.selfSpell = new Subspell(this.selfSpellName);
        if (!this.selfSpell.process()) {
            if (!this.selfSpellName.equals(this.defaultSpellName)) {
                MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell-on-hit-self defined!");
            }
            this.selfSpell = null;
        }
        this.tickSpell = new Subspell(this.tickSpellName);
        if (!this.tickSpell.process()) {
            if (!this.tickSpellName.equals(this.defaultSpellName)) {
                MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell-on-tick defined!");
            }
            this.tickSpell = null;
        }
        this.groundSpell = new Subspell(this.groundSpellName);
        if (!this.groundSpell.process()) {
            if (!this.groundSpellName.equals(this.defaultSpellName)) {
                MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell-on-hit-ground defined!");
            }
            this.groundSpell = null;
        }
        this.entitySpell = new Subspell(this.entitySpellName);
        if (!this.entitySpell.process()) {
            if (!this.entitySpellName.equals(this.defaultSpellName)) {
                MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell-on-hit-entity defined!");
            }
            this.entitySpell = null;
        }
        this.durationSpell = new Subspell(this.durationSpellName);
        if (!this.durationSpell.process()) {
            if (!this.durationSpellName.equals(this.defaultSpellName)) {
                MagicSpells.error("ArmorStandProjectile '" + this.internalName + "' has an invalid spell-on-duration-end defined!");
            }
            this.durationSpell = null;
        }
    }
    
    public Spell.PostCastAction castSpell(final Player player, final Spell.SpellCastState state, final float power, final String[] args) {
        if (state == Spell.SpellCastState.NORMAL) {
            new ProjectileTracker(player, player.getLocation(), power);
            this.playSpellEffects(EffectPosition.CASTER, (Entity)player);
        }
        return Spell.PostCastAction.HANDLE_NORMALLY;
    }
    
    public boolean castAtLocation(final Player caster, final Location target, final float power) {
        final Location loc = target.clone();
        loc.setDirection(caster.getLocation().getDirection());
        new ProjectileTracker(caster, target, power);
        this.playSpellEffects(EffectPosition.CASTER, (Entity)caster);
        return true;
    }
    
    public boolean castAtLocation(final Location target, final float power) {
        new ProjectileTracker(null, target, power);
        this.playSpellEffects(EffectPosition.CASTER, target);
        return true;
    }
    
    static /* synthetic */ void access$0(final ArmorStandProjectile armorStandProjectile, final EffectPosition effectPosition, final Location location) {
        armorStandProjectile.playSpellEffects(effectPosition, location);
    }
    
    static /* synthetic */ void access$1(final ArmorStandProjectile armorStandProjectile, final EffectPosition effectPosition, final Entity entity) {
        armorStandProjectile.playSpellEffects(effectPosition, entity);
    }
    
    class ProjectileTracker implements Runnable
    {
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
        Spell.ValidTargetChecker entitySpellChecker;
        ArmorStand as;
        int counter;
        int lingerCounter;
        float angleX;
        float angleY;
        float angleZ;
        
        public ProjectileTracker(final Player caster, final Location from, final float power) {
            this.counter = 0;
            this.lingerCounter = 0;
            this.angleX = 0.0f;
            this.angleY = 0.0f;
            this.angleZ = 0.0f;
            this.caster = caster;
            this.power = power;
            this.startTime = System.currentTimeMillis();
            this.startLocation = from.clone();
            this.startDirection = caster.getLocation().getDirection().normalize();
            final Vector horizOffset = new Vector(-this.startDirection.getZ(), 0.0, this.startDirection.getX()).normalize();
            this.startLocation.add(horizOffset.multiply(ArmorStandProjectile.this.startZOffset)).getBlock().getLocation();
            this.startLocation.add(this.startLocation.getDirection().multiply(ArmorStandProjectile.this.startXOffset));
            this.startLocation.setY(this.startLocation.getY() + ArmorStandProjectile.this.startYOffset);
            this.previousLocation = this.startLocation.clone();
            this.currentLocation = this.startLocation.clone();
            this.currentVelocity = from.getDirection();
            if (ArmorStandProjectile.this.projectileHorizOffset != 0.0f) {
                Util.rotateVector(this.currentVelocity, ArmorStandProjectile.this.projectileHorizOffset);
            }
            if (ArmorStandProjectile.this.projectileVertOffset != 0.0f) {
                this.currentVelocity.add(new Vector(0.0f, ArmorStandProjectile.this.projectileVertOffset, 0.0f)).normalize();
            }
            if (ArmorStandProjectile.this.projectileSpread > 0.0f) {
                this.currentVelocity.add(new Vector(ArmorStandProjectile.this.rand.nextFloat() * ArmorStandProjectile.this.projectileSpread, ArmorStandProjectile.this.rand.nextFloat() * ArmorStandProjectile.this.projectileSpread, ArmorStandProjectile.this.rand.nextFloat() * ArmorStandProjectile.this.projectileSpread));
            }
            if (ArmorStandProjectile.this.hugSurface) {
                this.currentLocation.setY(this.currentLocation.getY() + ArmorStandProjectile.this.heightFromSurface);
                this.currentVelocity.setY(0).normalize();
                this.currentLocation.setPitch(0.0f);
            }
            if (ArmorStandProjectile.this.powerAffectsVelocity) {
                this.currentVelocity.multiply(power);
            }
            this.currentVelocity.multiply(ArmorStandProjectile.this.projectileVelocity / ArmorStandProjectile.this.ticksPerSecond);
            this.taskId = MagicSpells.scheduleRepeatingTask((Runnable)this, 0, ArmorStandProjectile.this.tickInterval);
            if (ArmorStandProjectile.this.targetList.canTargetPlayers() || ArmorStandProjectile.this.targetList.canTargetLivingEntities()) {
                (this.inRange = (List<LivingEntity>)this.currentLocation.getWorld().getLivingEntities()).removeIf(e -> !ArmorStandProjectile.this.targetList.canTarget(caster, e));
                this.inRange.removeIf(e -> e instanceof ArmorStand);
            }
            this.immune = new HashMap<LivingEntity, Long>();
            this.maxHitLimit = new ArrayList<LivingEntity>();
            this.hitBox = new BoundingBox(this.currentLocation, (double)ArmorStandProjectile.this.hitRadius, (double)ArmorStandProjectile.this.verticalHitRadius);
            this.currentLocation.setDirection(this.currentVelocity);
            (this.as = (ArmorStand)this.currentLocation.getWorld().spawn(this.currentLocation, (Class)ArmorStand.class)).setCollidable(false);
            this.as.setHelmet(ArmorStandProjectile.this.helm);
            this.as.setInvulnerable(true);
            this.as.setVisible(false);
            if (ArmorStandProjectile.this.small) {
                this.as.setSmall(true);
            }
            else {
                this.as.setSmall(false);
            }
            if (ArmorStandProjectile.this.spawnRotX > 0.0f || ArmorStandProjectile.this.spawnRotY > 0.0f || ArmorStandProjectile.this.spawnRotZ > 0.0f) {
                this.as.setHeadPose(new EulerAngle(Math.toRadians(ArmorStandProjectile.this.spawnRotX), Math.toRadians(ArmorStandProjectile.this.spawnRotY), Math.toRadians(ArmorStandProjectile.this.spawnRotZ)));
            }
            else {
                this.as.setHeadPose(new EulerAngle(Math.toRadians(this.currentLocation.getPitch()), 0.0, 0.0));
            }
            this.as.setGravity(false);
        }
        
        @Override
        public void run() {
            if (this.caster != null && !this.caster.isValid()) {
                this.stop();
                return;
            }
            if (ArmorStandProjectile.this.maxDuration > 0 && this.startTime + ArmorStandProjectile.this.maxDuration < System.currentTimeMillis()) {
                if (ArmorStandProjectile.this.hitAirAfterDuration && ArmorStandProjectile.this.durationSpell != null && ArmorStandProjectile.this.durationSpell.isTargetedLocationSpell()) {
                    ArmorStandProjectile.this.durationSpell.castAtLocation(this.caster, this.currentLocation, this.power);
                    ArmorStandProjectile.access$0(ArmorStandProjectile.this, EffectPosition.TARGET, this.currentLocation);
                }
                this.stop();
                return;
            }
            this.previousLocation = this.currentLocation.clone();
            this.currentLocation.add(this.currentVelocity);
            if (ArmorStandProjectile.this.hugSurface && (this.currentLocation.getBlockX() != this.currentX || this.currentLocation.getBlockZ() != this.currentZ)) {
                Block b = this.currentLocation.subtract(0.0, (double)ArmorStandProjectile.this.heightFromSurface, 0.0).getBlock();
                int attempts = 0;
                boolean ok = false;
                while (attempts++ < 10) {
                    if (BlockUtils.isPathable(b)) {
                        b = b.getRelative(BlockFace.DOWN);
                        if (!BlockUtils.isPathable(b)) {
                            ok = true;
                            break;
                        }
                        this.currentLocation.add(0.0, -1.0, 0.0);
                    }
                    else {
                        b = b.getRelative(BlockFace.UP);
                        this.currentLocation.add(0.0, 1.0, 0.0);
                        if (BlockUtils.isPathable(b)) {
                            ok = true;
                            break;
                        }
                        continue;
                    }
                }
                if (!ok) {
                    this.stop();
                    return;
                }
                this.currentLocation.setY((double)((int)this.currentLocation.getY() + ArmorStandProjectile.this.heightFromSurface));
                this.currentX = this.currentLocation.getBlockX();
                this.currentZ = this.currentLocation.getBlockZ();
            }
            else if (ArmorStandProjectile.this.projectileVertGravity != 0.0f) {
                this.currentVelocity.setY(this.currentVelocity.getY() - ArmorStandProjectile.this.projectileVertGravity / ArmorStandProjectile.this.ticksPerSecond);
            }
            if (ArmorStandProjectile.this.projectileHorizGravity != 0.0f) {
                Util.rotateVector(this.currentVelocity, ArmorStandProjectile.this.projectileHorizGravity / ArmorStandProjectile.this.ticksPerSecond * this.counter);
            }
            if (ArmorStandProjectile.this.projectileHorizGravity != 0.0f || ArmorStandProjectile.this.projectileVertGravity != 0.0f) {
                this.currentLocation.setDirection(this.currentVelocity);
            }
            if (ArmorStandProjectile.this.canRender) {
                ArmorStandProjectile.this.effect.display(ArmorStandProjectile.this.data, this.currentLocation, (Color)null, (double)ArmorStandProjectile.this.renderDistance, ArmorStandProjectile.this.particleXSpread, ArmorStandProjectile.this.particleYSpread, ArmorStandProjectile.this.particleZSpread, ArmorStandProjectile.this.particleSpeed, ArmorStandProjectile.this.particleCount);
            }
            if (ArmorStandProjectile.this.specialEffectInterval > 0 && this.counter % ArmorStandProjectile.this.specialEffectInterval == 0) {
                ArmorStandProjectile.access$0(ArmorStandProjectile.this, EffectPosition.SPECIAL, this.currentLocation);
            }
            if (ArmorStandProjectile.this.acceleration != 0.0f && ArmorStandProjectile.this.accelerationDelay > 0 && this.counter % ArmorStandProjectile.this.accelerationDelay == 0) {
                this.currentVelocity.multiply(ArmorStandProjectile.this.acceleration);
            }
            if (ArmorStandProjectile.this.rotateX > 0.0f) {
                if (this.angleX > 360.0f) {
                    this.angleX = 0.0f;
                }
                this.as.setHeadPose(new EulerAngle(Math.toRadians(this.angleX), 0.0, 0.0));
                this.angleX += ArmorStandProjectile.this.rotateX;
            }
            if (ArmorStandProjectile.this.rotateY > 0.0f) {
                if (this.angleY > 360.0f) {
                    this.angleY = 0.0f;
                }
                this.as.setHeadPose(new EulerAngle(0.0, Math.toRadians(this.angleY), 0.0));
                this.angleY += ArmorStandProjectile.this.rotateY;
            }
            if (ArmorStandProjectile.this.rotateZ > 0.0f) {
                if (this.angleZ > 360.0f) {
                    this.angleZ = 0.0f;
                }
                this.as.setHeadPose(new EulerAngle(0.0, 0.0, Math.toRadians(this.angleZ)));
                this.angleZ += ArmorStandProjectile.this.rotateZ;
            }
            this.as.teleport(this.currentLocation);
            ++this.counter;
            if (ArmorStandProjectile.this.hitAirDuring && this.counter % ArmorStandProjectile.this.spellInterval == 0 && ArmorStandProjectile.this.tickSpell != null && ArmorStandProjectile.this.tickSpell.isTargetedLocationSpell()) {
                ArmorStandProjectile.this.tickSpell.castAtLocation(this.caster, this.currentLocation.clone(), this.power);
            }
            if (!BlockUtils.isPathable(this.currentLocation.getBlock())) {
                if (ArmorStandProjectile.this.hitGround && ArmorStandProjectile.this.groundSpell != null && ArmorStandProjectile.this.groundSpell.isTargetedLocationSpell()) {
                    Util.setLocationFacingFromVector(this.previousLocation, this.currentVelocity);
                    ArmorStandProjectile.this.groundSpell.castAtLocation(this.caster, this.previousLocation, this.power);
                    ArmorStandProjectile.access$0(ArmorStandProjectile.this, EffectPosition.TARGET, this.currentLocation);
                }
                if (ArmorStandProjectile.this.stopOnHitGround) {
                    this.stop();
                }
            }
            else if (this.currentLocation.distanceSquared(this.startLocation) >= ArmorStandProjectile.this.maxDistanceSquared) {
                if (ArmorStandProjectile.this.hitAirAtEnd && ArmorStandProjectile.this.airSpell != null && ArmorStandProjectile.this.airSpell.isTargetedLocationSpell()) {
                    ArmorStandProjectile.this.airSpell.castAtLocation(this.caster, this.currentLocation.clone(), this.power);
                    ArmorStandProjectile.access$0(ArmorStandProjectile.this, EffectPosition.TARGET, this.currentLocation);
                }
                this.stop();
            }
            else if (this.inRange != null) {
                this.hitBox.setCenter(this.currentLocation);
                for (int i = 0; i < this.inRange.size(); ++i) {
                    LivingEntity e = this.inRange.get(i);
                    if (!e.isDead()) {
                        if (this.hitBox.contains(e.getLocation().add(0.0, 0.6, 0.0))) {
                            if (ArmorStandProjectile.this.entitySpell != null && ArmorStandProjectile.this.entitySpell.isTargetedEntitySpell()) {
                                this.entitySpellChecker = ArmorStandProjectile.this.entitySpell.getSpell().getValidTargetChecker();
                                if (this.entitySpellChecker != null && !this.entitySpellChecker.isValidTarget(e)) {
                                    this.inRange.remove(i);
                                    break;
                                }
                                final SpellTargetEvent event = new SpellTargetEvent((Spell)ArmorStandProjectile.this.thisSpell, this.caster, e, this.power);
                                EventUtil.call((Event)event);
                                if (event.isCancelled()) {
                                    this.inRange.remove(i);
                                    break;
                                }
                                e = event.getTarget();
                                this.power = event.getPower();
                                ArmorStandProjectile.this.entitySpell.castAtEntity(this.caster, e, this.power);
                                ArmorStandProjectile.access$1(ArmorStandProjectile.this, EffectPosition.TARGET, (Entity)e);
                            }
                            else if (ArmorStandProjectile.this.entitySpell != null && ArmorStandProjectile.this.entitySpell.isTargetedLocationSpell()) {
                                ArmorStandProjectile.this.entitySpell.castAtLocation(this.caster, this.currentLocation.clone(), this.power);
                                ArmorStandProjectile.access$0(ArmorStandProjectile.this, EffectPosition.TARGET, this.currentLocation);
                            }
                            this.inRange.remove(i);
                            this.maxHitLimit.add(e);
                            this.immune.put(e, System.currentTimeMillis());
                            if (ArmorStandProjectile.this.maxEntitiesHit > 0 && this.maxHitLimit.size() >= ArmorStandProjectile.this.maxEntitiesHit) {
                                this.stop();
                                break;
                            }
                            break;
                        }
                    }
                }
                if (this.immune == null || this.immune.isEmpty()) {
                    return;
                }
                final Iterator<Map.Entry<LivingEntity, Long>> iter = this.immune.entrySet().iterator();
                while (iter.hasNext()) {
                    final Map.Entry<LivingEntity, Long> entry = iter.next();
                    if (entry.getValue() < System.currentTimeMillis() - 2000L) {
                        iter.remove();
                        this.inRange.add(entry.getKey());
                    }
                }
            }
        }
        
        public void stop() {
            ArmorStandProjectile.access$0(ArmorStandProjectile.this, EffectPosition.DELAYED, this.currentLocation);
            MagicSpells.cancelTask(this.taskId);
            this.caster = null;
            this.startLocation = null;
            this.previousLocation = null;
            this.currentLocation = null;
            this.currentVelocity = null;
            this.maxHitLimit.clear();
            this.maxHitLimit = null;
            this.immune.clear();
            this.immune = null;
            if (this.inRange == null) {
                return;
            }
            this.inRange.clear();
            this.inRange = null;
            if (ArmorStandProjectile.this.linger > 0) {
                new ProjectileTracker.ArmorStandProjectile$ProjectileTracker$1(this).runTaskLater((Plugin)MagicSpells.getInstance(), (long)ArmorStandProjectile.this.linger);
            }
        }
    }
}
