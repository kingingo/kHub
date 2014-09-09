package me.kingingo.khub;

import lombok.Getter;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandMuteAll;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.SERVER_INFO_ALL;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UpdateAsync.UpdaterAsync;
import me.kingingo.kcore.memory.MemoryFix;

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
	//private PetManager pet;
	
	public void onEnable(){
		long time = System.currentTimeMillis();
		loadConfig();
		Updater=new Updater(this);
		c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"HUB"+getConfig().getInt("Config.Lobby"),this,Updater);
		mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		pManager=new PermissionManager(this,mysql);
		UpdaterAsync=new UpdaterAsync(this);
		PacketManager=new PacketManager(this,c);
		new MemoryFix(this);
		Manager=new HubManager(this,mysql,pManager,PacketManager);
		PacketManager.SendPacket("DATA-SERVER", new SERVER_INFO_ALL());
		Manager.getCmd().register(CommandMuteAll.class, new CommandMuteAll(pManager));
		//pet=new PetManager(Manager);
	    new AddonNight(this,Bukkit.getWorld("world"));
		Manager.DebugLog(time, 45, this.getClass().getName());
	}
	
	public void onDisable(){
		//pet.disable();
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
