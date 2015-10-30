package com.SkyIsland.EnderDragonFridays.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossNameGenerator implements NameGenerator {
	
	private Random rand;
	private List<String> firstNames;
	private List<String> lastNames;
	
	/**
	 * Creates a name generator ready to spit out awesome boss names!
	 */
	public BossNameGenerator() {
		rand = new Random();
		firstNames = new ArrayList<String>(); //we use array lists for fast random access
		lastNames = new ArrayList<String>();
		
		setupFirst(); //load up first names
		setupLast(); //load up last names
		
	}
	/**
	 * Returns a boss name.<br />
	 * This is a complete name, composed of a first name and a last name separated by a space.
	 * <p />
	 * This is the same as 
	 *        getFirstName() + " " + getLastName()
	 */
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
	
	/**
	 * Add our first names to a list
	 */
	private void setupFirst() {
		firstNames.add("Uldar");
		firstNames.add("Bred");
		firstNames.add("Lyssur");
		firstNames.add("Nervi");
		firstNames.add("Gerptor");
		firstNames.add("Venox");
		firstNames.add("Cervox");
		firstNames.add("Bromethius");
		firstNames.add("Vincent");
		firstNames.add("Greg");
		firstNames.add("Bob");
		firstNames.add("Zeike");
		firstNames.add("Lloyd");
		firstNames.add("Ogur");
		firstNames.add("Prunt");
		firstNames.add("Reque");
		firstNames.add("Rex");
		firstNames.add("Hethe");
		firstNames.add("Lugutharr");
		firstNames.add("Yggdrasil");
		firstNames.add("Wyste");
	}
	
	/**
	 * Add all our last names to a list
	 */
	private void setupLast() {
		lastNames.add("Bonecrusher");
		lastNames.add("Stoneweaver");
		lastNames.add("The Destroyer");
		lastNames.add("The Crusher");
		lastNames.add("The Slayer");
		lastNames.add("DragonBreathe");
		lastNames.add("Ronningston");
		lastNames.add("Smith");
		lastNames.add("Wirthmire");
		lastNames.add("The Great");
		lastNames.add("The Grand");
		lastNames.add("The Nimble");
		lastNames.add("The Weak");
		lastNames.add("The Pitiful");
		lastNames.add("The Builder");
		lastNames.add("Sorrowbreath");
		lastNames.add("Deathfang");
		lastNames.add("Ironsong");
		lastNames.add("Sharpspeak");
		lastNames.add("Reyunk");
		lastNames.add("Firebreather");
	}
}
