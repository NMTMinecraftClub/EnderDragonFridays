package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.BlindnessVeil;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.BlindnessVeilEvent;

public class SkeletonBoss implements Listener, Boss {

	private int level;						//The level of the boss
	private LivingEntity boss;				//The actual Entity for the Ender Boss
	private Map<UUID, Double> damageMap;	//The damage each player has done to the ender boss
	private double damageTaken;
	private org.bukkit.entity.EnderDragon healthbar;
	private String name;
	
	/**
	 * Creates a default skeleton boss
	 * @param level The level of the boss
	 * @param name The name of the boss
	 */
	public SkeletonBoss(int level, String name) {

		this.damageTaken = 0;
		
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

		//Spawn an ender boss (skeleton boss)
		boss = (LivingEntity) startingLocation.getWorld().spawnEntity(startingLocation, EntityType.SKELETON);
		((Skeleton) boss).setSkeletonType(SkeletonType.WITHER);
		boss.setRemoveWhenFarAway(false);
		
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
		
		
		boss.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 4);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
		boss.getEquipment().setItemInMainHand(sword);
		
		//Set the boss's name
		if (name != null && name.length() > 0) {
			boss.setCustomName(name + " (Lvl " + level + ")");
			boss.setCustomNameVisible(true);
		}
		this.healthbar = (org.bukkit.entity.EnderDragon) startingLocation.getWorld().spawnEntity(startingLocation.add(0, -40000, 0), EntityType.ENDER_DRAGON);
		
		//Set the boss's health
		boss.setMaxHealth(healthbar.getMaxHealth() * (2 + (Math.log(level)/Math.log(2))));
		boss.setHealth(boss.getMaxHealth());
		
		healthbar.setMaxHealth(boss.getMaxHealth());
		healthbar.setHealth(boss.getMaxHealth());
		healthbar.setCustomName(boss.getCustomName());
		
		new BlindnessVeil(this, 20, 30);
		//least delay is what it was before. Max is the same + 5 ticks

		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);

	}
	
	public LivingEntity getEntity() {
		return this.boss;
	}
	
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
	
	@EventHandler
	public void bossDamage(EntityDamageByEntityEvent event) {
		
		//Do nothing if the boss wasn't damaged
		if (!event.getEntity().equals(boss)) {
			return;
		}
		
		//Add the damage to the total counter
		damageTaken += event.getDamage();
		
		//update healthbar
		healthbar.setHealth(boss.getHealth());
		Random rand = new Random();
		
		//Try to get the player who damaged the boss
		Player player = null;
		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
			Player tmp;
			tmp = boss.getWorld().getPlayers().get(rand.nextInt(boss.getWorld().getPlayers().size()));
			boss.teleport(tmp.getLocation().add(rand.nextInt(10), 0, rand.nextInt(10)));
		}
		else if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player) {
				player = (Player) proj.getShooter();
				boss.teleport(player.getLocation().add(rand.nextInt(10), 0, rand.nextInt(10)));
			}
		}
		
		//If we couldn't find a player, do nothing
		if (player == null) {
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
	
	@EventHandler
	public void bossDeath(EntityDeathEvent event) {
		
		//if the boss has died
		if (event.getEntity().equals(boss)) {
			win();
			Bukkit.getPluginManager().callEvent(new BossDeathEvent(this));
		}
	}
	
	/**
	 * Specifies that the fight was won. This is different from endFight in that this method spawns rewards and
	 * kills the fight.
	 */
	public void win() {
		kill();
		Random rand = new Random();
		for (int i = 0; i < 300; i++) {
			boss.getLocation().getWorld()
			.spawnEntity(boss.getLocation().add(rand.nextFloat() * 10, 0, rand.nextFloat()), EntityType.EXPERIENCE_ORB);
		}
	}
	
	@EventHandler
	public void cannonFired(BlindnessVeilEvent event) {
		//reset health bar just cause
		healthbar.teleport(event.getShooter().getWorld().getSpawnLocation().add(0, -100, 0));
		//blind everyone
		for (Player p : event.getShooter().getWorld().getPlayers()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
		}
		
		Skeleton skel = (Skeleton) boss;
		if (skel.getTarget() == null) {
			List<Player> plays = boss.getWorld().getPlayers();
			if (plays.isEmpty()) {
				return;
			}
			Random rand = new Random();
			skel.setTarget(plays.get(rand.nextInt(plays.size())));
		}
	}

	public void kill() {
		healthbar.damage(healthbar.getMaxHealth());
		healthbar.remove();
		if (!boss.isDead()) {
			boss.damage(boss.getMaxHealth());
		}
	}
	
	public boolean isAlive() {
		
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
	
	
}
