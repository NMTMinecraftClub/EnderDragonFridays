package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Represents a boss monster.<br />
 * Bosses are intended to be a class that works to manipulate in-game objects to
 * present players with a challenging boss fight.<br />
 * Bosses are <b>not</b> a decendent of {@link org.bukkit.entity.Entity Entity} and shouldn't
 * be thought of as such. Instead, a Boss envolopes all entities involved in the boss fight, all
 * components a boss fight will have (e.g. cannons in the case of EnderDragonFridays EnderDragons), AND
 * all data and methods related to the boss fight itself.<br />
 * Also note that bosses start not 'started'. When started, the boss should create all the entities that are associated with
 * it and listen fore vents, play the fight.
 * @author Skyler
 *
 */
public interface Boss {
	
	public void start(Location startLocation);
	
	public boolean isAlive();
	
	public LivingEntity getEntity();
	
	public Player getMostDamage();
	
	public double getDamageTaken();
	
	public void win();
	
	public void kill();
	
	public List<UUID> getDamageList();
	
	public boolean equals(Boss boss);
	
	public Map<UUID, Double> getDamageMap();
}
