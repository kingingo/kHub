package me.kingingo.khub.InvisbleManager;

import java.util.HashMap;

import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.HubManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class InvisibleManager extends kListener{

	private HashMap<Player,Long> invisible = new HashMap<>();
	private HubManager manager;
	
	public InvisibleManager(HubManager manager){
		super(manager.getInstance(),"InvisbleManager");		
		this.manager=manager;
	}
	
	public boolean visible(Player player){
		if(!invisible.containsKey(player))return true;
		if(invisible.get(player)>System.currentTimeMillis()){
			return false;
		}
		invisible.remove(player);
		for(Player p : UtilServer.getPlayers()){
			if(p.getName().equalsIgnoreCase(player.getName()))continue;
			player.showPlayer(p);
		}
		return true;
	}
	
	public void invisible(Player player){
		invisible.put(player, System.currentTimeMillis()+(TimeSpan.SECOND*3));
		for(Player p : UtilServer.getPlayers()){
			if(p.getName().equalsIgnoreCase(player.getName()))continue;
			player.hidePlayer(p);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp()){
			ev.setCancelled(true);
		}
		if(manager.getLManager().getLogin().containsKey(ev.getPlayer())||manager.getLManager().getRegister().contains(ev.getPlayer()))return;
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.BLAZE_ROD){
				ev.getPlayer().getItemInHand().setType(Material.STICK);
				invisible(ev.getPlayer());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.STICK){
				if(visible(ev.getPlayer())){
					ev.getPlayer().getItemInHand().setType(Material.BLAZE_ROD);
				}
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		invisible.remove(ev.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void Join(PlayerJoinEvent ev){
		ev.getPlayer().getInventory().setItem(8, UtilItem.Item(new ItemStack(Material.BLAZE_ROD), new String[]{"§bKlick mich die Spieler un/sichtbar."}, "§7Spieler Un/sichtbar"));
		for(Player p : invisible.keySet())p.hidePlayer(ev.getPlayer());
	}
	
}
