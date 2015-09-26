package me.kingingo.khub.Listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HubLoginListener extends kListener{

	private HashMap<Player,Long> list;
	private HashMap<String,Integer> bot;
	private HubManager manager;
	private Location spawn;
	
	public HubLoginListener(HubManager manager) {
		super(manager.getInstance(), "HubLoginListener");
		this.list=new HashMap<>();
		this.manager=manager;
		this.bot=new HashMap<>();
		this.spawn=Bukkit.getWorld("world").getSpawnLocation().add(0, 5, 0);
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		list.remove(ev.getPlayer());
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §aLoginHub "+manager.getId(), "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
		list.put(ev.getPlayer(), System.currentTimeMillis());
		for(Player player : UtilServer.getPlayers()){
			if(!player.isOp())player.hidePlayer(ev.getPlayer());
			if(!ev.getPlayer().isOp())ev.getPlayer().hidePlayer(player);
		}
		ev.getPlayer().teleport(spawn);
	}
	
	Player player;
	@EventHandler
	public void updater(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			for(int i = 0; i < list.size(); i++){
				player=(Player)list.keySet().toArray()[i];
				if( (System.currentTimeMillis()-list.get(player)) > TimeSpan.SECOND*10){
					if(UtilLocation.isSameLocation(player.getLocation(), spawn)){
						Log("detected Bot "+player.getName());
						addBot(player);
						player.kickPlayer("§dEs joinen grad zu viele Spieler bitte versuch es später erneut!");
						list.remove(player);
					}else{
						list.remove(player);
					}
				}
			}
		}
	}
	
	public void addBot(Player p){
		if(bot.containsKey(p.getAddress().getAddress().getHostAddress())){
			int i = bot.get(p.getAddress().getAddress().getHostAddress());
			i++;
			bot.remove(p.getAddress().getAddress().getHostAddress());
			if(i>2){
				Date MyDate = new Date();
				SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				df2.setTimeZone(TimeZone.getDefault());
				df2.format(MyDate);
				Calendar gc2 = new GregorianCalendar();
				Date now = gc2.getTime();
				manager.getMysql().Update("INSERT INTO BG_Ban (name,name_uuid, nameip,banner,banner_uuid,bannerip,time,reason, level,aktiv) VALUES ('" + p.getName() + "','"+UtilPlayer.getRealUUID(p)+"', 'null', 'CONSOLE','CONSOLE', 'null', '" + df2.format(now) + "', 'Bot Detection', '5', 'true')");
				System.err.println("[Bot-Detection] "+p.getName()+" wurde gebannt");
			}else{
				bot.put(p.getAddress().getAddress().getHostAddress(), i);
			}
			
		}
		bot.put(p.getAddress().getAddress().getHostAddress(), 1);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Interact(PlayerInteractEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
}
