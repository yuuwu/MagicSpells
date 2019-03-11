package com.nisovin.magicspells.materials;

import com.nisovin.magicspells.util.MaterialHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Objects;

public class MagicUnknownMaterial extends MagicMaterial {
	
	@Deprecated int type;
	Material material;
	short data;
	
	public MagicUnknownMaterial(int type, short data) {
		this.material = MaterialHelper.getFromNumericalId(type);
		this.data = data;
	}
	
	@Override
	public Material getMaterial() {
		return this.material;
	}
	
	@Override
	public MaterialData getMaterialData() {
		return this.material.getNewData((byte) this.data);
	}
	
	@Override
	public void setBlock(Block block, boolean applyPhysics) {
		if (this.data < 16) block.setTypeIdAndData(this.type, (byte)this.data, applyPhysics);
	}
	
	@Override
	public FallingBlock spawnFallingBlock(Location location) {
		return location.getWorld().spawnFallingBlock(location, getMaterialData());
	}
	
	@Override
	public ItemStack toItemStack(int quantity) {
		return new ItemStack(this.type, quantity, this.data);
	}
	
	@Override
	public boolean equals(MaterialData matData) {
		return matData.getItemType() == this.material && matData.getData() == this.data;
	}
	
	@Override
	public boolean equals(ItemStack itemStack) {
		return itemStack.getType() == this.material && itemStack.getDurability() == this.data;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
			this.material,
			":",
			this.data
		);
	}
	
}
