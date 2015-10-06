package com.SkyIsland.EnderDragonFridays;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.EnderDragon;
import com.SkyIsland.EnderDragonFridays.Boss.JackTheSkeleton;
import com.SkyIsland.EnderDragonFridays.Boss.MegaDragon;
import com.SkyIsland.EnderDragonFridays.Boss.Turkey;
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
	
	private Set<DragonFight> fights;
	
	public void onLoad() {
		EnderDragonFridaysPlugin.plugin = this;
	}
	
	public void onEnable() {
		fights = new HashSet<DragonFight>();
		bossName = new BossNameGenerator();
		
		rand = new Random();
	}
	
	public void onDisable() {
		if (!fights.isEmpty()) {
			Iterator<DragonFight> it = fights.iterator();
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
			boss = new MegaDragon(playerCount, bossName.getName());
		} else if (args[2].equals("halloween")) {
			boss = new JackTheSkeleton(playerCount, bossName.getName());
		} else if (args[2].equals("thanksgiving")) {
			boss = new Turkey(playerCount, bossName.getName());
		} else {
			//just do default dragon
			boss = new EnderDragon(playerCount, bossName.getName());
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
		
		DragonFight fight = new DragonFight(args[1],
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
		
		DragonFight fight = getFight(args[1]);
		
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
		
		DragonFight fight = getFight(args[1]);
		
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
		
		if (fight.getState() != DragonFight.State.FINISHED) {
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
		
		DragonFight fight = getFight(args[1]);
		
		if (fight == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Unable to find fight " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RESET);
			return;
		}
		
		if (fight.getState() == DragonFight.State.DURING) {
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
		
		DragonFight fight = getFight(args[1]);
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
		for (DragonFight fight : fights) {
			sender.sendMessage(ChatColor.DARK_PURPLE + fight.getName() + ChatColor.YELLOW + " [" + fight.getID() + "] " 
					+ ChatColor.BLUE + fight.getState().toString() + ChatColor.RESET);
		}
		
	}
	
	/**
	 * Tries to look up a fight by it's sessionName
	 * @param sessionNAme
	 * @return The fight, if we have record of it. Null otherwise
	 */
	private DragonFight getFight(String sessionName) {
		for (DragonFight fight : fights) {
			if (fight.getName().equals(sessionName)) {
				return fight;
			}
		}
		
		return null;
	}
}
