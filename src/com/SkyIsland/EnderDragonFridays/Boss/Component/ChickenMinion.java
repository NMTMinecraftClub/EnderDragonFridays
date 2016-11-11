package com.SkyIsland.EnderDragonFridays.Boss.Component;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Boss;

public class ChickenMinion implements Listener {
	
	private Chicken chicken;
	
	private Wolf vehicle;
	
	private Boss boss;
	
	private boolean falling;
	
	private FallDetector fallDetector;
	
	private class FallDetector extends BukkitRunnable {
		
		private Chicken chicken;
		
		public FallDetector(Chicken chicken) {
			this.chicken = chicken;
			System.out.println("Creating Fall Detector!");
		}
		
		@Override
		public void run() {
			//check if chicken has landed
			if (chicken == null) {
				return;
			}
			if (chicken.isOnGround()) {
				Bukkit.getPluginManager().callEvent(new ChickenLandEvent(chicken));
				chicken = null;
			}
		}
	}
	
	public ChickenMinion(Boss _boss, Location loc) {
		this.boss = _boss;
		this.falling = true;
		chicken = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
		chicken.setRemoveWhenFarAway(false);
		
		Bukkit.getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);
		
		fallDetector = new FallDetector(chicken);
		fallDetector.runTaskTimer(EnderDragonFridaysPlugin.plugin, 10, 10);
	}
	
	private void mount() {
		vehicle = (Wolf) chicken.getWorld().spawnEntity(chicken.getLocation(), EntityType.WOLF);
		vehicle.setPassenger(chicken);
		vehicle.setBaby();
		vehicle.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 1));
		vehicle.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
		vehicle.setTamed(false);
		vehicle.setAngry(true);
		vehicle.setRemoveWhenFarAway(false);
		vehicle.setAgeLock(true);
		
		List<Player> players = chicken.getWorld().getPlayers();
		vehicle.setTarget(players.get(EnderDragonFridaysPlugin.rand.nextInt(players.size())));
		//lulz attack a random player in the world
	}
	
	@EventHandler
	public void land(ChickenLandEvent e) {
		if (this.falling && e.getChicken().equals(chicken)) {
			//our chicken has landed
			
			mount();
			this.falling = false;
			this.fallDetector.cancel();
			this.fallDetector = null;
			
			if (vehicle.getTarget() == null) {
				return;
			}
			
			//is our new fighter too far away?
			if (vehicle.getLocation().distance(vehicle.getTarget().getLocation()) > 30 || !vehicle.hasLineOfSight(vehicle.getTarget())) {
				//if far away or behind a wall, teleport them close
				//teleport each entity separately because it doesn't work if vehicle stuff
				vehicle.eject();
				if (chicken.isInsideVehicle()) {
					chicken.leaveVehicle();
				}
				vehicle.teleport(vehicle.getTarget().getLocation().add(EnderDragonFridaysPlugin.rand.nextDouble() * 20, 10, EnderDragonFridaysPlugin.rand.nextDouble() * 20));
				chicken.teleport(vehicle.getLocation());
				vehicle.setPassenger(chicken);
			}
		}
	}
	
	
	@EventHandler
	public void regroup(ChickenRegroupEvent e) {
		//time to regroup. Play a teleport effect and kill self
		if (!chicken.isDead()) {
			chicken.getWorld().playEffect(chicken.getLocation(), Effect.ENDER_SIGNAL, 2003);
			chicken.remove();
		}
		if (vehicle != null && !vehicle.isDead()) {
			vehicle.remove();
		}
	}
	
	@EventHandler
	public void chickenDeath(EntityDeathEvent e) {
		if (e.getEntity() == chicken) {
			if (vehicle != null && !vehicle.isDead()) {
				vehicle.remove();
			}
		}
	}
	
	@EventHandler
	public void chickenDamage(EntityDamageEvent e) {
		//make sure our wolf doesn't die from fall damage
		if (e.getEntity() == vehicle) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		if (!vehicle.isAngry() || vehicle.getTarget() == null) {
			//get a new target
			vehicle.setAngry(true);
			List<Player> players = vehicle.getWorld().getPlayers();
			vehicle.setTarget(players.get(EnderDragonFridaysPlugin.rand.nextInt(players.size())));
		}
	}
	
	public Chicken getEntity() {
		return this.chicken;
	}
	
	public Boss getBoss() {
		return this.boss;
	}
	
	public void kill() {
		//kill entities if they aren't dead
		if (!chicken.isDead()) {
			chicken.remove();
		}
		if (vehicle != null && !vehicle.isDead()) {
			vehicle.remove();
		}
	}
	
	public boolean equals(ChickenMinion _chicken) {
		return this.chicken.getUniqueId().equals(_chicken.getEntity().getUniqueId());
	}

}
