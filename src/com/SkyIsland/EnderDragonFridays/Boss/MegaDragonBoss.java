package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.BlazeCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.FireballCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.FireFireballEvent;
import com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType;

public class MegaDragonBoss implements Listener, Boss {
	
	private int level;						//The level of the boss
	private LivingEntity boss;				//The actual Entity for the boss
	private Map<UUID, Double> damageMap;	//The damage each player has done to the boss
	private double damageTaken;
	private String name;
	private MegaDragonBoss partner;
	private boolean doCannonFire;
	private boolean hasPartner;
	private boolean started;
	
	/**
	 * Creates a default mega enderdragon
	 * @param level The level of the boss
	 * @param name The name of the boss
	 */
	public MegaDragonBoss(int level, String name) {
		
		this.damageTaken = 0;
		this.doCannonFire = true;
		
		//Ensure level is a positive integer
		if (level <= 0) {
			level = 1;
		}
		this.level = level;
		this.name = name;

		//Initialize the map of damage each player does to the boss
		damageMap = new HashMap<UUID, Double>();

	}
	/**
	 * Creates a default mega enderdragon
	 * @param level The level of the boss
	 * @param name The name of the boss
	 * @param bossPartner the boss's partner
	 */
	public MegaDragonBoss(int level, String name, Boss bossPartner) {
		if(bossPartner == null){
			this.partner = null;
			this.hasPartner = true;
		}else{
			this.partner = (MegaDragonBoss) bossPartner;
			this.hasPartner = true;
		}
		this.damageTaken = 0;
		this.doCannonFire = true;
		this.started = false;
		
		//Ensure level is a positive integer
		if (level <= 0) {
			level = 1;
		}
		this.level = level;
		this.name = name;

		//Initialize the map of damage each player does to the boss
		damageMap = new HashMap<UUID, Double>();

	}
	@Override
	public void start(Location startingLocation) {
		this.started = true;
		if(hasPartner && !partner.isStarted()){
			partner.start(startingLocation);
		}
		//Spawn an ender boss
		boss = (LivingEntity) startingLocation.getWorld().spawnEntity(startingLocation, EntityType.ENDER_DRAGON);
		//must fix bug with AIs
		EnderDragon temp = (EnderDragon) boss;
		temp.setPhase(EnderDragon.Phase.STRAFING);
		
		//Set the boss's name
		if (name != null && name.length() > 0) {
			boss.setCustomName(name + " (Lvl " + level + ")");
			boss.setCustomNameVisible(true);
		}
		
		//Set the boss's health
		boss.setMaxHealth(boss.getMaxHealth() * (2 + (Math.log(level)/Math.log(2))));
		boss.setHealth(boss.getMaxHealth());
		
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
	
	private boolean isStarted() {
		return this.started;
	}
	@Override
	public LivingEntity getEntity() {
		return this.boss;
	}

	@Override
	public Player getMostDamage() {
		if (damageMap.isEmpty()) {
			return null;
		}
		
		Player player = null;
		double max = -999999.0;
		Player play;
		for (Entry<UUID, Double> entry : damageMap.entrySet()) {
			play = Bukkit.getPlayer(entry.getKey());
			if (play != null && entry.getValue() > max && play.getWorld().getName().equals(boss.getWorld().getName()))
			{
				player = play;
				max = entry.getValue();
			}
		}
		
		return player;
	}
	/**
	 * Adds a check to make sure pairs of dragons do not glitch while attempting to roost on the same spot.
	 * @param event dragon has changed phases
	 */
	@EventHandler
	public void onPhaseChange(EnderDragonChangePhaseEvent event){
		if(this.hasPartner){
			EnderDragon other = (EnderDragon)this.partner.getEntity();
			EnderDragon boss = event.getEntity();
			if(boss.getPhase() == EnderDragon.Phase.FLY_TO_PORTAL){
				checkDragonPhase(boss, other);
			}else if(boss.getPhase() == EnderDragon.Phase.LAND_ON_PORTAL){
				checkDragonPhase(boss, other);
			}
		}
	}
	/**
	 * checks to see if the phase conflicts with the actions of the other dragon
	 * if true, revert to an attack instead
	 * @param boss the dragon attempting to go to the portal
	 * @param other the partner to be checked
	 */
	private void checkDragonPhase(EnderDragon boss, EnderDragon other){
		if(other.getPhase() == EnderDragon.Phase.FLY_TO_PORTAL){
			boss.setPhase(EnderDragon.Phase.CIRCLING);
		}else if(other.getPhase() == EnderDragon.Phase.HOVER){
			boss.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
		}else if(other.getPhase() == EnderDragon.Phase.LAND_ON_PORTAL){
			boss.setPhase(EnderDragon.Phase.BREATH_ATTACK);
		}
	}
	
	@EventHandler
	public void bossDamage(EntityDamageByEntityEvent event) {
		
		//Do nothing if the boss wasn't damaged
		if (!event.getEntity().equals(boss)) {
				return;
		}
		if(event.getEntity().equals(boss) ){
			//Add the damage to the total counter
			damageTaken += event.getDamage();
			
			Bukkit.getLogger().info(this.name + ": " + boss.getHealth());
			//Try to get the player who damaged the boss
			Player player = null;
			if (event.getDamager() instanceof Player) {
				player = (Player) event.getDamager();
			}
			else if (event.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) event.getDamager();
				if (proj.getShooter() instanceof Player){
					player = (Player) proj.getShooter();
				}
			}
			
			//If we couldn't find a player, do nothing
			if (player == null){
				return;
			}
			
			
			//Add the player to the hashmap if needed
			if (!damageMap.containsKey(player.getUniqueId())) {
				damageMap.put(player.getUniqueId(), 0.0);
			}
			
			//Update the damage for the player
			double oldDamage = damageMap.get(player.getUniqueId());
			damageMap.put(player.getUniqueId(), oldDamage + event.getDamage()); 
		}
		else if(event.getEntity().equals(partner.getEntity())){
			
		}
	}
	
