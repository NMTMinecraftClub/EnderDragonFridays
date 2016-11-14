package com.SkyIsland.EnderDragonFridays.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShieldNameGenerator implements NameGenerator {
	private Random rand;
	private List<String> firstNames; 
	private List<String> lastNames;

	public ShieldNameGenerator() {
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
		lastNames.add("Handheld Protection");
		lastNames.add("Defender");
		lastNames.add("Portable Wall");
		lastNames.add("Buckler");
		lastNames.add("Targ");
		lastNames.add("Blocker");
		lastNames.add("Shield");
		lastNames.add("Aegis");
		lastNames.add("Carapace");
		lastNames.add("Barrier");
		
	}
}
