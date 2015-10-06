package com.SkyIsland.EnderDragonFridays;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.BossDeathEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;

/**
 * Session for a dragon fight.<br />
 * Holds state and strates session-wide events
 * @author Skyler
 *
 */
public class DragonFight implements Listener {
	
	public enum State {
		PREFIGHT,
		DURING,
		FINISHED;
	}
	
	private State state;
	
	private Boss boss;
	
	private Location chestLocation;
	
	private Map<UUID, Inventory> inventories;
	
	private double difficulty;
	
	private double difficultyBase;
	
	private World world;
	
	private String ID;
	
	private String sessionName;
	
	public final static String IDPrefix = "DragonFight#";
	
	public DragonFight(String sessionName, World world, Boss boss, double difficulty, double difficultyBase, Location chestLocation) {
		this.sessionName = sessionName;
		this.boss = boss;
		this.chestLocation = chestLocation;
		this.difficulty = difficulty;
		this.difficultyBase = difficultyBase;
		this.world = world;
		
		this.state = State.PREFIGHT;
		
		inventories = new HashMap<UUID, Inventory>();
		
//		ID = (new Date()).toString().replace(" ", "_");
//		ID = ID.replace(":", "_");
		ID = "" + (new Date()).getTime();
	}
	
	/**
	 * Starts the fight, if it's able to be started.
	 * @return true if the fight is then started, or false if an error occurs
	 */
	public boolean start() {
		if (state != State.PREFIGHT) {
			EnderDragonFridaysPlugin.plugin.getLogger().warning("Cannot start an instance that's already going! State = " + state);
			return false;
		}
		
		Bukkit.getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);
		
		state = State.DURING;
		boss.start(world.getSpawnLocation().add(0, 50.0, 0));
		
