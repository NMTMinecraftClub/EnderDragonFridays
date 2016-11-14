package com.SkyIsland.EnderDragonFridays.Name;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class ToolNameGenerator implements NameGenerator {
	
	private Random rand;
	private List<String> firstNames; 
	private List<String> lastNames;
	
	public ToolNameGenerator() {
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
		firstNames.add("Stone");
		firstNames.add("Dirt");
		firstNames.add("Sand");
		firstNames.add("Air");
		firstNames.add("Extreme");
		firstNames.add("Ultra");
		firstNames.add("Super");
		firstNames.add("Weak");
		firstNames.add("Regular");
		firstNames.add("World");
		firstNames.add("Mythical");
		firstNames.add("Legendary");
		firstNames.add("Corrupting");
		firstNames.add("A rather useful");
		firstNames.add("Useful");
		firstNames.add("Maliwan");
		firstNames.add("Binary");
		firstNames.add("\"Dubble\"");
		firstNames.add("Woeful");
		firstNames.add("Gentleman's");
		firstNames.add("Frau");
		firstNames.add("Ice");
		firstNames.add("Magma");
		firstNames.add("Digital");
		firstNames.add("Analog");
		firstNames.add("Fraudulent");
		firstNames.add("Gooey");
		firstNames.add("Not as good as the other guy's");
		firstNames.add("Your");
		firstNames.add("Malfunctioning");
		firstNames.add("I.O.U. one");
		firstNames.add("Universal");
		firstNames.add("Happy");
		firstNames.add("Questionably Legal");
		firstNames.add("VERY Illegal");
	}
	
	private void setupLast() {
		lastNames.add("Eater");
		lastNames.add("Destroyer");
		lastNames.add("Maniac");
		lastNames.add("Conqueror");
		lastNames.add("Harbinger");
		lastNames.add("Levithan");
		lastNames.add("Cracker");
		lastNames.add("Breaker");
		lastNames.add("Ratchet");
		lastNames.add("Tire Iron");
		lastNames.add("Oil Filter");
		lastNames.add("Carburetor");
		lastNames.add("Fuel injector");
		lastNames.add("Radiator");
		lastNames.add("Multimeter");
		lastNames.add("Portal Gun");
		lastNames.add("Credit Card");
		lastNames.add("Debit Card");
		lastNames.add("Mix tape");
		lastNames.add("Walkman");
		lastNames.add("Bukkit");
		lastNames.add("Gameboy");
		lastNames.add("SD Card");
		lastNames.add("Doom");
		lastNames.add("Doomsday Device");
	}
}
