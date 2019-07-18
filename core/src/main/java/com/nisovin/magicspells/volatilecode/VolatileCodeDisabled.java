package com.nisovin.magicspells.volatilecode;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Fireball;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.SmallFireball;
import org.bukkit.block.data.Powerable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;

import net.minecraft.server.v1_13_R2.*;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.BoundingBox;
import com.nisovin.magicspells.util.DisguiseManager;

public class VolatileCodeDisabled implements VolatileCodeHandle {

	List<EntityFireworks> fireworks = new ArrayList<>();

	@Override
	public void addPotionGraphicalEffect(LivingEntity entity, int color, int duration) {
		// Need the volatile code for this
	}

	@Override
	public void entityPathTo(LivingEntity entity, LivingEntity target) {
		// Need the volatile code for this
	}

	@Override
	public void creaturePathToLoc(Creature creature, Location loc, float speed) {
		EntityCreature entity = ((CraftCreature) creature).getHandle();
		PathEntity pathEntity = entity.getNavigation().a(loc.getX(), loc.getY(), loc.getZ());
		entity.getNavigation().a(pathEntity, speed);
	}

	@Override
	public void sendFakeSlotUpdate(Player player, int slot, ItemStack item) {
		net.minecraft.server.v1_13_R2.ItemStack nmsItem;
		if (item != null) nmsItem = CraftItemStack.asNMSCopy(item);
		else nmsItem = null;

		PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, (short) slot + 36, nmsItem);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public void toggleLeverOrButton(Block block) {
		Powerable powerable = ((Powerable) block.getBlockData());
		powerable.setPowered(true);
		block.setBlockData(powerable, true);
	}

	@Override
	public void pressPressurePlate(Block block) {
		if (block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE || block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			AnaloguePowerable powerable = ((AnaloguePowerable) block.getBlockData());
			powerable.setPower(powerable.getMaximumPower());
			block.setBlockData(powerable, true);
			return;
		}
		Powerable powerable = ((Powerable) block.getBlockData());
		powerable.setPowered(true);
		block.setBlockData(powerable, true);
	}

	@Override
	public boolean simulateTnt(Location target, LivingEntity source, float explosionSize, boolean fire) {
		return false;
	}

	@Override
	public boolean createExplosionByPlayer(Player player, Location location, float size, boolean fire, boolean breakBlocks) {
		return location.getWorld().createExplosion(location, size, fire);
	}

	@Override
	public void playExplosionEffect(Location location, float size) {
		location.getWorld().createExplosion(location, 0F);
	}

	@Override
	public void setExperienceBar(Player player, int level, float percent) {
		PacketPlayOutExperience packet = new PacketPlayOutExperience(percent, player.getTotalExperience(), level);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public Fireball shootSmallFireball(Player player) {
		return player.launchProjectile(SmallFireball.class);
	}

	@Override
	public void setTarget(LivingEntity entity, LivingEntity target) {
		if (entity instanceof Creature) ((Creature) entity).setTarget(target);
	}

	@Override
	public void playSound(Location location, String sound, float volume, float pitch) {
		for (Player player : location.getWorld().getPlayers()) {
			player.playSound(location, sound, volume, pitch);
		}
	}

	@Override
	public void playSound(Player player, String sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}

	@Override
	public ItemStack addFakeEnchantment(ItemStack item) {
		return item;
	}

	@Override
	public void setFallingBlockHurtEntities(FallingBlock block, float damage, int max) {
		// Need the volatile code for this
	}

	@Override
	public void createFireworksExplosion(Location location, boolean flicker, boolean trail, int type, int[] colors, int[] fadeColors, int flightDuration) {
		if (flightDuration > 50) flightDuration = 50;

		ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);

		net.minecraft.server.v1_13_R2.ItemStack itemStack = CraftItemStack.asNMSCopy(firework);

		NBTTagCompound tag = itemStack.getTag();
		if (tag == null) tag = new NBTTagCompound();

		NBTTagCompound expTag = new NBTTagCompound();
		expTag.setByte("Flicker", flicker ? (byte) 1 : (byte) 0);
		expTag.setByte("Trail", trail ? (byte) 1 : (byte) 0);
		expTag.setByte("Type", (byte) type);
		expTag.setIntArray("Colors", colors);
		expTag.setIntArray("FadeColors", fadeColors);

		NBTTagCompound fwTag = new NBTTagCompound();
		fwTag.setByte("Flight", (byte) 3);
		NBTTagList expList = new NBTTagList();
		expList.add(expTag);
		fwTag.set("Explosions", expList);
		tag.set("Fireworks", fwTag);

		itemStack.setTag(tag);

		WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

		EntityFireworks entity = new EntityFireworks(world, location.getX(), location.getY(), location.getZ(), itemStack);
		world.addEntity(entity);
		fireworks.add(entity);

		if (flightDuration == 0) {
			world.broadcastEntityEffect(entity, (byte) 17);
			fireworks.remove(entity);
			entity.die();
		} else {
			MagicSpells.scheduleDelayedTask(() -> {
				world.broadcastEntityEffect(entity, (byte) 17);
				fireworks.remove(entity);
				entity.die();
			}, flightDuration);
		}

		/*FireworkEffect.Type t = Type.BALL;
		if (type == 1) t = Type.BALL_LARGE;
		else if (type == 2) t = Type.STAR;
		else if (type == 3) t = Type.CREEPER;
		else if (type == 4) t = Type.BURST;

		Color[] c1 = new Color[colors.length];
		for (int i = 0; i < colors.length; i++) {
			c1[i] = Color.fromRGB(colors[i]);
		}
		Color[] c2 = new Color[fadeColors.length];
		for (int i = 0; i < fadeColors.length; i++) {
			c2[i] = Color.fromRGB(fadeColors[i]);
		}
		FireworkEffect effect = FireworkEffect.builder()
				.flicker(flicker)
				.trail(trail)
				.with(t)
				.withColor(c1)
				.withFade(c2)
				.build();
		Firework firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		meta.setPower(0);
		firework.setFireworkMeta(meta);
		firework.setSilent(true);
		MagicSpells.scheduleDelayedTask(() -> {
			if (firework == null) return;
			if (!firework.isValid()) return;
			if (firework.isDead()) return;
			firework.detonate();
		}, flightDuration); */
	}

