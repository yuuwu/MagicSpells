package com.nisovin.magicspells.util;

import me.dpohvar.powernbt.utils.ReflectionUtils;
import me.dpohvar.powernbt.utils.ReflectionUtils.RefClass;

public class VolatileReferenceHelper {

	//nms PacketPlayOutWorldParticles
	public static RefClass<?> packetPlayOutWorldParticlesClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutWorldParticles");

	//nms PacketPlayOutPlayerListHeaderFooter
	public static RefClass<?> packetPlayOutPlayerListHeaderFooterClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutPlayerListHeaderFooter");

	//nms PacketPlayOutChat
	public static RefClass<?> packetPlayOutChatClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutChat");

	//nms PacketPlayOutTitle
	public static RefClass<?> packetPlayOutTitleClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutTitle");

	//nms PacketPlayOutEntityDestroy
	public static RefClass<?> packetPlayOutEntityDestroyClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutEntityDestroy");

	//nms PacketPlayOutEntityTeleport
	public static RefClass<?> packetPlayOutEntityTeleportClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutEntityTeleport");

	//nms PacketPlayOutEntityMetadata
	public static RefClass<?> packetPlayOutEntityMetadataClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutEntityMetadata");

	//nms PacketPlayOutSpawnEntityLiving
	public static RefClass<?> packetPlayOutSpawnEntityLivingClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutSpawnEntityLiving");

	//nms EnumTitleAction (enum in PacketPlayOutTitle)

	//nms PacketPlayOutSetSlot
	public static RefClass<?> packetPlayOutSetSlotClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutSetSlot");

	//nms PacketPlayOutEntityStatus
	public static RefClass<?> packetPlayOutEntityStatusClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutEntityStatus");

	//nms PacketPlayOutExperience
	public static RefClass<?> packetPlayOutExperienceClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutExperience");

	//nms PacketPlayOutExplosion
	public static RefClass<?> packetPlayOutExplosionClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutExplosion");

	//nms PacketPlayOutNamedSoundEffect
	public static RefClass<?> packetPlayOutNamedSoundEffectClass = ReflectionUtils.getRefClass("{nms}.PacketPlayOutNamedSoundEffect");

	//nms EntityLiving
	public static RefClass<?> entityLivingClass = ReflectionUtils.getRefClass("{nms}.EntityLiving");

	//nms EntityInsentient
	public static RefClass<?> entityInsentientClass = ReflectionUtils.getRefClass("{nms}.EntityInsentient");

	//nms PathfinderGoalSelector
	public static RefClass<?> pathfinderGoalSelectorClass = ReflectionUtils.getRefClass("{nms}.PathfinderGoalSelector");

	//nms ChatComponentText
	public static RefClass<?> chatComponentTextClass = ReflectionUtils.getRefClass("{nms}.ChatComponentText");
	
	//nms IChatBaseComponent
	public static RefClass<?> iChatBaseComponentClass = ReflectionUtils.getRefClass("{nms}.IChatBaseComponent");
	
	//nms PathfinderGoalLookAtPlayer
	public static RefClass<?> pathfinderGoalLookAtPlayerClass = ReflectionUtils.getRefClass("{nms}.PathfinderGoalLookAtPlayer");

	//nms PathfinderGoalFloat
	public static RefClass<?> pathfinderGoalFloatClass = ReflectionUtils.getRefClass("{nms}.PathfinderGoalFloat");

	//nms EnumParticle (an enum)
	public static RefClass<?> enumParticleClass = ReflectionUtils.getRefClass("{nms}.EnumParticle");

	//nms EntityWither
	public static RefClass<?> entityWitherClass = ReflectionUtils.getRefClass("{nms}.EntityWither");

	//nms EntityWitch
	public static RefClass<?> entityWitchClass = ReflectionUtils.getRefClass("{nms}.EntityWitch");

	//nms AttributeInstance (interface)
	public static RefClass<?> attributeInstanceClass = ReflectionUtils.getRefClass("{nms}.AttributeInstance");

	//nms AttributeModifier
	public static RefClass<?> attributeModifierClass = ReflectionUtils.getRefClass("{nms}.AttributeModifier");

	//nms DataWatcher
	public static RefClass<?> dataWatcherClass = ReflectionUtils.getRefClass("{nms}.DataWatcher");

	//nms EntityEnderDragon
	public static RefClass<?> entityEnderDragonClass = ReflectionUtils.getRefClass("{nms}.EntityEnderDragon");

	//nms EntityHuman
	public static RefClass<?> entityHumanClass = ReflectionUtils.getRefClass("{nms}.EntityHuman");

	//nms EntityOcelot
	public static RefClass<?> entityOcelotClass = ReflectionUtils.getRefClass("{nms}.EntityOcelot");

	//nms MobEffect
	public static RefClass<?> mobEffectClass = ReflectionUtils.getRefClass("{nms}.MobEffect");

	//nms EntityFallingBlock
	public static RefClass<?> entityFallingBlockClass = ReflectionUtils.getRefClass("{nms}.EntityFallingBlock");

	//nms EntitySmallFireball
	public static RefClass<?> entitySmallFireballClass = ReflectionUtils.getRefClass("{nms}.EntitySmallFireball");

	//nms EntityTNTPrimed
	public static RefClass<?> entityTNTPrimedClass = ReflectionUtils.getRefClass("{nms}.EntityTNTPrimed");

	//nms EntityVillager
	public static RefClass<?> entityVillagerClass = ReflectionUtils.getRefClass("{nms}.EntityVillager");

	//nms GenericAttributes
	public static RefClass<?> genericAttributesClass = ReflectionUtils.getRefClass("{nms}.GenericAttributes");

	//nms IAttribute (interface)
	public static RefClass<?> iAttributeClass = ReflectionUtils.getRefClass("{nms}.IAttribute");
	
	//cb CraftServer
	public static RefClass<?> craftServerClass = ReflectionUtils.getRefClass("{cb}.CraftServer");
	
	//cb CraftWorld
	public static RefClass<?> craftWorldClass = ReflectionUtils.getRefClass("{cb}.CraftWorld");
	
	//cb entity.CraftFallingSand
	public static RefClass<?> craftFallingSandClass = ReflectionUtils.getRefClass("{cb}.entity.CraftFallingSand");
	
	//cb entity.CraftLivingEntity
	public static RefClass<?> craftLivingEntityClass = ReflectionUtils.getRefClass("{cb}.entity.CraftLivingEntity");
	
	//cb entity.CraftPlayer
	public static RefClass<?> craftPlayerClass = ReflectionUtils.getRefClass("{cb}.entity.CraftPlayer");
	
	//cb entity.CraftTNTPrimed
	public static RefClass<?> craftTNTPrimedClass = ReflectionUtils.getRefClass("{cb}.entity.CraftTNTPrimed");
	
	//cb inventory.CraftItemStack
	public static RefClass<?> craftItemStackClass = ReflectionUtils.getRefClass("{cb}.inventoy.CraftItemStack");
	
	//nms ItemStack
	public static RefClass<?> nmsItemStackClass = ReflectionUtils.getRefClass("{nms}.ItemStack");
	
	//nms World
	public static RefClass<?> nmsWorldClass = ReflectionUtils.getRefClass("{nms}.World");
	
	//nms Entity
	public static RefClass<?> nmsEntityClass = ReflectionUtils.getRefClass("{nms}.Entity");
	
	//nms Vec3D
	public static RefClass<?> vec3DClass = ReflectionUtils.getRefClass("{nms}.Vec3D");
}
