package com.SkyIsland.EnderDragonFridays.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.SkyIsland.EnderDragonFridays.Name.DefaultNames;
import com.SkyIsland.EnderDragonFridays.Name.NameGenerator;

public class LootGenerator {
	
	/**
	 * Core rarity for this generator. This affects the stats put on the items as well as the
	 * items given. This is more a seed than anything. This is set for a group of generations,
	 * and is not to be confused with the individual contribution used to determine exact
	 * drops for each player.
	 */
	private double rarity;
	
	private List<String> names;
	
	private NameGenerator swordNameGen;
	
	private NameGenerator bowNameGen;
	
	private NameGenerator armorNameGen;
	
	private NameGenerator toolNameGen;
	
	private Random rand;
	
	private List<LootEnchantment> swordEnchantments;
	
	private List<LootEnchantment> bowEnchantments;
	
	private List<LootEnchantment> armorEnchantments;
	
	private List<LootEnchantment> toolEnchantments;
	
	/**
	 * Creates a loot generator with the passed rarity.<br />
	 * The loot generator will use a list of names defined in {@link com.SkyIsland.EnderDragonFridays.Name.DefaultNames DefaultNames}
	 * @param rarity The relative rarity of items produced through this generator. A good range of values are from 1 to 10, but any number is theoritically supported.
	 */
	public LootGenerator(double rarity) {
		this.rarity = rarity;
		this.names = DefaultNames.generate();
		this.rand = new Random();
		loadEnchantments();
	}
	
	public LootGenerator(double rarity, List<String> names) {
		this.rarity = rarity;
		this.names = names;
		this.rand = new Random();
		loadEnchantments();
	}
	
	public LootGenerator(double rarity, NameGenerator SwordNameGenerator, NameGenerator BowNameGenerator, NameGenerator ArmorNameGenerator, NameGenerator ToolNameGenerator) {
		this.rarity = rarity;
		this.swordNameGen = SwordNameGenerator;
		this.bowNameGen = BowNameGenerator;
		this.toolNameGen = ToolNameGenerator;
		this.armorNameGen = ArmorNameGenerator;
		
		if (this.swordNameGen == null || this.bowNameGen == null || this.toolNameGen == null || this.armorNameGen == null) {
			names = DefaultNames.generate();
		}
		this.rand = new Random();
		loadEnchantments();
	}
	
	/**
	 * Goes through and loads up the enchantments in their respective lists.
	 */
	private void loadEnchantments() {
		loadSwordEnchantments();
		loadBowEnchantments();
		loadArmorEnchantments();
		loadToolEnchantments();
	}
	
	private void loadSwordEnchantments() {
		swordEnchantments = new ArrayList<LootEnchantment>();
		swordEnchantments.add(new LootEnchantment(Enchantment.DAMAGE_ALL, 2.5, 10)); //sharpness at 2.5
		swordEnchantments.add(new LootEnchantment(Enchantment.DAMAGE_UNDEAD, 1.5, 10)); //who cares about smite? nobody!
		swordEnchantments.add(new LootEnchantment(Enchantment.FIRE_ASPECT, 3.0, 4)); //large weight
		swordEnchantments.add(new LootEnchantment(Enchantment.DURABILITY, 3.0, 5)); //kind of large, too
		swordEnchantments.add(new LootEnchantment(Enchantment.KNOCKBACK, 3.0, 3)); //also kind of large
		swordEnchantments.add(new LootEnchantment(Enchantment.LOOT_BONUS_MOBS, 4.0, 4)); //Looting is very weighty!
	}
	
	private void loadBowEnchantments() {
		bowEnchantments = new ArrayList<LootEnchantment>();
		bowEnchantments.add(new LootEnchantment(Enchantment.ARROW_DAMAGE, 2.5, 10));
		bowEnchantments.add(new LootEnchantment(Enchantment.ARROW_FIRE, 3.5, 3));
		bowEnchantments.add(new LootEnchantment(Enchantment.ARROW_INFINITE, 10.0, 1));
		bowEnchantments.add(new LootEnchantment(Enchantment.ARROW_KNOCKBACK, 3.0, 3));
		bowEnchantments.add(new LootEnchantment(Enchantment.DURABILITY, 3.5, 4));
	}
	
