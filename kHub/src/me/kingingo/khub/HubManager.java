package me.kingingo.khub;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandCoins;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandGiveGems;
import me.kingingo.kcore.Command.Admin.CommandGroup;
import me.kingingo.kcore.DeliveryPet.DeliveryObject;
import me.kingingo.kcore.DeliveryPet.DeliveryPet;
import me.kingingo.kcore.Disguise.DisguiseManager;
import me.kingingo.kcore.Disguise.DisguiseShop;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.TWIITTER_IS_PLAYER_FOLLOWER;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Pet.Shop.PetShop;
import me.kingingo.kcore.Pet.Shop.PlayerPetHandler;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Gems;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.Command.CommandBroadcast;
import me.kingingo.khub.Command.CommandDelivery;
import me.kingingo.khub.Command.CommandTraitor;
import me.kingingo.khub.InvisbleManager.InvisibleManager;
import me.kingingo.khub.Listener.HubListener;
import me.kingingo.khub.Listener.HubLoginListener;
import me.kingingo.khub.Listener.HubVersusListener;
import me.kingingo.khub.Listener.Listener;
import me.kingingo.khub.Listener.Holidays.BirthdayListener;
import me.kingingo.khub.Listener.Holidays.SilvesterListener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class HubManager{
	@Getter
	private JavaPlugin instance;
	@Getter
	private MySQL mysql;
	@Getter
	private PermissionManager permissionManager;
	private Coins coins;
	private Gems gems;
	@Getter
	private PacketManager PacketManager;
	@Getter
	private CommandHandler cmd;
	@Getter
	private int id;
	@Getter
	CalendarType holiday=null;
	@Getter
	ArrayList<Player> invisble = new ArrayList<>();
	@Getter
	private InventoryBase shop;
	@Getter
	private PetManager pet;
	@Getter
	private InvisibleManager invisibleManager;
	@Getter
	private DisguiseManager disguiseManager;
	@Getter
	private Hologram hologram;
	
	public HubManager(JavaPlugin instance,CommandHandler cmd,MySQL mysql,PacketManager pmana){
		this.instance=instance;
		this.id=instance.getConfig().getInt("Config.Lobby");
		this.cmd=cmd;
		this.mysql=mysql;
		this.PacketManager=pmana;
		
		if(!kHub.hubType.equalsIgnoreCase("LoginHub")){
			this.permissionManager=new PermissionManager(instance,GroupTyp.GAME,PacketManager,mysql);
			new ChatListener(instance, null,permissionManager);
			this.hologram=new Hologram(instance);
			cmd.register(CommandCoins.class, new CommandCoins(getCoins(),getPacketManager()));
			cmd.register(CommandGiveGems.class, new CommandGiveGems(getGems(),getPacketManager()));
			this.pet=new PetManager(instance);
			this.disguiseManager=new DisguiseManager(getInstance());
			this.shop=new InventoryBase(getInstance(), 9, "Shop");
			PlayerPetHandler petHandler = new PlayerPetHandler(ServerType.GAME, getPet(), shop, getPermissionManager());
			PetShop petShop = new PetShop(petHandler, getGems(),getCoins());
			this.shop.getMain().addButton(2, new ButtonOpenInventory(petShop, UtilItem.Item(new ItemStack(Material.BONE), new String[]{"§bKlick mich um in den Pet Shop zukommen."}, "§7PetShop")));
			this.shop.addPage(petShop);
			DisguiseShop disguiseShop = new DisguiseShop(shop,permissionManager,getGems(),coins,disguiseManager);
			this.shop.getMain().addButton(6, new ButtonOpenInventory(disguiseShop, UtilItem.Item(new ItemStack(Material.NAME_TAG), new String[]{"§bKlick mich um in den Disguise Shop zukommen."}, "§7DisguiseShop")));
			this.shop.addPage(disguiseShop);
			this.shop.getMain().fill(Material.STAINED_GLASS_PANE,(byte)7);
			this.holiday=Calendar.getHoliday();
			
			if(holiday!=null){
				switch(holiday){
				case HELLOWEEN:
					new AddonNight(getInstance(), Bukkit.getWorld("world"));
					break;
				case GEBURSTAG:
					if(Calendar.isFixHolidayDate(CalendarType.GEBURSTAG)){
						new BirthdayListener(this);
					}
					new AddonNight(instance, Bukkit.getWorld("world"));
					break;
				case WEIHNACHTEN:
					break;
				case SILVESTER:
						new SilvesterListener(this);
						new AddonNight(getInstance(), Bukkit.getWorld("world"));
					break;
				default:
					new AddonDay(instance, Bukkit.getWorld("world"));
				}
			}else{
				new AddonDay(instance, Bukkit.getWorld("world"));
			}
		}
		
		switch(instance.getConfig().getString("Config.HubType")){
			case "LoginHub":
			new HubLoginListener(this);
				break;
			case "Versus":
				new HubVersusListener(this);
				break;
			default:
				this.invisibleManager=new InvisibleManager(getInstance(),null);
				new HubListener(this);
				
				UtilServer.createDeliveryPet(new DeliveryPet(getShop(),null,new DeliveryObject[]{
						new DeliveryObject(new String[]{"","§7Click for Vote!","","§ePvP Rewards:","§7   200 Epics","§7   1x Inventory Repair","","§eGame Rewards:","§7   25 Gems","§7   100 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},null,false,10,"§aVote for EpicPvP",Material.PAPER,new Click(){

							@Override
							public void onClick(Player p, ActionType a,Object obj) {
								p.closeInventory();
								p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
								p.sendMessage(Language.getText(p,"PREFIX")+" ");
								p.sendMessage(Language.getText(p,"PREFIX")+"Vote Link:§a http://goo.gl/wxdAj4");
								p.sendMessage(Language.getText(p,"PREFIX")+" ");
								p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
							}
							
						},-1),
						new DeliveryObject(new String[]{"§aOnly for Premium Players!","","§ePvP Rewards:","§7   200 Epics","§7   10 Level","","§eGame Rewards:","§7   200 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},kPermission.RANK_COINS_DAILY,true,12,"§cRank Day Reward",Material.EMERALD,new Click(){

							@Override
							public void onClick(Player p, ActionType a,Object obj) {
								getCoins().addCoins(p, true, 200);
							}
							
						},TimeSpan.DAY),
						new DeliveryObject(new String[]{"§aOnly for Premium Players!","","§ePvP Rewards:","§7   5000 Epics","§7   5x Golden Apple","","§eGame Rewards:","§7   5000 Coins","§7   5x TTT Paesse","","§eSkyBlock Rewards:","§7   5000 Epics","§7   15x Diamonds","§7   15x Iron Ingot","§7   15x Gold Ingot"},kPermission.RANK_COINS_MONTH,true,14,"§cRank Month Reward",Material.EMERALD_BLOCK,new Click(){

							@Override
							public void onClick(Player p, ActionType a,Object obj) {
								getCoins().addCoins(p, true, 5000);
							}
							
						},TimeSpan.DAY*30),
						new DeliveryObject(new String[]{"§7/twitter [TwitterName]","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","","§eSkyBlock Rewards:","§7   300 Epics","§7   15 Level"},null,false,16,"§cTwitter Reward",Material.getMaterial(351),4,new Click(){

							@Override
							public void onClick(Player p, ActionType a,Object obj) {
								String s1 = getMysql().getString("SELECT twitter FROM BG_TWITTER WHERE uuid='"+UtilPlayer.getRealUUID(p)+"'");
								if(s1.equalsIgnoreCase("null")){
									p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_ACC_NOT"));
								}else{
									getPacketManager().SendPacket("DATA", new TWIITTER_IS_PLAYER_FOLLOWER(s1, p.getName()));
									p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_CHECK"));
								}
							}
							
						},TimeSpan.DAY*7),
				},"§bThe Delivery Jockey!",EntityType.CHICKEN,CommandDelivery.getDelivery(),ServerType.GAME,getHologram(),getMysql())
				);
				
				break;
		}
		
		getCmd().register(CommandTraitor.class, new CommandTraitor());
		getCmd().register(CommandGroup.class, new CommandGroup(permissionManager));
		getCmd().register(CommandBroadcast.class, new CommandBroadcast());
		getCmd().register(CommandFlyspeed.class, new CommandFlyspeed());
		new Listener(this);
		UtilServer.createLagListener(this.cmd);
	}
	
	public Gems getGems(){
		if(gems==null)gems=new Gems(getMysql());
		return gems;
	}
	
	public Coins getCoins(){
		if(coins==null)coins=new Coins(getInstance(),getMysql());
		return coins;
	}
	
	public void DebugLog(long time,int zeile,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Zeile: "+zeile);
		//System.err.println("[DebugMode]: Zeit: "+UtilTime.convertString(System.currentTimeMillis() - time, 1, UtilTime.TimeUnit.FIT));
	}
	
	public void DebugLog(String m){
		System.err.println("[DebugMode]: "+m);
	}
}
