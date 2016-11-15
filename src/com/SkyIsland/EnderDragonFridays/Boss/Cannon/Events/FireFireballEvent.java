package com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events;


import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class FireFireballEvent extends CannonFireEvent {

	public FireFireballEvent(LivingEntity shooter, Player target, Location fromLocation) {
		super(shooter, target, fromLocation);
	}
	
	
}
