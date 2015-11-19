package me.kingingo.khub;

import lombok.Getter;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Disguise.DisguiseManager;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Gems;
import me.kingingo.kcore.Util.UtilServer;

public class kManager {
	@Getter
	private kHub instance;
	@Getter
	private MySQL mysql;
	@Getter
	private PacketManager packetManager;
	@Getter
	private CommandHandler cmdHandler;
	@Getter
	private GroupTyp type;
	private PermissionManager permissionManager;
	private Gems gems;
	private Coins coins;
	private DisguiseManager disguiseManager;
	private PetManager petManager;
	private Hologram hologram;

	public kManager(kHub instance,CommandHandler cmdHandler,MySQL mysql,PacketManager packetManager){
		this.instance=instance;
		this.cmdHandler=cmdHandler;
		this.mysql=mysql;
		this.packetManager=packetManager;
		if(kHub.hubType.toLowerCase().endsWith("hub"))this.type=GroupTyp.GAME;
		Calendar.getHoliday();
		
		UtilServer.createLagListener(getCmdHandler());
		new Listener(this);
	}
	
	public PermissionManager getPermissionManager(){
		if(this.permissionManager==null)this.permissionManager=new PermissionManager(getInstance(), getType(), getPacketManager(), getMysql());
		return this.permissionManager;
	}
	
	public Hologram getHologram(){
		if(this.hologram==null)this.hologram=new Hologram(getInstance());
		return this.hologram;
	}
	
	public Coins getCoins(){
		if(this.coins==null)this.coins=new Coins(getInstance(), getMysql());
		return this.coins;
	}
	
	public PetManager getPetManager(){
		if(this.petManager==null)this.petManager=new PetManager(getInstance());
		return this.petManager;
	}
	
	public DisguiseManager getDisguiseManager(){
		if(this.disguiseManager==null)this.disguiseManager=new DisguiseManager(getInstance());
		return this.disguiseManager;
	}
	
	public Gems getGems(){
		if(this.gems==null)this.gems=new Gems(getMysql());
		return this.gems;
	}
	
	public void DebugLog(long time,int zeile,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Zeile: "+zeile);
	}
	
	public void DebugLog(String m){
		System.err.println("[DebugMode]: "+m);
	}
}
