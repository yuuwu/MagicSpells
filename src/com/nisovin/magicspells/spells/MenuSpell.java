package com.nisovin.magicspells.spells;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.castmodifiers.ModifierSet;
import com.nisovin.magicspells.events.MagicSpellsGenericPlayerEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.util.ConfigData;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.util.SpellType;
import com.nisovin.magicspells.util.SpellTypes;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.Util;

@SpellType(types={SpellTypes.TARGETED_ENTITY_SPELL, SpellTypes.TARGETED_LOCATION_SPELL})
public class MenuSpell extends TargetedSpell implements TargetedEntitySpell, TargetedLocationSpell {

	Random random = new Random();
	
	@ConfigData(field="title", dataType="String", defaultValue="Window Title  + <spellName>")
	String title;
	
	@ConfigData(field="delay", dataType="int", defaultValue="0")
	int delay;
	
	@ConfigData(field="require-entity-target", dataType="boolean", defaultValue="false")
	boolean requireEntityTarget;
	
	@ConfigData(field="require-location-target", dataType="boolean", defaultValue="false")
	boolean requireLocationTarget;
	
	@ConfigData(field="target-opens-menu-instead", dataType="boolean", defaultValue="false")
	boolean targetOpensMenuInstead;
	
	@ConfigData(field="bypass-normal-cast", dataType="boolean", defaultValue="true")
	boolean bypassNormalCast;
	
	@ConfigData(field="unique-names", dataType="boolean", defaultValue="false")
	boolean uniqueNames;
	
	Map<String, MenuOption> options = new LinkedHashMap<String, MenuOption>();
	
	int size = 9;
	
	Map<String, Float> castPower = new HashMap<String, Float>();
	Map<String, LivingEntity> castEntityTarget = new HashMap<String, LivingEntity>();
	Map<String, Location> castLocTarget = new HashMap<String, Location>();
	
	public MenuSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		title = ChatColor.translateAlternateColorCodes('&', getConfigString("title", "Window Title " + spellName));
		delay = getConfigInt("delay", 0);
		requireEntityTarget = getConfigBoolean("require-entity-target", false);
		requireLocationTarget = getConfigBoolean("require-location-target", false);
		targetOpensMenuInstead = getConfigBoolean("target-opens-menu-instead", false);
		bypassNormalCast = getConfigBoolean("bypass-normal-cast", true);
		uniqueNames = getConfigBoolean("unique-names", false);
		
		int maxSlot = 8;
		for (String optionName : getConfigKeys("options")) {
			int optionSlot = getConfigInt("options." + optionName + ".slot", -1);
			String optionSpellName = getConfigString("options." + optionName + ".spell", "");
			float optionPower = getConfigFloat("options." + optionName + ".power", 1);
			ItemStack optionItem;
			if (isConfigSection("options." + optionName + ".item")) {
				optionItem = Util.getItemStackFromConfig(getConfigSection("options." + optionName + ".item"));
			} else {
				optionItem = Util.getItemStackFromString(getConfigString("options." + optionName + ".item", "stone"));
			}
			int optionQuantity = getConfigInt("options." + optionName + ".quantity", 1);
			List<String> modifierList = getConfigStringList("options." + optionName + ".modifiers", null);
			boolean optionStayOpen = getConfigBoolean("options." + optionName + ".stay-open", false);
			if (optionSlot >= 0 && !optionSpellName.isEmpty() && optionItem != null) { //TODO flatten this a bit
				optionItem.setAmount(optionQuantity);
				Util.setLoreData(optionItem, optionName);
				MenuOption option = new MenuOption();
				option.slot = optionSlot;
				option.menuOptionName = optionName;
				option.spellName = optionSpellName;
				option.power = optionPower;
				option.item = optionItem;
				option.modifierList = modifierList;
				option.stayOpen = optionStayOpen;
				String optionKey = uniqueNames ? getOptionKey(option.item) : optionName;
				options.put(optionKey, option);
				if (optionSlot > maxSlot) maxSlot = optionSlot;
			}
		}
		size = ((maxSlot / 9) * 9) + 9;
		
		if (options.isEmpty()) MagicSpells.error("The MenuSpell '" + spellName + "' has no menu options!");
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		for (MenuOption option : options.values()) {
			Subspell spell = new Subspell(option.spellName);
			if (spell.process()) {
				option.spell = spell;
				if (option.modifierList != null) option.menuOptionModifiers = new ModifierSet(option.modifierList);
			} else {
				MagicSpells.error("The MenuSpell '" + internalName + "' has an invalid spell listed on '" + option.menuOptionName + "'");
			}
		}
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			LivingEntity entityTarget = null;
			Location locTarget = null;
			
			Player opener = player;
			
			if (requireEntityTarget) {
				TargetInfo<LivingEntity> targetInfo = getTargetedEntity(player, power);
				if (targetInfo != null) entityTarget = targetInfo.getTarget();
				if (entityTarget == null) return noTarget(player);
				if (targetOpensMenuInstead) {
					if (!(entityTarget instanceof Player)) return noTarget(player);
					opener = (Player)entityTarget;
					entityTarget = null;
				}
			} else if (requireLocationTarget) {
				Block block = getTargetedBlock(player, power);
				if (block == null || block.getType() == Material.AIR) return noTarget(player);
				locTarget = block.getLocation();
			}
			
			open(player, opener, entityTarget, locTarget, power, args);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	String getOptionKey(ItemStack item) {
		return item.getType().name() + "_" + item.getDurability() + "_" + item.getItemMeta().getDisplayName();
	}
	
