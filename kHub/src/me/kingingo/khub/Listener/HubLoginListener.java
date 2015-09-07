package me.kingingo.khub.Listener;

import java.util.HashMap;

import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HubLoginListener extends kListener{

	private HashMap<Player,Long> list;
	private HubManager manager;
	private Location spawn;
	
	public HubLoginListener(HubManager manager) {
		super(manager.getInstance(), "HubLoginListener");
		this.list=new HashMap<>();
		this.manager=manager;
		this.spawn=Bukkit.getWorld("world").getSpawnLocation().add(0, 5, 0);
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		list.remove(ev.getPlayer());
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §aLoginHub "+manager.getId(), "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
		list.put(ev.getPlayer(), System.currentTimeMillis());
		for(Player player : UtilServer.getPlayers()){
			if(!player.isOp())player.hidePlayer(ev.getPlayer());
			if(!ev.getPlayer().isOp())ev.getPlayer().hidePlayer(player);
		}
		ev.getPlayer().teleport(spawn);
	}
	
	Player player;
	@EventHandler
	public void updater(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			for(int i = 0; i < list.size(); i++){
				player=(Player)list.keySet().toArray()[i];
				if( (System.currentTimeMillis()-list.get(player)) > TimeSpan.SECOND*20){
					if(UtilLocation.isSameLocation(player.getLocation(), spawn)){
						Log("detected Bot "+player.getName());
						player.kickPlayer("§dEs joinen grad zu viele Spieler bitte versuch es später erneut!");
						list.remove(player);
					}else{
						list.remove(player);
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
