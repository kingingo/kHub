package me.kingingo.khub;

import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.Admin.CommandMem;
import me.kingingo.kcore.Command.Admin.CommandMemFix;
import me.kingingo.kcore.Command.Admin.CommandMuteAll;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UpdateAsync.UpdaterAsync;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.memory.MemoryFix;
import me.kingingo.khub.Command.CommandJump;

import org.bukkit.Bukkit;
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
			mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			Updater=new Updater(this);
			c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"HUB"+getConfig().getInt("Config.Lobby"),this,Updater);
			UpdaterAsync=new UpdaterAsync(this);
			PacketManager=new PacketManager(this,c);
			new MemoryFix(this);
			pManager=new PermissionManager(this,PacketManager,mysql);
			Manager=new HubManager(this,mysql,pManager,PacketManager);
			Manager.getCmd().register(CommandMuteAll.class, new CommandMuteAll(pManager));
			Manager.getCmd().register(CommandMem.class, new CommandMem(pManager));
			Manager.getCmd().register(CommandMemFix.class, new CommandMemFix(pManager));
			Manager.getCmd().register(CommandJump.class, new CommandJump(this));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/muteall");
			Manager.DebugLog(time, 45, this.getClass().getName());
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
