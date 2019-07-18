package com.nisovin.magicspells.spells.targeted;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.ValidTargetList;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class SilenceSpell extends TargetedSpell implements TargetedEntitySpell {

	private Map<UUID, Unsilencer> silenced;

	private SpellFilter filter;

	private String strSilenced;

	private int duration;

	private boolean preventCast;
	private boolean preventChat;
	private boolean preventCommands;
	
	public SilenceSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		strSilenced = getConfigString("str-silenced", "You are silenced!");

		duration = getConfigInt("duration", 200);

		preventCast = getConfigBoolean("prevent-cast", true);
		preventChat = getConfigBoolean("prevent-chat", false);
		preventCommands = getConfigBoolean("prevent-commands", false);

		List<String> allowedSpellNames = getConfigStringList("allowed-spells", null);
		List<String> disallowedSpellNames = getConfigStringList("disallowed-spells", null);
		List<String> tagList = getConfigStringList("allowed-spell-tags", null);
		List<String> deniedTagList = getConfigStringList("disallowed-spell-tags", null);
		filter = new SpellFilter(allowedSpellNames, disallowedSpellNames, tagList, deniedTagList);

		if (preventChat) silenced = new ConcurrentHashMap<>();
		else silenced = new HashMap<>();

		validTargetList = new ValidTargetList(true, false);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		if (preventCast) registerEvents(new CastListener());
		if (preventChat) registerEvents(new ChatListener());
		if (preventCommands) registerEvents(new CommandListener());
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<Player> target = getTargetedPlayer(player, power);
			if (target == null) return noTarget(player);
			
			silence(target.getTarget(), target.getPower());
			playSpellEffects(player, target.getTarget());
			sendMessages(player, target.getTarget());
			return PostCastAction.NO_MESSAGES;
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		if (!(target instanceof Player)) return false;
		silence((Player) target, power);
		playSpellEffects(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (!(target instanceof Player)) return false;
		silence((Player) target, power);
		playSpellEffects(EffectPosition.TARGET, target);
		return true;
	}

	private void silence(Player player, float power) {
		Unsilencer u = silenced.get(player.getUniqueId());
		if (u != null) u.cancel();
		silenced.put(player.getUniqueId(), new Unsilencer(player, Math.round(duration * power)));
	}

	public boolean isSilenced(Player player) {
		return silenced.containsKey(player.getUniqueId());
	}

	public void removeSilence(Player player) {
		if (!isSilenced(player)) return;
		Unsilencer unsilencer = silenced.get(player.getUniqueId());
		unsilencer.cancel();
		silenced.remove(player.getUniqueId());
	}
	
	public class CastListener implements Listener {
		
		@EventHandler(ignoreCancelled=true)
		public void onSpellCast(final SpellCastEvent event) {
			if (event.getCaster() == null) return;
			if (!silenced.containsKey(event.getCaster().getUniqueId())) return;
			if (filter.check(event.getSpell())) return;
			event.setCancelled(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(MagicSpells.plugin, () -> sendMessage(strSilenced, event.getCaster(), event.getSpellArgs()));
		}
		
	}
	
	public class ChatListener implements Listener {
		
		@EventHandler(ignoreCancelled=true)
		public void onChat(AsyncPlayerChatEvent event) {
			if (!silenced.containsKey(event.getPlayer().getUniqueId())) return;
			event.setCancelled(true);
			sendMessage(strSilenced, event.getPlayer(), MagicSpells.NULL_ARGS);
		}
		
	}
	
	public class CommandListener implements Listener {
		
		@EventHandler(ignoreCancelled=true)
		public void onCommand(PlayerCommandPreprocessEvent event) {
			if (!silenced.containsKey(event.getPlayer().getUniqueId())) return;
			event.setCancelled(true);
			sendMessage(strSilenced, event.getPlayer(), MagicSpells.NULL_ARGS);
		}
		
	}
	
	private class Unsilencer implements Runnable {

		private UUID id;
		private int taskId;
		private boolean canceled = false;

		private Unsilencer(Player player, int delay) {
			id = player.getUniqueId();
			taskId = MagicSpells.scheduleDelayedTask(this, delay);
		}
		
		@Override
		public void run() {
			if (!canceled) silenced.remove(id);
		}

		private void cancel() {
			canceled = true;
			if (taskId > 0) MagicSpells.cancelTask(taskId);
		}
		
	}

}
