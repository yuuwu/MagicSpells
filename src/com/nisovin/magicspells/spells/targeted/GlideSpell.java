package com.nisovin.magicspells.spells.targeted;

import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.TargetInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GlideSpell extends TargetedSpell implements TargetedEntitySpell{
	
	private TargetBooleanState targetState;
	
	public GlideSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		targetState = TargetBooleanState.getFromName(getConfigString("target-state", "toggle"));
		
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<LivingEntity> targetInfo = getTargetedEntity(player, power);
			if (targetInfo == null) return noTarget(player);
			LivingEntity target = targetInfo.getTarget();
			if (target == null) return noTarget(player);
			target.setGliding(targetState.getBooleanState(target.isGliding()));
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		target.setGliding(targetState.getBooleanState(target.isGliding()));
		return true;
	}
	
	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return castAtEntity(null, target, power);
	}
	
	
	enum TargetBooleanState {
		
		ON("on", "yes", "true", "enable", "enabled") {
			
			@Override
			public boolean getBooleanState(boolean current) {
				return true;
			}
			
		},
		
		
		OFF("off", "no", "false", "disable", "disabled") {
			
			@Override
			public boolean getBooleanState(boolean current) {
				return false;
			}
			
		},
		
		
		TOGGLE("toggle", "switch") {
			
			@Override
			public boolean getBooleanState(boolean current) {
				return !current;
			}
			
		}
		
		;
		
		private final String[] names;
		
		TargetBooleanState(String... names) {
			this.names = names;
		}
		
		public abstract boolean getBooleanState(boolean current);
		
		private static Map<String, TargetBooleanState> nameToState = new HashMap<>();
		static {
			for (TargetBooleanState value : TargetBooleanState.values()) {
				for (String name : value.names) {
					nameToState.put(name, value);
				}
			}
		}
		
		public static TargetBooleanState getFromName(String name) {
			TargetBooleanState ret = nameToState.get(name.toLowerCase());
			if (ret == null) ret = TargetBooleanState.TOGGLE;
			return ret;
		}
		
	}
	
}
