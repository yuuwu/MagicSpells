package com.nisovin.magicspells.spells.instant;

import java.util.HashSet;

import com.nisovin.magicspells.Subspell;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class LeapSpell extends InstantSpell {

	private double forwardVelocity;
	private double upwardVelocity;
	private boolean cancelDamage;
	private boolean clientOnly;
	private Subspell landSpell;

	private HashSet<Player> jumping = new HashSet<>();

	public LeapSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		this.forwardVelocity = getConfigInt("forward-velocity", 40) / 10D;
		this.upwardVelocity = getConfigInt("upward-velocity", 15) / 10D;
		this.cancelDamage = getConfigBoolean("cancel-damage", true);
		this.clientOnly = getConfigBoolean("client-only", false);

		String landSpellName = getConfigString("land-spell", null);

		if (landSpellName != null) landSpell = new Subspell(landSpellName);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (landSpell == null) return;
		if (!landSpell.process()) {
			landSpell = null;
			MagicSpells.error("Invalid land-spell for " + this.getInternalName() + " defined");
		}
	}

	public boolean isJumping(Player pl) {
		return jumping.contains(pl);
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Vector v = player.getLocation().getDirection();
			v.setY(0).normalize().multiply(this.forwardVelocity * power).setY(this.upwardVelocity * power);
			if (this.clientOnly) {
				MagicSpells.getVolatileCodeHandler().setClientVelocity(player, v);
			} else {
				player.setVelocity(v);
			}
			this.jumping.add(player);
			playSpellEffects(EffectPosition.CASTER, player);
		}

		return PostCastAction.HANDLE_NORMALLY;
	}

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL || !(e.getEntity() instanceof Player)) return;
        Player pl = (Player)e.getEntity();
        if (this.jumping.isEmpty()) return;
        if (!jumping.remove(pl)) return;
        if (landSpell != null) landSpell.cast(pl, 1);
        playSpellEffects(EffectPosition.TARGET, pl.getLocation());
        if (cancelDamage) e.setCancelled(true);
    }

}