		return true;
	}
	
	/**
	 * Tries to stop the fight.
	 * @param win should this stop be considered as a win?
	 * @return Whether the stop was successful or not
	 */
	public boolean stop(boolean win) {
		if (state != State.DURING) {
			EnderDragonFridaysPlugin.plugin.getLogger().warning("Cannot stop a session that's not going! State = " + state);
			return false;
		}
		
		state = State.FINISHED;
		
		if (win) {
			
			boss.win();
			spawnRewards();
			return true;
		} else {
			boss.kill();
			return true;
		}
	}
	
	private void spawnRewards() {
		
		if (boss.getDamageList().isEmpty()) {
			EnderDragonFridaysPlugin.plugin.getLogger().info("There were no recorded contributions to the dragon!");
			return;
		}
		
		//spawn the loot chest, and create inventories for every player
		inventories = ChestContentGenerator.generate(difficultyBase + (difficulty / 5), boss.getDamageMap());
		congratulatePlayers(boss.getDamageMap());
		
		//first make sure map isn't empty. If it is... something went wrong, but we're just 
		//going to ignore it for now
		if (inventories.isEmpty()) {
			EnderDragonFridaysPlugin.plugin.getLogger().info("Map of contributions was empty!\nSpawning no rewards...");
			return;
		}
		
		//do fancy stuff
		chestLocation.getWorld().spawnEntity(chestLocation, EntityType.LIGHTNING);

		//Create our loot chest
		chestLocation.getBlock().setType(Material.ENDER_CHEST);
		
		//save our inventories! #backups
		
		YamlConfiguration backupConfig = new YamlConfiguration();
		ConfigurationSection playSex, invSex;
		String name;
		for (UUID id : inventories.keySet()) {
			name = Bukkit.getOfflinePlayer(id).getName();
			if (name == null || name.trim().isEmpty()) {
				name = id.toString();
			}
			
			playSex = backupConfig.createSection(name);
			playSex.set("uuid", id.toString());
			invSex = playSex.createSection("inventory");
			Iterator<ItemStack> it = inventories.get(id).iterator();
			int index = 0;
			while (it.hasNext()) {
				invSex.set(index + "", it.next());
				index++;
			}
		}
		File saveFile = new File(EnderDragonFridaysPlugin.plugin.getDataFolder(), 
				"Save" + getName() + "_" + getID() + ".yml");
		if (!saveFile.exists()) {
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			backupConfig.save(saveFile);
		} catch (IOException e) {
			System.out.println("Failed to save file!");
			e.printStackTrace();
		}
		
		//tell players it's there
		for (Player p : chestLocation.getWorld().getPlayers()) {
			p.sendMessage("The loot chest has been generated at (" 
		+ chestLocation.getBlockX() + ", "
		+ chestLocation.getBlockY() + ", "
		+ chestLocation.getBlockZ() + ")");
		}
		
	}
	
	/**
	 * Print out custom message to player letting them know how they did
	 * @param map
	 */
	private void congratulatePlayers(Map<UUID, Double> map) {
		for (Entry<UUID, Double> entry : map.entrySet()) {
			
			Player player = Bukkit.getPlayer(entry.getKey());
			try{
				player.sendMessage("Nice Fight!\n  "
					+ "You did " + (entry.getValue().intValue()) + " points of damage!\n"
							+ "Your contribution was " + ((entry.getValue() / boss.getDamageTaken()) * 100) + "%!");
			}
			catch (Exception e){
				; //do nothing. minor bug, will be reported elsewhere
			}
		}
	}
	
	@EventHandler
	public void onBossDeath(BossDeathEvent e) {
		if (e.getBoss().equals(boss)) {
			//our boss has died!
			state = State.FINISHED;
			spawnRewards();
		}
	}
	
	@EventHandler
	public void onLoot(PlayerInteractEvent e) {
		
		if (state != State.FINISHED) {
			return;
		}

		if (e.isCancelled()) {
			return;
		}
		
		if (e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR) {
			return;
		}
		
		if (inventories == null || inventories.isEmpty()) {
			return;
		}
		
		if (e.getClickedBlock().getLocation().equals(chestLocation.getBlock().getLocation()))
		if (inventories.containsKey(e.getPlayer().getUniqueId())) {
			//player clicked it and has a chest
			e.setCancelled(true);
			e.getPlayer().openInventory(inventories.get(e.getPlayer().getUniqueId()));
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return (o.toString().equals(toString()));
	}
	
	@Override
	public String toString() {
		return IDPrefix + ID;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getName() {
		return sessionName;
	}
	
	public boolean isStarted() {
		return (!(state == State.PREFIGHT));
	}
	
	public State getState() {
		return state;
	}
	
	/**
	 * Returns a string containing information on this session, including:<br />
	 * name, id, state, and chest location.
	 * @param all Should this return extended information, including contribution information?
	 * @return
	 */
	public String getInfo(boolean all) {
		String ret =  ChatColor.DARK_PURPLE + getName() + "\n" + ChatColor.YELLOW + getID() + "\n"
				+ ChatColor.BLUE + "State: " + getState().toString() + "\n"
				+ ChatColor.DARK_GREEN + "Chest Location: (" + 
					chestLocation.getBlockX() + ", " + chestLocation.getBlockY() + ", " + chestLocation.getBlockZ() + ")"
				+ ChatColor.RESET;
		
		if (all) {
			if (boss.getDamageList().isEmpty()) {
				ret += ChatColor.YELLOW + "\nContribution list is empty!";
			} else {
				OfflinePlayer p;
				Map<UUID, Double> map = boss.getDamageMap();
				for (UUID id : map.keySet()) {
					p = Bukkit.getOfflinePlayer(id);
					ret += ChatColor.DARK_BLUE + "\n" + p.getName();
					ret += " - " + ChatColor.DARK_GREEN + map.get(id);
					ret += " (" + map.get(id)/boss.getDamageTaken() * 100 + "%)";
				}
				ret += ChatColor.RESET;
			}
		}
		
		return ret;
	}
}
