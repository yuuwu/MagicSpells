package com.nisovin.magicspells.util;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.NetherWarts;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;

public class BlockUtils {
	
	private static HashMap<NetherWartsState, Integer> wartStateToInt = new HashMap<>();
	private static HashMap<Integer, NetherWartsState> intToWartState = new HashMap<>();
	
	static {
		wartStateToInt.put(NetherWartsState.SEEDED, 1);
		wartStateToInt.put(NetherWartsState.STAGE_ONE, 2);
		wartStateToInt.put(NetherWartsState.STAGE_TWO, 3);
		wartStateToInt.put(NetherWartsState.RIPE, 4);
		
		intToWartState.put(1, NetherWartsState.SEEDED);
		intToWartState.put(2, NetherWartsState.STAGE_ONE);
		intToWartState.put(3, NetherWartsState.STAGE_TWO);
		intToWartState.put(4, NetherWartsState.RIPE);
	}
	
	public static boolean isTransparent(Spell spell, Block block) {
		return spell.getLosTransparentBlocks().contains(block.getType());
	}
	
	public static Block getTargetBlock(Spell spell, LivingEntity entity, int range) {
		try {
			if (spell != null) return entity.getTargetBlock(spell.getLosTransparentBlocks(), range);
			return entity.getTargetBlock(MagicSpells.getTransparentBlocks(), range);				
		} catch (IllegalStateException e) {
			DebugHandler.debugIllegalState(e);
			return null;
		}
	}
	
	public static List<Block> getLastTwoTargetBlock(Spell spell, LivingEntity entity, int range) {
		try {
			return entity.getLastTwoTargetBlocks(spell.getLosTransparentBlocks(), range);
		} catch (IllegalStateException e) {
			DebugHandler.debugIllegalState(e);
			return null;
		}
	}
	
	public static void setTypeAndData(Block block, Material material, byte data, boolean physics) {
		//block.setTypeIdAndData(material.getId(), data, physics);
		block.setType(material);
		block.setBlockData((BlockData) material.getNewData(data));
		// TODO see if the physics thing can be applied still
	}
	
	public static void setBlockFromFallingBlock(Block block, FallingBlock fallingBlock, boolean physics) {
		//block.setTypeIdAndData(fallingBlock.getBlockId(), fallingBlock.getBlockData(), physics);
		// TODO test and figure out physics
		BlockData blockData = fallingBlock.getBlockData();
		block.setType(blockData.getMaterial());
		block.setBlockData(blockData);
	}
	
	public static int getWaterLevel(Block block) {
		return block.getData();
	}
	
	public static int getGrowthLevel(Block block) {
		return block.getData();
	}
	
	public static void setGrowthLevel(Block block, int level) {
		//block.setData((byte)level);
		block.setBlockData((BlockData) block.getBlockData().getMaterial().getNewData((byte) level));
	}
	
	public static boolean growWarts(NetherWarts wart, int stagesToGrow) {
		if (wart.getState() == NetherWartsState.RIPE) return false;
		int state = wartStateToInt.get(wart.getState());
		state= Math.min(state+stagesToGrow, 4);
		wart.setState(intToWartState.get(state));
		return true;
		
	}
	
	public static int getWaterLevel(BlockState blockState) {
		return blockState.getRawData();
	}
	
	public static boolean isPathable(Block block) {
		return isPathable(block.getType());
	}
	
	// TODO try using a switch for this
	public static boolean isPathable(Material material) {
		return
				material == Material.AIR ||
				material == Material.LEGACY_SAPLING ||
				material == Material.WATER ||
				material == Material.LEGACY_STATIONARY_WATER ||
				material == Material.POWERED_RAIL ||
				material == Material.DETECTOR_RAIL ||
				material == Material.LEGACY_LONG_GRASS ||
				material == Material.DEAD_BUSH ||
				material == Material.LEGACY_YELLOW_FLOWER ||
				material == Material.LEGACY_RED_ROSE ||
				material == Material.BROWN_MUSHROOM ||
				material == Material.RED_MUSHROOM ||
				material == Material.TORCH ||
				material == Material.FIRE ||
				material == Material.REDSTONE_WIRE ||
				material == Material.LEGACY_CROPS ||
				material == Material.LEGACY_SIGN_POST ||
				material == Material.LADDER ||
				material == Material.LEGACY_RAILS ||
				material == Material.WALL_SIGN ||
				material == Material.LEVER ||
				material == Material.LEGACY_STONE_PLATE ||
				material == Material.LEGACY_WOOD_PLATE ||
				material == Material.LEGACY_REDSTONE_TORCH_OFF ||
				material == Material.LEGACY_REDSTONE_TORCH_ON ||
				material == Material.STONE_BUTTON ||
				material == Material.SNOW ||
				material == Material.LEGACY_SUGAR_CANE_BLOCK ||
				material == Material.VINE ||
				material == Material.LEGACY_WATER_LILY ||
				material == Material.LEGACY_NETHER_STALK ||
				material == Material.LEGACY_CARPET;
	}
	
	public static boolean isSafeToStand(Location location) {
		if (!isPathable(location.getBlock())) return false;
		if (!isPathable(location.add(0, 1, 0).getBlock())) return false;
		return !isPathable(location.subtract(0, 2, 0).getBlock()) || !isPathable(location.subtract(0, 1, 0).getBlock());
	}
	
}
