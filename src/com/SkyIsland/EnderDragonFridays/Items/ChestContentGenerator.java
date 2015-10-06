package com.SkyIsland.EnderDragonFridays.Items;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Name.ArmorNameGenerator;
import com.SkyIsland.EnderDragonFridays.Name.BowNameGenerator;
import com.SkyIsland.EnderDragonFridays.Name.ItemNameGenerator;
import com.SkyIsland.EnderDragonFridays.Name.NameGenerator;
import com.SkyIsland.EnderDragonFridays.Name.SwordNameGenerator;

/**
 * Generates a chest-ful of equipment for players!<br />
 * Takes a mapping of players to their contribution and generates loot. The results are returned
 * as a map between players and chests.<br />The chest the players are mapped to contain the loot
 * generated for that player.
 * @author Skyler
 * @note This should be abstracted out when converting to a framework. Let people make their own ChestGenerators that don't,<br />
 * for example, exclude 0% contribution
 */
public class ChestContentGenerator {
	
	private static LootGenerator gen;
	

	private static YamlConfiguration backup;
	private static final File backupFile = new File(EnderDragonFridaysPlugin.plugin.getDataFolder(), "backup.yml");
	
	
	public static Map<UUID, Inventory> generate(double rarity, Map<UUID, Double> inputMap) {
		

		backup = new YamlConfiguration();
		setupBackup();
		
		//First, create our generator
		//get some name generators
		NameGenerator tools, armor, sword, bow;
		tools = new ItemNameGenerator();
		armor = new ArmorNameGenerator(); 
		sword = new SwordNameGenerator();
		bow = new BowNameGenerator();
		gen = new LootGenerator(rarity, sword, bow, armor, tools);
		
		//Next, we set up our new map that will connect players to their chests
		Map<UUID, Inventory> output = new HashMap<UUID, Inventory>();
		
		Inventory chest;
		List<Integer> rec = new LinkedList<Integer>();
		//dragon drops up to 3 eggs, so choose up to 3 people to receive it.
		Random rand = new Random();		
		rec.add(rand.nextInt(inputMap.keySet().size())); //0, 1, 2, ... , n
		rec.add(rand.nextInt(inputMap.keySet().size())); //if we get the same number as before, that just
		rec.add(rand.nextInt(inputMap.keySet().size())); //means we wont get 3 eggs
		int number = 0;
		try {
			for (UUID uuid : inputMap.keySet()) {
				
				Player player = Bukkit.getPlayer(uuid);
				
				//Before anything, make sure they contributed!
				if (inputMap.get(uuid) <= .01) {
					//they have only contributed 1% or less of total health!
					EnderDragonFridaysPlugin.plugin.getLogger().info("Did not give " + player.getDisplayName() + " an item"
							+ " because they only did " + inputMap.get(uuid) + " contribution!");
					continue; //nothing for them!
				}
				
				//Create a chest
				chest = Bukkit.getServer().createInventory(null, 27);
				
				//we are going to populate it with two items
				ItemStack item;
				item = gen.generateItem(  inputMap.get(uuid)  );
				
				//record generated item incase of accidents
				backup.set(Bukkit.getPlayer(uuid).getName() + uuid + ".item1", item);
				chest.addItem(item); //generate item. Use the double passed with player as weight
				
				//do again
				item = gen.generateItem(  inputMap.get(uuid)  );
				backup.set(Bukkit.getPlayer(uuid).getName() + uuid + ".item2", item);
				chest.addItem(item);
				//chest.addItem(new ItemStack(Material.DIAMOND_AXE));
				
	
				//check if they got a dragon egg
				if (rec.contains(number)) {
					ItemStack egg = new SpawnEgg(EntityType.ENDER_DRAGON).toItemStack(1);
					ItemMeta meta = egg.getItemMeta();
					
					meta.setDisplayName("Easter Egg: " + "Boss Egg");
					
					List<String> lore = new LinkedList<String>();
					lore.add(ChatColor.BLACK + "The Egg of an Ender Boss");
					meta.setLore(lore);
					
					egg.setItemMeta(meta);
					

					backup.set(Bukkit.getPlayer(uuid).getName() + uuid + ".egg", item);
					chest.addItem(egg);
					EnderDragonFridaysPlugin.plugin.getLogger().info("Gave an egg to " + player.getDisplayName());
				}
				
				//add this inventory to the map
				output.put(uuid, chest);
				number++;
				EnderDragonFridaysPlugin.plugin.getLogger().info("Finished generating items for: " + player.getDisplayName());
				
			}
			EnderDragonFridaysPlugin.plugin.getLogger().info("Generated a total of " + number + " chests!");
		}
		catch (Exception e) {
			
		}
		finally {
			saveBackup();
		}
		
		return output;
	}
	
	
	
	private static void setupBackup() {
		
		if (backupFile.exists()) {
			backupFile.delete();
		}
		try {
			backupFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			backup.load(backupFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public static void saveBackup() {
		try {
			backup.save(backupFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


