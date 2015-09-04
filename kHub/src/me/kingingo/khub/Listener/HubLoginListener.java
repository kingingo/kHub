package me.kingingo.khub.Listener;

import lombok.Getter;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Login.LoginManager;
import me.kingingo.khub.Login.Events.PlayerLoadInvEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class HubLoginListener extends kListener{

	@Getter
	private LoginManager loginManager;
	private HubManager Manager;
	
	public HubLoginListener(HubManager manager) {
		super(manager.getInstance(), "HubLoginListener");
		this.Manager=manager;
		this.loginManager= new LoginManager(manager);
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
	
	@EventHandler
	public void login(PlayerLoadInvEvent ev){
		UtilBG.sendToServer(ev.getPlayer(), "hub", Manager.getInstance());
	}
	
}
