package com.SkyIsland.EnderDragonFridays.Name;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class ArmorNameGenerator implements NameGenerator {

	private Random rand;
	private List<String> firstNames; 
	private List<String> lastNames;

	public ArmorNameGenerator() {
		this.rand = new Random();
		this.firstNames = new ArrayList<String>();
		this.lastNames = new ArrayList<String>();
		
		setupFirst();
		setupLast();
	}
	
	@Override
	public String getName() {
		return (getFirstName() + " " + getLastName());
	}

	@Override
	public String getFirstName() {
		return firstNames.get(rand.nextInt(firstNames.size()));
	}

	@Override
	public String getLastName() {
		return lastNames.get(rand.nextInt(lastNames.size()));
	}
	
	private void setupFirst() {
		firstNames.add("Compressed");
		firstNames.add("Dynamic");
		firstNames.add("Obsidian Infused");
		firstNames.add("Void");
		firstNames.add("Almost Ultimate");
		firstNames.add("EMRTC");
		firstNames.add("Frozen");
		firstNames.add("Molten");
		firstNames.add("Regular");
		firstNames.add("Reactive");
		firstNames.add("Explosive");
		firstNames.add("Shadow");
		firstNames.add("Synthetic");
		firstNames.add("Crystalline");
		firstNames.add("Nuclear");
		firstNames.add("Mystic");
		firstNames.add("Volcanic");
		firstNames.add("Solar");
		firstNames.add("Gravity");
		firstNames.add("Electric");
		firstNames.add("Electron");
		firstNames.add("Positron");
		firstNames.add("Proton");
		firstNames.add("Blessed");
		firstNames.add("Cursed");
		firstNames.add("Sponge");
		firstNames.add("Ordinary");
		firstNames.add("Fabulous");
		firstNames.add("New");
		firstNames.add("Mythril");
		firstNames.add("Special");
		firstNames.add("My New");
		firstNames.add("Probably Sucky");
		firstNames.add("Frozen");
		firstNames.add("Legendary");
		firstNames.add("Almost Legendary");
		firstNames.add("Pretty Awful");
	}
	
	private void setupLast() {
		lastNames.add("Protector");
		lastNames.add("Vanguard");
		lastNames.add("Paragon");
		lastNames.add("Buckler");
		lastNames.add("Targ");
		lastNames.add("Cocoon");
		lastNames.add("Jacket");
		lastNames.add("Palisade");
		lastNames.add("Hull");
		lastNames.add("Protection Field V1.02");
		lastNames.add("Sock");
		lastNames.add("Pod");
		lastNames.add("Sheath");
		lastNames.add("Coating");
		lastNames.add("Shield");
		lastNames.add("Wrapper");
		lastNames.add("Facing");
		lastNames.add("Aegis");
		lastNames.add("Ward");
		lastNames.add("Bukkit");
		lastNames.add("Rune");
		lastNames.add("Armor");
		lastNames.add("Bracelet");
		lastNames.add("Underwear");
		lastNames.add("Shell");
		lastNames.add("Barrier");
		
	}

}
