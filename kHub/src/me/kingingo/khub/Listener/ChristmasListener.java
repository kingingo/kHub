package me.kingingo.khub.Listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Scoreboard.PlayerScoreboard;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.inventory.Inventory;

import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class ChristmasListener extends kListener{

	@Getter
	private HubManager manager;
	private int day;
	private ItemStack christmas;
	private Location center;
	private Inventory inventory;
	private ItemStack item;
	
	public ChristmasListener(HubManager manager){
		super(manager.getInstance(),"[ChristmasListener]");
		this.manager=manager;
	    this.day=Integer.valueOf(new SimpleDateFormat ("dd").format(new Date()));
	    this.center=Bukkit.getWorld("world").getSpawnLocation().add(0, 3, 0);
	    this.inventory=Bukkit.createInventory(null, 27, "§aAdventskalender:");
	    this.item=UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL), "§aAdventskalender");

		getManager().getMysql().Update("CREATE TABLE IF NOT EXISTS CHRISTMAS(name varchar(30),day int)");
	    
	    int place=0;
	    for(int i = 1; i<=24;i++){
	    	for(int a = 0; a < 200; a++){
	    		place=UtilMath.RandomInt(24, 0);
	    		if(inventory.getItem(place)==null||inventory.getItem(place).getType()==Material.AIR)break;
	    	}
	    	if(i==day){
	    		this.inventory.setItem(place,UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL,i), "§aTürchen "+i));
	    	}else{
	    		this.inventory.setItem(place,UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL,i), "§cTürchen "+i));
	    	}
	    }
	    
		if(day==24){
			Log("Heute ist Wheinachten ihr BITCHES !!!!!");
			this.christmas=UtilItem.Head("KingIngoHD");
		}else{
			Log("Heute ist der "+day+"te dauert noch bis Wheinachten BITCH!");
		}
	}
	
	int c;
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)
				|| (e.getCursor() == null || e.getCurrentItem() == null)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();

		if (e.getInventory().getName().equalsIgnoreCase("§aAdventskalender:")) {
			if (e.getCurrentItem().getType() == Material.SNOW_BALL) {
				if(day==e.getCurrentItem().getAmount()){
					if(!getManager().getMysql().getString("SELECT `name` FROM `CHRISTMAS` WHERE day='"+day+"' AND name='"+p.getName().toLowerCase()+"'").equalsIgnoreCase("null")){
						p.sendMessage(Text.PREFIX.getText()+"§cDu hast bereits dein Türchen geöffnet!");						
					}else{
						getManager().getMysql().Update("INSERT INTO CHRISTMAS (name,day) VALUES ('"+p.getName().toLowerCase()+"','"+day+"');");
						c = e.getCurrentItem().getAmount()*UtilMath.RandomInt(4, 1)*7;
						getManager().getCoins().addCoins(p, true, c);
						p.sendMessage(Text.PREFIX.getText()+"§aDu hast das Türchen geöffnet und §e"+c+"§a Coins erhalten!");
					}
				}else{
					p.sendMessage(Text.PREFIX.getText()+"§cIst heute der "+e.getCurrentItem().getAmount()+"te ?");
				}
				e.setCancelled(true);
				p.closeInventory();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if(!manager.getLManager().getLogin().containsKey(ev.getPlayer())&&!manager.getLManager().getRegister().contains(ev.getPlayer())&&UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.SNOW_BALL){
				ev.getPlayer().openInventory(inventory);
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Inventory(InventoryMoveItemEvent  ev){
		if(ev.getSource().getHolder() instanceof Player){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST)return;
		UtilParticle.FIREWORKS_SPARK.display(10F, 4F, 10F, 0, 60, center, 10);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Join(PlayerJoinEvent ev){
		if(day==24){
			ev.getPlayer().getInventory().setHelmet(christmas);
		}
		
		ev.getPlayer().getInventory().setItem(8, item.clone());
	}
	
}
