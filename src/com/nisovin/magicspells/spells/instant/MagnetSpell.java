package com.nisovin.magicspells.spells.instant;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class MagnetSpell extends InstantSpell {
	
	private double range;	
	private double velocity;
	
	private boolean teleport;
	private boolean forcepickup;
	private boolean removeItemGravity;
	
	public MagnetSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		range = getConfigDouble("range", 5.0);
		velocity = getConfigDouble("velocity", 1.0);
		teleport = getConfigBoolean("teleport-items", false);
		forcepickup = getConfigBoolean("force-pickup", false);
		removeItemGravity = getConfigBoolean("remove-item-gravity", false);
		
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			
			double radius = this.range*power;			
			
			List<Entity> entities = player.getNearbyEntities(radius, radius, radius);
			for (Entity entity : entities) {
				if (entity instanceof Item) {
									
					Item item = (Item) entity;
					ItemStack stack = item.getItemStack();
					
					if (stack.getAmount() > 0 && !(item.isDead())) {
						if (forcepickup) {						
							item.setPickupDelay(0);
							magnet(player, item, power);
					    } else {
							if (item.getPickupDelay() < item.getTicksLived()){
								magnet(player, item, power);								
						    }
						}
					}
				}					
			}
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	private void magnet(Player player, Item item, float power) {
		
		// handle gravity removal
		if (removeItemGravity) {
			MagicSpells.getVolatileCodeHandler().setGravity(item, false);
		}
		
		// handle item entity movement
		if (teleport) {
			item.teleport(player);	
		} else {
			item.setVelocity(player.getLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(velocity*power));
		}
		
		playSpellEffects(EffectPosition.PROJECTILE, item);
		playSpellEffects(EffectPosition.CASTER, player);
	}
	
}
