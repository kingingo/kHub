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
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.khub.Command.CommandBroadcast;
import me.kingingo.khub.Command.CommandTraitor;
import me.kingingo.khub.InvisbleManager.InvisibleManager;
import me.kingingo.khub.Listener.BirthdayListener;
import me.kingingo.khub.Listener.HubListener;
import me.kingingo.khub.Listener.HubVersusListener;
import me.kingingo.khub.Listener.Listener;
import me.kingingo.khub.Listener.SilvesterListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
	private PetShop shop;
	@Getter
	private PetManager pet;
	@Getter
	private InvisibleManager invisibleManager;
	
	public HubManager(JavaPlugin instance,MySQL mysql,PermissionManager pManager,PacketManager pmana){
		this.instance=instance;
		this.id=instance.getConfig().getInt("Config.Lobby");
		this.cmd=new CommandHandler(instance);
		this.permissionManager=pManager;
		this.mysql=mysql;
		this.coins=new Coins(instance,mysql);
		this.pet=new PetManager(instance);
		this.shop=new PetShop(pet,pManager, coins);
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

		this.PacketManager=pmana;
		this.invisibleManager=new InvisibleManager(getInstance(),null);
		
		switch(instance.getConfig().getString("Config.HubType")){
			case "Versus":
				new HubVersusListener(this);
				break;
			default:
				new HubListener(this);
				break;
		}
		
		getCmd().register(CommandTraitor.class, new CommandTraitor());
		getCmd().register(CommandGroup.class, new CommandGroup(pManager));
		getCmd().register(CommandBroadcast.class, new CommandBroadcast());
		getCmd().register(CommandFlyspeed.class, new CommandFlyspeed());
		new Listener(this);
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