	private void loadToolEnchantments() {
		toolEnchantments = new ArrayList<LootEnchantment>();
		toolEnchantments.add(new LootEnchantment(Enchantment.DIG_SPEED, 2.5, 8));
		toolEnchantments.add(new LootEnchantment(Enchantment.DURABILITY, 3.0, 4));
		toolEnchantments.add(new LootEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 7.0, 4));
		toolEnchantments.add(new LootEnchantment(Enchantment.SILK_TOUCH, 10.0, 1));
	}
	
	private void loadArmorEnchantments() {
		armorEnchantments = new ArrayList<LootEnchantment>();
		armorEnchantments.add(new LootEnchantment(Enchantment.DURABILITY, 4.0, 3));
		armorEnchantments.add(new LootEnchantment(Enchantment.OXYGEN, 3.0, 1));
		armorEnchantments.add(new LootEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3.5, 7));
		armorEnchantments.add(new LootEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 3.0, 8));
		armorEnchantments.add(new LootEnchantment(Enchantment.PROTECTION_FIRE, 3.0, 8));
		armorEnchantments.add(new LootEnchantment(Enchantment.PROTECTION_PROJECTILE, 3.0, 8));
		armorEnchantments.add(new LootEnchantment(Enchantment.PROTECTION_FALL, 3.5, 5));
		armorEnchantments.add(new LootEnchantment(Enchantment.THORNS, 2.5, 5));
		armorEnchantments.add(new LootEnchantment(Enchantment.WATER_WORKER, 2.0, 3));
	}
	

	
	/**
	 * This method determines an item quality with 0 being no item and values near or above 1 are 'rares'.
	 * <p>DO NOT pass this function negative numbers otherwise undefined behavior will be exhibited.
	 * </p>
	 * @param rarity This parameter determines the overall rarity of the item
	 * @param weight This parameter alters the specific rarity of the item, if this is zero, no item will be given
	 * @return The item quality
	 */
	private double itemQuality(double rarity, double weight) {
		return Math.sqrt(( Math.pow(Math.log( Math.abs(rarity) + 1), 4)*(Math.log(Math.abs(weight) + 1) / 5)));
	}
	
	/**
	 * Generates an item, given the generator's rarity and the provided weight.
	 * <p>The weight provided modifies the item within the realm of the generator's given rarity.<br />
	 * In other words, rarity specifies the quality of items for all items from this generator, where as the
	 * prescribed weight specifies which subset quality the item will return.</p>
	 * <p>A concrete example is a boss fight. Let's say you run many boss fights, and the one we're looking at
	 * right now is only moderately difficulty. If you give a huge, very difficulty boss fight a rarity
	 * of 10, you might want to give this one a 5. Let's also say you want to scale the items that players
	 * get based on how much damage they do (in % of max of the boss). This second parameter is what's termed
	 * as weight. You would create a single LootGenerator for the fight with an overall weight of 5, and generate
	 * items for each player based on how much they contributed.
	 * @param weight Individual item parameter for this specific item
	 * @return
	 */
	public ItemStack generateItem(double weight) {
		ItemStack item = null;
		double quality = itemQuality(rarity, weight * 30); //added * 30 because of the ago. It takes a weight from
														   //0 to 30 instead of 0 to 1s?
		System.out.println("Quality: " + quality);
		switch (rand.nextInt(6)) {
		case 0: 
			item = generateBow(); 
			enchant(bowEnchantments, item, quality);
		break;
		case 1:
			item = generateSword(quality); 
			enchant(swordEnchantments, item, quality);
		break;
		case 2:
		case 3:
			item = generateArmor(quality); 
			enchant(armorEnchantments, item, quality);
		break;
		case 4:
		case 5:
			item = generateTool(quality); 
			enchant(toolEnchantments, item, quality);
		break;
		}
		
		return item;
	}
	
	/**
	 * This method generates a random tool (being an Axe, Shovel, or Pickaxe).
	 * <p>The material of the generated tool is dependent on how rare the boss was and how much the player contributed<p>
	 * @param rarity The rarity of the boss (for ideal properties use 10 as the maximum)
	 * @param weight How much the player contributed to the boss fight (for ideal properties use 0 as no contribution and 30 as 100% contribution)
	 * @return Returns a generated tool
	 */
	private ItemStack generateTool(double quality) {
		ItemStack tool;
		String name;
		
		switch (rand.nextInt(3)) {
		case 0:
			if (quality < 2)
				tool = new ItemStack(Material.STONE_AXE);
			else if (quality < 3)
				tool = new ItemStack(Material.IRON_AXE);
			else
				tool = new ItemStack(Material.DIAMOND_AXE);
			break;
		case 1:
			if (quality < 2)
				tool = new ItemStack(Material.STONE_SPADE);
			else if (quality < 3)
				tool = new ItemStack(Material.IRON_SPADE);
			else
				tool = new ItemStack(Material.DIAMOND_SPADE);
			break;
		case 2:
		default:
			if (quality < 2)
				tool = new ItemStack(Material.STONE_PICKAXE);
			else if (quality < 3)
				tool = new ItemStack(Material.IRON_PICKAXE);
			else
				tool = new ItemStack(Material.DIAMOND_PICKAXE);
		}
		
		name = getToolName();
		
		ItemMeta meta = tool.getItemMeta();
		meta.setDisplayName(name);
		tool.setItemMeta(meta);
		
		return tool;
	}
	
	/**
	 * This method generates a random armor item (being a helment, chestplate, leggings, or boots)
	 * <p>The material of the generated armor is dependent on how rare the boss was and how much the player contributed</p>
	 * @param rarity The rarity of the boss (for ideal properties use 10 as the maximum)
	 * @param weight How much the player contributed to the boss fight (for ideal properties use 0 as no contribution and 30 as 100% contribution)
	 * @return Returns a generated armor item
	 */
	private ItemStack generateArmor(double quality) {
		String name;
		ItemStack armor;
		
		switch(rand.nextInt(4)) {
		case 0:
			if (quality < 2)
				armor =  new ItemStack(Material.LEATHER_HELMET);
			else if (quality < 3)
				armor =  new ItemStack(Material.IRON_HELMET);
			else
				armor =  new ItemStack(Material.DIAMOND_HELMET);
			break;
		case 1:
			if (quality < 2)
				armor =  new ItemStack(Material.LEATHER_CHESTPLATE);
			else if (quality < 3)
				armor =  new ItemStack(Material.IRON_CHESTPLATE);
			else
				armor =  new ItemStack(Material.DIAMOND_CHESTPLATE);
			break;
		case 2:
			if (quality < 2)
				armor =  new ItemStack(Material.LEATHER_LEGGINGS);
			else if (quality < 3)
				armor =  new ItemStack(Material.IRON_LEGGINGS);
			else
				armor =  new ItemStack(Material.DIAMOND_LEGGINGS);
			break;
		case 3:
		default:
			if (quality < 2)
				armor =  new ItemStack(Material.LEATHER_BOOTS);
			else if (quality < 3)
				armor =  new ItemStack(Material.IRON_BOOTS);
			else
				armor =  new ItemStack(Material.DIAMOND_BOOTS);
		}
		
		name = getArmorName();
		
		ItemMeta meta = armor.getItemMeta();
		meta.setDisplayName(name);
		armor.setItemMeta(meta);
		
		
		return armor;
	}
	
	/**
	 * This method generates a random sword
	 * <p>The material of the generated armor is dependent on how rare the boss was and how much the player contributed</p>
	 * @param rarity The rarity of the boss (for ideal properties use 10 as the maximum)
	 * @param weight How much the player contributed to the boss fight (for ideal properties use 0 as no contribution and 30 as 100% contribution)
	 * @return Returns a generated sword
	 */
	private ItemStack generateSword(double quality) {
		String name;
		ItemStack sword;
		name = getSwordName();
		if (quality < 2)
			sword =  new ItemStack(Material.STONE_SWORD);
		else if (quality < 3)
			sword =  new ItemStack(Material.IRON_SWORD);
		else
			sword = new ItemStack(Material.DIAMOND_SWORD);
		
		//set sword name
		ItemMeta meta = sword.getItemMeta();
		meta.setDisplayName(name);
		sword.setItemMeta(meta);
		
		return sword;
	}
	
	/**
	 * Generates a bow. Needs no parameters as there is only one type of bow.
	 * @return A generated bow.
	 */
	private ItemStack generateBow() {
		ItemStack bow = new ItemStack(Material.BOW);
		String name;
		name = getBowName();

		ItemMeta meta = bow.getItemMeta();
		meta.setDisplayName(name);
		bow.setItemMeta(meta);
		return bow;
	}
	
	private String getSwordName() {
		String name;
		if (this.swordNameGen == null) {
			name = names.get(rand.nextInt(names.size()));
		}
		else {
			name = swordNameGen.getName();
		}
		
		return name;
	}
	
	private String getBowName() {
		String name;
		if (this.bowNameGen == null) {
			name = names.get(rand.nextInt(names.size()));
		}
		else {
			name = bowNameGen.getName();
		}
		
		return name;
	}
	
	private String getToolName() {
		String name;
		if (this.toolNameGen == null) {
			name = names.get(rand.nextInt(names.size()));
		}
		else {
			name = toolNameGen.getName();
		}
		
		return name;
	}
	
	private String getArmorName() {
		String name;
		if (this.armorNameGen == null) {
			name = names.get(rand.nextInt(names.size()));
		}
		else {
			name = armorNameGen.getName();
		}
		
		return name;
	}
	
	private void enchant(List<LootEnchantment> list, ItemStack item, double quality) {
		Collections.shuffle(list); //assign random priorities. lulz
		double enchantingPoints, cost;
		int level;
		
		//Enchanting points, as defined in Skylo's Algo are
		enchantingPoints = 8 * quality; //these are used to 'purchase' enchantments
		for (LootEnchantment enchantment : list) {
			if (enchantingPoints < enchantment.getWeight()) {
				//we can't afford a single level of this enchantment
				continue;
			}
			
			level = enchantment.getEnchantmentLevel(quality, enchantingPoints);
			cost = level * enchantment.getWeight();
			
			try {
				//item.addEnchantment(enchantment.getEnchantment(), level);
				item.addUnsafeEnchantment(enchantment.getEnchantment(), level);
				enchantingPoints -= cost;
			}
			catch (IllegalArgumentException e) {
				//couldn't add the enchantment because
				//     it wasn't applicable for the item
				//     it conflicts with a previous enchantment
			}
			
			if (enchantingPoints < .0001) { // == 0
				break; // no more enchantments, we're spent
			}
			
		}
		
	}
}
