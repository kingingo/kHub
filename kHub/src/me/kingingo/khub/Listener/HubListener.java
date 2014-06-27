package me.kingingo.khub.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Packet.Packet;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.Permission;
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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HubListener extends kListener{

	private HubManager manager;
	
	public HubListener(HubManager manager) {
		super(manager.getInstance(),"HubListener");
		this.manager=manager;
	}
	
	@EventHandler
	public void LobbyMenu(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.NETHER_STAR){
				ev.getPlayer().openInventory(manager.getLobbyInv());
				ev.setCancelled(true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.COMPASS){
				ev.getPlayer().openInventory(manager.getLobbyInv());
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
				UtilBG.SendToBungeeCord(
						"lobby/"
								+ manager.getLobbyList()
										.get(e.getCurrentItem().getItemMeta()
												.getDisplayName().split("a")[1])
										.getBG() + "/" + p.getName(), p,manager.getInstance());
			} else if (e.getCurrentItem().getType() == Material.SUGAR) {
				e.setCancelled(true);
				p.closeInventory();
				if (manager.getPManager().hasPermission(p, Permission.PREMIUM_LOBBY)) {
					UtilBG.SendToBungeeCord(
							"lobby/"
									+ manager.getLobbyList().get(
											e.getCurrentItem().getItemMeta()
													.getDisplayName()
													.split("a")[1]).getBG()
									+ "/" + p.getName(), p,manager.getInstance());
				} else {
					p.sendMessage("§6Du hast keine§a Premium§6 Rechte.");
				}
			} else {
				e.setCancelled(true);
				p.closeInventory();
			}
		}

		

	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		ev.getPlayer().getInventory().clear();
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().getWorld().setWeatherDuration(0);
		ev.getPlayer().getWorld().setStorm(false);
		ev.getPlayer().setFoodLevel(20);
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		ev.getPlayer().getInventory().setItem(0,UtilItem.Item(new ItemStack(Material.NETHER_STAR), new String[]{"§bKlick mich um die Lobby zu wechseln."},"§aLobby Teleporter"));
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
		if (ev.getPlayer().isOp())
			return;
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
			for(int i=0; i<4; i++){
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
	public void SignAndInv(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(list.isEmpty())return;
		l=(HashMap<GameType,HashMap<Integer,ServerInfo>>)list.clone();
		for(GameType type : l.keySet()){
			for(int i : l.get(type).keySet()){
				se=l.get(type).get(i);
				sign=manager.getSigns().get(type).get(i);
				if(se!=null){
					
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
