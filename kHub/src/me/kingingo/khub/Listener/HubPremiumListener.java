package me.kingingo.khub.Listener;

import me.kingingo.kcore.Permission.Event.GroupLoadedEvent;
import me.kingingo.khub.HubManager;

import org.bukkit.event.EventHandler;

public class HubPremiumListener extends HubListener{

	public HubPremiumListener(final HubManager manager) {
		super(manager,true);
	}	
	
	@EventHandler
	public void groupload(GroupLoadedEvent ev){
		if(ev.getGroup().equalsIgnoreCase("default")){
			ev.getManager().getGroups().get(ev.getGroup()).setPrefix("§e");
		}
	}
	
}