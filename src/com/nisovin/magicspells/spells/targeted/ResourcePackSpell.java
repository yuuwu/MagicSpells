package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.TargetInfo;

public class ResourcePackSpell extends TargetedSpell {

	private String url = null;
	
	public ResourcePackSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		url = getConfigString("url", null);
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<Player> target = getTargetedPlayer(player, power);
			Player targetPlayer = target.getTarget();
			if (targetPlayer == null) return noTarget(player);
			targetPlayer.setResourcePack(url);
			return PostCastAction.HANDLE_NORMALLY;
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

}
