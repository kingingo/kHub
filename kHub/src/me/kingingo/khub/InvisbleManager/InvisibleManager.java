package me.kingingo.khub.InvisbleManager;

import java.util.HashMap;

import lombok.Setter;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.Listener.HubListener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InvisibleManager extends kListener{

	private HashMap<Player,Long> invisible = new HashMap<>();
	@Setter
	private HubListener listener=null;
	
	public InvisibleManager(JavaPlugin instance,HubListener listener){
		super(instance,"InvisbleManager");		
		this.listener=listener;
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
		
		if(listener!=null && listener.getLoginManager().getLogin().containsKey(ev.getPlayer())||listener!=null && listener.getLoginManager().getRegister().contains(ev.getPlayer()))return;
		
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getTypeId()==351){
				if(UtilInv.GetData(ev.getPlayer().getItemInHand()) == 8){
					ev.getPlayer().getItemInHand().setDurability((byte)10);
					invisible(ev.getPlayer());
				}else if(UtilInv.GetData(ev.getPlayer().getItemInHand()) == 10){
					if(visible(ev.getPlayer())){
						ev.getPlayer().getItemInHand().setDurability((byte)8);
					}
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
		ev.getPlayer().getInventory().setItem(7, UtilItem.Item(new ItemStack(351,1,(byte)8), new String[]{"�bKlick mich die Spieler un/sichtbar."}, "�7Spieler Un/sichtbar"));
		for(Player p : invisible.keySet())p.hidePlayer(ev.getPlayer());
	}
	
}
