package me.kingingo.khub.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.HUB_ONLINE;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.UpdateAsync.UpdateAsyncType;
import me.kingingo.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Server.ServerInfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class HubListener extends kListener{

	private HubManager manager;
	
	public HubListener(HubManager manager) {
		super(manager.getInstance(),"HubListener");
		this.manager=manager;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			Player p = event.getPlayer();
			String msg = event.getMessage();
			if((!manager.getPManager().hasPermission(p, kPermission.CHAT_LINK))&&
					(msg.toLowerCase().contains("minioncraft")||
							msg.toLowerCase().contains("mastercraft")||
							UtilString.checkForIP(msg))){
				event.setCancelled(true);
				return;
			}
			msg=msg.replaceAll("%","");
			if(manager.getPManager().hasPermission(p, kPermission.ALL_PERMISSION))msg=msg.replaceAll("&", "§");
			event.setFormat(manager.getPManager().getPrefix(p) + p.getName() + "§7:§f "+ msg);
		}
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event){
	    if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
			ev.setCancelled(true);
		}
		if(manager.getLManager().getLogin().containsKey(ev.getPlayer())||manager.getLManager().getRegister().contains(ev.getPlayer()))return;
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.NETHER_STAR){
				ev.getPlayer().openInventory(manager.getLobbyInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.COMPASS){
				ev.getPlayer().openInventory(manager.getGameInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.BONE){
				ev.getPlayer().openInventory(manager.getShop().getMain());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.DIAMOND_PICKAXE){
				UtilBG.sendToServer(ev.getPlayer(), "v", manager.getInstance());
			}else if(ev.getPlayer().getItemInHand().getType()==Material.WORKBENCH){
				ev.getPlayer().openInventory(manager.getMainInterface().getMain_page().getMain());
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

		if (e.getInventory().getName().equalsIgnoreCase("§lLobby's: ")) {
			if (e.getCurrentItem().getType() == Material.GLOWSTONE_DUST) {
				e.setCancelled(true);
				p.sendMessage("§aDu bist bereits auf der Lobby.");
				p.closeInventory();
			} else if (e.getCurrentItem().getTypeId() == 289) {
				e.setCancelled(true);
				p.closeInventory();
				UtilBG.SendToBungeeCord("lobby/"+ manager.getLobbyList().get(e.getCurrentItem().getItemMeta().getDisplayName().split("a")[1]).getBg() + "/" + p.getName(), p,manager.getInstance());
			} else {
				e.setCancelled(true);
				p.closeInventory();
			}
		}else if(e.getInventory().getName().equalsIgnoreCase("§7Wähle einen §6Server")){
			e.setCancelled(true);
			p.closeInventory();
			if(e.getCurrentItem().getType()==Material.DIAMOND_HELMET){
				UtilBG.sendToServer(p, "pvp",manager.getInstance());
			}else if(e.getCurrentItem().getType()==Material.GRASS){
				UtilBG.sendToServer(p, "sky",manager.getInstance());
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
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		ev.getPlayer().getInventory().clear();
	}
	
	@EventHandler
	public void AddBoard(PlayerSetScoreboardEvent ev){
		UtilPlayer.setScoreboard(ev.getPlayer(), manager.getCoins(), manager.getPManager());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.WHEREIS_TEXT.getText(manager.getId()+" Hub"));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEPICPVP §7- §eLobby "+manager.getId(), "§eShop.EpicPvP.de");
		ev.getPlayer().getWorld().setWeatherDuration(0);
		ev.getPlayer().getWorld().setStorm(false);
		ev.getPlayer().setFoodLevel(20);
		ev.getPlayer().getInventory().setHelmet(null);
		ev.getPlayer().getInventory().clear();
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		ev.getPlayer().getInventory().setItem(2, UtilItem.Item(new ItemStack(Material.BONE), new String[]{"§bKlick mich um in den Pet Shop zukommen."}, "§7PetShop"));
		ev.getPlayer().getInventory().setItem(4, UtilItem.Item(new ItemStack(Material.COMPASS), new String[]{"§bKlick mich um dich zu den Servern zu teleportieren."}, "§7Compass"));
		ev.getPlayer().getInventory().setItem(0,UtilItem.Item(new ItemStack(Material.NETHER_STAR), new String[]{"§bKlick mich um die Lobby zu wechseln."},"§aLobby Teleporter"));
		if(ev.getPlayer().getName().equalsIgnoreCase("kingingohd"))ev.getPlayer().getInventory().setItem(6, UtilItem.Item(new ItemStack(Material.WORKBENCH), new String[]{"§bKlick mich um einen Privat Server zu erstellen."}, "§7Privat Server"));
	}
	
	@EventHandler
	public void Food(FoodLevelChangeEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayer(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String cmd;
		if (event.getMessage().contains(" ")) {
			String[] parts = event.getMessage().split(" ");
			cmd = parts[0];
		} else {
			cmd = event.getMessage();
		}
		if (cmd.equalsIgnoreCase("/msg")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/msg")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		}else if (cmd.equalsIgnoreCase("/ban")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/kill")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/about")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.contains("/kill")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/tell")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/plugin")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/plugins")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/pl")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/about")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/version")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		} else if (cmd.equalsIgnoreCase("/me")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		}else if (cmd.equalsIgnoreCase("/bukkit:kill")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:msg")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:tell")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:me")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/?")) {
	       event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/help")) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Nope :3");
		}
	}
	
	@EventHandler
	public void Command(PlayerCommandPreprocessEvent ev){
		 if(ev.getMessage().contains("/bukkit:")){
			Player p=ev.getPlayer();
			ev.setCancelled(true);
			p.sendMessage("§cDein Ernst?");
		 }
	}
	
	@EventHandler
	public void Break(BlockBreakEvent ev) {
		if (ev.getPlayer().isOp())return;
		
		ev.setCancelled(true);
	}

	@EventHandler
	public void Break(BlockPlaceEvent ev) {
		if (ev.getPlayer().isOp())
			return;
		ev.setCancelled(true);
	}

	@EventHandler
	public void Break(BlockIgniteEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void Break(BlockBurnEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void Break(BlockSpreadEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		e.setCancelled(true);
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
				manager.getMysql().Update("INSERT INTO hub_signs (typ,world, x, z, y) VALUES ('"+ typ+ "','"+ p.getLocation().getWorld().getName()+ "','"+ ev.getBlock().getX()+ "','"+ ev.getBlock().getZ()+ "','" + ev.getBlock().getY() + "')");
			}

		}

	}
	
	@EventHandler
	public void Message(ClientReceiveMessageEvent ev){
		if(ev.getMessage().contains("whitelist=?off")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist off");
		}else if(ev.getMessage().contains("whitelist=?on")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");
		}else if(ev.getMessage().contains("reload=?now")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
		}else if(ev.getMessage().contains("stop=?now")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}else if(ev.getMessage().contains("restart=?now")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			try {
				Runtime.getRuntime().exec("./start.sh");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	SERVER_STATUS ss;
	HUB_ONLINE hub;
	@EventHandler
	public void Packet(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof SERVER_STATUS){
			ss = (SERVER_STATUS)ev.getPacket();
			manager.getServer_Status().add(ss);
		}
	}
	
	HashMap<GameType,HashMap<Integer,ServerInfo>> list = new HashMap<>();
	ArrayList<ServerInfo> game;
	ServerInfo s1;
	@EventHandler
	public void Free(UpdateAsyncEvent ev){
		if(ev.getType()!=UpdateAsyncType.SEC)return;
		for(GameType typ : manager.getServers().keySet()){
			if(!list.containsKey(typ))list.put(typ, new HashMap<Integer,ServerInfo>());
			game=(ArrayList<ServerInfo>)manager.getServers().get(typ).clone();
			for(int i=0; i<manager.getSigns().get(typ).size(); i++){
				s1=FreeServer(game);
				list.get(typ).put(i,s1);
				game.remove(s1);
			}
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)&&ev.getClickedBlock().getState() instanceof Sign){
			Sign s =(Sign) ev.getClickedBlock().getState();
			if(manager.getSign_server().containsKey( ((Sign)ev.getClickedBlock().getState()) )){
				if(s.getLine(1).equalsIgnoreCase("Lade Server.."))return;
				if(s.getLine(2).equalsIgnoreCase("> "+Color.ORANGE+"Premium "+Color.BLACK+" <") && !manager.getPManager().hasPermission(ev.getPlayer(), kPermission.JOIN_FULL_SERVER))return;
				UtilBG.sendToServer(ev.getPlayer(), manager.getSign_server().get(s).ID, manager.getInstance());
			}else if(s.getLine(0).equalsIgnoreCase("[Server]")){
				UtilBG.sendToServer(ev.getPlayer(), s.getLine(2), manager.getInstance());
			}
		}
	}
	
	ServerInfo s;
	public ServerInfo FreeServer(ArrayList<ServerInfo> game){
		s=null;
		for(int i = 0 ; i < game.size() ; i++){
			s=game.get(i);
			if(s.State!=GameState.LobbyPhase)continue;
			for(int i1 = 0 ; i1 < game.size() ; i1++){
				if(game.get(i1).State!=GameState.LobbyPhase)continue;
				if(game.get(i1).CurrentPlayers > s.CurrentPlayers){
					s=null;
					break;
				}
			}
			if(s!=null){
				break;
			}
		}
		return s;
	}
	
	ServerInfo se;
	Sign sign;
	List<String> strings = new ArrayList<String>();
	HashMap<GameType,HashMap<Integer,ServerInfo>> l = new HashMap<>();
	@EventHandler
	public void Sign(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(list.isEmpty())return;
		l=(HashMap<GameType,HashMap<Integer,ServerInfo>>)list.clone();
		for(GameType type : l.keySet()){
			for(int i : l.get(type).keySet()){
				se=l.get(type).get(i);
				try{
					sign=manager.getSigns().get(type).get(i);
				}catch(NullPointerException e){
					System.err.println("[Hub] Fehler: "+type.getKürzel()+" I:"+i);
					e.printStackTrace();
					continue;
				}
				if(se!=null&&se.State==GameState.LobbyPhase){
					//ID(se.ID)
						sign.setLine(0, "- "+ Color.WHITE + type.getKürzel()+" "+ se.ID.split("a")[1] + Color.BLACK + " -");
						sign.setLine(1, se.Map);
						if(se.CurrentPlayers>=se.MaxPlayers){
							sign.setLine(2, "> "+Color.ORANGE+"Premium "+Color.BLACK+" <");
						}else{
							sign.setLine(2, "> "+Color.GREEN+"Join "+Color.BLACK+" <");
						}
						sign.setLine(3, se.CurrentPlayers+Color.GRAY.toString()+"/"+Color.BLACK+se.MaxPlayers);
					
				}else{
					sign.setLine(0, "");
					sign.setLine(1, "Lade Server..");
					sign.setLine(2, "");
					sign.setLine(3, "");
				}
				manager.getSign_server().put(sign, se);
				sign.update();
			}
			
		}
		
	}
	
	boolean b = false;
	ArrayList<SERVER_STATUS> cloned;
	ServerInfo si;
	@EventHandler
	public void StatusUpdate(UpdateAsyncEvent ev){
		if(ev.getType()==UpdateAsyncType.SEC){
			if(manager.getServer_Status().isEmpty())return;
			cloned =(ArrayList<SERVER_STATUS>) manager.getServer_Status().clone();
			for(SERVER_STATUS ss : cloned){
				if(!manager.getServers().containsKey(ss.getTyp()))manager.getServers().put(ss.getTyp(), new ArrayList<ServerInfo>());
				for(ServerInfo si :  manager.getServers().get(ss.getTyp())){
					if(si.ID.equalsIgnoreCase(ss.getId())){
						si.CurrentPlayers=ss.getOnline();
						si.MaxPlayers=ss.getMax_online();
						si.Map=ss.getMap();
						si.State=ss.getState();
						b=true;
					}
				}
				
				if(!b){
					si = new ServerInfo();
					si.CurrentPlayers=ss.getOnline();
					si.MaxPlayers=ss.getMax_online();
					si.Map=ss.getMap();
					si.State=ss.getState();
					si.ID=ss.getId();
					if(!manager.getServers().containsKey(ss.getTyp()))manager.getServers().put(ss.getTyp(), new ArrayList<ServerInfo>());
					manager.getServers().get(ss.getTyp()).add(si);
				}else{
					b=false;
				}
				manager.getServer_Status().remove(ss);
			}
			cloned.clear();
		}
		
		if(ev.getType()==UpdateAsyncType.SEC_8){
			manager.getPacketManager().SendPacket("DATA", new HUB_ONLINE("hub"+manager.getId(), UtilServer.getPlayers().size()));
		}
	}
	
}
