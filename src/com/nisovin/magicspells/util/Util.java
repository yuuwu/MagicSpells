package com.nisovin.magicspells.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.materials.ItemNameResolver.ItemTypeAndData;
import com.nisovin.magicspells.materials.MagicMaterial;
import com.nisovin.magicspells.util.itemreader.BannerHandler;
import com.nisovin.magicspells.util.itemreader.LeatherArmorHandler;
import com.nisovin.magicspells.util.itemreader.LoreHandler;
import com.nisovin.magicspells.util.itemreader.NameHandler;
import com.nisovin.magicspells.util.itemreader.PotionHandler;
import com.nisovin.magicspells.util.itemreader.RepairableHandler;
import com.nisovin.magicspells.util.itemreader.SkullHandler;
import com.nisovin.magicspells.util.itemreader.WrittenBookHandler;

import de.slikey.effectlib.util.VectorUtils;

public class Util {

	public static Map<String, ItemStack> predefinedItems = new HashMap<String, ItemStack>();
	
	private static Random random = new Random();
	public static int getRandomInt(int bound) {
		return random.nextInt(bound);
	}
	
	/**
	 * Format is<br />
	 * 
	 * <code>itemID#color;enchant-level+enchant-level+enchant-level...|name|lore|lore...</code><p />
	 * 
	 * OR<p>
	 * 
	 * <code>predefined item key</code><br />
	 * 
	 * @param string The string to resolve to an item
	 * 
	 * @return the item stack represented by the string
	 */
	public static ItemStack getItemStackFromString(String string) {
		try {
			if (predefinedItems.containsKey(string)) return predefinedItems.get(string).clone();

			ItemStack item;
			String s = string;
			String name = null;
			String[] lore = null;
			HashMap<Enchantment, Integer> enchants = null;
			int color = -1;
			if (s.contains("|")) {
				String[] temp = s.split("\\|");
				s = temp[0];
				if (temp.length == 1) {
					name = "";
				} else {
					name = ChatColor.translateAlternateColorCodes('&', temp[1].replace("__", " "));
					if (temp.length > 2) {
						lore = Arrays.copyOfRange(temp, 2, temp.length);
						for (int i = 0; i < lore.length; i++) {
							lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i].replace("__", " "));
						}
					}
				}
			}
			if (s.contains(";")) {
				String[] temp = s.split(";", 2);
				s = temp[0];
				enchants = new HashMap<Enchantment, Integer>();
				if (temp[1].length() > 0) {
					String[] split = temp[1].split("\\+");
					for (int i = 0; i < split.length; i++) {
						String[] enchantData = split[i].split("-");
						Enchantment ench;
						ench = MagicValues.Enchantments.getEnchantmentType(enchantData[0]);
						if (ench != null && enchantData[1].matches("[0-9]+")) {
							enchants.put(ench, Integer.parseInt(enchantData[1]));
						}
					}
				}
			}
			if (s.contains("#")) { 
				String[] temp = s.split("#");
				s = temp[0];
				if (temp[1].matches("[0-9A-Fa-f]+")) {
					color = Integer.parseInt(temp[1], 16);
				}
			}
			ItemTypeAndData itemTypeAndData = MagicSpells.getItemNameResolver().resolve(s);
			if (itemTypeAndData != null) {
				item = new ItemStack(itemTypeAndData.id, 1, itemTypeAndData.data);
			} else {
				return null;
			}
			if (name != null || lore != null || color >= 0) {
				try {
					ItemMeta meta = item.getItemMeta();
					if (name != null) {
						meta.setDisplayName(name);
					}
					if (lore != null) {
						meta.setLore(Arrays.asList(lore));
					}
					if (color >= 0 && meta instanceof LeatherArmorMeta) {
						((LeatherArmorMeta)meta).setColor(Color.fromRGB(color));
					}
					item.setItemMeta(meta);
				} catch (Exception e) {
					MagicSpells.error("Failed to process item meta for item: " + s);
				}
			}
			if (enchants != null) {
				if (enchants.size() > 0) {
					item.addUnsafeEnchantments(enchants);
				} else {
					item = MagicSpells.getVolatileCodeHandler().addFakeEnchantment(item);
				}
			}
			return item;
		} catch (Exception e) {
			MagicSpells.handleException(e);
			return null;
		}
	}
	
	
	/**
	 * <strong>Global Options</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>All Items</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>type</code>: &lt;String&gt;<br />
	 * Description: The name of the material type.
	 * <p />
	 * 
	 * <code>name</code>: &lt;String&gt;<br />
	 * Description: The custom name for the item.
	 * <p />
	 * 
	 * <code>lore</code>: &lt;String or String List&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <code>enchants</code>: &lt;String List&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <code>hide-tooltip</code>: &lt;Boolean&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <code>unbreakable</code>: &lt;Boolean&gt;<br />
	 * Description: If true, the item will not take durability damage by vanilla behavior.
	 * <p />
	 * 
	 * <code>attributes</code>: &lt;Configuration Section&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <hr>
	 * 
	 * <strong>LeatherArmorMeta</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>Leather Armor</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>color</code>: &lt;String&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <hr>
	 * 
	 * <strong>PotionMeta</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>Potions</li>
	 * 	<li>Tipped Arrows</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>potioneffects</code>: &lt;String List&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <hr>
	 * 
	 * <strong>SkullMeta</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>Skulls</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>skullowner</code>: &lt;String&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <hr>
	 * 
	 * <strong>Repairable</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>TODO</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>repaircost</code>: &lt;Integer&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <hr>
	 * 
	 * <strong>BookMeta</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>Written Books</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>title</code>: &lt;String&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <code>author</code>: &lt;String&gt;<br />
	 * Description: the name to use as the author of the book.
	 * <p />
	 * 
	 * <code>pages</code>: &lt;String List&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <hr>
	 * 
	 * <strong>BannerMeta</strong><br />
	 * Currently Applies to:
	 * <ul>
	 * 	<li>Banners</li>
	 * </ul>
	 * <p />
	 * 
	 * <code>color</code>: &lt;String&gt;<br />
	 * //TODO explain
	 * <p />
	 * 
	 * <code>patterns</code>: &lt;String List&gt;<br />
	 * //TODO explain
	 * 
	 */
	public static ItemStack getItemStackFromConfig(ConfigurationSection config) {
		try {
			if (!config.contains("type")) return null;
			
			// basic item
			MagicMaterial material = MagicSpells.getItemNameResolver().resolveItem(config.getString("type"));
			if (material == null) return null;
			ItemStack item = material.toItemStack();
			ItemMeta meta = item.getItemMeta();
			
			// name and lore
			meta = NameHandler.process(config, meta);
			meta = LoreHandler.process(config, meta);
			
			// enchants
			boolean emptyEnchants = false;
			if (config.contains("enchants") && config.isList("enchants")) {
				List<String> enchants = config.getStringList("enchants");
				for (String enchant : enchants) {
					String[] data = enchant.split(" ");
					Enchantment e = null;
					e = MagicValues.Enchantments.getEnchantmentType(data[0]);
					if (e == null) {
						MagicSpells.error("'" + data[0] + "' could not be connected to an enchantment");
					}
					if (e != null) {
						int level = 0;
						if (data.length > 1) {
							try {
								level = Integer.parseInt(data[1]);
							} catch (NumberFormatException ex) {
								DebugHandler.debugNumberFormat(ex);
							}
						}
						if (meta instanceof EnchantmentStorageMeta) {
							((EnchantmentStorageMeta)meta).addStoredEnchant(e, level, true);
						} else {
							meta.addEnchant(e, level, true);
						}
					}
				}
				if (enchants.size() == 0) {
					emptyEnchants = true;
				}
			}
			
			// armor color
			meta = LeatherArmorHandler.process(config, meta);
			
			// potioneffects
			// potioncolor
			meta = PotionHandler.process(config, meta);
			
			// skull owner
			meta = SkullHandler.process(config, meta);
			
			// flower pot
			/*if (config.contains("flower") && item.getType() == Material.FLOWER_POT && meta instanceof BlockStateMeta) {
				MagicMaterial flower = MagicSpells.getItemNameResolver().resolveBlock(config.getString("flower"));
				BlockState state = ((BlockStateMeta)meta).getBlockState();
				MaterialData data = state.getData();
				if (data instanceof FlowerPot) {
					((FlowerPot)data).setContents(new MaterialData(flower.getMaterial()));
				}
				state.setData(data);
				((BlockStateMeta)meta).setBlockState(state);
			}*/
			
			// repair cost
			meta = RepairableHandler.process(config, meta);
			
			// written book
			meta = WrittenBookHandler.process(config, meta);
			
			// banner
			meta = BannerHandler.process(config, meta);
			
			// set meta
			item.setItemMeta(meta);
			
			// hide tooltip
			if (config.getBoolean("hide-tooltip", MagicSpells.hidePredefinedItemTooltips())) {
				item = MagicSpells.getVolatileCodeHandler().hideTooltipCrap(item);
			}
			
			// unbreakable
			if (config.getBoolean("unbreakable", false)) {
				item = MagicSpells.getVolatileCodeHandler().setUnbreakable(item);
			}
			
			// empty enchant
			if (emptyEnchants) {
				item = MagicSpells.getVolatileCodeHandler().addFakeEnchantment(item);
			}
			
			// attributes
			if (config.contains("attributes")) {
				Set<String> attrs = config.getConfigurationSection("attributes").getKeys(false);
				String[] attrNames = new String[attrs.size()];
				String[] attrTypes = new String[attrs.size()];
				double[] attrAmounts = new double[attrs.size()];
				int[] attrOperations = new int[attrs.size()];
				String[] slots = new String[attrs.size()];
				int i = 0;
				for (String attrName : attrs) {
					String[] attrData = config.getString("attributes." + attrName).split(" ");
					String attrType = attrData[0];
					double attrAmt = 1;
					try {
						attrAmt = Double.parseDouble(attrData[1]);
					} catch (NumberFormatException e) {
						DebugHandler.debugNumberFormat(e);
					}
					int attrOp = 0; // add number
					if (attrData.length > 2) {
						if (attrData[2].toLowerCase().startsWith("mult")) {
							attrOp = 1; // multiply percent
						} else if (attrData[2].toLowerCase().contains("add") && attrData[2].toLowerCase().contains("perc")) {
							attrOp = 2; // add percent
						}
					}
					String slot = null;
					if (attrData.length > 3) {
						slot = attrData[3];
					}
					if (attrType != null) {
						attrNames[i] = attrName;
						attrTypes[i] = attrType;
						attrAmounts[i] = attrAmt;
						attrOperations[i] = attrOp;
						slots[i] = slot;
					}
					i++;
				}
				item = MagicSpells.getVolatileCodeHandler().addAttributes(item, attrNames, attrTypes, attrAmounts, attrOperations, slots);
			}
			
			return item;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// just checks to see if the passed string could be lore data
	public static boolean isLoreData(String line) {
		if (line == null) return false;
		line = ChatColor.stripColor(line);
		return line.startsWith("MS$:");
	}
	
	public static void setLoreData(ItemStack item, String data) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore;
		if (meta.hasLore()) {
			lore = meta.getLore();
			if (lore.size() > 0) {
				for (int i = 0; i < lore.size(); i++) {
					if (isLoreData(lore.get(i))) {
						lore.remove(i);
						break;
					}
				}
			}
		} else {
			lore = new ArrayList<String>();
		}
		lore.add(ChatColor.BLACK.toString() + ChatColor.MAGIC.toString() + "MS$:" + data);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public static String getLoreData(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null && meta.hasLore()) {
			List<String> lore = meta.getLore();
			if (lore.size() > 0) {
				for (int i = 0; i < lore.size(); i++) {
					String s = ChatColor.stripColor(lore.get(lore.size() - 1));
					if (s.startsWith("MS$:")) {
						return s.substring(4);
					}
				}
			}
		}
		return null;
	}
	
	public static void removeLoreData(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore;
		if (meta.hasLore()) {
			lore = meta.getLore();
			if (lore.size() > 0) {
				boolean removed = false;
				for (int i = 0; i < lore.size(); i++) {
					String s = ChatColor.stripColor(lore.get(i));
					if (s.startsWith("MS$:")) {
						lore.remove(i);
						removed = true;
						break;
					}
				}
				if (removed) {
					if (lore.size() > 0) {
						meta.setLore(lore);
					} else {
						meta.setLore(null);
					}
					item.setItemMeta(meta);
				}
			}
		}
	}

	static Map<String, EntityType> entityTypeMap = new HashMap<String, EntityType>();
	static {
		for (EntityType type : EntityType.values()) {
			if (type != null && type.getName() != null) {
				entityTypeMap.put(type.getName().toLowerCase(), type);
				entityTypeMap.put(type.name().toLowerCase(), type);
				entityTypeMap.put(type.name().toLowerCase().replace("_", ""), type);
			}
		}
		entityTypeMap.put("zombiepig", EntityType.PIG_ZOMBIE);
		entityTypeMap.put("mooshroom", EntityType.MUSHROOM_COW);
		entityTypeMap.put("cat", EntityType.OCELOT);
		entityTypeMap.put("golem", EntityType.IRON_GOLEM);
		entityTypeMap.put("snowgolem", EntityType.SNOWMAN);
		entityTypeMap.put("dragon", EntityType.ENDER_DRAGON);
		Map<String, EntityType> toAdd = new HashMap<String, EntityType>();
		for (String s : entityTypeMap.keySet()) {
			toAdd.put(s + "s", entityTypeMap.get(s));
		}
		entityTypeMap.putAll(toAdd);
		entityTypeMap.put("endermen", EntityType.ENDERMAN);
		entityTypeMap.put("wolves", EntityType.WOLF);
	}
	
	public static EntityType getEntityType(String type) {
		if (type.equalsIgnoreCase("player")) return EntityType.PLAYER;
		return entityTypeMap.get(type.toLowerCase());
	}
	
	public static PotionEffectType getPotionEffectType(String type) {
		return MagicValues.PotionEffect.getPotionEffectType(type.trim());
	}
	
	public static Enchantment getEnchantmentType(String type) {
		return MagicValues.Enchantments.getEnchantmentType(type);
	}
	
	public static void sendFakeBlockChange(Player player, Block block, MagicMaterial mat) {
		player.sendBlockChange(block.getLocation(), mat.getMaterial(), mat.getMaterialData().getData());
	}
	
	public static void restoreFakeBlockChange(Player player, Block block) {
		player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
	}
	
	public static void setFacing(Player player, Vector vector) {
		Location loc = player.getLocation();
		setLocationFacingFromVector(loc, vector);
		player.teleport(loc);
	}
	
	public static void setLocationFacingFromVector(Location location, Vector vector) {
		double yaw = getYawOfVector(vector);
		double pitch = Math.toDegrees(-Math.asin(vector.getY()));				
		location.setYaw((float)yaw);
		location.setPitch((float)pitch);
	}
	
	public static double getYawOfVector(Vector vector) {
		return Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ()));
	}
	
	public static boolean arrayContains(int[] array, int value) {
		for (int i : array) {
			if (i == value) {
				return true;
			}
		}
		return false;
	}

	public static boolean arrayContains(String[] array, String value) {
		for (String i : array) {
			if (i.equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean arrayContains(Object[] array, Object value) {
		for (Object i : array) {
			if (i != null && i.equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public static String arrayJoin(String[] array, char with) {
		if (array == null || array.length == 0) {
			return "";
		}
		int len = array.length;
		StringBuilder sb = new StringBuilder(16 + len * 8);
		sb.append(array[0]);
		for (int i = 1; i < len; i++) {
			sb.append(with);
			sb.append(array[i]);
		}
		return sb.toString();
	}
	
	public static String listJoin(List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		int len = list.size();
		StringBuilder sb = new StringBuilder(len * 12);
		sb.append(list.get(0));
		for (int i = 1; i < len; i++) {
			sb.append(' ');
			sb.append(list.get(i));
		}
		return sb.toString();
	}
	
	public static String[] splitParams(String string, int max) {
		String[] words = string.trim().split(" ");
		if (words.length <= 1) {
			return words;
		}
		ArrayList<String> list = new ArrayList<String>();		
		char quote = ' ';
		String building = "";
		
		for (String word : words) {
			if (word.length() == 0) continue;
			if (max > 0 && list.size() == max - 1) {
				if (!building.isEmpty()) building += " ";
				building += word;
			} else if (quote == ' ') {
				if (word.length() == 1 || (word.charAt(0) != '"' && word.charAt(0) != '\'')) {
					list.add(word);
				} else {
					quote = word.charAt(0);
					if (quote == word.charAt(word.length() - 1)) {
						quote = ' ';
						list.add(word.substring(1, word.length() - 1));
					} else {
						building = word.substring(1);
					}
				}
			} else {
				if (word.charAt(word.length() - 1) == quote) {
					list.add(building + " " + word.substring(0, word.length() - 1));
					building = "";
					quote = ' ';
				} else {
					building += " " + word;
				}
			}
		}
		if (!building.isEmpty()) {
			list.add(building);
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static String[] splitParams(String string) {
		return splitParams(string, 0);
	}
	
	public static String[] splitParams(String[] split, int max) {
		return splitParams(arrayJoin(split, ' '), max);
	}
	
	public static String[] splitParams(String[] split) {
		return splitParams(arrayJoin(split, ' '), 0);
	}
	
	public static List<String> tabCompleteSpellName(CommandSender sender, String partial) {
		List<String> matches = new ArrayList<String>();
		if (sender instanceof Player) {
			Spellbook spellbook = MagicSpells.getSpellbook((Player)sender);
			for (Spell spell : spellbook.getSpells()) {
				if (spellbook.canTeach(spell)) {
					if (spell.getName().toLowerCase().startsWith(partial)) {
						matches.add(spell.getName());
					} else {
						String[] aliases = spell.getAliases();
						if (aliases != null && aliases.length > 0) {
							for (String alias : aliases) {
								if (alias.toLowerCase().startsWith(partial)) {
									matches.add(alias);
								}
							}
						}
					}
				}
			}
		} else if (sender.isOp()) {
			for (Spell spell : MagicSpells.spells()) {
				if (spell.getName().toLowerCase().startsWith(partial)) {
					matches.add(spell.getName());
				} else {
					String[] aliases = spell.getAliases();
					if (aliases != null && aliases.length > 0) {
						for (String alias : aliases) {
							if (alias.toLowerCase().startsWith(partial)) {
								matches.add(alias);
							}
						}
					}
				}
			}
		}
		if (matches.size() > 0) {
			return matches;
		}
		return null;
	}
	
	public static boolean removeFromInventory(Inventory inventory, ItemStack item) {
		int amt = item.getAmount();
		ItemStack[] items = inventory.getContents();
		for (int i = 0; i < 36; i++) {
			if (items[i] != null && item.isSimilar(items[i])) {
				if (items[i].getAmount() > amt) {
					items[i].setAmount(items[i].getAmount() - amt);
					amt = 0;
					break;
				} else if (items[i].getAmount() == amt) {
					items[i] = null;
					amt = 0;
					break;
				} else {
					amt -= items[i].getAmount();
					items[i] = null;
				}
			}
		}
		if (amt == 0) {
			inventory.setContents(items);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean addToInventory(Inventory inventory, ItemStack item, boolean stackExisting, boolean ignoreMaxStack) {
		int amt = item.getAmount();
		ItemStack[] items = inventory.getContents();
		if (stackExisting) {
			for (int i = 0; i < 36; i++) {
				if (items[i] != null && item.isSimilar(items[i])) {
					if (items[i].getAmount() + amt <= items[i].getMaxStackSize()) {
						items[i].setAmount(items[i].getAmount() + amt);
						amt = 0;
						break;
					} else {
						int diff = items[i].getMaxStackSize() - items[i].getAmount();
						items[i].setAmount(items[i].getMaxStackSize());
						amt -= diff;
					}
				}
			}
		}
		if (amt > 0) {
			for (int i = 0; i < 36; i++) {
				if (items[i] == null) {
					if (amt > item.getMaxStackSize() && !ignoreMaxStack) {
						items[i] = item.clone();
						items[i].setAmount(item.getMaxStackSize());
						amt -= item.getMaxStackSize();
					} else {
						items[i] = item.clone();
						items[i].setAmount(amt);
						amt = 0;
						break;
					}
				}
			}
		}
		if (amt == 0) {
			inventory.setContents(items);
			return true;
		} else {
			return false;
		}
	}
	
	public static void rotateVector(Vector v, float degrees) {
		double rad = Math.toRadians(degrees);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		double x = (v.getX() * cos) - (v.getZ() * sin);
		double z = (v.getX() * sin) + (v.getZ() * cos);
		v.setX(x);
		v.setZ(z);
	}
	
	public static Location applyRelativeOffset(Location loc, Vector relativeOffset) {
		return loc.add(VectorUtils.rotateVector(relativeOffset, loc));
	}
	
	public static Location applyAbsoluteOffset(Location loc, Vector offset) {
		return loc.add(offset);
	}
	
	public static Location applyOffsets(Location loc, Vector relativeOffset, Vector absoluteOffset) {
		return applyAbsoluteOffset(applyRelativeOffset(loc, relativeOffset), absoluteOffset);
	}
	
	public static Location faceTarget(Location origin, Location target) {
		return origin.setDirection(getVectorToTarget(origin, target));
	}
	
	public static Vector getVectorToTarget(Location origin, Location target) {
		return target.toVector().subtract(origin.toVector());
	}
	
	public static boolean downloadFile(String url, File file) {
		try {
			URL website = new URL(url);
		    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		    fos.close();
		    rbc.close();
		    return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void createFire(Block block, byte d) {
		block.setTypeIdAndData(Material.FIRE.getId(), d, false);
	}
	
	public static ItemStack getEggItemForEntityType(EntityType type) {
		ItemStack ret = new ItemStack(Material.MONSTER_EGG, 1);
		ItemMeta meta = ret.getItemMeta();
		if (meta instanceof SpawnEggMeta) {
			((SpawnEggMeta) meta).setSpawnedType(type);
			ret.setItemMeta(meta);
		}
		return ret;
	}
	
	public static String getStringNumber(double number, int places) {
		if (places < 0) return number+"";
		if (places == 0) return (int)Math.round(number) + "";
		int x = (int)Math.pow(10, places);
		return ((double)Math.round(number * x) / x) + "";
	}
	
	public static String getStringNumber(String textNumber, int places) {
		String ret = "";
		try {
			ret = getStringNumber(Double.parseDouble(textNumber), places);
		} catch (NumberFormatException nfe) {
			ret = textNumber;
		}
		
		return ret;
	}
	
	private static Map<String, String> uniqueIds = new HashMap<String, String>();
	
	public static String getUniqueId(Player player) {
		String uid = player.getUniqueId().toString().replace("-", "");
		uniqueIds.put(player.getName(), uid);
		return uid;
	}
	
	public static String getUniqueId(String playerName) {
		if (uniqueIds.containsKey(playerName)) {
			return uniqueIds.get(playerName);
		}
		Player player = Bukkit.getPlayerExact(playerName);
		if (player != null) {
			return getUniqueId(player);
		}
		return null;
	}
	
	public static String flattenLineBreaks(String raw) {
		return raw.replaceAll("\n", "\\n");
	}
}
