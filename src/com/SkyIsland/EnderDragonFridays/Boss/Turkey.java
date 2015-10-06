package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Component.ChickenMinion;
import com.SkyIsland.EnderDragonFridays.Boss.Component.ChickenRegroupEvent;

public class Turkey implements Boss, Listener {
	
	/**
	 * A concrete list of all the living entities involved with this boss
	 */
	private List<LivingEntity> entities;
	
	/**
	 * A list of any chickens spawned as parts
	 */
	private List<ChickenMinion> chickens;
	
	/**
	 * Health count for the boss. These aren't supposed to equate to minecraft health counts. Instead, this
	 * is our own count of health. 
	 */
	private int maxHealth;
	private int health;
	
	private String name;
	
	/**
	 * Who has damaged the boss and how much
	 */
	private Map<UUID, Double> damageMap;
	
	/**
	 * Is the boss alive, or dying?
	 */
	private boolean alive;
	
	/**
	 * Whether or not the boss is in dragon form.
	 */
	private boolean dragonForm;
	
	/**
	 * What world does this boss belong to?
	 */
	private World world;
	
	private int level;
	
	
	public Turkey(int level) {
		this(level, null);
	}
	
	public Turkey(int level, String name) {
		if (world == null) {
			EnderDragonFridaysPlugin.plugin.getLogger().info(ChatColor.RED + "INVALID TURKEY CREATION! WORLD"
					+ " is null!" + ChatColor.RESET);
			return;
		}
		
		if (name == null) {
			//hardcoded default!
			name = "Kjilnor the Fierce";
		}
		
		this.name = name;
		
		if (level <= 0) {
			level = 1;
		}
		
		this.level = level;
		
		/**
		 * the Health is going to be how many chickens compose the boss.
		 * This is standard 1 + (log2 of level) * 25, where 25 is the base. Every new power of 8
		 * achieved increases the chicken count by  25:
		 * 1: 25
		 * 2: 50
		 * 4: 75
		 * 8: 100   etc.
		 */
		maxHealth = (int) (Math.log(level)/Math.log(2) + 1) * (25);
		health = maxHealth;

		
		damageMap = new HashMap<UUID, Double>();
		
	}
	
	@Override
	public void start(Location startingLocation) {
		dragonForm = true;
		this.world = startingLocation.getWorld();
		
		org.bukkit.entity.EnderDragon dragon = (EnderDragon) world.spawnEntity(world.getSpawnLocation().add(0, 50, 0), EntityType.ENDER_DRAGON);
		
		dragon.setMaxHealth(maxHealth);
		dragon.setHealth(health);
		dragon.setCustomName(name + " (Lvl. " + level + ")");
		
		alive = true;
		
		entities = new LinkedList<LivingEntity>();
		entities.add(dragon);
		chickens = new LinkedList<ChickenMinion>();
		
		Bukkit.getPluginManager().registerEvents(this,  EnderDragonFridaysPlugin.plugin);
	}
	
