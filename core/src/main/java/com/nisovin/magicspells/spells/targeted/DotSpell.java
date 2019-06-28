package com.nisovin.magicspells.spells.targeted;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.spells.SpellDamageSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;
import com.nisovin.magicspells.events.MagicSpellsEntityDamageByEntityEvent;

public class DotSpell extends TargetedSpell implements TargetedEntitySpell, SpellDamageSpell {

	private Map<UUID, Dot> activeDots;

	private int delay;
	private int interval;
	private int duration;

	private float damage;

	private boolean preventKnockback;

	private String spellDamageType;

	public DotSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		delay = getConfigInt("delay", 1);
		interval = getConfigInt("interval", 20);
		duration = getConfigInt("duration", 200);

		damage = getConfigFloat("damage", 2);

		preventKnockback = getConfigBoolean("prevent-knockback", false);

		spellDamageType = getConfigString("spell-damage-type", "");

		activeDots = new HashMap<>();
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<LivingEntity> targetInfo = getTargetedEntity(player, power);
			if (targetInfo == null) return noTarget(player);
			applyDot(player, targetInfo.getTarget(), targetInfo.getPower());
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		applyDot(caster, target, power);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		applyDot(null, target, power);
		return true;
	}

	@Override
	public String getSpellDamageType() {
		return spellDamageType;
	}
	
	private void applyDot(Player caster, LivingEntity target, float power) {
		Dot dot = activeDots.get(target.getUniqueId());
		if (dot != null) {
			dot.dur = 0;
			dot.power = power;
		} else {
			dot = new Dot(caster, target, power);
			activeDots.put(target.getUniqueId(), dot);
		}

		if (caster != null) playSpellEffects(caster, target);
		else playSpellEffects(EffectPosition.TARGET, target);
	}
	
	@EventHandler
	void onDeath(PlayerDeathEvent event) {
		Dot dot = activeDots.get(event.getEntity().getUniqueId());
		if (dot != null) dot.cancel();
	}
	
	private class Dot implements Runnable {
		
		private Player caster;
		private LivingEntity target;
		private float power;

		private int taskId;
		private int dur = 0;

		private Dot(Player caster, LivingEntity target, float power) {
			this.caster = caster;
			this.target = target;
			this.power = power;
			taskId = MagicSpells.scheduleRepeatingTask(this, delay, interval);
		}
		
		@Override
		public void run() {
			dur += interval;
			if (dur > duration) {
				cancel();
				return;
			}

			if (target.isDead() || !target.isValid()) {
				cancel();
				return;
			}

			double dam = damage * power;
			SpellApplyDamageEvent event = new SpellApplyDamageEvent(DotSpell.this, caster, target, dam, DamageCause.MAGIC, spellDamageType);
			EventUtil.call(event);
			dam = event.getFinalDamage();

			if (preventKnockback) {
				MagicSpellsEntityDamageByEntityEvent devent = new MagicSpellsEntityDamageByEntityEvent(caster, target, DamageCause.ENTITY_ATTACK, damage);
				EventUtil.call(devent);
				if (!devent.isCancelled()) target.damage(devent.getDamage());
			} else target.damage(dam, caster);

			target.setNoDamageTicks(0);
			playSpellEffects(EffectPosition.DELAYED, target);
		}

		private void cancel() {
			MagicSpells.cancelTask(taskId);
			activeDots.remove(target.getUniqueId());
		}

	}

}
