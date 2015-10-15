package me.kingingo.khub;

import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandCMDMute;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandFly;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTrackingRange;
import me.kingingo.kcore.Command.Admin.CommandgBroadcast;
import me.kingingo.kcore.Command.Commands.CommandPing;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.memory.MemoryFix;
import me.kingingo.khub.Command.CommandDelivery;
import me.kingingo.khub.Command.CommandTime;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class kHub extends JavaPlugin{

	private Client c;
	private Updater Updater;
	public static MySQL mysql;
	public static String hubType;
	private HubManager Manager;
	private PacketManager PacketManager;
	
	public void onEnable(){
		try{
			long time = System.currentTimeMillis();
			loadConfig();
			this.hubType=getConfig().getString("Config.HubType");
			this.mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			Language.load(mysql);
			this.Updater=new Updater(this);
			this.c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),getConfig().getString("Config.HubType").toUpperCase()+getConfig().getInt("Config.Lobby"),this,Updater);
			this.PacketManager=new PacketManager(this,c);
			new MemoryFix(this);
			CommandHandler cmd = new CommandHandler(this);
			cmd.register(CommandFly.class, new CommandFly(this));
			cmd.register(CommandCMDMute.class, new CommandCMDMute(this));	
			cmd.register(CommandChatMute.class, new CommandChatMute(this));
			cmd.register(CommandToggle.class, new CommandToggle(this));
			cmd.register(CommandTime.class, new CommandTime());
			cmd.register(CommandgBroadcast.class, new CommandgBroadcast(PacketManager));
			cmd.register(CommandPing.class, new CommandPing());
			cmd.register(CommandTrackingRange.class, new CommandTrackingRange());
			cmd.register(CommandDelivery.class, new CommandDelivery(this));
			
			new ListenerCMD(this);
			
			for(Entity e : Bukkit.getWorld("world").getEntities()){
				if(!(e instanceof Player))e.remove();
			}
			
			this.Manager=new HubManager(this,cmd,mysql,PacketManager);
			Manager.DebugLog(time, 45, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, "hub"+getConfig().getInt("Config.Lobby"), Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		c.disconnect(false);
		mysql.close();
		Updater.stop();
		Manager.getHologram().RemoveText();
		if(UtilServer.getDeliveryPet()!=null)UtilServer.getDeliveryPet().onDisable();
	}
	
	public void loadConfig(){
		getConfig().addDefault("Config.MySQL.Host", "NONE");
	    getConfig().addDefault("Config.MySQL.DB", "NONE");
	    getConfig().addDefault("Config.MySQL.User", "NONE");
	    getConfig().addDefault("Config.MySQL.Password", "NONE");
	    getConfig().addDefault("Config.Client.Host", "79.133.55.5");
	    getConfig().addDefault("Config.Client.Port", 9051);
	    getConfig().addDefault("Config.Lobby", "1");
	    getConfig().addDefault("Config.HubType", "hub");
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	}
	
}
