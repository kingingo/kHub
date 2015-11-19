package me.kingingo.khub.Event;

import me.kingingo.kcore.Listener.kListener;

import org.bukkit.plugin.java.JavaPlugin;

public class HubEventListener extends kListener{

	public HubEventListener(JavaPlugin instance) {
		super(instance, "EventListener");
	}

}
