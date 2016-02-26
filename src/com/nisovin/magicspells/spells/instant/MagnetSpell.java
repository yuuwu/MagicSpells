package com.nisovin.magicspells.spells.instant;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class MagnetSpell extends InstantSpell {
	
	private int range;	
	private int velocity;
	
	private boolean teleport;
	private boolean forcepickup;

	public MagnetSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		range = getConfigInt("range", 5);
		velocity = getConfigInt("velocity", 10);
		teleport = getConfigBoolean("teleport-items", false);
		forcepickup = getConfigBoolean("force-pickup", false);
		
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			
			int radius = Math.round(this.range*power);			
			
			List<Entity> entities = player.getNearbyEntities(radius, radius, radius);
			for (Entity entity : entities) {
				if(entity instanceof Item) {
									
					Item item = (Item) entity;
					ItemStack stack = item.getItemStack();
					
					if(stack.getAmount() > 0 && !(item.isDead())) {
						if(forcepickup) {						
							item.setPickupDelay(0);
							magnet(player, item, power);
					    } else {
							if(item.getPickupDelay() < item.getTicksLived()){
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
		if(teleport) {
		item.teleport(player);	
		} else {
		item.setVelocity(player.getLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply((velocity / 10.0)*power));
		}
		playSpellEffects(EffectPosition.CASTER, player);
	}
	
}
