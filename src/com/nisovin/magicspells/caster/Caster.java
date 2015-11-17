package com.nisovin.magicspells.caster;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.SpellReagents;

public abstract class Caster {

	public abstract String getName();
	
	public abstract String getDisplayName();
	
	public abstract Location getLocation();
	
	public abstract World getWorld();
	
	public abstract boolean canCast(Spell spell);
	
	public abstract void sendMessage(String message);
	
	public abstract boolean hasPermission(String perm);
	
	public abstract boolean hasReagents(SpellReagents reagents);
	
	public abstract void removeReagents(SpellReagents reagents);
	
	public abstract void giveExp(int exp);
	
	public abstract boolean isValid();
	
	public abstract double getHealth();
	
	public abstract int getFoodLevel();
	
	public abstract int getLevel();
	
	public abstract void setLevel(int level);
	
	public abstract ItemStack getItemInHand();
	
	public abstract void setItemInHand(ItemStack arg0);
	
	public abstract Inventory getInventory();
	
	public abstract void setHealth(double arg0);
	
	public abstract void setFoodLevel(int arg0);
	
	public abstract List<Entity> getNearbyEntities(double arg0, double arg1, double arg2);
	
	
}
