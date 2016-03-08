package com.SkyIsland.EnderDragonFridays.Boss.Component;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;

/**
 * A piece of a boss designed to run with little-to-no synchronization.<br />
 * Components aren't designed to significantly alter a boss's behavior. Components, instead, should be
 * things like cannons and status effect inducers that run independently of the boss. Communication between
 * each component and the boss (Which runs on the main thread!!!) should be achieved through event handling
 * and other similarly asynchronous methods.
 * @author Skyler
 *
 */
public abstract class BossComponent implements Runnable {
	
	/**
	 * The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} this component is part of.
	 */
	protected Boss boss;
	
	/**
	 * Returns the boss this component is part of
	 * @return The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} this component belongs to.
	 */
	public Boss getBoss() {
		return this.boss;
	}
	
	
}
