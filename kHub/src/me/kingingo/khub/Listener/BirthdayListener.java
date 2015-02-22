package me.kingingo.khub.Listener;

import lombok.Getter;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.khub.HubManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class BirthdayListener extends kListener{

	@Getter
	private HubManager manager;
	private ItemStack t3ker;
	private ItemStack kingingo;
	
	public BirthdayListener(HubManager manager){
		super(manager.getInstance(),"[BirthdayListener]");
		this.manager=manager;
		this.t3ker=UtilItem.Head("t3ker");
		this.kingingo=UtilItem.Head("kingingo");
		Log("Happy Birthday Felix und Moritz!");
	}
	
	@EventHandler
	public void Inventory(InventoryMoveItemEvent  ev){
		if(ev.getSource().getHolder() instanceof Player){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Join(PlayerJoinEvent ev){
		if(UtilMath.RandomInt(1, 0)==0){
			ev.getPlayer().getInventory().setHelmet(this.kingingo);
		}else{
			ev.getPlayer().getInventory().setHelmet(this.t3ker);
		}
	}
	
}
