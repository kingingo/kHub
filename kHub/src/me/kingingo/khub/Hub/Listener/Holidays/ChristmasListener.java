package me.kingingo.khub.Hub.Listener.Holidays;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonBase;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Packets.BROADCAST;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.kHub;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ChristmasListener extends kListener{

	@Getter
	private HubManager manager;
	private int day;
	private InventoryPageBase inventory;
	private ItemStack item;
	
	public ChristmasListener(HubManager manager){
		super(manager.getInstance(),"ChristmasListener");
		this.manager=manager;
	    this.day=Integer.valueOf(new SimpleDateFormat ("dd").format(new Date()));
	    this.inventory=new InventoryPageBase(InventorySize._27, "§aAdventskalender:");
	    this.item=UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL), "§aAdventskalender");

		getManager().getMysql().Update("CREATE TABLE IF NOT EXISTS CHRISTMAS(uuid varchar(30), name varchar(30),day int)");
	    Click click = new Click() {
			
			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				if( ((ItemStack)obj).getType() == Material.SNOW_BALL ){
					if(day==((ItemStack)obj).getAmount()){
						if(!getManager().getMysql().getString("SELECT `uuid` FROM `CHRISTMAS` WHERE day='"+day+"' AND uuid='"+UtilPlayer.getRealUUID(player)+"'").equalsIgnoreCase("null")){
							player.sendMessage(Language.getText(player, "PREFIX")+"§cDu hast bereits dein Türchen geöffnet!");						
						}else{
							getManager().getMysql().Update("INSERT INTO CHRISTMAS (uuid,name,day) VALUES ('"+UtilPlayer.getRealUUID(player)+"','"+player.getName().toLowerCase()+"','"+day+"');");
							int c = ((ItemStack)obj).getAmount()*UtilMath.RandomInt(4, 1)*7;
							getManager().getCoins().addCoins(player, true, c);
							getManager().getGems().addGems(player, true, (c/2));
							player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "XMAS_DOOR",new String[]{c+"",(c/2)+""}));
							if(!player.hasPermission(kPermission.PET_SNOWMAN.getPermissionToString())&&UtilMath.r(150) == 74){
								getManager().getPermissionManager().addPermission(player, kPermission.PET_SNOWMAN);
								player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "XMAS_DOOR1"));
								getManager().getPacketManager().SendPacket("BG", new BROADCAST(Language.getText("PREFIX")+Language.getText("XMAS_RARE",player.getName())));
							}
							c=0;
						}
					}else{
						player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "XMAS_DAY",((ItemStack)obj).getAmount()));
					}
				}
			}
		};
		
	    int place=0;
	    for(int i = 1; i<=24;i++){
	    	for(int a = 0; a < 200; a++){
	    		place=UtilMath.RandomInt(24, 0);
	    		if(inventory.getItem(place)==null||inventory.getItem(place).getType()==Material.AIR)break;
	    	}
	    	if(i==day){
	    		this.inventory.addButton(place, new ButtonBase(click, UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL,i), "§aTürchen "+i)));
	    	}else{
	    		this.inventory.addButton(place, new ButtonBase(click, UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL,i), "§cTürchen "+i)));
	    	}
	    }
	    
	    this.inventory.fill(Material.STAINED_GLASS_PANE,15);
	    getManager().getShop().addPage(this.inventory);
		if(day==24){
			Log("Heute ist Weihnachten!");
		}else{
			Log("Heute ist der "+day+"te dauert noch bis Weihnachten!");
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand().getType()==Material.SNOW_BALL){
			ev.getPlayer().openInventory(inventory);
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST)return;
		try {
			for(Player player : UtilServer.getPlayers())
					UtilParticle.FIREWORKS_SPARK.sendToPlayer(player, player.getLocation(), 10F, 4F, 10F, 0, 60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Join(PlayerJoinEvent ev){
		if(!kHub.hubType.equalsIgnoreCase("VersusHub"))ev.getPlayer().getInventory().setItem(6, item.clone());
	}
	
}
