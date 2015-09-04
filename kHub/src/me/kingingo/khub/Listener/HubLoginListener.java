package me.kingingo.khub.Listener;

import me.kingingo.kcore.Listener.kListener;
import me.kingingo.khub.HubManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class HubLoginListener extends kListener{

	private HubManager Manager;
	
	public HubLoginListener(HubManager manager) {
		super(manager.getInstance(), "HubLoginListener");
		this.Manager=manager;
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
}
