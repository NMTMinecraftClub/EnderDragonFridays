package com.SkyIsland.EnderDragonFridays.Name;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class BowNameGenerator implements NameGenerator {

	private Random rand;
	private List<String> firstNames; 
	private List<String> lastNames;

	public BowNameGenerator() {
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
		firstNames.add("Ranged");
		firstNames.add("Fiery");
		firstNames.add("Vampiric");
		firstNames.add("Stone");
		firstNames.add("Composite");
		firstNames.add("Strengthened");
		firstNames.add("Icy");
		firstNames.add("Iron");
		firstNames.add("Legendary");
		firstNames.add("Fantastic");
		firstNames.add("Bow-tastic");
		firstNames.add("Fletched");
	}
	
	private void setupLast() {
		lastNames.add("Long Bow");
		lastNames.add("Short Bow");
		lastNames.add("Bow");
		lastNames.add("Repeater");
		lastNames.add("Crossbow");
		lastNames.add("Aegis");
		lastNames.add("Spitter");
		lastNames.add("Nimbus");
		
	}

}
