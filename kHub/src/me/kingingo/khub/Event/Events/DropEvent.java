package me.kingingo.khub.Event.Events;

import me.kingingo.kcore.Command.Admin.CommandGiveAll;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.Event.EventManager;
import me.kingingo.khub.Event.SoloEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DropEvent extends SoloEvent{

	private boolean auto_drop_item=false;
	
	public DropEvent(EventManager eventManager) {
		super(eventManager,"DropEvent");
		
		getEventManager().getCmdHandler().register(CommandGiveAll.class, new CommandGiveAll());
	}
	
	public void start() {
		
	}

	public void stop() {
		
	}

	public void reset() {
		
	}

	public void select() {
		Location loc = CommandLocations.getLocation("DropEvent:spawn");
		if(loc.getBlockX()!=0&&loc.getBlockZ()!=0)Bukkit.getWorld("world").setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		for(Player player : UtilServer.getPlayers())player.teleport(loc);
	}
}