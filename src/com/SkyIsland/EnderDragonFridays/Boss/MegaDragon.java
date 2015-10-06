package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.BlazeCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.FireballCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType;

public class MegaDragon extends Dragon {
	
	private String name;
	
	/**
	 * Creates a default mega dragon
	 * @param plugin The EnderDragonFridays plugin this dragon is associated with
	 * @param level The level of the dragon
	 * @param loc
	 * @param name it's name
	 */
	public MegaDragon(int level, String name) {
		
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
	public void start(Location startingLocation) {

		//Spawn an ender dragon
		dragon = (LivingEntity) startingLocation.getWorld().spawnEntity(startingLocation, EntityType.ENDER_DRAGON);
		
		//Set the dragon's name
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name + " (Lvl " + level + ")");
			dragon.setCustomNameVisible(true);
		}
		
		//Set the dragon's health
		dragon.setMaxHealth(dragon.getMaxHealth() * (2 + (Math.log(level)/Math.log(2))));
		dragon.setHealth(dragon.getMaxHealth());
		
		//calculate base-times
		Double baseTime = (1 + (Math.log(level)/Math.log(2)));
		
		//Create cannons
		new FireballCannon(this, TargetType.MOSTDAMAGE, (40 / baseTime), (40 / baseTime) + 5, 10.0, 0.0, 0.0);
		new FireballCannon(this, TargetType.MOSTDAMAGE, (40 / baseTime), (40 / baseTime) + 5, -10.0, 0.0, 0.0);
		new BlazeCannon(this, TargetType.MOSTDAMAGE, (20 / baseTime), (20 / baseTime) + 5, 0.0, 0.0, 10.0);
		
		Random rand = new Random();
		for (int i = 5; i < level; i+=4) {
			boolean fireball = rand.nextBoolean(); //is it going to be a fireball cannon or a blaze cannon?
			TargetType type;
			if (rand.nextBoolean()) { //is it going to by all_cyclic?
				type = TargetType.ALL_CYCLE;
			}
			else {
				type = TargetType.RANDOM;
			}
			System.out.println("Making an additional cannon of target type [" + type.toString() + "]!");
			if (fireball) {
				new FireballCannon(this, type, (40 / baseTime), (40 / baseTime) + 5, (rand.nextDouble() * 10) - 5, (rand.nextDouble() * 10) - 5, (rand.nextDouble() * 10) - 5);
			} else {
				new BlazeCannon(this, type, (20 / baseTime), (20 / baseTime) + 5, (rand.nextDouble() * 10) - 5, (rand.nextDouble() * 10) - 5, (rand.nextDouble() * 10) - 5);
			}
		}
		
		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);

	}
	
}
