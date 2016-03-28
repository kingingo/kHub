package eu.epicpvp.khub.Hub.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import bin.me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import bin.me.kingingo.kcore.Packet.Packets.HUB_ONLINE;
import bin.me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import bin.me.kingingo.kcore.Packet.Packets.TWIITTER_IS_PLAYER_FOLLOWER;
import bin.me.kingingo.kcore.Packet.Packets.TWITTER_PLAYER_FOLLOW;
import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.gamestats.ServerType;
import dev.wolveringer.dataclient.gamestats.StatsKey;
import eu.epicpvp.kcore.Addons.AddonDoubleJump;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.DeliveryPet.DeliveryObject;
import eu.epicpvp.kcore.DeliveryPet.DeliveryPet;
import eu.epicpvp.kcore.Disguise.DisguiseType;
import eu.epicpvp.kcore.Disguise.disguises.DisguiseBase;
import eu.epicpvp.kcore.Disguise.disguises.livings.DisguisePlayer;
import eu.epicpvp.kcore.Enum.GameState;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonTeleport;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Language.LanguageType;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Listener.EntityClick.EntityClickListener;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.UpdateAsync.UpdateAsyncType;
import eu.epicpvp.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.TabTitle;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilFile;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.kConfig.kConfig;
import eu.epicpvp.khub.kHub;
import eu.epicpvp.khub.kManager;
import eu.epicpvp.khub.Hub.HubManager;
import eu.epicpvp.khub.Hub.Lobby;
import eu.epicpvp.khub.Hub.InvisbleManager.InvisibleManager;
import eu.epicpvp.khub.Hub.Listener.spezial.VoteListener;
import lombok.Getter;

public class HubListener extends kListener{

	@Getter
	private kManager manager;
	@Getter
	private InventoryPageBase GameInv;
	@Getter
	private HashMap<String,Sign> gungame_signs = new HashMap<>();
	@Getter
	private HashMap<GameType,HashMap<String,ArrayList<Sign>>> signs = new HashMap<>();
	@Getter
	private HashMap<Sign,SERVER_STATUS> sign_server = new HashMap<>();
	@Getter
	private InventoryPageBase LobbyInv;
	private InventoryPageBase language_inv;
	private kConfig signconfig;
	
	public HubListener(final HubManager manager) {
		this(manager,true);
	}
	
