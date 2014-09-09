package me.kingingo.khub.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Scoreboard.PlayerScoreboard;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.UpdateAsync.UpdateAsyncType;
import me.kingingo.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Server.ServerInfo;
import net.minecraft.server.v1_7_R4.Block;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

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
			msg=msg.replaceAll("%","");
			if(manager.getPManager().hasPermission(p, Permission.ALL_PERMISSION))msg=msg.replaceAll("&", "§");
			event.setFormat(manager.getPManager().getPrefix(p) + p.getName() + "§7: "+ msg);
		}
	}
	
	@EventHandler
	public void LobbyMenu(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.NETHER_STAR){
				ev.getPlayer().openInventory(manager.getLobbyInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.COMPASS){
				ev.getPlayer().openInventory(manager.getGameInv());
				ev.setCancelled(true);
			}
		}
		else if(UtilEvent.isAction(ev, ActionType.PHYSICAL)){
			org.bukkit.block.Block b = ev.getPlayer().getLocation().getBlock();
			if(b.getTypeId()==70){
				ev.getPlayer().sendMessage(Text.PREFIX.getText()+"§eAbonniere uns auf Youtube!");
				ev.getPlayer().sendMessage(Text.PREFIX.getText()+"§eAb 5k folgt ein §4neuer§e OPENWORLD Server!");
				ev.getPlayer().sendMessage(Text.PREFIX.getText()+"§cLink: §bwww.Youtube.com/KingingoHD");
				ev.getPlayer().sendMessage(Text.PREFIX.getText()+"§cLink: §bwww.Youtube.com/momofilmt");
			}
		}
	}
	
