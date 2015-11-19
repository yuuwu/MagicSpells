package com.nisovin.magicspells.spelleffects;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ConfigData;

public class SmokeSwirlEffect extends SpellEffect {

	@ConfigData(field="duration", dataType="int", defaultValue="20")
	int duration = 20;
	
	@Override
	public void loadFromString(String string) {
		if (string != null && !string.isEmpty()) {
			try {
				duration = Integer.parseInt(string);
			} catch (NumberFormatException e) {
				DebugHandler.debugNumberFormat(e);
			}
		}
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		duration = config.getInt("duration", duration);
	}

	int[] x = {1,1,0,-1,-1,-1,0,1};
	int[] z = {0,1,1,1,0,-1,-1,-1};
	int[] v = {7,6,3,0,1,2,5,8};
	
	@Override
	public void playEffectLocation(Location location) {		
		new Animator(location, 1, duration);
	}
	
	@Override
	public void playEffectEntity(Entity entity) {
		new Animator(entity, 1, duration);
	}
	
	private class Animator implements Runnable {
		
		private Entity entity;
		private Location location;
		private int interval;
		private int duration;
		private int iteration;
		private int taskId;
		
		public Animator(Location location, int interval, int duration) {
			this(interval, duration);
			this.location = location;
		}
		
		public Animator(Entity entity, int interval, int duration) {
			this(interval, duration);
			this.entity = entity;
		}
		
		public Animator(int interval, int duration) {
			this.interval = interval;
			this.duration = duration;
			this.iteration = 0;
			this.taskId = MagicSpells.scheduleRepeatingTask(this, 0, interval);
		}
		
		public void run() {
			if (iteration * interval > duration) {
				Bukkit.getScheduler().cancelTask(taskId);
			} else {
				int i = iteration % 8;
				Location loc;
				if (location != null) {
					loc = location;
				} else {
					loc = entity.getLocation();
				}
				loc.getWorld().playEffect(loc.clone().add(x[i], 0, z[i]), Effect.SMOKE, v[i]);
				iteration++;
			}
		}
	}
	
}
