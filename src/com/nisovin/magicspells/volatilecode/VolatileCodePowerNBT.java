package com.nisovin.magicspells.volatilecode;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.EntityUtils;
import me.dpohvar.powernbt.utils.ItemStackUtils;
import me.dpohvar.powernbt.utils.PacketUtils;
import me.dpohvar.powernbt.utils.ReflectionUtils;
import me.dpohvar.powernbt.utils.ReflectionUtils.MethodCondition;
import me.dpohvar.powernbt.utils.ReflectionUtils.RefConstructor;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityOcelot;
import net.minecraft.server.v1_8_R3.EntitySmallFireball;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IAttribute;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BoundingBox;
import com.nisovin.magicspells.util.DisguiseManager;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.VolatileReferenceHelper;

public class VolatileCodePowerNBT implements VolatileCodeHandle {

	NBTManager nbt = PowerNBT.getApi();
	PacketUtils packetUtil = PacketUtils.packetUtils;
	EntityUtils entityUtil = EntityUtils.entityUtils;

	EntityInsentient bossBarEntity;
	VolatileCodeDisabled fallback = new VolatileCodeDisabled();

	private static ItemStack setTag(ItemStack item, NBTCompound tag) {
		return setTag(item, tag);
	}

	public VolatileCodePowerNBT() {
		try {

			packet63Fields[0] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("a");
			packet63Fields[1] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("b");
			packet63Fields[2] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("c");
			packet63Fields[3] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("d");
			packet63Fields[4] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("e");
			packet63Fields[5] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("f");
			packet63Fields[6] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("g");
			packet63Fields[7] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("h");
			packet63Fields[8] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("i");
			packet63Fields[9] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("j");
			packet63Fields[10] = VolatileReferenceHelper.packetPlayOutWorldParticlesClass.getRealClass().getDeclaredField("k");
			for (int i = 0; i <= 10; i++) {
				packet63Fields[i].setAccessible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (EnumParticle particle : EnumParticle.values()) {
			if (particle != null) {
				particleMap.put(particle.b(), particle);
			}
		}

		bossBarEntity = new EntityWither(((CraftWorld)Bukkit.getWorlds().get(0)).getHandle());
		bossBarEntity.setCustomNameVisible(false);
		bossBarEntity.getDataWatcher().watch(0, (Byte)(byte)0x20);
		bossBarEntity.getDataWatcher().watch(20, (Integer)0);

	}

	@Override
	public void addPotionGraphicalEffect(LivingEntity entity, int color, int duration) {
		
		final EntityLiving el = (EntityLiving) entityUtil.getHandleEntity(entity);
		final DataWatcher dw = (DataWatcher) getEntityDataWatcher(el);
		//nms DataWatcher.watch( int, Integer)
		dw.watch(7, Integer.valueOf(color));

		if (duration > 0) {
			MagicSpells.scheduleDelayedTask(new Runnable() {
				public void run() {
					int c = 0;
					if (!el.effects.isEmpty()) {
						//c = net.minecraft.server.v1_8_R3.PotionBrewer.a(Collection<MobEffect>);
						c = net.minecraft.server.v1_8_R3.PotionBrewer.a(el.effects.values());
					}
					dw.watch(7, Integer.valueOf(c));
				}
			}, duration);
		}
	}
	
	private Object getEntityDataWatcher(Object entity) {
		if (!VolatileReferenceHelper.nmsEntityClass.isInstance(entity)) return null;
		try {
			return VolatileReferenceHelper.nmsEntityClass.getMethod("getDataWatcher", null).getRealMethod().invoke(entity, null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void entityPathTo(LivingEntity creature, LivingEntity target) {
		//EntityCreature entity = ((CraftCreature)creature).getHandle();
		//entity.pathEntity = entity.world.findPath(entity, ((CraftLivingEntity)target).getHandle(), 16.0F, true, false, false, false);
	}

	@Override
	public void sendFakeSlotUpdate(Player player, int slot, ItemStack item) {
		Object nmsItem;
		if (item != null) {
			nmsItem = ItemStackUtils.itemStackUtils.createNmsItemStack(item);
		} else {
			nmsItem = null;
		}
		RefConstructor<?> constructor = VolatileReferenceHelper.packetPlayOutSetSlotClass.getConstructor(int.class, int.class, VolatileReferenceHelper.nmsItemStackClass.getRealClass());
		Object packet = constructor.create(0, (short)slot+36, nmsItem);
		packetUtil.sendPacket(player, packet);
	}

	@Override
	public void toggleLeverOrButton(Block block) {
		// TODO: fix this
		fallback.toggleLeverOrButton(block);
		//net.minecraft.server.v1_8_R3.Block.getById(block.getType().getId()).interact(((CraftWorld)block.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()), null, 0, 0, 0, 0);
	}

	@Override
	public void pressPressurePlate(Block block) {
		fallback.pressPressurePlate(block);
		// TODO: fix this
		//block.setData((byte) (block.getData() ^ 0x1));
		//net.minecraft.server.v1_8_R3.World w = ((CraftWorld)block.getWorld()).getHandle();
		//w.applyPhysics(block.getX(), block.getY(), block.getZ(), net.minecraft.server.v1_8_R3.Block.getById(block.getType().getId()));
		//w.applyPhysics(block.getX(), block.getY()-1, block.getZ(), net.minecraft.server.v1_8_R3.Block.getById(block.getType().getId()));
	}

	@Override
	public boolean simulateTnt(Location target, LivingEntity source, float explosionSize, boolean fire) {
		//nms World, double, double, double, nms EntityLiving
		//VolatileReferenceHelper.nmsWorldClass.getRealClass(), double.class, double.class, VolatileReferenceHelper.entityLivingClass.getRealClass()
		RefConstructor<?> primedTntConstructor = VolatileReferenceHelper.entityTNTPrimedClass.getConstructor(VolatileReferenceHelper.nmsWorldClass.getRealClass(), double.class, double.class, VolatileReferenceHelper.entityLivingClass.getRealClass());
		Object craftWorldHandle = ReflectionUtils.getRefClass(target.getWorld().getClass()).findMethod(new MethodCondition().withName("getHandle").withReturnType(void.class)).of(target.getWorld()).call(null);
		Object sourceHandle = entityUtil.getHandleEntity(source);
		Object e = primedTntConstructor.create(craftWorldHandle, target.getX(), target.getY(), target.getZ(), sourceHandle);

		//cb entity.CraftTNTPrimed(cb CraftServer, nms EntityTNTPrimed)
		RefConstructor<?> craftTNTPrimedConstructor = VolatileReferenceHelper.craftTNTPrimedClass.getConstructor(VolatileReferenceHelper.craftServerClass.getRealClass(), VolatileReferenceHelper.entityTNTPrimedClass.getRealClass());
		Object c = craftTNTPrimedConstructor.create(Bukkit.getServer(), e);
		ExplosionPrimeEvent event = new ExplosionPrimeEvent((Entity) c, explosionSize, fire);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event.isCancelled();
	}

	@Override
	public boolean createExplosionByPlayer(Player player, Location location, float size, boolean fire, boolean breakBlocks) {
		return !((CraftWorld)location.getWorld()).getHandle().createExplosion(((CraftPlayer)player).getHandle(), location.getX(), location.getY(), location.getZ(), size, fire, breakBlocks).wasCanceled;
	}

	@Override
	public void playExplosionEffect(Location location, float size) {
		//double, double, double, float, List<BlockPosition>, Vec3D
		//TODO make sure it isn't needed to specify List<BlockPosition>.class rather than just List.class
		RefConstructor<?> packetConstructor = VolatileReferenceHelper.packetPlayOutExplosionClass.getConstructor(double.class, double.class, double.class, float.class, List.class, VolatileReferenceHelper.vec3DClass);
		Object packet = packetConstructor.create(location.getX(), location.getY(), location.getZ(), size, new ArrayList(), null);
		for (Player player : location.getWorld().getPlayers()) {
			if (player.getLocation().distanceSquared(location) < 50 * 50) {
				packetUtil.sendPacket(player, packet);
			}
		}
	}

	@Override
	public void setExperienceBar(Player player, int level, float percent) {
		//float, int, int
		RefConstructor<?> packetConstructor = VolatileReferenceHelper.packetPlayOutExperienceClass.getConstructor(float.class, int.class, int.class);
		Object packet = packetConstructor.create(percent, player.getTotalExperience(), level);
		packetUtil.sendPacket(player, packet);
	}

	@Override
	public Fireball shootSmallFireball(Player player) {
		net.minecraft.server.v1_8_R3.World w = ((CraftWorld)player.getWorld()).getHandle();
		Location playerLoc = player.getLocation();
		Vector loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(10));

		double d0 = loc.getX() - playerLoc.getX();
		double d1 = loc.getY() - (playerLoc.getY() + 1.5);
		double d2 = loc.getZ() - playerLoc.getZ();
		EntitySmallFireball entitysmallfireball = new EntitySmallFireball(w, ((CraftPlayer)player).getHandle(), d0, d1, d2);

		entitysmallfireball.locY = playerLoc.getY() + 1.5;
		w.addEntity(entitysmallfireball);

		return (Fireball)entitysmallfireball.getBukkitEntity();
	}

	@Override
	public void setTarget(LivingEntity entity, LivingEntity target) {
		if (entity instanceof Creature) {
			((Creature)entity).setTarget(target);
		}
		((EntityInsentient)((CraftLivingEntity)entity).getHandle()).setGoalTarget(((CraftLivingEntity)target).getHandle());
	}

	@Override
	public void playSound(Location location, String sound, float volume, float pitch) {
		((CraftWorld)location.getWorld()).getHandle().makeSound(location.getX(), location.getY(), location.getZ(), sound, volume, pitch);
	}

	@Override
	public void playSound(Player player, String sound, float volume, float pitch) {
		Location loc = player.getLocation();
		PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, loc.getX(), loc.getY(), loc.getZ(), volume, pitch);
		packetUtil.sendPacket(player, packet);
	}

	@Override
	public ItemStack addFakeEnchantment(ItemStack item) {
		NBTCompound tag = nbt.read(item);		
		if (tag == null) {
			tag = new NBTCompound();
		}
		if (!tag.containsKey("ench")) {
			tag.bind("ench", new NBTList());
		}		
		return setTag(item, tag);
	}

	@Override
	public void setFallingBlockHurtEntities(FallingBlock block, float damage, int max) {
		EntityFallingBlock efb = ((CraftFallingSand)block).getHandle();
		try {
			Field field = VolatileReferenceHelper.entityFallingBlockClass.getRealClass().getDeclaredField("hurtEntities");
			field.setAccessible(true);
			field.setBoolean(efb, true);

			field = VolatileReferenceHelper.entityFallingBlockClass.getRealClass().getDeclaredField("fallHurtAmount");
			field.setAccessible(true);
			field.setFloat(efb, damage);

			field = VolatileReferenceHelper.entityFallingBlockClass.getRealClass().getDeclaredField("fallHurtMax");
			field.setAccessible(true);
			field.setInt(efb, max);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playEntityAnimation(final Location location, final EntityType entityType, final int animationId, boolean instant) {
		final EntityLiving entity;
		if (entityType == EntityType.VILLAGER) {
			entity = new EntityVillager(((CraftWorld)location.getWorld()).getHandle());
		} else if (entityType == EntityType.WITCH) {
			entity = new EntityWitch(((CraftWorld)location.getWorld()).getHandle());
		} else if (entityType == EntityType.OCELOT) {
			entity = new EntityOcelot(((CraftWorld)location.getWorld()).getHandle());
		} else {
			entity = null;
		}
		if (entity == null) return;

		entity.setPosition(location.getX(), instant ? location.getY() : -5, location.getZ());
		((CraftWorld)location.getWorld()).getHandle().addEntity(entity);
		entity.addEffect(new MobEffect(14, 40));
		if (instant) {
			((CraftWorld)location.getWorld()).getHandle().broadcastEntityEffect(entity, (byte)animationId);
			entity.getBukkitEntity().remove();
		} else {
			entity.setPosition(location.getX(), location.getY(), location.getZ());
			MagicSpells.scheduleDelayedTask(new Runnable() {
				public void run() {
					((CraftWorld)location.getWorld()).getHandle().broadcastEntityEffect(entity, (byte)animationId);
					entity.getBukkitEntity().remove();
				}
			}, 8);
		}
	}

	@Override
	public void createFireworksExplosion(Location location, boolean flicker, boolean trail, int type, int[] colors, int[] fadeColors, int flightDuration) {
		fallback.createFireworksExplosion(location, flicker, trail, type, colors, fadeColors, flightDuration);
	}

	Field[] packet63Fields = new Field[11];
	Map<String, EnumParticle> particleMap = new HashMap<String, EnumParticle>();
	@Override
	public void playParticleEffect(Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius, float yOffset) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		EnumParticle particle = particleMap.get(name);
		int[] data = null;
		if (name.contains("_")) {
			String[] split = name.split("_");
			name = split[0] + "_";
			particle = particleMap.get(name);
			if (split.length > 1) {
				String[] split2 = split[1].split(":");
				data = new int[split2.length];
				for (int i = 0; i < data.length; i++) {
					data[i] = Integer.parseInt(split2[i]);
				}
			}
		}
		if (particle == null) {
			MagicSpells.error("Invalid particle: " + name);
			return;
		}
		try {
			packet63Fields[0].set(packet, particle);
			packet63Fields[1].setFloat(packet, (float)location.getX());
			packet63Fields[2].setFloat(packet, (float)location.getY() + yOffset);
			packet63Fields[3].setFloat(packet, (float)location.getZ());
			packet63Fields[4].setFloat(packet, spreadHoriz);
			packet63Fields[5].setFloat(packet, spreadVert);
			packet63Fields[6].setFloat(packet, spreadHoriz);
			packet63Fields[7].setFloat(packet, speed);
			packet63Fields[8].setInt(packet, count);
			packet63Fields[9].setBoolean(packet, radius >= 200);
			if (data != null) {
				packet63Fields[10].set(packet,data);
			}
			int rSq = radius * radius;

			for (Player player : location.getWorld().getPlayers()) {
				if (player.getLocation().distanceSquared(location) <= rSq) {
					packetUtil.sendPacket(player, packet);
				} else {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playDragonDeathEffect(Location location) {
		EntityEnderDragon dragon = new EntityEnderDragon(((CraftWorld)location.getWorld()).getHandle());
		dragon.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0F);

		PacketPlayOutSpawnEntityLiving packet24 = new PacketPlayOutSpawnEntityLiving(dragon);
		PacketPlayOutEntityStatus packet38 = new PacketPlayOutEntityStatus(dragon, (byte)3);
		final PacketPlayOutEntityDestroy packet29 = new PacketPlayOutEntityDestroy(dragon.getBukkitEntity().getEntityId());

		BoundingBox box = new BoundingBox(location, 64);
		final List<Player> players = new ArrayList<Player>();
		for (Player player : location.getWorld().getPlayers()) {
			if (box.contains(player)) {
				players.add(player);
				packetUtil.sendPacket(player, packet24);
				packetUtil.sendPacket(player, packet38);
			}
		}

		MagicSpells.scheduleDelayedTask(new Runnable() {
			public void run() {
				for (Player player : players) {
					if (player.isValid()) {
						packetUtil.sendPacket(player, packet29);
					}
				}
			}
		}, 250);
	}

	@Override
	public void setKiller(LivingEntity entity, Player killer) {
		((CraftLivingEntity)entity).getHandle().killer = ((CraftPlayer)killer).getHandle();
	}

	@Override
	public DisguiseManager getDisguiseManager(MagicConfig config) {
		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			return new DisguiseManager_1_8_R3(config);
		} else {
			return new DisguiseManagerEmpty(config);
		}
	}

	@Override
	public ItemStack addAttributes(ItemStack item, String[] names, String[] types, double[] amounts, int[] operations) {
		NBTCompound tag = nbt.read(item);

		NBTList list = new NBTList();
		for (int i = 0; i < names.length; i++) {
			if (names[i] != null) {
				NBTCompound attr = new NBTCompound();
				attr.put("Name", names[i]);
				attr.put("AttributeName", types[i]);
				attr.put("Amount", amounts[i]);
				attr.put("Operation", operations[i]);
				UUID uuid = UUID.randomUUID();
				attr.put("UUIDLeast", uuid.getLeastSignificantBits());
				attr.put("UUIDMost", uuid.getMostSignificantBits());
				list.add(attr);
			}
		}

		tag.bind("AttributeModifiers", list);

		setTag(item, tag);
		return item;
	}

	@Override
	public ItemStack hideTooltipCrap(ItemStack item) {
		if (!(item instanceof CraftItemStack)) {
			item = CraftItemStack.asCraftCopy(item);
		}
		NBTCompound tag = nbt.read(item);
		if (tag == null) {
			tag = new NBTCompound();
		}
		tag.put("HideFlags", (int)63);
		setTag(item, tag);
		return item;
	}

	@Override
	public void addEntityAttribute(LivingEntity entity, String attribute, double amount, int operation) {
		EntityInsentient nmsEnt = (EntityInsentient) ((CraftLivingEntity)entity).getHandle();
		IAttribute attr = null;
		if (attribute.equals("generic.maxHealth")) {
			attr = GenericAttributes.maxHealth;
		} else if (attribute.equals("generic.followRange")) {
			attr = GenericAttributes.FOLLOW_RANGE;
		} else if (attribute.equals("generic.knockbackResistance")) {
			attr = GenericAttributes.c;
		} else if (attribute.equals("generic.movementSpeed")) {
			attr = GenericAttributes.MOVEMENT_SPEED;
		} else if (attribute.equals("generic.attackDamage")) {
			attr = GenericAttributes.ATTACK_DAMAGE;
		}
		if (attr != null) {
			AttributeInstance attributes = nmsEnt.getAttributeInstance(attr);
			attributes.b(new AttributeModifier("MagicSpells " + attribute, amount, operation));
		}
	}

	@Override
	public void resetEntityAttributes(LivingEntity entity) {
		try {
			EntityLiving e = ((CraftLivingEntity)entity).getHandle();
			Field field = VolatileReferenceHelper.entityLivingClass.getRealClass().getDeclaredField("c");
			field.setAccessible(true);
			field.set(e, null);
			e.getAttributeMap();
			Method method = null;
			Class<?> clazz = e.getClass();
			while (clazz != null) {
				try {
					method = clazz.getDeclaredMethod("aW");
					break;
				} catch (NoSuchMethodException e1) {
					clazz = clazz.getSuperclass();
				}
			}
			if (method != null) {
				method.setAccessible(true);
				method.invoke(e);
			} else {
				throw new Exception("No method aW found on " + e.getClass().getName());
			}
		} catch (Exception e) {
			MagicSpells.handleException(e);
		}		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removeAI(LivingEntity entity) {
		try {
			//EntityInsentient ev = entity.getHandle()
			Object ev = entityUtil.getHandleEntity(entity);

			Field goalsField = VolatileReferenceHelper.entityInsentientClass.getRealClass().getDeclaredField("goalSelector");
			goalsField.setAccessible(true);

			//PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get(ev)
			Object goals = goalsField.get(ev);

			Field listField = VolatileReferenceHelper.pathfinderGoalSelectorClass.getRealClass().getDeclaredField("b");
			listField.setAccessible(true);
			List list = (List)listField.get(goals);
			list.clear();
			listField = VolatileReferenceHelper.pathfinderGoalSelectorClass.getRealClass().getDeclaredField("c");
			listField.setAccessible(true);
			list = (List)listField.get(goals);
			list.clear();

			//PathfinderGoalFloat newGoal = new PathfinderGoalFloat( nms EntityInsentient)
			Object newGoal = VolatileReferenceHelper.pathfinderGoalFloatClass.getConstructor(VolatileReferenceHelper.entityInsentientClass.getRealClass()).create(ev);

			//PathfinderGoalSelector.a(int, nms PathfinderGoal)
			VolatileReferenceHelper.pathfinderGoalSelectorClass.getMethod("a", int.class, VolatileReferenceHelper.pathfinderGoalFloatClass.getRealClass()).getRealMethod().invoke(goals, 0, newGoal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addAILookAtPlayer(LivingEntity entity, int range) {
		try {
			//EntityInsentient ev
			Object ev = entityUtil.getHandleEntity(entity);

			Field goalsField = VolatileReferenceHelper.entityInsentientClass.getRealClass().getDeclaredField("goalSelector");
			goalsField.setAccessible(true);

			//PathfinderGoalSelector goals
			Object goals = goalsField.get(ev);
			//PathfinderGoalLookAtPlayer goal = new PathfinderGoalLookAtPlayer(nms EntityInsentient, Class<? extends nms Entity>, float, float)
			RefConstructor<?> goalConstructor = VolatileReferenceHelper.pathfinderGoalLookAtPlayerClass.getConstructor(VolatileReferenceHelper.entityInsentientClass.getRealClass(), Class.class, float.class, float.class);
			Object goal = goalConstructor.create(ev, VolatileReferenceHelper.entityHumanClass.getRealClass(), range, 1.0F);

			//int, nms PathfinderGoal
			VolatileReferenceHelper.pathfinderGoalSelectorClass.getMethod("a", int.class, VolatileReferenceHelper.pathfinderGoalLookAtPlayerClass.getRealClass()).getRealMethod().invoke(goals, 1, goal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBossBar(Player player, String title, double percent) {
		updateBossBarEntity(player, title, percent);


		RefConstructor<?> destroyPacketConstructor = VolatileReferenceHelper.packetPlayOutEntityDestroyClass.getConstructor(int[].class);
		Object packetDestroy = destroyPacketConstructor.create(bossBarEntity.getId());
		packetUtil.sendPacket(player, packetDestroy);

		RefConstructor<?> packetSpawnConstructor = VolatileReferenceHelper.packetPlayOutSpawnEntityLivingClass.getConstructor(VolatileReferenceHelper.nmsEntityClass);
		Object packetSpawn = packetSpawnConstructor.create(bossBarEntity);
		packetUtil.sendPacket(player, packetSpawn);

		RefConstructor<?> packetTeleportConstructor = VolatileReferenceHelper.packetPlayOutEntityTeleportClass.getConstructor(VolatileReferenceHelper.nmsEntityClass);
		Object packetTeleport = packetTeleportConstructor.create(bossBarEntity);
		packetUtil.sendPacket(player, packetTeleport);

		//PacketPlayOutEntityVelocity packetVelocity = new PacketPlayOutEntityVelocity(bossBarEntity.getId(), 1, 0, 1);		
		//((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetVelocity);
	}

	@Override
	public void updateBossBar(Player player, String title, double percent) {
		updateBossBarEntity(player, title, percent);

		if (title != null) {
			//int, DataWatcher, Boolean
			RefConstructor<?> packetDataConstructor = VolatileReferenceHelper.packetPlayOutEntityMetadataClass.getConstructor(int.class, VolatileReferenceHelper.dataWatcherClass.getRealClass(), boolean.class);
			Object packetData = packetDataConstructor.create(bossBarEntity.getId(), bossBarEntity.getDataWatcher(), true);
			packetUtil.sendPacket(player, packetData);
		}

		//nms Entity
		RefConstructor<?> packetTeleportConstructor = VolatileReferenceHelper.packetPlayOutEntityTeleportClass.getConstructor(VolatileReferenceHelper.nmsEntityClass);
		Object packetTeleport = packetTeleportConstructor.create(bossBarEntity);
		packetUtil.sendPacket(player, packetTeleport);

		//PacketPlayOutEntityVelocity packetVelocity = new PacketPlayOutEntityVelocity(bossBarEntity.getId(), 1, 0, 1);
		//((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetVelocity);
	}

	private void updateBossBarEntity(Player player, String title, double percent) {
		if (title != null) {
			if (percent <= 0.01) percent = 0.01D;
			bossBarEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', title));
			bossBarEntity.getDataWatcher().watch(6, (float)(percent * 300f));
		}

		Location l = player.getLocation();
		l.setPitch(l.getPitch() + 10);
		Vector v = l.getDirection().multiply(20);
		Util.rotateVector(v, 15);
		l.add(v);
		bossBarEntity.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
	}

	@Override
	public void removeBossBar(Player player) {
		//constructor parameter type actually was 'int...'
		RefConstructor<?> packetConstructor = VolatileReferenceHelper.packetPlayOutEntityDestroyClass.getConstructor(int[].class);
		Object packetDestroy = packetConstructor.create(bossBarEntity.getId());
		packetUtil.sendPacket(player, packetDestroy);
	}

	@Override
	public void saveSkinData(Player player, String name) {
		GameProfile profile = ((CraftPlayer)player).getHandle().getProfile();
		Collection<Property> props = profile.getProperties().get("textures");
		for (Property prop : props) {
			String skin = prop.getValue();
			String sig = prop.getSignature();

			File folder = new File(MagicSpells.getInstance().getDataFolder(), "disguiseskins");
			if (!folder.exists()) {
				folder.mkdir();
			}
			File skinFile = new File(folder, name + ".skin.txt");
			File sigFile = new File(folder, name + ".sig.txt");
			try {
				FileWriter writer = new FileWriter(skinFile);
				writer.write(skin);
				writer.flush();
				writer.close();
				writer = new FileWriter(sigFile);
				writer.write(sig);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}
	}

	@Override
	public ItemStack setUnbreakable(ItemStack item) {
		NBTCompound tag = nbt.read(item);
		if (tag == null) {
			tag = new NBTCompound();
		}
		tag.put("Unbreakable", (byte)1);
		nbt.write(item, tag);
		return item;
	}

	@Override
	public void setArrowsStuck(LivingEntity entity, int count) {
		// TODO: fix this
		//((CraftLivingEntity)entity).getHandle().a(count);
	}

	@Override
	public void sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
		packetUtil.sendPacket(player, packet);
		if (title != null) {
			//packet = new PacketPlayOutTitle(EnumtitleAction.TITLE, new ChatComponentText(title))
			packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, new ChatComponentText(title));
			packetUtil.sendPacket(player, packet);
		}
		if (subtitle != null) {
			//packet = new PacketPlayOutTitle(EnumtitleAction.SUBTITLE, new ChatComponentText(subtitle))
			packet = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
			packetUtil.sendPacket(player, packet);
		}
	}

	@Override
	public void sendActionBarMessage(Player player, String message) {
		RefConstructor<?> constructor = VolatileReferenceHelper.packetPlayOutChatClass.getConstructor(VolatileReferenceHelper.iChatBaseComponentClass.getRealClass(), byte.class);
		Object packet = constructor.create(makeChatComponentText(message), (byte)2);
		packetUtil.sendPacket(player, packet);
	}

	@Override
	public void setTabMenuHeaderFooter(Player player, String header, String footer) {
		try {
			Object packet = VolatileReferenceHelper.packetPlayOutPlayerListHeaderFooterClass.getRealClass().newInstance();
			Field field1 = VolatileReferenceHelper.packetPlayOutPlayerListHeaderFooterClass.getRealClass().getDeclaredField("a");
			Field field2 = VolatileReferenceHelper.packetPlayOutPlayerListHeaderFooterClass.getRealClass().getDeclaredField("b");
			field1.setAccessible(true);
			field1.set(packet, makeChatComponentText(header));
			field2.setAccessible(true);
			field2.set(packet, makeChatComponentText(footer));
			packetUtil.sendPacket(player, packet);
		} catch (Exception e) {
			MagicSpells.handleException(e);
		}
	}

	private static Object makeChatComponentText(String content) {
		RefConstructor<?> constructor = VolatileReferenceHelper.chatComponentTextClass.getConstructor(String.class);
		return constructor.create(content);
	}

}
