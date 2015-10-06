package com.SkyIsland.EnderDragonFridays.Boss;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Indicates a boss had died.
 * @author Skyler
 *
 */
public class BossDeathEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Boss boss;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public BossDeathEvent(Boss boss) {
		this.boss = boss;
	}
	
	public Boss getBoss() {
		return boss;
	}
	
}
