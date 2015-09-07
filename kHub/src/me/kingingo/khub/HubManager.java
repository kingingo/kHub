package me.kingingo.khub;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandGroup;
import me.kingingo.kcore.Disguise.DisguiseManager;
import me.kingingo.kcore.Disguise.DisguiseShop;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.Item.ButtonOpenInventory;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Pet.Shop.PetShop;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.Command.CommandBroadcast;
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
	
	public HubManager(JavaPlugin instance,MySQL mysql,PacketManager pmana){
		this.instance=instance;
		this.id=instance.getConfig().getInt("Config.Lobby");
		this.cmd=new CommandHandler(instance);
		this.mysql=mysql;
		this.PacketManager=pmana;
		
		if(!kHub.hubType.equalsIgnoreCase("LoginHub")){
			this.permissionManager=new PermissionManager(instance,GroupTyp.GAME,PacketManager,mysql);
			new ChatListener(instance, null,permissionManager);
			this.hologram=new Hologram(instance);
			this.coins=new Coins(instance,mysql);
			this.pet=new PetManager(instance);
			this.disguiseManager=new DisguiseManager(getInstance());
			this.shop=new InventoryBase(getInstance(), 9, "Shop");
			PetShop petShop = new PetShop(shop,pet,permissionManager, coins);
			this.shop.getMain().addButton(2, new ButtonOpenInventory(petShop, UtilItem.Item(new ItemStack(Material.BONE), new String[]{"§bKlick mich um in den Pet Shop zukommen."}, "§7PetShop")));
			this.shop.addPage(petShop);
			DisguiseShop disguiseShop = new DisguiseShop(shop,permissionManager,coins,disguiseManager);
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
				break;
		}
		
		getCmd().register(CommandTraitor.class, new CommandTraitor());
		getCmd().register(CommandGroup.class, new CommandGroup(permissionManager));
		getCmd().register(CommandBroadcast.class, new CommandBroadcast());
		getCmd().register(CommandFlyspeed.class, new CommandFlyspeed());
		new Listener(this);
		UtilServer.createLagListener(this.cmd);
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
