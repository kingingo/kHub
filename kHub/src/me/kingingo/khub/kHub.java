package me.kingingo.khub;

import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandDebug;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandHubFly;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTrackingRange;
import me.kingingo.kcore.Command.Admin.CommandUnBan;
import me.kingingo.kcore.Command.Admin.CommandgBroadcast;
import me.kingingo.kcore.Command.Commands.CommandNacht;
import me.kingingo.kcore.Command.Commands.CommandPing;
import me.kingingo.kcore.Command.Commands.CommandSonne;
import me.kingingo.kcore.Command.Commands.CommandTag;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.AntiCrashListener.AntiCrashListener;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.Util.UtilEnt;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilServer;
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

	public static String hubType;
	public static int hubID;
	private kManager manager;
	
	public void onEnable(){
		try{
			long time = System.currentTimeMillis();
			loadConfig();
			removeEntity(Bukkit.getWorld("world"));
			this.hubType=getConfig().getString("Config.HubType");
			this.hubID=getConfig().getInt("Config.Lobby");
			
			UtilServer.createMySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			UtilServer.createUpdater(this);
			UtilServer.createUpdaterAsync(this);
			UtilServer.createClient(this,getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),this.hubType+this.hubID);
			UtilServer.createPacketManager(this);
			Language.load(UtilServer.getMysql());
			
			UtilServer.createCommandHandler(this);
			UtilServer.getCommandHandler().register(CommandHubFly.class, new CommandHubFly(this));
			UtilServer.getCommandHandler().register(CommandFlyspeed.class, new CommandFlyspeed());
			UtilServer.getCommandHandler().register(CommandChatMute.class, new CommandChatMute(this));
			UtilServer.getCommandHandler().register(CommandToggle.class, new CommandToggle(this));
			UtilServer.getCommandHandler().register(CommandTag.class, new CommandTag());
			UtilServer.getCommandHandler().register(CommandNacht.class, new CommandNacht());
			UtilServer.getCommandHandler().register(CommandSonne.class, new CommandSonne());
			UtilServer.getCommandHandler().register(CommandgBroadcast.class, new CommandgBroadcast(UtilServer.getPacketManager()));
			UtilServer.getCommandHandler().register(CommandPing.class, new CommandPing());
			UtilServer.getCommandHandler().register(CommandTrackingRange.class, new CommandTrackingRange());
			UtilServer.getCommandHandler().register(CommandLocations.class, new CommandLocations(this));
			UtilServer.getCommandHandler().register(CommandDebug.class, new CommandDebug());
			UtilServer.getCommandHandler().register(CommandUnBan.class, new CommandUnBan(UtilServer.getMysql()));
			
			Location loc = CommandLocations.getLocation("spawn");
			if(loc.getBlockX()!=0&&loc.getBlockZ()!=0)Bukkit.getWorld("world").setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			
			new ListenerCMD(this);
			new BungeeCordFirewallListener(UtilServer.getMysql(),UtilServer.getCommandHandler(), this.hubType+this.hubID);

			if(this.hubType.equalsIgnoreCase("event")){
				this.manager=new EventManager(this, UtilServer.getCommandHandler(), UtilServer.getMysql(), UtilServer.getPacketManager());
			}else{
				this.manager=new HubManager(this, UtilServer.getCommandHandler(), UtilServer.getMysql(), UtilServer.getPacketManager());
			}

			new AntiCrashListener(this.manager.getPacketManager(),UtilServer.getMysql());
			this.manager.DebugLog(time, 45, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, "hub"+getConfig().getInt("Config.Lobby"), Bukkit.getIp(),UtilServer.getMysql());
		}
	}
	
	public void onDisable(){
		UtilServer.disable();
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
