package com.nisovin.magicspells.shop;

import java.util.HashMap;
import java.util.Set;

import com.nisovin.magicspells.util.compat.CompatBasics;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.nisovin.magicspells.util.ExperienceUtils;

public class CurrencyHandler {

	private HashMap<String,String> currencies = new HashMap<>();
	private String defaultCurrency;
	private Economy economy;
	
	public CurrencyHandler(Configuration config) {
		ConfigurationSection sec = config.getConfigurationSection("currencies");
		if (sec == null) {
			defaultCurrency = "money";
			currencies.put("money", "vault");
		} else {
			Set<String> keys = sec.getKeys(false);
			for (String key : keys) {
				if (defaultCurrency == null) defaultCurrency = key;
				currencies.put(key.toLowerCase(), sec.getString(key).toLowerCase());
			}
			if (defaultCurrency == null) {
				defaultCurrency = "money";
				currencies.put("money", "vault");
			}
		}

		// Set up vault hook
		if (currencies.containsValue("vault") && CompatBasics.pluginEnabled("Vault")) {
			RegisteredServiceProvider<Economy> provider = CompatBasics.getServiceProvider(Economy.class);
			if (provider != null) economy = provider.getProvider();
		}
	}
	
	public boolean has(Player player, double amount) {
		return has(player, amount, defaultCurrency);
	}
	
	public boolean has(Player player, double amount, String currency) {
		String c = currency == null ? null : currencies.get(currency.toLowerCase());
		if (c == null) c = currencies.get(defaultCurrency);
		
		if (c == null) return false;
		if (c.equalsIgnoreCase("vault") && economy != null) return economy.has(player.getName(), amount);
		if (c.equalsIgnoreCase("levels")) return player.getLevel() >= (int)amount;
		if (c.equalsIgnoreCase("experience") || c.equalsIgnoreCase("xp")) return ExperienceUtils.hasExp(player, (int)amount);
		if (c.matches("^[0-9]+$")) return inventoryContains(player.getInventory(), new ItemStack(Integer.parseInt(c), (int)amount));
		if (c.matches("^[0-9]+:[0-9]+$")) {
			String[] s = c.split(":");
			int type = Integer.parseInt(s[0]);
			short data = Short.parseShort(s[1]);
			return inventoryContains(player.getInventory(), new ItemStack(type, (int)amount, data));
		}
		return false;
	}
	
	public void remove(Player player, double amount) {
		remove(player, amount, defaultCurrency);
	}
	
	public void remove(Player player, double amount, String currency) {
		String c = currency == null ? null : currencies.get(currency.toLowerCase());
		if (c == null) c = currencies.get(defaultCurrency);
		
		if (c == null) {
		} else if (c.equalsIgnoreCase("vault") && economy != null) {
			economy.withdrawPlayer(player.getName(), amount);
		} else if (c.equalsIgnoreCase("levels")) {
			player.setLevel(player.getLevel() - (int)amount);
		} else if (c.equalsIgnoreCase("experience") || c.equalsIgnoreCase("xp")) {
			ExperienceUtils.changeExp(player, -(int)amount);
		} else if (c.matches("^[0-9]+$")) {
			removeFromInventory(player.getInventory(), new ItemStack(Integer.parseInt(c), (int)amount));
			player.updateInventory();
		} else if (c.matches("^[0-9]+:[0-9]+$")) {
			String[] s = c.split(":");
			int type = Integer.parseInt(s[0]);
			short data = Short.parseShort(s[1]);
			removeFromInventory(player.getInventory(), new ItemStack(type, (int)amount, data));
			player.updateInventory();
		}
	}
	
	public boolean isValidCurrency(String currency) {
		return currency != null && currencies.containsKey(currency);
	}
	
	private boolean inventoryContains(Inventory inventory, ItemStack item) {
		int count = 0;
		ItemStack[] items = inventory.getContents();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
				count += items[i].getAmount();
			}
			if (count >= item.getAmount()) return true;
		}
		return false;
	}
	
	private void removeFromInventory(Inventory inventory, ItemStack item) {
		int amt = item.getAmount();
		ItemStack[] items = inventory.getContents();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
				if (items[i].getAmount() > amt) {
					items[i].setAmount(items[i].getAmount() - amt);
					break;
				} else if (items[i].getAmount() == amt) {
					items[i] = null;
					break;
				} else {
					amt -= items[i].getAmount();
					items[i] = null;
				}
			}
		}
		inventory.setContents(items);
	}
	
}