	@Override
	public void playParticleEffect(Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius, float yOffset) {
		// Can't do this without the volatile code
	}

	@Override
	public void playParticleEffect(Location location, String name, float spreadX, float spreadY, float spreadZ, float speed, int count, int radius, float yOffset) {
		// Need volatile code
	}

	@Override
	public void playDragonDeathEffect(Location location) {
		EntityEnderDragon dragon = new EntityEnderDragon(((CraftWorld) location.getWorld()).getHandle());
		dragon.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0F);

		PacketPlayOutSpawnEntityLiving packet24 = new PacketPlayOutSpawnEntityLiving(dragon);
		PacketPlayOutEntityStatus packet38 = new PacketPlayOutEntityStatus(dragon, (byte) 3);
		final PacketPlayOutEntityDestroy packet29 = new PacketPlayOutEntityDestroy(dragon.getBukkitEntity().getEntityId());

		BoundingBox box = new BoundingBox(location, 64);
		final List<Player> players = new ArrayList<>();
		for (Player player : location.getWorld().getPlayers()) {
			if (!box.contains(player)) continue;
			players.add(player);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet24);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet38);
		}

		MagicSpells.scheduleDelayedTask(() -> {
			for (Player player : players) {
				if (!player.isValid()) continue;
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet29);
			}
		}, 250);
	}

	@Override
	public void setKiller(LivingEntity entity, Player killer) {
		((CraftLivingEntity) entity).getHandle().killer = ((CraftPlayer) killer).getHandle();
	}

	@Override
	public DisguiseManager getDisguiseManager(MagicConfig config) {
		return null;
	}

	@Override
	public ItemStack addAttributes(ItemStack item, String[] names, String[] types, double[] amounts, int[] operations, String[] slots) {
		return item;
	}

	@Override
	public void removeAI(LivingEntity entity) {
		// Need the volatile code for this
	}

	@Override
	public void addEntityAttribute(LivingEntity entity, String attribute, double amount, int operation) {
		// Need the volatile code for this
	}

	@Override
	public void addAILookAtPlayer(LivingEntity entity, int range) {
		try {
			EntityInsentient ev = (EntityInsentient) ((CraftLivingEntity) entity).getHandle();

			Field goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
			goalsField.setAccessible(true);
			PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get(ev);

			goals.a(1, new PathfinderGoalLookAtPlayer(ev, EntityHuman.class, range, 1.0F));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveSkinData(Player player, String name) {
		// Need the volatile code for this
	}

	@Override
	public ItemStack setUnbreakable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public void sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
		connection.sendPacket(packet);
		if (title != null) {
			packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title));
			connection.sendPacket(packet);
		}
		if (subtitle != null) {
			packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
			connection.sendPacket(packet);
		}
	}

	@Override
	public void sendActionBarMessage(Player player, String message) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.GAME_INFO);
		connection.sendPacket(packet);
	}

	@Override
	public ItemStack hideTooltipCrap(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public void setClientVelocity(Player player, Vector velocity) {
		PacketPlayOutEntityVelocity packet = new PacketPlayOutEntityVelocity(player.getEntityId(), velocity.getX(), velocity.getY(), velocity.getZ());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public double getAbsorptionHearts(LivingEntity entity) {
		return ((CraftLivingEntity) entity).getHandle().getAbsorptionHearts();
	}

	@Override
	public void showItemCooldown(Player player, ItemStack item, int duration) {
		// Need volatile code for this
	}

	@Override
	public void setTexture(SkullMeta meta, String texture, String signature) {
		// Need volatile code for this
	}

	@Override
	public void setSkin(Player player, String skin, String signature) {
		// Need volatile code for this
	}

	@Override
	public void setTexture(SkullMeta meta, String texture, String signature, String uuid, String name) {
		// Need volatile code for this
	}

	public void turnOff() {
		for (EntityFireworks entity : fireworks) {
			entity.die();
		}

		fireworks.clear();
	}

}
