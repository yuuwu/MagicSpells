package com.nisovin.magicspells.util.compat;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ExemptionAssistant {
	
	// The node objects should be the optimized nodes from optimizeNodes
	void exemptRunnable(Runnable runnable, Player player, Collection<?> nodes);
	
	Collection<Object> optimizeNodes(Object[] nodes);
	
}
