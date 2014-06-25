package me.kingingo.khub;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Tokens;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.khub.Listener.HubListener;
import me.kingingo.khub.Lobby.Lobby;
import me.kingingo.khub.Server.ServerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class HubManager{

	@Getter
	private JavaPlugin instance;
	@Getter
	private MySQL mysql;
	@Getter
	private HashMap<GameType,ArrayList<ServerInfo>> servers = new HashMap<>();
	@Getter
	private PermissionManager pManager;
	@Getter
	private ArrayList<SERVER_STATUS> Server_Status = new ArrayList<>();
	@Getter
	private HashMap<GameType,ArrayList<Sign>> signs = new HashMap<>();
	@Setter
	@Getter
	private Inventory GameMenue;
	@Getter
	private HashMap<String,Lobby> LobbyList = new HashMap<String,Lobby>();
	@Getter
	private Inventory LobbyInv = Bukkit.createInventory(null, 18, "§lLobby's: ");  	
	@Getter
	private Coins coins;
	@Getter
	private Tokens tokens;
	@Getter
	private PacketManager PacketManager;
	
	public HubManager(JavaPlugin instance,MySQL mysql,PermissionManager pManager,PacketManager pmana){
		this.instance=instance;
		this.pManager=pManager;
		this.mysql=mysql;
		this.PacketManager=pmana;
		this.tokens=new Tokens(instance,mysql);
		this.coins=new Coins(mysql,instance);
		new HubListener(this);
		mysql.Update("CREATE TABLE IF NOT EXISTS hub_signs(typ varchar(30),world varchar(30), x double, z double, y double)");
		loadSigns();
		loadLobbys();
	}
	
	public void loadLobbys(){
		try {
			ResultSet rs = mysql.Query("SELECT `name`,`bg`,`ip` FROM BungeeCord_Lobby");

			while (rs.next()) {
				
				LobbyList.put(rs.getString(1),new Lobby(rs.getString(1),rs.getString(2),rs.getString(3)));
			}

			rs.close();
		} catch (Exception err) {
			System.err.println(err);
		}
		
		ItemStack[] items = new ItemStack[18];
		for(String s : LobbyList.keySet()){
			Lobby l = LobbyList.get(s);
			if(l.getName().equalsIgnoreCase("Premium-Lobby")){
				if(l.getIP().equalsIgnoreCase(Bukkit.getServer().getIp())){
					ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
					ItemStack p = new ItemStack(160,1,(short)7);
					
					ItemMeta im = p.getItemMeta();
					im.setDisplayName("§a");
			    	p.setItemMeta(im);
					
					im = item.getItemMeta();
					im.setDisplayName("§a"+l.getName());
					ArrayList<String> lore = new ArrayList<String>();
			    	lore.add("§6Klicke um die Lobby §cPremium-Lobby§6 zu betreten ");
			    	im.setLore(lore);
			    	item.setItemMeta(im);
			    	
			    	items[0]=p;
			    	items[1]=p;
			    	items[2]=p;
			    	items[3]=p;
			    	items[4]=item;
			    	items[5]=p;
			    	items[6]=p;
			    	items[7]=p;
			    	items[8]=p;
				}else{
					ItemStack item = new ItemStack(Material.SUGAR);
					ItemStack p = new ItemStack(160,1,(short)7);
					
					ItemMeta im = p.getItemMeta();
					im.setDisplayName("§a");
			    	p.setItemMeta(im);
					
					im = item.getItemMeta();
					im.setDisplayName("§a"+l.getName());
					ArrayList<String> lore = new ArrayList<String>();
			    	lore.add("§6Klicke um die Lobby §cPremium-Lobby§6 zu betreten ");
			    	im.setLore(lore);
			    	item.setItemMeta(im);
			    	
			    	items[0]=p;
			    	items[1]=p;
			    	items[2]=p;
			    	items[3]=p;
			    	items[4]=item;
			    	items[5]=p;
			    	items[6]=p;
			    	items[7]=p;
			    	items[8]=p;
				}
			}else{
				int place = Integer.valueOf(l.getName().split(" ")[1]);
				place=place+8;
				if(l.getIP().equalsIgnoreCase(Bukkit.getServer().getIp())){
					ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
					ItemMeta im = item.getItemMeta();
					im.setDisplayName("§a"+l.getName());
					ArrayList<String> lore = new ArrayList<String>();
			    	lore.add("§6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten ");
			    	im.setLore(lore);
			    	item.setItemMeta(im);
			    	items[place]=item;
					//Data.LobbyInv.setItem(place, item);	
				}else{
					ItemStack item = new ItemStack(289);
					ItemMeta im = item.getItemMeta();
					im.setDisplayName("§a"+l.getName());
					ArrayList<String> lore = new ArrayList<String>();
			    	lore.add("§6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten ");
			    	im.setLore(lore);
			    	item.setItemMeta(im);
			    	items[place]=item;
					//Data.LobbyInv.setItem(place, LobbyItem(new ItemStack(289),l.getIP(),l.getName()));
				}
			}
		}
		
		for(int i = 0; i<18;i++){
			if(items[i]==null){
				ItemStack item = new ItemStack(289);
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§cLobby §4Offline§c ...");
				ArrayList<String> lore = new ArrayList<String>();
		    	lore.add("§cDiese Lobby ist momentan §4Offline§c.");
		    	im.setLore(lore);
		    	item.setItemMeta(im);
		    	items[i]=item;
			}
		}
		
		LobbyInv.setContents(items);
	}
	
	public void loadSigns(){
		try
	    {
	      ResultSet rs = mysql.Query("SELECT typ,x,y,z FROM hub_signs");
	      while (rs.next()){
	    	  try{
	    		  if(GameType.valueOf(rs.getString(1))!=null&&!signs.containsKey(GameType.valueOf(rs.getString(1))))signs.put(GameType.valueOf(rs.getString(1)), new ArrayList<Sign>());
		    	  signs.get(GameType.valueOf(rs.getString(1))).add( ((Sign) (new Location(Bukkit.getWorld("world"),rs.getInt(2),rs.getInt(3),rs.getInt(4))).getBlock().getState() ) );
	    	  }catch(IllegalArgumentException e){
	    		  System.out.println("NOT FOUND: "+rs.getString(1));
	    	  }
	      }
	      rs.close();
	  }catch (SQLException e){
	      e.printStackTrace();
	  }
	}
	
	public void DebugLog(long time,int zeile,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Zeile: "+zeile);
		System.err.println("[DebugMode]: Zeit: "+UtilTime.convertString(System.currentTimeMillis() - time, 1, UtilTime.TimeUnit.FIT));
	}
	
	public void DebugLog(String m){
		System.err.println("[DebugMode]: "+m);
	}
	
	
	
}
