package me.kingingo.khub;

import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandFly;
import me.kingingo.kcore.Command.Admin.CommandMem;
import me.kingingo.kcore.Command.Admin.CommandMemFix;
import me.kingingo.kcore.Command.Admin.CommandMute;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UpdateAsync.UpdaterAsync;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class kHub extends JavaPlugin{

	private Client c;
	private Updater Updater;
	private UpdaterAsync UpdaterAsync;
	public static MySQL mysql;
	public static PermissionManager pManager;
	private HubManager Manager;
	private PacketManager PacketManager;
	
	public void onEnable(){
		try{
			long time = System.currentTimeMillis();
			loadConfig();
			this.mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			this.Updater=new Updater(this);
			this.c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"HUB"+getConfig().getInt("Config.Lobby"),this,Updater);
			this.UpdaterAsync=new UpdaterAsync(this);
			this.PacketManager=new PacketManager(this,c);
			new MemoryFix(this);
			this.pManager=new PermissionManager(this,GroupTyp.GAME,PacketManager,mysql);
			this.Manager=new HubManager(this,mysql,pManager,PacketManager);
			Manager.getCmd().register(CommandFly.class, new CommandFly(this));
			Manager.getCmd().register(CommandMute.class, new CommandMute(this));	
			Manager.getCmd().register(CommandChatMute.class, new CommandChatMute(this));
			Manager.getCmd().register(CommandToggle.class, new CommandToggle(this));
			Manager.getCmd().register(CommandMem.class, new CommandMem(pManager));
			Manager.getCmd().register(CommandMemFix.class, new CommandMemFix(pManager));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/muteall");
			new ChatListener(this, null,this.pManager);
			Manager.DebugLog(time, 45, this.getClass().getName());
			
			for(Entity e : Bukkit.getWorld("world").getEntities()){
				if(!(e instanceof Player))e.remove();
			}
			
		}catch(Exception e){
			UtilException.catchException(e, "hub"+getConfig().getInt("Config.Lobby"), Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		c.disconnect(false);
		mysql.close();
		Updater.stop();
		UpdaterAsync.stop();
	}
	
	public void loadConfig(){
		getConfig().addDefault("Config.MySQL.Host", "NONE");
	    getConfig().addDefault("Config.MySQL.DB", "NONE");
	    getConfig().addDefault("Config.MySQL.User", "NONE");
	    getConfig().addDefault("Config.MySQL.Password", "NONE");
	    getConfig().addDefault("Config.Client.Host", "79.133.55.5");
	    getConfig().addDefault("Config.Client.Port", 9051);
	    getConfig().addDefault("Config.Lobby", "1");
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	}
	
}