	@EventHandler
	public void bossDeath(EntityDeathEvent event) {
		//if not the main boss, don't win;
		if(this.hasPartner){
			if(this.partner.isAlive()){
				return;
			}
		}
		//if the main boss has died
		if (event.getEntity().equals(boss)) {
			win();
			Bukkit.getPluginManager().callEvent(new BossDeathEvent(this));
		}
	}
	
	/**
	 * Wins the fight, taking care of the cleanup
	 */
	@Override
	public void win() {
		kill();
	}
	
	@EventHandler
	public void cannonFired(FireFireballEvent event){
		if(this.doCannonFire){
			LivingEntity target = event.getTarget();
			LivingEntity shooter = event.getShooter();
			Bukkit.getLogger().info("Shooting at: " + ((Player)target).getDisplayName());
			Bukkit.getLogger().info("Shooting from: " + (shooter).getCustomName());
			Vector launchV;
			Location pPos, dPos;
			dPos = shooter.getEyeLocation();
			pPos = target.getEyeLocation();
			launchV = pPos.toVector().subtract(dPos.toVector());
			
			LargeFireball f = shooter.launchProjectile(LargeFireball.class);
			f.setDirection(launchV.normalize());
		}
	}

	@Override
	public void kill() {
		if (hasPartner){
			if(!boss.isDead()){
				boss.remove();
				
			}
			if(partner.isAlive()){
				partner.getEntity().remove();
			}
		}
		if (!boss.isDead()) {
			System.out.println("killing boss");
			boss.remove();
		}
	}

	@Override
	public boolean isAlive(){
		
		if (boss == null) {
			return false;
		}
		
		return (!boss.isDead());
	}

	@Override
	public List<UUID> getDamageList() {
		return new ArrayList<UUID>(damageMap.keySet());
	}
	
	@Override
	public Map<UUID, Double> getDamageMap() {
		return damageMap;
	}

	@Override
	public double getDamageTaken() {
		return damageTaken;
	}


	@Override
	public boolean equals(Boss _boss) {
		return this.boss.getUniqueId().equals(_boss.getEntity().getUniqueId());
	}
	public void setCannon(boolean b) {
		this.doCannonFire = b;
		
	}
	public void setPartner(Boss boss) {
		this.hasPartner = true;
		this.partner = (MegaDragonBoss)boss;
	}
	
}
