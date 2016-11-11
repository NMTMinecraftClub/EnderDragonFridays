package com.SkyIsland.EnderDragonFridays.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a list of random names for the Wither boss.
 * 
 * @author Skyler Manzanares, James Pelster
 */
public class WitherNameGenerator implements NameGenerator {

	private Random rand;
	private List<String> firstNames;
	private List<String> lastNames;
	
	/**
	 * Creates a name generator ready to spit out awesome boss names!
	 */
	public WitherNameGenerator() {
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
		firstNames.add("Abbadon");
		firstNames.add("Gelnoth");
		firstNames.add("Blitzkreig");
		firstNames.add("Nox Dessius");
		firstNames.add("Darth Mwothzxyz");
		firstNames.add("Walt");
		firstNames.add("Memelord");
	}
	
	/**
	 * Add all our last names to a list
	 */
	private void setupLast() {
		lastNames.add("The Corrupted");
		lastNames.add("The Anihilator");
		lastNames.add("The Ultimate");
		lastNames.add("The Wither");
		lastNames.add("The Terrible");
		lastNames.add("The Really, Really Bad");
		lastNames.add("The Dark");
		lastNames.add("The Dank");
	}

}
