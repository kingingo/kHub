package me.kingingo.khub.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.MySQL.MySQLErr;
import me.kingingo.kcore.MySQL.Events.MySQLErrorEvent;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.HUB_ONLINE;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.PrivatServer.Interface.Button.MainInterface;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Lobby.Lobby;
import me.kingingo.khub.Login.LoginManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HubListener extends kListener{

	@Getter
	private HubManager manager;
	@Getter
	private Inventory GameInv = Bukkit.createInventory(null, 45, "�7W�hle einen �6Server");
	@Getter
	private HashMap<GameType,ArrayList<Sign>> signs = new HashMap<>();
	@Getter
	private HashMap<Sign,SERVER_STATUS> sign_server = new HashMap<>();
	@Setter
	@Getter
	private Inventory GameMenue;
	@Getter
	private HashMap<String,Lobby> LobbyList = new HashMap<String,Lobby>();
	@Getter
	private Inventory LobbyInv;
	@Getter
	private LoginManager loginManager;
	@Getter
	private MainInterface mainInterface;
	
	public HubListener(HubManager manager) {
		super(manager.getInstance(), "HubListener");
		this.manager=manager;
		this.loginManager= new LoginManager(manager);
		this.manager.getInvisibleManager().setListener(this);
		this.mainInterface=new MainInterface(manager.getInstance(), "hub"+manager.getId(), manager.getPacketManager());
		
		if(("HUB"+manager.getInstance().getConfig().getInt("Config.Lobby")).equalsIgnoreCase("HUB2")&&Bukkit.getPluginManager().getPlugin("Votifier")!=null){
			new VoteListener(manager.getMysql(),manager.getCoins(),manager.getPacketManager());
		}
		
		if(manager.getHoliday()!=null&&manager.getHoliday()==CalendarType.WEIHNACHTEN){
			new ChristmasListener(this);
			new AddonNight(manager.getInstance(), Bukkit.getWorld("world"));
		}
		
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS BG_Lobby(ip varchar(30),name varchar(30),bg varchar(30), count int,place int)");
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS hub_signs(typ varchar(30),world varchar(30), x double, z double, y double)");
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
		}
		fillGameInv();
	}	
	
	//UNSICHTBAR / PET SHOP / Walk Effect / FLY
	public void fillGameInv(){
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
		GameInv.setItem(19, UtilItem.RenameItem(new ItemStack(160,1,(byte)1)," "));
		
		GameInv.setItem(22,UtilItem.RenameItem(new ItemStack(Material.ANVIL), "�6Spawn"));
		GameInv.setItem(2, UtilItem.RenameItem(new ItemStack(Material.DIAMOND_HELMET),"�6PvP-Server"));
		GameInv.setItem(6, UtilItem.RenameItem(new ItemStack(Material.GRASS),"�6Sky-Server"));
		GameInv.setItem(10, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)14),"�6SheepWars"));
		GameInv.setItem(28, UtilItem.RenameItem(new ItemStack(Material.BED,1),"�6BedWars"));
		GameInv.setItem(38, UtilItem.RenameItem(new ItemStack(Material.STICK),"�6TroubleInMinecraft"));
		GameInv.setItem(40, UtilItem.Item( UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), org.bukkit.Color.RED) ,new String[]{""},"�6Coming Soon"));
		GameInv.setItem(42, UtilItem.RenameItem(new ItemStack(Material.CHEST),"�6DeathGames"));
		GameInv.setItem(34, UtilItem.RenameItem(new ItemStack(Material.IRON_SPADE),"�6SkyPvP"));
		GameInv.setItem(25, UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR),"�6Falldown"));
		GameInv.setItem(16, UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD),"�6QuickSurvivalGames"));
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
	
	Sign s;
	public void loadSigns(){
		try
	    {
	      ResultSet rs = manager.getMysql().Query("SELECT typ,x,y,z FROM hub_signs");
	      while (rs.next()){
	    	  try{
	    		  if(GameType.valueOf(rs.getString(1))!=null&&!signs.containsKey(GameType.valueOf(rs.getString(1))))signs.put(GameType.valueOf(rs.getString(1)), new ArrayList<Sign>());
	    		  try{
	    			  s=((Sign) (new Location(Bukkit.getWorld("world"),rs.getInt(2),rs.getInt(3),rs.getInt(4))).getBlock().getState() );
		    		  signs.get(GameType.valueOf(rs.getString(1))).add( s );
	    		  }catch(ClassCastException e){
	    			  System.err.println("[kHub] Sign nicht gefunden ...");
	    		  }
	    	  }catch(IllegalArgumentException e){
	    		  System.out.println("NOT FOUND: "+rs.getString(1));
	    	  }
	      }
	      rs.close();
	  }catch (SQLException e){
	      e.printStackTrace();
	  }
	}
	
	public void loadLobbys(){
		try {
			ResultSet rs = manager.getMysql().Query("SELECT `name`,`bg`,`ip`,`place` FROM BG_Lobby");
			while (rs.next())LobbyList.put(rs.getString(1),new Lobby(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4)));
			rs.close();
		} catch (Exception err) {
			Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,manager.getMysql()));
		}
		
		int a = LobbyList.size();
		
		if(a<=4){
			a=9;
		}else {
			a=18;
		}
		
		this.LobbyInv=Bukkit.createInventory(null, a, "�lLobby's: ");
		ItemStack[] items = new ItemStack[LobbyInv.getSize()];
		for(String s : LobbyList.keySet()){
			Lobby l = LobbyList.get(s);
				int place = l.getPlace();
				if(l.getIp().equalsIgnoreCase(Bukkit.getServer().getIp())){
					ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
					ItemMeta im = item.getItemMeta();
					im.setDisplayName("�a"+l.getName());
					ArrayList<String> lore = new ArrayList<String>();
			    	lore.add("�6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten ");
			    	im.setLore(lore);
			    	item.setItemMeta(im);
			    	items[place]=item;	
				}else{
					ItemStack item = new ItemStack(289);
					ItemMeta im = item.getItemMeta();
					im.setDisplayName("�a"+l.getName());
					ArrayList<String> lore = new ArrayList<String>();
			    	lore.add("�6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten ");
			    	im.setLore(lore);
			    	item.setItemMeta(im);
			    	items[place]=item;
				}
		}
		
		for(int i = 0; i<LobbyInv.getSize();i++){
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

	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.WHEREIS_TEXT.getText(manager.getId()+" Hub"));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "�eEPICPVP �7- �eLobby "+manager.getId(), "�eShop.EpicPvP.de");
		ev.getPlayer().getInventory().setItem(2, UtilItem.Item(new ItemStack(Material.COMPASS), new String[]{"�bKlick mich um dich zu den Servern zu teleportieren."}, "�7Compass"));
		ev.getPlayer().getInventory().setItem(8,UtilItem.Item(new ItemStack(Material.NETHER_STAR), new String[]{"�bKlick mich um die Lobby zu wechseln."},"�aLobby Teleporter"));
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		if(ev.getPlayer().getName().equalsIgnoreCase("kingingohd"))ev.getPlayer().getInventory().setItem(6, UtilItem.Item(new ItemStack(Material.WORKBENCH), new String[]{"�bKlick mich um einen Privat Server zu erstellen."}, "�7Privat Server"));
    }
	
	@EventHandler
	public void StatusUpdate(UpdateEvent ev){
		if(ev.getType()==UpdateType.SLOW){
			manager.getPacketManager().SendPacket("DATA", new HUB_ONLINE("hub"+manager.getId(), UtilServer.getPlayers().size()));
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)&&ev.getClickedBlock().getState() instanceof Sign){
			Sign s =(Sign) ev.getClickedBlock().getState();
			if(getSign_server().containsKey( ((Sign)ev.getClickedBlock().getState()) )){
				if(s.getLine(1).equalsIgnoreCase("Lade Server.."))return;
				if(s.getLine(2).equalsIgnoreCase("> "+Color.ORANGE+"Premium "+Color.BLACK+" <") && !manager.getPermissionManager().hasPermission(ev.getPlayer(), kPermission.JOIN_FULL_SERVER))return;
				UtilBG.sendToServer(ev.getPlayer(), getSign_server().get(s).getId(), manager.getInstance());
			}else if(s.getLine(0).equalsIgnoreCase("[Server]")){
				UtilBG.sendToServer(ev.getPlayer(), s.getLine(2), manager.getInstance());
			}
		}
	}

	SERVER_STATUS ss;
	Sign sign;
	HUB_ONLINE hub;
	@EventHandler
	public void Packet(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof SERVER_STATUS){
			ss = (SERVER_STATUS)ev.getPacket();
			if(!getSigns().containsKey(ss.getTyp()))return;
			sign=getSigns().get(ss.getTyp()).get(ss.getSign());
			sign.setLine(0, "- "+ Color.WHITE + ss.getTyp().getK�rzel()+" "+ ss.getId().split("a")[1] + Color.BLACK + " -");
			sign.setLine(1, ss.getMap());
			if(ss.getOnline()>=ss.getMax_online()){
				sign.setLine(2, "> "+Color.ORANGE+"Premium "+Color.BLACK+" <");
			}else{
				sign.setLine(2, "> "+Color.GREEN+"Join "+Color.BLACK+" <");
			}
			sign.setLine(3, ss.getOnline()+Color.GRAY.toString()+"/"+Color.BLACK+ss.getMax_online());
			sign.update();
			getSign_server().put(sign, ss);
		}
	}
	
	@EventHandler
	public void onSign(SignChangeEvent ev) {
		Player p = ev.getPlayer();

		if (p.isOp()) {
			String sign = ev.getLine(0);
			ev.setLine(0, ev.getLine(0).replaceAll("&", "�"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "�"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "�"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "�"));

			if (sign.equalsIgnoreCase("[S]") && p.isOp()) {
				String typ = ev.getLine(1);
				getManager().getMysql().Update("INSERT INTO hub_signs (typ,world, x, z, y) VALUES ('"+ typ+ "','"+ p.getLocation().getWorld().getName()+ "','"+ ev.getBlock().getX()+ "','"+ ev.getBlock().getZ()+ "','" + ev.getBlock().getY() + "')");
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
			ev.setCancelled(true);
		}
		if(getLoginManager().getLogin().containsKey(ev.getPlayer())||getLoginManager().getRegister().contains(ev.getPlayer()))return;
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.NETHER_STAR){
				ev.getPlayer().openInventory(getLobbyInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.COMPASS){
				ev.getPlayer().openInventory(getGameInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.BONE){
				ev.getPlayer().openInventory(getManager().getShop().getMain());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.DIAMOND_PICKAXE){
				UtilBG.sendToServer(ev.getPlayer(), "v", getManager().getInstance());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.WORKBENCH){
				ev.getPlayer().openInventory(getMainInterface().getMain_page().getMain());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.FIREWORK){
				UtilBG.sendToServer(ev.getPlayer(), "event", getManager().getInstance());
				ev.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)
				|| (e.getCursor() == null || e.getCurrentItem() == null)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();

		if (e.getInventory().getName().equalsIgnoreCase("�lLobby's: ")) {
			if (e.getCurrentItem().getType() == Material.GLOWSTONE_DUST) {
				e.setCancelled(true);
				p.sendMessage("�aDu bist bereits auf der Lobby.");
				p.closeInventory();
			} else if (e.getCurrentItem().getTypeId() == 289) {
				e.setCancelled(true);
				p.closeInventory();
				UtilBG.SendToBungeeCord("lobby/"+ getLobbyList().get(e.getCurrentItem().getItemMeta().getDisplayName().split("a")[1]).getBg() + "/" + p.getName(), p,getManager().getInstance());
			} else {
				e.setCancelled(true);
				p.closeInventory();
			}
		}else if(e.getInventory().getName().equalsIgnoreCase("�7W�hle einen �6Server")){
			e.setCancelled(true);
			p.closeInventory();
			if(e.getCurrentItem().getType()==Material.DIAMOND_HELMET){
				UtilBG.sendToServer(p, "pvp",getManager().getInstance());
			}else if(e.getCurrentItem().getType()==Material.GRASS){
				UtilBG.sendToServer(p, "sky",getManager().getInstance());
			}else if(e.getCurrentItem().getType()==Material.ANVIL){
				p.teleport(p.getWorld().getSpawnLocation());
			}else if(e.getCurrentItem().getType()==Material.BED){
				Location loc = new Location(p.getWorld(),66.107,67,-1.973);
				loc.setPitch((float) 3.6);
				loc.setYaw((float) 178.2);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.WOOL){
				Location loc = new Location(p.getWorld(),65.201,67.06250,3.157);
				loc.setPitch((float) 4.7);
				loc.setYaw((float) 3.9);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.LEATHER_HELMET){
				p.teleport(p.getWorld().getSpawnLocation());
			}else if(e.getCurrentItem().getType()==Material.STICK){
				Location loc = new Location(p.getWorld(),0.53106,65.5,68.74096);
				loc.setPitch(0);
				loc.setYaw((float) 0.72094727);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.CHEST){
				Location loc = new Location(p.getWorld(),-74.48174,67,-3.86677);
				loc.setPitch(2);
				loc.setYaw((float) 179.83789);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.DIAMOND_SWORD){
				Location loc = new Location(p.getWorld(),41.38345,65,44.67852);
				loc.setPitch(3);
				loc.setYaw((float) -91.886475);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.IRON_SPADE){
				Location loc = new Location(p.getWorld(),52.07203,65,-48.50810);
				loc.setPitch(3);
				loc.setYaw((float) -90.53198);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.NETHER_STAR){
				Location loc = new Location(p.getWorld(),48.62277,65,-52.23374);
				loc.setPitch(2);
				loc.setYaw((float) 176.39111);
				p.teleport(loc);
			}
		}
	}

}
