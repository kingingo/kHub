package me.kingingo.khub;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Tokens;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.khub.Command.CommandBroadcast;
import me.kingingo.khub.Command.CommandEnderMode;
import me.kingingo.khub.Command.CommandGroup;
import me.kingingo.khub.Command.CommandTraitor;
import me.kingingo.khub.Listener.HubListener;
import me.kingingo.khub.Lobby.Lobby;
import me.kingingo.khub.Login.LoginManager;
import me.kingingo.khub.Server.ServerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
	@Getter
	private HashMap<Sign,ServerInfo> sign_server = new HashMap<>();
	@Setter
	@Getter
	private Inventory GameMenue;
	@Getter
	private HashMap<String,Lobby> LobbyList = new HashMap<String,Lobby>();
	@Getter
	private Inventory LobbyInv = Bukkit.createInventory(null, 9, "§lLobby's: ");  
	@Getter
	private Inventory GameInv = Bukkit.createInventory(null, 45, "§7Wähle einen §6Server");
	@Getter
	private Coins coins;
	@Getter
	private Tokens tokens;
	@Getter
	private PacketManager PacketManager;
	@Getter
	private LoginManager lManager;
	@Getter
	private CommandHandler cmd;
	
	public HubManager(JavaPlugin instance,MySQL mysql,PermissionManager pManager,PacketManager pmana){
		this.instance=instance;
		this.cmd=new CommandHandler(instance);
		this.lManager= new LoginManager(this);
		this.pManager=pManager;
		this.mysql=mysql;
		this.PacketManager=pmana;
		this.tokens=new Tokens(instance,mysql);
		this.coins=new Coins(mysql,instance);
		new HubListener(this);
		mysql.Update("CREATE TABLE IF NOT EXISTS hub_signs(typ varchar(30),world varchar(30), x double, z double, y double)");
		loadSigns();
		loadLobbys();
		
		for(GameType t : this.getSigns().keySet()){
			for(Sign s : this.getSigns().get(t)){
				s.setLine(0, "");
				s.setLine(1, "Lade Server");
				s.setLine(2, "");
				s.setLine(3, "");
				s.update();
			}
			
			getCmd().register(CommandTraitor.class, new CommandTraitor());
			getCmd().register(CommandEnderMode.class, new CommandEnderMode(this));
			getCmd().register(CommandGroup.class, new CommandGroup());
			getCmd().register(CommandBroadcast.class, new CommandBroadcast());
		}
		
		GameInv.setItem(0, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(9, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(18, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(27, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(36, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		
		GameInv.setItem(8, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(17, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(26, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(35, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		GameInv.setItem(44, UtilItem.RenameItem(new ItemStack(160,1,(byte)7)," "));
		
		GameInv.setItem(12, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		GameInv.setItem(14, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		GameInv.setItem(24, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		GameInv.setItem(32, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		GameInv.setItem(30, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		GameInv.setItem(20, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		
		GameInv.setItem(22,UtilItem.RenameItem(new ItemStack(Material.FIRE), "§6Spawn"));
		

		GameInv.setItem(2, UtilItem.RenameItem(new ItemStack(Material.DIAMOND_HELMET),"§6PvP-Server"));
		GameInv.setItem(6, UtilItem.RenameItem(new ItemStack(Material.GRASS),"§6Sky-Server"));
		

		GameInv.setItem(10, UtilItem.RenameItem(new ItemStack(Material.BED),"§6Rush"));
		GameInv.setItem(19, UtilItem.RenameItem(new ItemStack(Material.BED),"§6MegaRush"));
		GameInv.setItem(28, UtilItem.RenameItem(new ItemStack(Material.BED),"§6BeastMode"));

		GameInv.setItem(38, UtilItem.RenameItem(new ItemStack(Material.STICK),"§6TIMV"));
		GameInv.setItem(40, UtilItem.Item(UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), Color.RED),new String[]{""},"§6MarioParty"));
		GameInv.setItem(42, UtilItem.RenameItem(new ItemStack(Material.EYE_OF_ENDER),"§6EnderMode"));
		
		GameInv.setItem(34, UtilItem.RenameItem(new ItemStack(Material.IRON_SPADE),"§6SkyPvP"));
		GameInv.setItem(25, UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR),"§6Falldown"));
		GameInv.setItem(16, UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD),"§6SurvivalGames"));
		for(int i = 0 ; i < GameInv.getSize(); i++){
			if(GameInv.getItem(i)==null||GameInv.getItem(i).getType()==Material.AIR){
				if(GameInv.getItem(i)==null)GameInv.setItem(i, new ItemStack(Material.IRON_FENCE));
				GameInv.getItem(i).setTypeId(160);
				GameInv.getItem(i).setDurability((short) 8);
				ItemMeta im = GameInv.getItem(i).getItemMeta();
				im.setDisplayName(" ");
				GameInv.getItem(i).setItemMeta(im);
			}
		}
	}
	
	public void loadLobbys(){
		try {
			ResultSet rs = mysql.Query("SELECT `name`,`bg`,`ip`,`place` FROM BungeeCord_Lobby");
			while (rs.next())LobbyList.put(rs.getString(1),new Lobby(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4)));
			rs.close();
		} catch (Exception err) {
			System.err.println(err);
		}
		
		ItemStack[] items = new ItemStack[9];
		for(String s : LobbyList.keySet()){
			Lobby l = LobbyList.get(s);
				int place = l.getPlace();
				if(l.getIp().equalsIgnoreCase(Bukkit.getServer().getIp())){
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
		
		for(int i = 0; i<9;i++){
			if(items[i]==null||items[i].getType()==Material.AIR){
				ItemStack item = new ItemStack(160);
				items[i]=item;
				items[i].setDurability((short) 1);
				ItemMeta im = items[i].getItemMeta();
				im.setDisplayName(" ");
				items[i].setItemMeta(im);
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
