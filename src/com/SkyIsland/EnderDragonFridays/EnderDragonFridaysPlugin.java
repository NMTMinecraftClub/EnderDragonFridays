package com.SkyIsland.EnderDragonFridays;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.EnderDragonBoss;
import com.SkyIsland.EnderDragonFridays.Boss.SkeletonBoss;
import com.SkyIsland.EnderDragonFridays.Boss.MegaDragonBoss;
import com.SkyIsland.EnderDragonFridays.Boss.TurkeyBoss;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;
import com.SkyIsland.EnderDragonFridays.Name.BossNameGenerator;

/**
 * The EnderDragonFridaysPlugin makes an Ender Boss appear once a week in the end.
 * Upon killing it, awesome custom loot is dropped.
 *
 */
public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private BossNameGenerator bossName;
	
	public static EnderDragonFridaysPlugin plugin;
	
	public static Random rand;
	
	public static final String savePrefix = "FightSave_";
	
	private Set<BossFight> fights;
	
	public void onLoad() {
		EnderDragonFridaysPlugin.plugin = this;
	}
	
	public void onEnable() {
		fights = new HashSet<BossFight>();
		bossName = new BossNameGenerator();
		
		rand = new Random();
	}
	
	public void onDisable() {
		if (!fights.isEmpty()) {
			Iterator<BossFight> it = fights.iterator();
			while (it.hasNext()) {
				it.next().stop(false);
				it.remove();
			}
		}
	}
	
	public void reload() {
		onDisable();
		onEnable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if (cmd.getName().equalsIgnoreCase("killalldragons")) {
			for (Entity e : ((Player) sender).getWorld().getEntities()) {
				if (e.getType() == EntityType.ENDER_DRAGON) {
					LivingEntity dragon = (LivingEntity) e;
					//dragon.damage(dragon.getMaxHealth());
					dragon.remove();
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("gentestloot")) {
			if ((Player) sender == null)
			{
				return false;
			}
			spawnTestRewards((Player)sender);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("enderdragonfridays")) {
			if (args.length == 0) {
				return false;
			}
			
			if (args[0].equalsIgnoreCase("start")) {
				commandStart(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				commandReload(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("create")) {
				commandCreate(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("stop")) {
				commandStop(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("remove")) {
				commandRemove(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("info")) {
				commandInfo(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				commandList(sender, args);
				return true;
			}
		}
		
		return false;
	}
	
	private void spawnTestRewards(Player player) {
		
		Random rand = new Random();
		Location chestLocation = player.getEyeLocation();
		double difficultyBase = rand.nextInt(1) + 1;
		double difficulty = rand.nextInt(30);
		EnderDragonFridaysPlugin.plugin.getLogger().info("Spawning test rewards. DifficultyBase=" + difficultyBase + ", Difficulty=" + difficulty + ".");
		
		//spawn the loot chest, and create inventories for every player
		Inventory inventory = ChestContentGenerator.generateTest(difficultyBase + (difficulty / 5), rand.nextDouble(), player);
		
		//do fancy stuff
		chestLocation.getWorld().spawnEntity(chestLocation, EntityType.LIGHTNING);
	
		//Create our loot chest
		chestLocation.getBlock().setType(Material.CHEST);
		((Chest) chestLocation.getBlock().getState()).getBlockInventory().setContents(inventory.getContents());
		
		String name;
		UUID id = player.getUniqueId();
		name = Bukkit.getOfflinePlayer(id).getName();
		if (name == null || name.trim().isEmpty()) {
			name = id.toString();
		}
		
		//save our inventories! #backups
		/* YamlConfiguration backupConfig = new YamlConfiguration();
		ConfigurationSection playSex, invSex;
		playSex = backupConfig.createSection(name);
		playSex.set("uuid", id.toString());
		invSex = playSex.createSection("inventory");
		Iterator<ItemStack> it = inventory.iterator();
		int index = 0;
		while (it.hasNext()) {
			invSex.set(index + "", it.next());
			index++;
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
		} */
		
		//tell players it's there
		for (Player p : chestLocation.getWorld().getPlayers()) {
			p.sendMessage("The loot chest has been generated at (" 
		+ chestLocation.getBlockX() + ", "
		+ chestLocation.getBlockY() + ", "
		+ chestLocation.getBlockZ() + ")");
		}
		
	}
	
	private void commandCreate(CommandSender sender, String[] args) {
		if (args.length < 3 || args.length > 4) {
			sender.sendMessage("/edf create " + ChatColor.DARK_PURPLE + "[sessionName]" + ChatColor.RESET + " [type] {basedifficulty}");
			return;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command!");
			return;
		}
		
		//make sure there aren't any with that name already
		if (getFight(args[1]) != null) {
			sender.sendMessage(ChatColor.DARK_RED + "A session named " + ChatColor.DARK_PURPLE + args[1] 
					+ ChatColor.DARK_RED + " already exists!" + ChatColor.RESET);
			return;
		}
		
		//go ahead with it.
		Player player = (Player) sender;
		
		//count players
		int playerCount = player.getWorld().getPlayers().size();
		
		Boss boss;
		if (args[2].equals("mega")) {
			
			boss = new MegaDragonBoss(playerCount, bossName.getName());
		} else if (args[2].equals("halloween")) {
			boss = new SkeletonBoss(playerCount, bossName.getName());
		} else if (args[2].equals("thanksgiving")) {
			boss = new TurkeyBoss(playerCount, bossName.getName());
		} else if (args[2].equals("double")){
			Boss bossPartner = new MegaDragonBoss(playerCount/2, bossName.getName() + " Jr",null);
			boss = new MegaDragonBoss(playerCount, bossName.getName(), bossPartner);
			((MegaDragonBoss)bossPartner).setPartner(boss);
			((MegaDragonBoss)boss).setCannon(false);
			((MegaDragonBoss)bossPartner).setCannon(false);
		} else {
			//just do default dragon
			boss = new EnderDragonBoss(playerCount, bossName.getName());
		}
		
		int base;
		if (args.length == 4) {
			try {
				base = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage("Unable to parse number: " + args[3]);
				boss = null;
				return;
			}
		} else {
			base = 5;
		}
		
		BossFight fight = new BossFight(args[1],
				player.getWorld(),
				boss, 
				playerCount,
				base,
				player.getLocation());

		fights.add(fight);
		
		sender.sendMessage("Successfully created session: " + ChatColor.DARK_PURPLE + fight.getName() + ChatColor.RESET);
		sender.sendMessage("Chest location set to your position!");
	}
	
	private void commandStart(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("/edf start " + ChatColor.DARK_PURPLE + "[sessionName]" + ChatColor.RESET);
			return;
		}
		
		BossFight fight = getFight(args[1]);
		
		if (fight == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Unable to find fight " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RESET);
			return;
		}
		
		if (fight.isStarted()) {
			sender.sendMessage(ChatColor.DARK_RED + "The fight " + ChatColor.DARK_PURPLE + fight.getName() 
				+ ChatColor.DARK_RED + " is already started!" + ChatColor.RESET);
			return;
		}
		
		fight.start();
		sender.sendMessage(ChatColor.DARK_GREEN + "Fight successfully started!" + ChatColor.RESET);
	}
	
	private void commandStop(CommandSender sender, String[] args) {
		if (args.length < 2 || args.length > 3) {
			sender.sendMessage("/edf stop " + ChatColor.DARK_PURPLE + "[sessionName]" + ChatColor.RESET + "{win}");
			return;
		}
		
		BossFight fight = getFight(args[1]);
		
		if (fight == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Unable to find fight " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RESET);
			return;
		}
		
		if (!fight.isStarted()) {
			sender.sendMessage(ChatColor.DARK_RED + "The fight " + ChatColor.DARK_PURPLE + fight.getName() 
				+ ChatColor.DARK_RED + " has not been started!" + ChatColor.RESET);
			return;
		}
		
		boolean win = false;
		if (args.length == 3) {
			if (args[2].equalsIgnoreCase("true")) {
				win = true;
			}
		}
		
		if (fight.getState() != BossFight.State.FINISHED) {
			sender.sendMessage(ChatColor.DARK_RED + "The session's already stopped!" + ChatColor.RESET);
		}
		
		if (!fight.stop(win)) {
			sender.sendMessage(ChatColor.DARK_RED + "Failed to stop fight!" + ChatColor.RESET);
		} else {
			sender.sendMessage(ChatColor.DARK_GREEN + "Fight successfully stopped!" + ChatColor.RESET);
		}
	}
	
	private void commandRemove(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("/edf remove " + ChatColor.DARK_PURPLE + "[sessionName]" + ChatColor.RESET);
			return;
		}
		
		BossFight fight = getFight(args[1]);
		
		if (fight == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Unable to find fight " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RESET);
			return;
		}
		
		if (fight.getState() == BossFight.State.DURING) {
			sender.sendMessage(ChatColor.DARK_RED + "The fight " + ChatColor.DARK_PURPLE + fight.getName() 
			+ ChatColor.DARK_RED + " must be stopped first!" + ChatColor.RESET);
			return;
		}
		
		HandlerList.unregisterAll(fight);
		fights.remove(fight);

		sender.sendMessage(ChatColor.DARK_GREEN + "Fight successfully removed!" + ChatColor.RESET);
		
	}
	
	private void commandReload(CommandSender sender, String[] args) {
		; //not implemented
	}
	
	private void commandInfo(CommandSender sender, String[] args) {
		if (args.length < 2 || args.length > 3) {
			sender.sendMessage("/edf info " + ChatColor.DARK_PURPLE + "[sessionName]"+ ChatColor.RESET + " {all}");
			return;
		}
		
		BossFight fight = getFight(args[1]);
		if (fight == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Unable to find fight " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RESET);
			return;
		}
		
		boolean all = false;
		if (args.length == 3) {
			if (args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("true")) {
				all = true;
			}
		}
		
		sender.sendMessage(fight.getInfo(all));
	}
	
	private void commandList(CommandSender sender, String[] args) {
		if (args.length > 1) {
			sender.sendMessage("/edf list");
			return;
		}
		
		if (fights == null) {
			sender.sendMessage(ChatColor.DARK_RED + "List is null!" + ChatColor.RESET);
			return;
		}
		
		if (fights.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "There are currently no sessions!" + ChatColor.RESET);
			return;
		}
		
		sender.sendMessage("There are currently " + ChatColor.GREEN + fights.size() + ChatColor.RESET + " fights:");
		for (BossFight fight : fights) {
			sender.sendMessage(ChatColor.DARK_PURPLE + fight.getName() + ChatColor.YELLOW + " [" + fight.getID() + "] " 
					+ ChatColor.BLUE + fight.getState().toString() + ChatColor.RESET);
		}
		
	}
	
	/**
	 * Tries to look up a fight by it's sessionName
	 * @param sessionName
	 * @return The fight, if we have record of it. Null otherwise
	 */
	private BossFight getFight(String sessionName) {
		for (BossFight fight : fights) {
			if (fight.getName().equals(sessionName)) {
				return fight;
			}
		}
		
		return null;
	}
}