	@EventHandler
	public void damageBoss(EntityDamageByEntityEvent e) {
		if (e.isCancelled() || entities == null || entities.isEmpty() || e.getCause() == DamageCause.CUSTOM) {
			return;
		}
		if (e.getEntity().getWorld().getName() != world.getName()) {
			return;
		}
		
		if (e.getEntity() instanceof Chicken) {
			//quickly check if it's void/suffocation and cancel if it is
			DamageCause d = e.getCause();
			if (d == DamageCause.VOID || d == DamageCause.SUFFOCATION) {
				e.setCancelled(true);
				e.getEntity().teleport(world.getSpawnLocation().add(0,50,0));
				return;
			}
		}
		
		if (!entities.contains(e.getEntity())) {
			return;
		}

		Entity damager = e.getDamager();
		if (!(damager instanceof Player) && !(damager instanceof Projectile)) {
			e.setCancelled(true);
			return;
		}
		
		Player player = null;
		if (damager instanceof Projectile) {
			Projectile p = (Projectile) damager;
			if (p.getShooter() instanceof Player) {
				player = (Player) p.getShooter();
				p.remove(); //don't wnat to generate arrows!
			}
		} else {
			player = (Player) damager;
		}
		
		if (player == null) {
			return;
		}
		
		//Chicken check
		if (e.getEntity() instanceof Chicken) {
			//chicken attacked. We only care if it's about to die.
			Chicken chicken = (Chicken) e.getEntity();
			if (chicken.getHealth() <= e.getDamage()) {
				//going to die. Let it, but award the player a point
				double damage;
				damage = 1;
				if (damageMap.containsKey(player.getUniqueId())) {
					damage += damageMap.get(player.getUniqueId());
				}
				damageMap.put(player.getUniqueId(), damage);
				
				//remove 1 from the boss's health
				health -= 1;
				
				world.playEffect(chicken.getLocation(), Effect.GHAST_SHRIEK, 1007); //play ender effect
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 1));
				
				//regroup every so often back into a dragon. This should happen n times per fight, where
				//n is the number of people playing!
				if (health == 1 || health % Math.floor(maxHealth / level) == 0) {
					//if n = 4, maxhealth is 75. so if health = 72, 54, 36, or 18. Or 0, but that shouldn't
					//happen
					groupUp();
				}
			}
			
			
			return;
		}
		

		//someone hit it while it was flying around. Remove a single HP and break down.
		health -= 1;
		
		//award that person with a point
		double damage;
		damage = 1;
		if (damageMap.containsKey(player.getUniqueId())) {
			damage += damageMap.get(player.getUniqueId());
		}
		damageMap.put(player.getUniqueId(), damage);
		
		//if health now = 0, then kill the dragon and finish the fight.
		if (health != 0) {
			e.setCancelled(true);
			//morph into chicken mode
			breakDown();
		}
		else {
			//make sure it dies
			((LivingEntity) e.getEntity()).damage(maxHealth);
			win();
			Bukkit.getPluginManager().callEvent(new BossDeathEvent(this));
		}
		
		
		
	}
	
	private void breakDown() {
		//extra check against fraudulent numbers
		if (health <= 1) {
			health = 2;
		}
		
		Entity dragon = entities.get(0);
		
		entities.clear();
		chickens.clear();
		
		Location loc;
		ChickenMinion min;
		
		for (int i = 0; i < health; i++) {
			//create a chicken for every point of health left
			loc = dragon.getLocation();
			loc.add(EnderDragonFridaysPlugin.rand.nextDouble() * 20, 0, EnderDragonFridaysPlugin.rand.nextDouble() * 20);
			min = new ChickenMinion(this, loc);
			chickens.add(min);
			entities.add(min.getChicken());
		}
		
		dragon.remove();
		dragonForm = false;
	}
	
	private void groupUp() {
		Bukkit.getPluginManager().callEvent(new ChickenRegroupEvent());
		
		entities.clear();
		chickens.clear();
		

		org.bukkit.entity.EnderDragon dragon = (EnderDragon) world.spawnEntity(world.getSpawnLocation().add(0, 50, 0), EntityType.ENDER_DRAGON);
		
		dragon.setMaxHealth(maxHealth);
		dragon.setHealth(health);
		dragon.setCustomName(name + " (Lvl. " + level + ")");
		dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 90000, 1));
		
		alive = true;
		
		entities = new LinkedList<LivingEntity>();
		entities.add(dragon);
		dragonForm = true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public LivingEntity getEntity() {
		if (entities != null && entities.size() > 0) {
			return entities.get(0);
		}
		else
			return null;
	}

	@Override
	public Player getMostDamage() {
		if (damageMap == null || damageMap.isEmpty()) {
			return null;
		}
		
		Player player = null;
		double max = -1.00000;
		
		for (UUID id : damageMap.keySet()) {
			if (damageMap.get(id) > max) {
				player = Bukkit.getPlayer(id);
				max = damageMap.get(id);
			}
			
		}
		
		
		return player;
	}

	@Override
	public void win() {
		if (alive) {
			kill();
		}
	}
	
	@Override
	public void kill() {
		alive = false;

		if (!entities.isEmpty()) {
			for (Entity e : entities) {
				if (!e.isDead()) {
					e.remove();
				}
			}
			entities.clear();
		}
		if (!chickens.isEmpty()) {
			for (ChickenMinion ch : chickens) {
				ch.kill();
			}
			chickens.clear();
		}
	}

	@Override
	public List<UUID> getDamageList() {
		return new LinkedList<UUID>(damageMap.keySet());
	}
	
	/**
	 * Print out custom message to player letting them know how they did
	 * @param map
	 */
	public void congradulatePlayers(Map<UUID, Double> map) {
		for (Entry<UUID, Double> entry : map.entrySet()) {
			
			Player player = Bukkit.getPlayer(entry.getKey());
			try{
				player.sendMessage("Nice Fight!\n  "
					+ "You did " + (entry.getValue().intValue()) + " points of damage!\n"
							+ "Your contribution was " + ((entry.getValue() / maxHealth) * 100) + "%!");
			}
			catch (Exception e){
				//Player wasn't online anymore
			}
		}
	}

	@Override
	public double getDamageTaken() {
		return maxHealth - health;
	}

	@Override
	public boolean equals(Boss boss) {
		return toString().equals(boss.toString());
	}
	
	@Override
	public String toString() {
		return "TurkeyBoss[" + name + "]";
	}

	@Override
	public Map<UUID, Double> getDamageMap() {
		return damageMap;
	}
	
	public boolean isDragonForm() {
		return dragonForm;
	}
}
