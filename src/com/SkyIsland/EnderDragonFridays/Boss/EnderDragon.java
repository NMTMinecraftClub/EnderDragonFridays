package com.SkyIsland.EnderDragonFridays.Boss;


import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.FireballCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType;

public class EnderDragon extends Dragon {

	private String name;
	
	/**
	 * Creates a default enderdragon
	 * @param plugin The EnderDragonFridays plugin this dragon is associated with
	 * @param level The level of the dragon
	 * @param loc
	 * @param name it's name
	 */
	public EnderDragon(int level, String name) {
		
		this.damageTaken = 0;
		
		//Ensure level is a positive integer
		if (level <= 0) {
			level = 1;
		}
		this.level = level;

		this.name = name;


		//Initialize the map of damage each player does to the dragon
		damageMap = new HashMap<UUID, Double>();
		
	}

	@Override
	public void start(Location startLocation) {
		//Spawn an ender dragon
				dragon = (LivingEntity) startLocation.getWorld().spawnEntity(startLocation, EntityType.ENDER_DRAGON);
				
				//Set the dragon's name
				if (name != null && name.length() > 0) {
					dragon.setCustomName(name + " (Lvl " + level + ")");
					dragon.setCustomNameVisible(true);
				}
				
				//Set the dragon's health
				dragon.setMaxHealth(dragon.getMaxHealth() * (1 + (Math.log(level)/Math.log(2))));
				dragon.setHealth(dragon.getMaxHealth());
				
				//Start firing the dragon's fireballs
				//Bukkit.getScheduler().scheduleSyncRepeatingTask(EnderDragonFridaysPlugin.plugin, new FireballCannon(this, 500, 2000), 20, (long) (20 / (1 + (Math.log(level)/Math.log(2)))));
				//Removed ^^ and handle this in FireballCannon instead
				
				new FireballCannon(this, TargetType.MOSTDAMAGE, (20 / (1 + (Math.log(level)/Math.log(2)))), (20 / (1 + (Math.log(level)/Math.log(2)))) + 5);
				//least delay is what it was before. Max is the same + 5 ticks
				
				Bukkit.getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);

	}
	
}
