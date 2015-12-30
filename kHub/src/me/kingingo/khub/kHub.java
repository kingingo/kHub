package me.kingingo.khub;

import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandDebug;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandHubFly;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTrackingRange;
import me.kingingo.kcore.Command.Admin.CommandgBroadcast;
import me.kingingo.kcore.Command.Commands.CommandNacht;
import me.kingingo.kcore.Command.Commands.CommandPing;
import me.kingingo.kcore.Command.Commands.CommandSonne;
import me.kingingo.kcore.Command.Commands.CommandTag;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.UtilEnt;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.memory.MemoryFix;
import me.kingingo.khub.Event.EventManager;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class kHub extends JavaPlugin{

	private Client client;
	private Updater Updater;
	public MySQL mysql;
	public static String hubType;
	public static int hubID;
	private kManager manager;
	private PacketManager packetManager;
	private CommandHandler cmdHandler;
	
	public void onEnable(){
		try{
			long time = System.currentTimeMillis();
			loadConfig();
			removeEntity(Bukkit.getWorld("world"));
			this.hubType=getConfig().getString("Config.HubType");
			this.hubID=getConfig().getInt("Config.Lobby");
			
			this.mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			this.Updater=new Updater(this);
			this.client = new Client(this,getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),this.hubType+this.hubID);
			this.packetManager=new PacketManager(this,this.client);
			Language.load(this.mysql);
			
			this.cmdHandler = new CommandHandler(this);
			this.cmdHandler.register(CommandHubFly.class, new CommandHubFly(this));
			this.cmdHandler.register(CommandFlyspeed.class, new CommandFlyspeed());
			this.cmdHandler.register(CommandChatMute.class, new CommandChatMute(this));
			this.cmdHandler.register(CommandToggle.class, new CommandToggle(this));
			this.cmdHandler.register(CommandTag.class, new CommandTag());
			this.cmdHandler.register(CommandNacht.class, new CommandNacht());
			this.cmdHandler.register(CommandSonne.class, new CommandSonne());
			this.cmdHandler.register(CommandgBroadcast.class, new CommandgBroadcast(this.packetManager));
			this.cmdHandler.register(CommandPing.class, new CommandPing());
			this.cmdHandler.register(CommandTrackingRange.class, new CommandTrackingRange());
			this.cmdHandler.register(CommandLocations.class, new CommandLocations(this));
			this.cmdHandler.register(CommandDebug.class, new CommandDebug());
			
			Location loc = CommandLocations.getLocation("spawn");
			if(loc.getBlockX()!=0&&loc.getBlockZ()!=0)Bukkit.getWorld("world").setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			
			new ListenerCMD(this);
			new BungeeCordFirewallListener(this.mysql, this.hubType+this.hubID);
			new MemoryFix(this);

			if(this.hubType.equalsIgnoreCase("event")){
				this.manager=new EventManager(this, this.cmdHandler, this.mysql, this.packetManager);
			}else{
				this.manager=new HubManager(this, this.cmdHandler, this.mysql, this.packetManager);
			}
			
			this.manager.DebugLog(time, 45, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, "hub"+getConfig().getInt("Config.Lobby"), Bukkit.getIp(),this.mysql);
		}
	}
	
	public void onDisable(){
		this.client.disconnect(false);
		this.mysql.close();
		this.Updater.stop();
		if(UtilServer.getDeliveryPet()!=null)UtilServer.getDeliveryPet().onDisable();
	}
	
	public void removeEntity(World world){
		for(Entity e : world.getEntities()){
			if(!(e instanceof Player)&&!(e instanceof ArmorStand)&&!(e instanceof ItemFrame)){
				e.remove();
			}
			
			if(e instanceof ArmorStand){
				UtilEnt.setSlotsDisabled( ((ArmorStand)e) , true);
			}
		}
	}
	
	public void loadConfig(){
		getConfig().addDefault("Config.MySQL.Host", "NONE");
	    getConfig().addDefault("Config.MySQL.DB", "NONE");
	    getConfig().addDefault("Config.MySQL.User", "NONE");
	    getConfig().addDefault("Config.MySQL.Password", "NONE");
	    getConfig().addDefault("Config.Client.Host", "");
	    getConfig().addDefault("Config.Client.Port", 9051);
	    getConfig().addDefault("Config.Lobby", "1");
	    getConfig().addDefault("Config.HubType", "hub");
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	}
	
}
