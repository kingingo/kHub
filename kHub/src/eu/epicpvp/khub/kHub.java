package eu.epicpvp.khub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.client.connection.ClientType;
import eu.epicpvp.khub.Hub.HubManager;
import eu.epicpvp.kcore.Command.Admin.CommandChatMute;
import eu.epicpvp.kcore.Command.Admin.CommandDebug;
import eu.epicpvp.kcore.Command.Admin.CommandFlyspeed;
import eu.epicpvp.kcore.Command.Admin.CommandHubFly;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.Command.Admin.CommandToggle;
import eu.epicpvp.kcore.Command.Admin.CommandTrackingRange;
import eu.epicpvp.kcore.Command.Admin.CommandUnBan;
import eu.epicpvp.kcore.Command.Admin.CommandgBroadcast;
import eu.epicpvp.kcore.Command.Commands.CommandNacht;
import eu.epicpvp.kcore.Command.Commands.CommandPing;
import eu.epicpvp.kcore.Command.Commands.CommandSonne;
import eu.epicpvp.kcore.Command.Commands.CommandTag;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Listener.AntiCrashListener.AntiCrashListener;
import eu.epicpvp.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import eu.epicpvp.kcore.Listener.Command.ListenerCMD;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilException;
import eu.epicpvp.kcore.Util.UtilServer;

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
			UtilServer.createClient(this,ClientType.LOBBY,getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),this.hubType+this.hubID);
			
			Language.load(UtilServer.getMysql());
			
			UtilServer.createCommandHandler(this);
			UtilServer.getCommandHandler().register(CommandHubFly.class, new CommandHubFly(this));
			UtilServer.getCommandHandler().register(CommandFlyspeed.class, new CommandFlyspeed());
			UtilServer.getCommandHandler().register(CommandChatMute.class, new CommandChatMute(this));
			UtilServer.getCommandHandler().register(CommandToggle.class, new CommandToggle(this));
			UtilServer.getCommandHandler().register(CommandTag.class, new CommandTag());
			UtilServer.getCommandHandler().register(CommandNacht.class, new CommandNacht());
			UtilServer.getCommandHandler().register(CommandSonne.class, new CommandSonne());
			UtilServer.getCommandHandler().register(CommandgBroadcast.class, new CommandgBroadcast(UtilServer.getClient()));
			UtilServer.getCommandHandler().register(CommandPing.class, new CommandPing());
			UtilServer.getCommandHandler().register(CommandTrackingRange.class, new CommandTrackingRange());
			UtilServer.getCommandHandler().register(CommandLocations.class, new CommandLocations(this));
			UtilServer.getCommandHandler().register(CommandDebug.class, new CommandDebug());
			UtilServer.getCommandHandler().register(CommandUnBan.class, new CommandUnBan(UtilServer.getMysql()));
			
			Location loc = CommandLocations.getLocation("spawn");
			if(loc.getBlockX()!=0&&loc.getBlockZ()!=0)Bukkit.getWorld("world").setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			
			new ListenerCMD(this);
			new BungeeCordFirewallListener(UtilServer.getMysql(),UtilServer.getCommandHandler(), this.hubType+this.hubID);
			this.manager=new HubManager(this, UtilServer.getCommandHandler(), UtilServer.getMysql());
			

			new AntiCrashListener(UtilServer.getClient(),UtilServer.getMysql());
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
