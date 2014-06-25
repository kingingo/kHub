package me.kingingo.khub;

import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UpdateAsync.UpdaterAsync;
import me.kingingo.kcore.memory.MemoryFix;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.SERVER_INFO_ALL;
import me.kingingokhub.Pet.PetManager;

import org.bukkit.plugin.java.JavaPlugin;

public class kHub extends JavaPlugin{

	private Client c;
	private Updater Updater;
	private UpdaterAsync UpdaterAsync;
	private MySQL mysql;
	private PermissionManager pManager;
	private HubManager Manager;
	private PacketManager PacketManager;
	private PetManager pet;
	//dd
	public void onEnable(){
		long time = System.currentTimeMillis();
		loadConfig();
		Updater=new Updater(this);
		c = new Client(getConfig().getInt("Config.Client.Port"),getConfig().getString("Config.Client.Host"),"HUB"+getConfig().getInt("Config.Lobby"),this,Updater);
		mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		pManager=new PermissionManager(this,mysql);
		UpdaterAsync=new UpdaterAsync(this);
		PacketManager=new PacketManager(this,c);
		new MemoryFix(this);
		Manager=new HubManager(this,mysql,pManager,PacketManager);
		PacketManager.SendPacket("DATA-SERVER", new SERVER_INFO_ALL());
		pet=new PetManager(Manager);
		Manager.DebugLog(time, 21, this.getClass().getName());
	}
	
	public void onDisable(){
		pet.disable();
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
