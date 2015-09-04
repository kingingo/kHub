package me.kingingo.khub.Listener;

import java.util.HashMap;

import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.khub.HubManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HubLoginListener extends kListener{

	private HashMap<Player,Long> list;
	
	public HubLoginListener(HubManager manager) {
		super(manager.getInstance(), "HubLoginListener");
		this.list=new HashMap<>();
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		list.put(ev.getPlayer(), System.currentTimeMillis());
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation().add(0, 5, 0));
	}
	
	Player player;
	@EventHandler
	public void updater(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			for(int i = 0; i < list.size(); i++){
				if( (System.currentTimeMillis()-list.get(i)) > TimeSpan.SECOND*4){
					player=(Player)list.keySet().toArray()[i];
					if((player.getWorld().getSpawnLocation().getBlockY()+5)-player.getLocation().getBlockY() > 3){
						Log("detected Bot "+player.getName());
						player.kickPlayer("§dEs joinen grad zu viele Spieler bitte versuch es später erneut!");
						list.remove(i);
					}else{
						list.remove(i);
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Interact(PlayerInteractEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
}