	public HubListener(final HubManager manager,boolean initialize) {
		super(manager.getInstance(), kHub.hubType+"Listener");
		this.manager=manager;
		this.signconfig=new kConfig(UtilFile.getYMLFile(manager.getInstance(), "signs"));
		Bukkit.getWorld("world").setAutoSave(false);
		if(initialize)initialize();
		
		Zombie z = (Zombie) CommandLocations.getLocation("versusc").getWorld().spawnCreature(CommandLocations.getLocation("versusc"), CreatureType.ZOMBIE);
		new NameTagMessage(NameTagType.SERVER, z.getEyeLocation().add(0, 0.2, 0), "§c§lVERSUS").send();
		z.getEquipment().setItemInHand(new ItemStack(Material.BOW));
		z.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		UtilEnt.setNoAI(z, true);
		UtilEnt.setSilent(z, true);
		
		DisguiseBase dbase = DisguiseType.newDisguise(z, DisguiseType.PLAYER, new Object[]{" "});
		((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("EpicPvPMC"));
		manager.getDisguiseManager().disguise(dbase);
		new EntityClickListener(manager.getInstance(), new Click(){

			@Override
			public void onClick(Player p, ActionType arg1, Object arg2) {
				UtilBG.sendToServer(p, "versus", manager.getInstance());
			}
			
		}, z);
	}	
	
	public void initialize(){
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS BG_Lobby(ip varchar(30),name varchar(30),bg varchar(30), count int,place int)");
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS "+kHub.hubType+"_signs(typ varchar(30),ctyp varchar(30),world varchar(30), x double, z double, y double)");
	
		loadSigns();
		loadLobbys();

		if(Bukkit.getPluginManager().getPlugin("Votifier")!=null){
			new VoteListener(getManager());
		}
		
		new AddonDoubleJump(manager.getInstance());
		new InvisibleManager(manager.getInstance(),this);
		initializeLanguageInv();
		initializeDeliveryPet();
	}

	public void initializeDeliveryPet(){
		UtilServer.createDeliveryPet(new DeliveryPet(((HubManager)getManager()).getShop(),null,new DeliveryObject[]{
			new DeliveryObject(new String[]{"","§7Click for Vote!","","§ePvP Rewards:","§7   200 Epics","§7   1x Inventory Repair","","§eGame Rewards:","§7   25 Gems","§7   100 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},PermissionType.DELIVERY_PET_VOTE,false,28,"§aVote for EpicPvP",Material.PAPER,Material.REDSTONE_BLOCK,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						p.closeInventory();
						p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
						p.sendMessage(Language.getText(p,"PREFIX")+" ");
						p.sendMessage(Language.getText(p,"PREFIX")+"Vote Link:§a http://goo.gl/wxdAj4");
						p.sendMessage(Language.getText(p,"PREFIX")+" ");
						p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
					}
					
				},-1),
				new DeliveryObject(new String[]{"§aOnly for §eVIP§a!","","§ePvP Rewards:","§7   200 Epics","§7   10 Level","","§eGame Rewards:","§7   200 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},PermissionType.DELIVERY_PET_VIP_WEEK,true,11,"§cRank §eVIP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getMoney().add(p, StatsKey.COINS, 200);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §6ULTRA§a!","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   300 Epics","§7   4x Diamonds","§7   4x Iron Ingot","§7   4x Gold Ingot"},PermissionType.DELIVERY_PET_ULTRA_WEEK,true,12,"§cRank §6ULTRA§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getMoney().add(p, StatsKey.COINS, 300);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §aLEGEND§a!","","§ePvP Rewards:","§7   400 Epics","§7   20 Level","","§eGame Rewards:","§7   400 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   400 Epics","§7   6x Diamonds","§7   6x Iron Ingot","§7   6x Gold Ingot"},PermissionType.DELIVERY_PET_LEGEND_WEEK,true,13,"§cRank §5LEGEND§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getMoney().add(p, StatsKey.COINS, 400);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§a!","","§ePvP Rewards:","§7   500 Epics","§7   25 Level","","§eGame Rewards:","§7   500 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   500 Epics","§7   8x Diamonds","§7   8x Iron Ingot","§7   8x Gold Ingot"},PermissionType.DELIVERY_PET_MVP_WEEK,true,14,"§cRank §3MVP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getMoney().add(p, StatsKey.COINS, 500);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§c+§a!","","§ePvP Rewards:","§7   600 Epics","§7   30 Level","","§eGame Rewards:","§7   600 Coins","§7   4x TTT Paesse","","§eSkyBlock Rewards:","§7   600 Epics","§7   10x Diamonds","§7   10x Iron Ingot","§7   10x Gold Ingot"},PermissionType.DELIVERY_PET_MVPPLUS_WEEK,true,15,"§cRank §9MVP§e+§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getMoney().add(p, StatsKey.COINS, 600);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§7/twitter [TwitterName]","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","","§eSkyBlock Rewards:","§7   300 Epics","§7   15 Level"},PermissionType.DELIVERY_PET_TWITTER,false,34,"§cTwitter Reward",Material.getMaterial(351),4,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						String s1 = getManager().getMysql().getString("SELECT twitter FROM BG_TWITTER WHERE uuid='"+UtilPlayer.getRealUUID(p)+"'");
						if(s1.equalsIgnoreCase("null")){
							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_ACC_NOT"));
						}else{
							getManager().getPacketManager().SendPacket("DATA", new TWIITTER_IS_PLAYER_FOLLOWER(s1, p.getName()));
							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_CHECK"));
						}
					}
					
				},TimeSpan.DAY*7),
		},"§bThe Delivery Jockey!",EntityType.CHICKEN,CommandLocations.getLocation("DeliveryPet"),ServerType.GAME,getManager().getHologram(),getManager().getMysql()));
	}
	
	public void initializeLanguageInv(){
		this.language_inv=new InventoryPageBase(9, "");
		
		for(LanguageType type : LanguageType.values()){
			this.language_inv.addButton(new ButtonBase(new Click(){

				@Override
				public void onClick(Player player, ActionType action, Object obj) {
					Language.updateLanguage(player, LanguageType.get( ((ItemStack)obj).getItemMeta().getDisplayName().substring(2, ((ItemStack)obj).getItemMeta().getDisplayName().length()) ),manager.getPacketManager());
					player.closeInventory();
					player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "LANGUAGE_CHANGE"));
				}
				
			}, UtilItem.RenameItem(new ItemStack(Material.PAPER), "§a"+type.getDef().toUpperCase())));
		}
		this.language_inv.fill(Material.STAINED_GLASS_PANE,(byte)15);
		((HubManager)getManager()).getShop().addPage(this.language_inv);
	}
	
	@EventHandler
	public void Portal(UpdateAsyncEvent ev){
		if(ev.getType()==UpdateAsyncType.SEC){
			for(Player player : UtilServer.getPlayers()){
				if(player.getEyeLocation().getBlock().getType()==Material.PORTAL){
					if(CommandLocations.getLocation("pvp").distance(player.getLocation())<10){
						UtilBG.sendToServer(player, "pvp", getManager().getInstance());
					}else if(CommandLocations.getLocation("sky").distance(player.getLocation())<10){
						UtilBG.sendToServer(player, "sky", getManager().getInstance());
					}else if(CommandLocations.getLocation("versus").distance(player.getLocation())<10){
						UtilBG.sendToServer(player, "versus", getManager().getInstance());
					}
				}
			}
		}
	}
	
	//UNSICHTBAR / PET SHOP / Walk Effect / FLY
	public void fillGameInv(){
		this.GameInv = new InventoryPageBase(InventorySize._36, "§8Game Menu");
		
		this.GameInv.addButton(4, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.ANVIL), "§6Spawn"), Bukkit.getWorld("world").getSpawnLocation()));
		this.GameInv.addButton(11, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "§aQuickSurvivalGames"), CommandLocations.getLocation("QuickSurvivalGames")));
		this.GameInv.addButton(12, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.STICK), "§aTroubleInMinecraft"), CommandLocations.getLocation("TroubleInMinecraft")));
		this.GameInv.addButton(13, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.EYE_OF_ENDER), "§aSkyWars"), CommandLocations.getLocation("SkyWars")));
		this.GameInv.addButton(14, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), "§aFalldown §7[§d§lNEW§7]"), CommandLocations.getLocation("DeathGames")));
		this.GameInv.addButton(15, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BED), "§aBedWars"), CommandLocations.getLocation("BedWars")));
		this.GameInv.addButton(16, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BOW), "§aVersus"), CommandLocations.getLocation("vs")));
		this.GameInv.addButton(10, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG,1,(byte)91), "§aSheepWars"), CommandLocations.getLocation("SheepWars")));
		this.GameInv.addButton(31, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GOLD_AXE), "§aGunGame"), CommandLocations.getLocation("GunGame")));

		this.GameInv.addButton(21, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GRASS), "§aSkyBlock"), CommandLocations.getLocation("skyblock")));
		this.GameInv.addButton(22, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_AXE), "§aPvP"), CommandLocations.getLocation("pvpt")));
		this.GameInv.addButton(23, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GOLD_SPADE), "§aMasterbuilders"), CommandLocations.getLocation("masterbuilders")));
		this.GameInv.fill(Material.STAINED_GLASS_PANE, 7);
		
		((HubManager)getManager()).getShop().addPage(this.GameInv);
	}	
	
	Sign s;
	public void loadSigns(){
		
		try
	    {
	      ResultSet rs = manager.getMysql().Query("SELECT typ,ctyp,x,y,z FROM "+kHub.hubType.toLowerCase()+"_signs");
	      while (rs.next()){
	    	  try{
	    		  if(GameType.get(rs.getString(1))==null){
		    		  System.out.println(rs.getString(1) +" == NULL");
	    			  continue;
	    		  }
	    		  
	    		  if(GameType.get(rs.getString(1))!=null&&!signs.containsKey(GameType.get(rs.getString(1))))signs.put(GameType.get(rs.getString(1)),new HashMap<>());
	    		  if(!signs.get(GameType.get(rs.getString(1))).containsKey(rs.getString(2)))signs.get(GameType.get(rs.getString(1))).put(rs.getString(2), new ArrayList<Sign>());
	    		  try{
	    			  s=((Sign) (new Location(Bukkit.getWorld("world"),rs.getInt(3),rs.getInt(4),rs.getInt(5))).getBlock().getState() );
		    		  signs.get(GameType.get(rs.getString(1))).get(rs.getString(2)).add( s );
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
		
		for(GameType t : this.getSigns().keySet()){
			for(String ct : this.getSigns().get(t).keySet()){
				for(Sign s : this.getSigns().get(t).get(ct)){
					s.setLine(0, "");
					s.setLine(1, "Lade Server");
					s.setLine(2, "");
					s.setLine(3, "");
					s.update();
				}
			}
		}
		fillGameInv();
		
		for(String server : signconfig.getPathList("Signs").keySet()){
			gungame_signs.put(server, ((Sign)signconfig.getLocation("Signs."+server).getBlock().getState()));
		}
		
		for(Sign s : this.getGungame_signs().values()){
			s.setLine(0, "");
			s.setLine(1, "Lade Server");
			s.setLine(2, "");
			s.setLine(3, "");
			s.update();
		}
		
	}
	
	public void loadLobbys(){
		this.LobbyInv=new InventoryPageBase(InventorySize._18, "§8Hub Selector");
		try {
			ResultSet rs = manager.getMysql().Query("SELECT `name`,`bg`,`ip`,`place` FROM BG_Lobby");
			while (rs.next()){
				final Lobby l =new Lobby(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4));
				
				if(l.getIp().equalsIgnoreCase(Bukkit.getServer().getIp()) && l.getPort() == Bukkit.getServer().getPort()){
					if(l.getBg().startsWith("premiumhub")){
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								player.closeInventory();
							}
						}, UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[]{"§6Klicke um die Premium Lobby "+ l.getName().split(" ")[2] + " zu betreten "}, "§b"+l.getName())));
					}else{
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								player.closeInventory();
							}
						}, UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[]{"§6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten "}, "§a"+l.getName())));
					}
				}else{
					if(l.getBg().startsWith("premiumhub")){
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								if(player.hasPermission(PermissionType.PREMIUM_LOBBY.getPermissionToString())){
									UtilBG.SendToBungeeCord("lobby/"+ l.getBg() + "/" + player.getName(), player,getManager().getInstance());
								}
							}
						}, UtilItem.Item(new ItemStack(353), new String[]{"§6Klicke um die Premium Lobby "+ l.getName().split(" ")[2] + " zu betreten "}, "§b"+l.getName())));
					}else{
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								UtilBG.SendToBungeeCord("lobby/"+ l.getBg() + "/" + player.getName(), player,getManager().getInstance());
							}
						}, UtilItem.Item(new ItemStack(289), new String[]{"§6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten "}, "§a"+l.getName())));
					}
				}
			}
			rs.close();
		} catch (Exception err) {
			Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,manager.getMysql()));
		}
		this.LobbyInv.fill(Material.getMaterial(160), 1);
		((HubManager)getManager()).getShop().addPage(this.LobbyInv);
	
	}

	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "WHEREIS_TEXT",kHub.hubID+" "+kHub.hubType));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §a"+kHub.hubType+" "+kHub.hubID, "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		ev.getPlayer().getInventory().setItem(1, UtilItem.RenameItem(new ItemStack(Material.COMPASS), Language.getText(ev.getPlayer(), "HUB_ITEM_COMPASS")));
		ev.getPlayer().getInventory().setItem(4, UtilItem.RenameItem(new ItemStack(Material.BOOK_AND_QUILL),Language.getText(ev.getPlayer(), "HUB_ITEM_BUCH")+" §c§lBETA"));
		ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), Language.getText(ev.getPlayer(), "HUB_ITEM_NETHERSTAR")));
	}
	
	@EventHandler
	public void StatusUpdate(UpdateAsyncEvent ev){
		if(ev.getType()==UpdateAsyncType.SLOW){
			manager.getPacketManager().SendPacket("DATA", new HUB_ONLINE(kHub.hubType+kHub.hubID, UtilServer.getPlayers().size(),(int)UtilServer.getLagMeter().getTicksPerSecond()));
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)&&ev.getClickedBlock().getState() instanceof Sign){
			Sign s =(Sign) ev.getClickedBlock().getState();
			if(getSign_server().containsKey( s )){
				if(s.getLine(1).equalsIgnoreCase("Lade Server.."))return;
				if(s.getLine(2).equalsIgnoreCase(Color.ORANGE+Color.BOLD+"Premium") && !manager.getPermissionManager().hasPermission(ev.getPlayer(), PermissionType.JOIN_FULL_SERVER))return;
				UtilBG.sendToServer(ev.getPlayer(), getSign_server().get(s).getId(), manager.getInstance());
			}else if(getGungame_signs().containsValue( s )){
				if(s.getLine(1).equalsIgnoreCase("Lade Server.."))return;
				if(s.getLine(2).equalsIgnoreCase("§4§lOFFLINE"))return;
				if(s.getLine(2).equalsIgnoreCase(Color.RED+"Full"))return;
				UtilBG.sendToServer(ev.getPlayer(), s.getLine(0).substring(2,s.getLine(0).length()).replaceAll(" ", ""), manager.getInstance());
			}else if(s.getLine(0).equalsIgnoreCase("[Server]")){
				UtilBG.sendToServer(ev.getPlayer(), s.getLine(2), manager.getInstance());
			}
		}
	}
	
	@EventHandler
	public void Inventory(InventoryMoveItemEvent  ev){
		if(ev.getSource().getHolder() instanceof Player){
			ev.setCancelled(true);
		}
	}
	
	SERVER_STATUS ss;
	Sign sign;
	HUB_ONLINE hub;
	@EventHandler
	public void Packet(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof SERVER_STATUS){
			ss = (SERVER_STATUS)ev.getPacket();
			
			try{
				if(getSigns().containsKey(ss.getTyp())){
					if(getSigns().get(ss.getTyp()).containsKey(ss.getCtyp())){
//						if(getSigns().get(ss.getTyp()).get(ss.getCtyp()).size() <= ss.getSign()){
							sign=getSigns().get(ss.getTyp()).get(ss.getCtyp()).get(ss.getSign());
							if(ss.getState()!=GameState.LobbyPhase){
								System.err.println("Fehler Server nicht in der LobbyPhase "+ss.getId()+" "+ss.getState().name()+" "+ss.getTyp().name()+" "+ss.getCtyp());
								if(!sign.getLine(1).equalsIgnoreCase("Lade Server...")){
									sign.setLine(0, "");
									sign.setLine(1, "§lLade Server...");
									sign.setLine(2, "");
									sign.setLine(3, "");
									sign.update();
								}
								return;
							}
							
							sign.setLine(0, Color.BOLD + ss.getTyp().getKürzel() + ss.getId().split("a")[1] + (ss.getCtyp().equalsIgnoreCase("none")?"":" "+ ss.getCtyp().replaceFirst("_", "")));
							
							if(ss.getOnline()>=ss.getMax_online()){
								sign.setLine(1, Color.ORANGE+Color.BOLD+"Premium");
							}else{
								sign.setLine(1, Color.GREEN+Color.BOLD+"Join");
							}

							sign.setLine(2, ss.getMap());
							
							sign.setLine(3, ss.getOnline()+"/"+ss.getMax_online());
							sign.update();
							getSign_server().put(sign, ss);
//						}else{
//							System.err.println("Sign not found "+ss.getId()+" "+ss.getState().name()+" "+ss.getTyp().name()+" "+ss.getCtyp()+" "+ss.getSign());
//						}
					}else{
						System.err.println("CTyp nicht gefunden "+ss.getId()+" "+ss.getState().name()+" "+ss.getTyp().name()+" "+ss.getCtyp());
					}
				}else if(gungame_signs.containsKey(ss.getTyp().getKürzel()+ss.getId())){
					
					sign=gungame_signs.get(ss.getTyp().getKürzel()+ss.getId());
					sign.setLine(0, "§l" + ss.getTyp().getKürzel()+" "+ ss.getId());
					sign.setLine(1, ss.getMap());
					if(ss.getState()==GameState.Restart){
						sign.setLine(2, "§4§lOFFLINE");
					}else{
						if(ss.getOnline()>=ss.getMax_online()){
							sign.setLine(2, Color.RED+"Full");
						}else{
							sign.setLine(2, Color.GREEN+"Join");
						}	
					}
					sign.setLine(3, ss.getOnline()+"/"+ss.getMax_online());
					sign.update();
				}
				
			}catch(Exception e){
				e.printStackTrace();
				if(getSigns().containsKey(ss.getTyp())){
					System.err.println("Find in Array: "+ss.getTyp());
					System.err.println("Array Amount "+getSigns().get(ss.getTyp()).size());
//					sign=getSigns().get(ss.getTyp()).get(ss.getCtyp()).get(ss.getSign());
//					System.err.println("SIGN: "+sign.getLocation().toString());
				}
				System.err.println("Sign: "+ss.getSign());
			}
		}else if(ev.getPacket() instanceof TWITTER_PLAYER_FOLLOW){
			TWITTER_PLAYER_FOLLOW tw = (TWITTER_PLAYER_FOLLOW)ev.getPacket();
			
			if(UtilPlayer.isOnline(tw.getPlayer())){
				Player p = Bukkit.getPlayer(tw.getPlayer());
				if(!tw.isFollow()){
					getManager().getMysql().Update("DELETE FROM BG_TWITTER WHERE uuid='" + UtilPlayer.getRealUUID(p) + "'");
					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_FOLLOW_N"));
					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_REMOVE"));
				}else{
					UtilServer.getDeliveryPet().deliveryBlock(p, "§cTwitter Reward");
					getManager().getCoins().addCoins(p, true, 300);
				}
			}
		}
	}
	
	@EventHandler
	public void breakB(BlockBreakEvent ev){
		if(ev.getBlock().getState() instanceof Sign){
			Sign s = (Sign)ev.getBlock().getState();

			if(gungame_signs.containsValue(s)){
				for(String ss : gungame_signs.keySet()){
					if(UtilLocation.isSameLocation(gungame_signs.get(ss).getLocation(), s.getLocation())){
						signconfig.set("Signs."+ss, null);
						signconfig.save();
						reloadSignConfig();
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void onSign(SignChangeEvent ev) {
		Player p = ev.getPlayer();

		if (p.isOp()) {
			String sign = ev.getLine(0);
			ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));

			if (sign.equalsIgnoreCase("[S]") && p.isOp()) {
				String typ = ev.getLine(1);
				String ctyp = ev.getLine(2);
				getManager().getMysql().Update("INSERT INTO "+kHub.hubType+"_signs (typ,cTyp,world, x, z, y) VALUES ('"+ typ+ "', '"+ctyp+"','"+ p.getLocation().getWorld().getName()+ "','"+ ev.getBlock().getX()+ "','"+ ev.getBlock().getZ()+ "','" + ev.getBlock().getY() + "')");
			}else if(sign.equalsIgnoreCase("[Server]")&&p.isOp()){
				GameType typ = GameType.get(ev.getLine(1));
				
				if(typ==null){
					p.sendMessage("§cGameType == NULL");
					return;
				}
				
				signconfig.setLocation("Signs."+(typ.getKürzel()+ev.getLine(2)), ev.getBlock().getLocation());
				signconfig.save();
				reloadSignConfig();
			}
		}
	}
	
	public void reloadSignConfig(){
		gungame_signs.clear();
		for(String server : signconfig.getPathList("Signs").keySet()){
			gungame_signs.put(server, ((Sign)signconfig.getLocation("Signs."+server).getBlock().getState()));
		}
		
		for(Sign s : this.getGungame_signs().values()){
			s.setLine(0, "");
			s.setLine(1, "Lade Server");
			s.setLine(2, "");
			s.setLine(3, "");
			s.update();
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
			ev.setCancelled(true);
		}
		
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.NETHER_STAR){
				ev.getPlayer().openInventory(getLobbyInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.COMPASS){
				ev.getPlayer().openInventory(getGameInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.CHEST){
				ev.getPlayer().openInventory(((HubManager)getManager()).getShop().getMain());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.DIAMOND_PICKAXE){
				UtilBG.sendToServer(ev.getPlayer(), "v", getManager().getInstance());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.BOOK_AND_QUILL){
				ev.setCancelled(true);
				ev.getPlayer().openInventory(language_inv);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.FIREWORK){
				UtilBG.sendToServer(ev.getPlayer(), "event", getManager().getInstance());
				ev.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void loadper(PlayerLoadPermissionEvent ev){
		if(ev.getPlayer().hasPermission(PermissionType.kFLY.getPermissionToString())){
			ev.getPlayer().setAllowFlight(true);
			ev.getPlayer().setFlying(true);
			ev.getPlayer().setLevel(3);
		}
	}
}