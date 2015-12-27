package me.kingingo.khub.Hub.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Addons.AddonDoubleJump;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.DeliveryPet.DeliveryObject;
import me.kingingo.kcore.DeliveryPet.DeliveryPet;
import me.kingingo.kcore.Disguise.DisguiseType;
import me.kingingo.kcore.Disguise.disguises.DisguiseBase;
import me.kingingo.kcore.Disguise.disguises.livings.DisguisePlayer;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Hologram.nametags.NameTagType;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonBase;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonTeleport;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Language.LanguageType;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Listener.EntityClick.EntityClickListener;
import me.kingingo.kcore.MySQL.MySQLErr;
import me.kingingo.kcore.MySQL.Events.MySQLErrorEvent;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.HUB_ONLINE;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Packet.Packets.TWIITTER_IS_PLAYER_FOLLOWER;
import me.kingingo.kcore.Packet.Packets.TWITTER_PLAYER_FOLLOW;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Permission.Event.PlayerLoadPermissionEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEnt;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.kHub;
import me.kingingo.khub.kManager;
import me.kingingo.khub.Hub.HubManager;
import me.kingingo.khub.Hub.Lobby;
import me.kingingo.khub.Hub.InvisbleManager.InvisibleManager;
import me.kingingo.khub.Hub.Listener.spezial.VoteListener;

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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HubListener extends kListener{

	@Getter
	private kManager manager;
	@Getter
	private InventoryPageBase GameInv;
	@Getter
	private HashMap<GameType,ArrayList<Sign>> signs = new HashMap<>();
	@Getter
	private HashMap<Sign,SERVER_STATUS> sign_server = new HashMap<>();
	@Getter
	private HashMap<Integer,Lobby> LobbyList = new HashMap<Integer,Lobby>();
	@Getter
	private Inventory LobbyInv;
	private InventoryPageBase language_inv;
	
	public HubListener(final HubManager manager) {
		this(manager,true);
	}

	public HubListener(final HubManager manager,boolean initialize) {
		super(manager.getInstance(), kHub.hubType+"Listener");
		this.manager=manager;
		Bukkit.getWorld("world").setAutoSave(false);
		if(initialize)initialize();
		
		Zombie z = (Zombie) CommandLocations.getLocation("versusc").getWorld().spawnCreature(CommandLocations.getLocation("versusc"), CreatureType.ZOMBIE);
		new NameTagMessage(NameTagType.SERVER, z.getEyeLocation().add(0, 0.2, 0), "§c§lVERSUS").send();
		z.getEquipment().setItemInHand(new ItemStack(Material.BOW));
		z.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		UtilEnt.setNoAI(z, true);
		
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
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS "+kHub.hubType+"_signs(typ varchar(30),world varchar(30), x double, z double, y double)");
	
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
			new DeliveryObject(new String[]{"","§7Click for Vote!","","§ePvP Rewards:","§7   200 Epics","§7   1x Inventory Repair","","§eGame Rewards:","§7   25 Gems","§7   100 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},kPermission.DELIVERY_PET_VOTE,false,28,"§aVote for EpicPvP",Material.PAPER,Material.REDSTONE_BLOCK,new Click(){

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
				new DeliveryObject(new String[]{"§aOnly for §eVIP§a!","","§ePvP Rewards:","§7   200 Epics","§7   10 Level","","§eGame Rewards:","§7   200 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},kPermission.DELIVERY_PET_VIP_WEEK,true,11,"§cRank §eVIP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getCoins().addCoins(p, true, 200);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §6ULTRA§a!","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   300 Epics","§7   4x Diamonds","§7   4x Iron Ingot","§7   4x Gold Ingot"},kPermission.DELIVERY_PET_ULTRA_WEEK,true,12,"§cRank §6ULTRA§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getCoins().addCoins(p, true, 300);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §aLEGEND§a!","","§ePvP Rewards:","§7   400 Epics","§7   20 Level","","§eGame Rewards:","§7   400 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   400 Epics","§7   6x Diamonds","§7   6x Iron Ingot","§7   6x Gold Ingot"},kPermission.DELIVERY_PET_LEGEND_WEEK,true,13,"§cRank §5LEGEND§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getCoins().addCoins(p, true, 400);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§a!","","§ePvP Rewards:","§7   500 Epics","§7   25 Level","","§eGame Rewards:","§7   500 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   500 Epics","§7   8x Diamonds","§7   8x Iron Ingot","§7   8x Gold Ingot"},kPermission.DELIVERY_PET_MVP_WEEK,true,14,"§cRank §3MVP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getCoins().addCoins(p, true, 500);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§c+§a!","","§ePvP Rewards:","§7   600 Epics","§7   30 Level","","§eGame Rewards:","§7   600 Coins","§7   4x TTT Paesse","","§eSkyBlock Rewards:","§7   600 Epics","§7   10x Diamonds","§7   10x Iron Ingot","§7   10x Gold Ingot"},kPermission.DELIVERY_PET_MVPPLUS_WEEK,true,15,"§cRank §9MVP§e+§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getManager().getCoins().addCoins(p, true, 600);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§7/twitter [TwitterName]","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","","§eSkyBlock Rewards:","§7   300 Epics","§7   15 Level"},kPermission.DELIVERY_PET_TWITTER,false,34,"§cTwitter Reward",Material.getMaterial(351),4,new Click(){

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
	public void Portal(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
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
		this.GameInv = new InventoryPageBase(InventorySize._27, "§8Game Menu");
		
		this.GameInv.addButton(4, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), "§6Spawn"), Bukkit.getWorld("world").getSpawnLocation()));
		this.GameInv.addButton(9, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GRASS), "§aSkyBlock"), CommandLocations.getLocation("skyblock")));
		this.GameInv.addButton(10, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_AXE), "§aPvP"), CommandLocations.getLocation("pvpt")));
		this.GameInv.addButton(11, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "§aQuickSurvivalGames"), CommandLocations.getLocation("QuickSurvivalGames")));
		this.GameInv.addButton(12, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.STICK), "§aTroubleInMinecraft"), CommandLocations.getLocation("TroubleInMinecraft")));
		this.GameInv.addButton(13, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.EYE_OF_ENDER), "§aSkyWars"), CommandLocations.getLocation("SkyWars")));
		this.GameInv.addButton(14, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BONE), "§aDeathGames"), CommandLocations.getLocation("DeathGames")));
		this.GameInv.addButton(15, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BED), "§aBedWars"), CommandLocations.getLocation("BedWars")));
		this.GameInv.addButton(16, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BOW), "§aVersus §7(§bBeta§7)"), CommandLocations.getLocation("vs")));
		this.GameInv.addButton(17, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG,1,(byte)91), "§aSheepWars"), CommandLocations.getLocation("SheepWars")));

		this.GameInv.fill(Material.STAINED_GLASS_PANE, 7);
		
		((HubManager)getManager()).getShop().addPage(this.GameInv);
	}	
	
	Sign s;
	public void loadSigns(){
		try
	    {
	      ResultSet rs = manager.getMysql().Query("SELECT typ,x,y,z FROM "+kHub.hubType+"_signs");
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
	
	public void loadLobbys(){
		try {
			ResultSet rs = manager.getMysql().Query("SELECT `name`,`bg`,`ip`,`place` FROM BG_Lobby");
			while (rs.next())LobbyList.put(rs.getInt(4),new Lobby(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4)));
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
		
		this.LobbyInv=Bukkit.createInventory(null, a, "§8Hub Selector");
		ItemStack[] items = new ItemStack[LobbyInv.getSize()];
		for(Lobby l : LobbyList.values()){
				int place = l.getPlace();
				if(l.getIp().equalsIgnoreCase(Bukkit.getServer().getIp())){
					if(l.getBg().startsWith("premiumhub")){
						items[place]=UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[]{"§6Klicke um die Premium Lobby "+ l.getName().split(" ")[2] + " zu betreten "}, "§b"+l.getName());
					}else{
						items[place]=UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[]{"§6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten "}, "§a"+l.getName());
					}
				}else{
					if(l.getBg().startsWith("premiumhub")){
						items[place]=UtilItem.Item(new ItemStack(353), new String[]{"§6Klicke um die Premium Lobby "+ l.getName().split(" ")[2] + " zu betreten "}, "§b"+l.getName());
					}else{
						items[place]=UtilItem.Item(new ItemStack(289), new String[]{"§6Klicke um die Lobby "+ l.getName().split(" ")[1] + " zu betreten "}, "§a"+l.getName());
					}
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
		ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "WHEREIS_TEXT",kHub.hubID+" "+kHub.hubType));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §a"+kHub.hubType+" "+kHub.hubID, "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		ev.getPlayer().getInventory().setItem(1, UtilItem.RenameItem(new ItemStack(Material.COMPASS), Language.getText(ev.getPlayer(), "HUB_ITEM_COMPASS")));
		ev.getPlayer().getInventory().setItem(4, UtilItem.RenameItem(new ItemStack(Material.BOOK_AND_QUILL),Language.getText(ev.getPlayer(), "HUB_ITEM_BUCH")+" §c§lBETA"));
		ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), Language.getText(ev.getPlayer(), "HUB_ITEM_NETHERSTAR")));
	}
	
	@EventHandler
	public void StatusUpdate(UpdateEvent ev){
		if(ev.getType()==UpdateType.SLOW){
			manager.getPacketManager().SendPacket("DATA", new HUB_ONLINE(kHub.hubType+kHub.hubID, UtilServer.getPlayers().size(),(int)UtilServer.getLagMeter().getTicksPerSecond()));
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
				if(!getSigns().containsKey(ss.getTyp()))return;
				sign=getSigns().get(ss.getTyp()).get(ss.getSign());
				sign.setLine(0, "- "+ Color.WHITE + ss.getTyp().getKürzel()+" "+ ss.getId().split("a")[1] + Color.BLACK + " -");
				sign.setLine(1, ss.getMap());
				if(ss.getOnline()>=ss.getMax_online()){
					sign.setLine(2, "> "+Color.ORANGE+"Premium "+Color.BLACK+" <");
				}else{
					sign.setLine(2, "> "+Color.GREEN+"Join "+Color.BLACK+" <");
				}
				sign.setLine(3, ss.getOnline()+Color.GRAY.toString()+"/"+Color.BLACK+ss.getMax_online());
				sign.update();
				getSign_server().put(sign, ss);
			}catch(Exception e){
				e.printStackTrace();
				if(getSigns().containsKey(ss.getTyp())){
					System.err.println("Find in Array: "+ss.getTyp());
					System.err.println("Array Amount "+getSigns().get(ss.getTyp()).size());
					sign=getSigns().get(ss.getTyp()).get(ss.getSign());
					System.err.println("SIGN: "+sign.getLocation().toString());
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
				getManager().getMysql().Update("INSERT INTO "+kHub.hubType+"_signs (typ,world, x, z, y) VALUES ('"+ typ+ "','"+ p.getLocation().getWorld().getName()+ "','"+ ev.getBlock().getX()+ "','"+ ev.getBlock().getZ()+ "','" + ev.getBlock().getY() + "')");
			}
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
		if(ev.getPlayer().hasPermission(kPermission.kFLY.getPermissionToString())){
			ev.getPlayer().setAllowFlight(true);
			ev.getPlayer().setFlying(true);
			ev.getPlayer().setLevel(3);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)
				|| (e.getCursor() == null || e.getCurrentItem() == null)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();

		if (e.getInventory().getName().equalsIgnoreCase("§8Hub Selector")) {
			if (e.getCurrentItem().getType() == Material.GLOWSTONE_DUST) {
				e.setCancelled(true);
				p.sendMessage("§aDu bist bereits auf der Lobby.");
				p.closeInventory();
			} else if (e.getCurrentItem().getTypeId() == 289) {
				e.setCancelled(true);
				p.closeInventory();
				UtilBG.SendToBungeeCord("lobby/"+ getLobbyList().get(e.getSlot()).getBg() + "/" + p.getName(), p,getManager().getInstance());
			}else if (e.getCurrentItem().getTypeId() == 353&&p.hasPermission(kPermission.PREMIUM_LOBBY.getPermissionToString())) {
				e.setCancelled(true);
				p.closeInventory();
				UtilBG.sendToServer(p, getLobbyList().get(e.getSlot()).getBg(), getManager().getInstance());
			} else {
				e.setCancelled(true);
				p.closeInventory();
			}
		}
	}

}