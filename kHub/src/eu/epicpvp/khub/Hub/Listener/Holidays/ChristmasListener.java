package eu.epicpvp.khub.Hub.Listener.Holidays;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.khub.kHub;
import eu.epicpvp.khub.Hub.HubManager;
import lombok.Getter;

public class ChristmasListener extends kListener{

	@Getter
	private HubManager manager;
	private int day;
	private InventoryPageBase inventory;
	private ItemStack item;
	private Click click;

	public ChristmasListener(HubManager manager){
		super(manager.getInstance(),"ChristmasListener");
		this.manager=manager;
	    this.day=Integer.valueOf(new SimpleDateFormat ("dd").format(new Date()));
	    this.inventory=new InventoryPageBase(InventorySize._27, "§aAdventskalender:");
	    this.item=UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL), "§aAdventskalender");

		getManager().getMysql().Update("CREATE TABLE IF NOT EXISTS CHRISTMAS(playerId int,day int)");
		click = new Click() {

			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				if( ((ItemStack)obj).getType() == Material.SNOW_BALL ){
					if(day==((ItemStack)obj).getAmount()){
						if(!getManager().getMysql().getString("SELECT `playerId` FROM `CHRISTMAS` WHERE day='"+day+"' AND playerId='"+UtilPlayer.getPlayerId(player)+"'").equalsIgnoreCase("null")){
							player.sendMessage(TranslationHandler.getText(player, "PREFIX")+"§cDu hast bereits dein Türchen geöffnet!");
						}else{
							getManager().getMysql().Update("INSERT INTO CHRISTMAS (playerId,day) VALUES ('"+UtilPlayer.getPlayerId(player)+"','"+day+"');");
							int c = ((ItemStack)obj).getAmount()*UtilMath.RandomInt(4, 1)*7;
							getManager().getMoney().add(player, StatsKey.COINS, c);
							getManager().getMoney().add(player, StatsKey.GEMS, c/2);
							player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "XMAS_DOOR",new String[]{c+"",(c/2)+""}));

							if(!player.hasPermission(PermissionType.PET_SNOWMAN.getPermissionToString()) && UtilMath.randomInteger( (int) (250 * Math.pow(0.962540842, day)) ) == 74){
								getManager().getPermissionManager().addPermission(player, PermissionType.PET_SNOWMAN);
								player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "XMAS_DOOR1"));
								UtilServer.getClient().broadcastMessage(null, TranslationHandler.getText("PREFIX")+TranslationHandler.getText("XMAS_RARE",player.getName()));
							}
							c=0;
						}
					}else{
						player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "XMAS_DAY",((ItemStack)obj).getAmount()));
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
	    UtilInv.getBase().addPage(this.inventory);
		if(day==24){
			logMessage("Heute ist Weihnachten!");
		}else{
			logMessage("Heute ist der "+day+"te dauert noch bis Weihnachten!");
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand().getType()==Material.SNOW_BALL){
			ev.getPlayer().openInventory(inventory);
			ev.setCancelled(true);
		}
	}

	int tday;
	@EventHandler
	public void timer(UpdateEvent ev){
		if(ev.getType()!=UpdateType.MIN_16)return;
	    this.tday=Integer.valueOf(new SimpleDateFormat ("dd").format(new Date()));
	    if(day!=tday){
	    	this.day=tday;
	    	this.inventory.clear();
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