	void open(final Player caster, Player opener, LivingEntity entityTarget, Location locTarget, final float power, final String[] args) {
		if (delay < 0) {
			openMenu(caster, opener, entityTarget, locTarget, power, args);
		} else {
			final Player p = opener;
			final LivingEntity e = entityTarget;
			final Location l = locTarget;
			MagicSpells.scheduleDelayedTask(new Runnable() {
				@Override
				public void run() {
					openMenu(caster, p, e, l, power, args);
				}
			}, delay);
		}
	}
	
	void openMenu(Player caster, Player opener, LivingEntity entityTarget, Location locTarget, float power, String[] args) {
		castPower.put(opener.getName(), power);
		if (requireEntityTarget && entityTarget != null) castEntityTarget.put(opener.getName(), entityTarget);
		if (requireLocationTarget && locTarget != null) castLocTarget.put(opener.getName(), locTarget);
		
		Inventory inv = Bukkit.createInventory(opener, size, title);
		applyOptionsToInventory(opener, inv, args);
		opener.openInventory(inv);
		
		if (entityTarget != null && caster != null) {
			playSpellEffects(caster, entityTarget);
		} else {
			if (caster != null) playSpellEffects(EffectPosition.CASTER, caster);
			playSpellEffects(EffectPosition.SPECIAL, opener);
			if (locTarget != null) playSpellEffects(EffectPosition.TARGET, locTarget);
		}
	}
	
	void applyOptionsToInventory(Player opener, Inventory inv, String[] args) {
		inv.clear();
		for (MenuOption option : options.values()) {
			if (option.spell != null && inv.getItem(option.slot) == null) {
				if (option.menuOptionModifiers != null) {
					MagicSpellsGenericPlayerEvent event = new MagicSpellsGenericPlayerEvent(opener);
					option.menuOptionModifiers.apply(event);
					if (event.isCancelled()) continue;
				}
				ItemStack item = option.item.clone();
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(MagicSpells.doArgumentAndVariableSubstitution(meta.getDisplayName(), opener, args));
				List<String> lore = meta.getLore();
				if (lore != null && lore.size() > 1) {
					for (int i = 0; i < lore.size() - 1; i++) {
						lore.set(i, MagicSpells.doArgumentAndVariableSubstitution(lore.get(i), opener, args));
					}
					meta.setLore(lore);
				}
				item.setItemMeta(meta);
				inv.setItem(option.slot, item);
			}
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equals(title)) {
			event.setCancelled(true);		
			if (event.getClick() == ClickType.LEFT) {
				final Player player = (Player)event.getWhoClicked();
				String playerName = player.getName();
				boolean close = true;
				
				ItemStack item = event.getCurrentItem();
				if (item != null) {
					String key = uniqueNames ? getOptionKey(item) : Util.getLoreData(item);
					if (key != null && !key.isEmpty() && options.containsKey(key)) {
						MenuOption option = options.get(key);
						Subspell spell = option.spell;
						if (spell != null) {
							float power = option.power;
							if (castPower.containsKey(playerName)) {
								power *= castPower.get(playerName).floatValue();
							}
							if (spell.isTargetedEntitySpell() && castEntityTarget.containsKey(playerName)) {
								spell.castAtEntity(player, castEntityTarget.get(playerName), power);
							} else if (spell.isTargetedLocationSpell() && castLocTarget.containsKey(playerName)) {
								spell.castAtLocation(player, castLocTarget.get(playerName), power);
							} else if (bypassNormalCast) {
								spell.cast(player, power);
							} else {
								spell.getSpell().cast(player, power, null);
							}
						}
						if (option.stayOpen) close = false;
					}
				}
				
				castPower.remove(playerName);
				castEntityTarget.remove(playerName);
				castLocTarget.remove(playerName);
				
				if (close) {
					MagicSpells.scheduleDelayedTask(new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
						}
					}, 0);
				} else {
					applyOptionsToInventory(player, event.getView().getTopInventory(), MagicSpells.NULL_ARGS);
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		String playerName = event.getPlayer().getName();
		castPower.remove(playerName);
		castEntityTarget.remove(playerName);
		castLocTarget.remove(playerName);
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		if (requireEntityTarget && !validTargetList.canTarget(caster, target)) return false;
		Player opener = caster;
		if (targetOpensMenuInstead) {
			if (!(target instanceof Player)) return false;
			opener = (Player)target;
			target = null;
		}
		open(caster, opener, target, null, power, MagicSpells.NULL_ARGS);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (!targetOpensMenuInstead) return false;
		if (requireEntityTarget && !validTargetList.canTarget(target)) return false;
		if (!(target instanceof Player)) return false;
		open(null, (Player)target, null, null, power, MagicSpells.NULL_ARGS);
		return true;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		open(caster, caster, null, target, power, MagicSpells.NULL_ARGS);
		return true;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return false;
	}
	
	@Override
	public boolean castFromConsole(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			Player player = PlayerNameUtils.getPlayer(args[0]);
			String[] spellArgs = (args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : null;
			if (player != null) {
				open(null, player, null, null, 1, spellArgs);
				return true;
			}
		}
		return false;
	}
	
	static class MenuOption {
		
		String menuOptionName;
		int slot;
		ItemStack item;
		String spellName;
		Subspell spell;
		float power;
		List<String> modifierList;
		ModifierSet menuOptionModifiers;
		boolean stayOpen;
		
	}

}
