package com.SkyIsland.EnderDragonFridays.Boss.Cannon;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.FireFireballEvent;
import com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType;

/**
 * 
 * @author Skyler
 * @note This could be extracted out to 'body part' class. There could be two. One that's independent and one that just
 * 			exists. I.e. one that extends thread and one that doens't get it's own.
 */
public class FireballCannon extends Cannon {
	

	private Random rand;
	private double offsetX, offsetY, offsetZ;
	private int listIndex;
	
	/**
	 * Creates a fireball cannon with the provided min and max delay. Increments default to 1/10 the range.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 */
	public FireballCannon(Boss boss, TargetType type, double min_delay, double max_delay) {
		this(boss, type, min_delay, max_delay, (max_delay-min_delay)/10); //default to 1/10 the range as increments
	}
	
	/**
	 * Creates a cannon with the passed delay and increments.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 * @param increments The smallest amount of time (<b>again, in ticks</b>) in-between different firing times.
	 */
	public FireballCannon(Boss boss, TargetType type, double min_delay, double max_delay, double increments) {
		this(boss, type, min_delay, max_delay, increments, 0.0, 0.0, 0.0);
	}
	
	/**
	 * Creates a cannon with the passed delay and increments.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 * @param offsetX The offset the fireball will be created from in relation to the boss
	 * @param offsetY The offset the fireball will be created from in relation to the boss
	 * @param offsetZ The offset the fireball will be created from in relation to the boss
	 */
	public FireballCannon(Boss boss, TargetType type, double min_delay, double max_delay, double offsetX, double offsetY, double offsetZ) {
		this(boss, type, min_delay, max_delay, (max_delay - min_delay) / 10, offsetX, offsetY, offsetZ);
	}
	
	/**
	 * Creates a cannon with the passed delay and increments.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 * @param increments The smallest amount of time (<b>again, in ticks</b>) in-between different firing times.
	 * @param offsetX The offset the fireball will be created from in relation to the boss
	 * @param offsetY The offset the fireball will be created from in relation to the boss
	 * @param offsetZ The offset the fireball will be created from in relation to the boss
	 */
	public FireballCannon(Boss boss, TargetType type, double min_delay, double max_delay, double increments, double offsetX, double offsetY, double offsetZ) {
		this.boss = boss;
		this.targetType = type;
		
		this.delayMin = min_delay;
		this.delayMax = max_delay;
		
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		
		listIndex = 0;
		
		//make sure min and max aren't reversed
		if (delayMin > delayMax) {
			delayMax = min_delay;
			delayMin = max_delay;
		}
		
		//make sure increments aren't negative
		delayIncr = Math.abs(increments);
		
		//we need to make sure the increments are sound
		if ( ((delayMax - delayMin) / delayIncr) % 1 <= .0001) {
			//if increments doesn't 'evenly' divide our range, we're in trouble
			//so we'll round to nearest increment that would
			float even = (int) Math.round((delayMax - delayMin) / delayIncr);
			delayIncr = (delayMax - delayMin) / even; 
		}
		
		delayRange = (int) Math.round((delayMax - delayMin) / delayIncr); //store so we don't have to do this math over and over again
		
		//create our random
		rand = new Random();
		
		Long time = (long) (delayMin + (delayIncr * (rand.nextInt(delayRange)))); 
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnderDragonFridaysPlugin.plugin, this, time);
	}
	
	@Override
	public void run() {
		LivingEntity dDragon = boss.getEntity();

		//very first, make sure boss is still alive. If not, kill self
		if (boss == null || !boss.isAlive()) {
			return;
		}
		Long time = (long) (delayMin + (delayIncr * (rand.nextInt(delayRange))));
		
		//make sure there are players to fire at
		if (dDragon.getWorld().getPlayers().isEmpty()) {
			//reschedule the task for a later time
			Bukkit.getScheduler().scheduleSyncDelayedTask(EnderDragonFridaysPlugin.plugin, this, time);
			return;
		}
		
		//there are players in the world
		
		//use our targetType to figure out who we want to attack
		Player target = null;
		
		switch (targetType) {
		case MOSTDAMAGE:
			//try and get who has done the most damage
			target = boss.getMostDamage();
			break;
		case ALL_CYCLE:
			if (boss.getDamageList().isEmpty()) {
				break;
			}
			Player player;
			if (listIndex >= boss.getDamageList().size()) {
				listIndex = 0;
			}
			int startIndex = listIndex;
			do {
				player = Bukkit.getPlayer(boss.getDamageList().get(listIndex));
				if (!player.getWorld().getName().equals(boss.getEntity().getWorld().getName())) {
					listIndex++;
					continue;
				}
				target = player;
				listIndex++;
				break;
			} while (startIndex != listIndex);
			break;
		
		case RANDOM:
			List<Player> plays = boss.getEntity().getWorld().getPlayers();
			
			if (plays.isEmpty()) {
				target = null;
				break;
			}
			
			target = plays.get(rand.nextInt(plays.size()));
			
			break;
			//if target is null, we want it to operate like NEAREST. So we don't break and instead continue;
		case NEAREST:
		default:
			boolean go = true;
			Entity next;
			for (int i = 10; go == true && i < 200; i++) {
				List<Entity> players = boss.getEntity().getNearbyEntities(i, 150, i);
				if (players.isEmpty())
					continue;
				Iterator<Entity> it = players.iterator();
				while (it.hasNext()) {
					next = it.next();
					if (next instanceof Player) {
						//we found a player
						target = (Player) next;
						go = false;
						break;
					}
				}
				//implicit else: continue
				//really hoping this doesn't throw concurrent modification. This is on a separate thread
			}
			break;
		}
				
		//reschedule this event to run
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnderDragonFridaysPlugin.plugin, this, time);
		if (target != null) {
			//actually launch fireball if we have a target
			Bukkit.getPluginManager().callEvent(new FireFireballEvent(boss.getEntity(), target, boss.getEntity().getLocation().add(offsetX, offsetY, offsetZ)));
		}
		
	}
}