//	@EventHandler
//	public void Move(PlayerMoveEvent ev){
//		if(ev.getPlayer().getLocation().getBlock().getTypeId()==70){
//			ev.getPlayer().sendMessage("§eAbonniere uns auf Youtube!");
//			ev.getPlayer().sendMessage("§7 Ab 5k folgt ein §4§lneuer§7§l OPENWORLD Server!");
//			ev.getPlayer().sendMessage("§cLink: §bwww.Youtube.com/KingingoHD");
//			ev.getPlayer().sendMessage("§cLink: §bwww.Youtube.com/momofilmt");
//		}
//	}

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
				UtilBG.SendToBungeeCord(
						"lobby/"
								+ manager.getLobbyList()
										.get(e.getCurrentItem().getItemMeta()
												.getDisplayName().split("a")[1])
										.getBg() + "/" + p.getName(), p,manager.getInstance());
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
			}else if(e.getCurrentItem().getType()==Material.WOOL){
				if(e.getCurrentItem().getAmount() == 8){
					Location loc = new Location(p.getWorld(),670.95906,91,1791.49289);
					loc.setPitch(3);
					loc.setYaw((float) -91.5903);
					p.teleport(loc);
				}else if(e.getCurrentItem().getAmount() == 16){
					Location loc = new Location(p.getWorld(),666.59299,91,1791.50829);
					loc.setPitch(1);
					loc.setYaw((float) 90.45407);
					p.teleport(loc);
				}
			}else if(e.getCurrentItem().getType()==Material.LEATHER_HELMET){
				Location loc = new Location(p.getWorld(),705.13355,91,1762.46768);
				loc.setPitch(3);
				loc.setYaw((float) -90.55322);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.STICK){
				Location loc = new Location(p.getWorld(),702.43580,91,1765.89488);
				loc.setPitch(0);
				loc.setYaw((float) -1.6535645);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.EYE_OF_ENDER){
				Location loc = new Location(p.getWorld(),702.51246,91,1758.74952);
				loc.setPitch(2);
				loc.setYaw((float) 179.59485);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.IRON_SWORD){
				Location loc = new Location(p.getWorld(),663.62425,92,1729.63968);
				loc.setPitch(1);
				loc.setYaw((float) 90.51337);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.IRON_SPADE){
				Location loc = new Location(p.getWorld(),673.42154,92,1729.64898);
				loc.setPitch(3);
				loc.setYaw((float) -90.34192);
				p.teleport(loc);
			}else if(e.getCurrentItem().getType()==Material.NETHER_STAR){
				Location loc = new Location(p.getWorld(),668.52374,92,1723.87255);
				loc.setPitch(2);
				loc.setYaw((float) 179.96045);
				p.teleport(loc);
			}
		}

		

	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		ev.getPlayer().getInventory().clear();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().getWorld().setWeatherDuration(0);
		ev.getPlayer().getWorld().setStorm(false);
		ev.getPlayer().setFoodLevel(20);
		ev.getPlayer().getInventory().clear();
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		ev.getPlayer().getInventory().setItem(4, UtilItem.Item(new ItemStack(Material.COMPASS), new String[]{"§bKlick mich um dich zu den Servern zu teleportieren."}, "§7Compass"));
		ev.getPlayer().getInventory().setItem(0,UtilItem.Item(new ItemStack(Material.NETHER_STAR), new String[]{"§bKlick mich um die Lobby zu wechseln."},"§aLobby Teleporter"));
		PlayerScoreboard ps = new PlayerScoreboard(ev.getPlayer());
		ps.addBoard(DisplaySlot.SIDEBAR, "§6§lInfo-Board");
		ps.setScore("Coins: ", DisplaySlot.SIDEBAR,manager.getMysql().getInt("SELECT coins FROM coins_list WHERE name='" + ev.getPlayer().getName().toLowerCase() + "'"));
		ps.setScore("Tokens: ", DisplaySlot.SIDEBAR,manager.getMysql().getInt("SELECT tokens FROM tokens_list WHERE name='" + ev.getPlayer().getName().toLowerCase() + "'"));
		ps.setBoard();
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
	
	public int max(GameType t){
		if(t==GameType.Falldown)return 16;
		if(t==GameType.BeastMode)return 16;
		if(t==GameType.TroubleMine)return 24;
		if(t==GameType.MarioParty)return 16;
		if(t==GameType.MegaRush)return 16;
		if(t==GameType.EnderMode)return 24;
		if(t==GameType.Rush)return 8;
		if(t==GameType.SurvivalGames)return 24;
		if(t==GameType.SkyPvP)return 24;
		return 16;
	}
	
	public GameState getS(GameType t,int i){
		if(t==GameType.TroubleMine){
			if(i==0)return GameState.Restart;
			if(i==1)return GameState.LobbyPhase;
			if(i==2)return GameState.SchutzModus;
			if(i==3)return GameState.InGame;
			if(i==4)return GameState.Restart;
		}else if(t==GameType.EnderMode){
			if(i==0)return GameState.Restart;
			if(i==1)return GameState.LobbyPhase;
			if(i==2)return GameState.InGame;
			if(i==3)return GameState.InGame;
			if(i==4)return GameState.Restart;
		}else if(t==GameType.SurvivalGames){
			if(i==0)return GameState.Restart;
			if(i==1)return GameState.LobbyPhase;
			if(i==2)return GameState.InGame;
			if(i==3)return GameState.InGame;
			if(i==4)return GameState.Restart;
		}else if(t==GameType.SkyPvP){
			if(i==0)return GameState.Restart;
			if(i==1)return GameState.LobbyPhase;
			if(i==2)return GameState.InGame;
			if(i==3)return GameState.InGame;
			if(i==4)return GameState.Restart;
		}else if(t==GameType.Falldown){
			if(i==0)return GameState.Restart;
			if(i==1)return GameState.LobbyPhase;
			if(i==2)return GameState.InGame;
			if(i==3)return GameState.SchutzModus;
			if(i==4)return GameState.Restart;
		}else{
			if(i==0)return GameState.Restart;
			if(i==1)return GameState.LobbyPhase;
			if(i==2)return GameState.InGame;
			if(i==3)return GameState.Restart;
		}
		return GameState.Restart;
	}
	
	public int ID(String typ){
		if(typ.contains("fd")){
			return Integer.valueOf(typ.split("fd")[1]);
		}else if(typ.contains("bm")){
			return Integer.valueOf(typ.split("bm")[1]);
		}else if(typ.contains("tm")){
			return Integer.valueOf(typ.split("tm")[1]);
		}else if(typ.contains("mp")){
			return Integer.valueOf(typ.split("mp")[1]);
		}else if(typ.contains("mr")){
			return Integer.valueOf(typ.split("mr")[1]);
		}else if(typ.contains("em")){
			return Integer.valueOf(typ.split("em")[1]);
		}else if(typ.contains("r")){
			return Integer.valueOf(typ.split("r")[1]);
		}else if(typ.contains("sg")){
			return Integer.valueOf(typ.split("sg")[1]);
		}else if(typ.contains("s")){
			return Integer.valueOf(typ.split("sk")[1]);
		}else if(typ.contains("a")){
			return Integer.valueOf(typ.split("a")[1]);
		}
		return -1;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void Packet(PacketReceiveEvent ev){
		if(ev.getPacket().getName().contains("SERVER_STATUS")){
			SERVER_STATUS ss = new SERVER_STATUS(ev.getPacket().toString().split("-/-"));
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
			for(int i=0; i<3; i++){
				s1=FreeServer(game);
				list.get(typ).put(i,s1);
				game.remove(s1);
			}
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)&&ev.getClickedBlock().getState() instanceof Sign&&manager.getSign_server().containsKey( ((Sign)ev.getClickedBlock().getState()) )){
			Sign s =(Sign) ev.getClickedBlock().getState();
			if(s.getLine(1).equalsIgnoreCase("Kein Server"))return;
			if(s.getLine(2).equalsIgnoreCase("> "+C.mOrange+"Premium "+C.cBlack+" <") && !manager.getPManager().hasPermission(ev.getPlayer(), Permission.JOIN_FULL_SERVER))return;
			UtilBG.sendToServer(ev.getPlayer(), manager.getSign_server().get(s).ID, manager.getInstance());
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
					
						sign.setLine(0, "- "+ C.cWhite + type.getKürzel()+" "+ID(se.ID) + C.cBlack + " -");
						sign.setLine(1, se.Map);
						if(se.CurrentPlayers>=se.MaxPlayers){
							sign.setLine(2, "> "+C.mOrange+"Premium "+C.cBlack+" <");
						}else{
							sign.setLine(2, "> "+C.cGreen+"Join "+C.cBlack+" <");
						}
						sign.setLine(3, se.CurrentPlayers+C.cGray+"/"+C.cBlack+se.MaxPlayers);
					
				}else{
					sign.setLine(0, "");
					sign.setLine(1, "Kein Server");
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
		if(ev.getType()!=UpdateAsyncType.SEC)return;
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
	
}
