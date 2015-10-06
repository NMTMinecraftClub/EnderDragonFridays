package com.SkyIsland.EnderDragonFridays.Name;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class SwordNameGenerator implements NameGenerator {

	private Random rand;
	private List<String> firstNames; 
	private List<String> lastNames;

	public SwordNameGenerator() {
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
		firstNames.add("Bronze");
		firstNames.add("Copper");
		firstNames.add("Mythril");
		firstNames.add("Sharp");
		firstNames.add("Golden");
		firstNames.add("Iconic");
		firstNames.add("Legendary");
		firstNames.add("Frozen");
		firstNames.add("Hungry");
		firstNames.add("Vampiric");
		firstNames.add("Heavy");
		firstNames.add("Light");
		firstNames.add("Fragile");
		firstNames.add("Sturdy");
		firstNames.add("Weak");
		firstNames.add("Invisible");
		firstNames.add("Magical");
		firstNames.add("Futuristic");
		firstNames.add("Mythical");
	}
	
	private void setupLast() {
		lastNames.add("Spatha");
		lastNames.add("Sword");
		lastNames.add("Saber");
		lastNames.add("Dirk");
		lastNames.add("Dagger");
		lastNames.add("Knife");
		lastNames.add("Claymore");
		lastNames.add("Rapier");
		lastNames.add("Longsword");
		lastNames.add("Shortsword");
		
	}

}
