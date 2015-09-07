package me.kingingo.khub.Listener;

import java.io.IOException;

import lombok.Getter;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.kHub;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Listener extends kListener{

	@Getter
	private HubManager manager;
	
	public Listener(HubManager manager) {
		super(manager.getInstance(),"Listener");
		this.manager=manager;
		UtilString.setupBadWords(null);
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event){
	    if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		ev.getPlayer().getInventory().clear();
	}
	
	@EventHandler
	public void AddBoard(PlayerSetScoreboardEvent ev){
		if(!kHub.hubType.equalsIgnoreCase("HubLogin"))UtilPlayer.setScoreboard(ev.getPlayer(), getManager().getCoins(), getManager().getPermissionManager());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		ev.getPlayer().getWorld().setWeatherDuration(0);
		ev.getPlayer().getWorld().setStorm(false);
		ev.getPlayer().setFoodLevel(20);
		ev.getPlayer().getInventory().setHelmet(null);
		ev.getPlayer().getInventory().clear();
		if(!kHub.hubType.equalsIgnoreCase("LoginHub"))ev.getPlayer().getInventory().setItem(1, UtilItem.RenameItem(new ItemStack(Material.CHEST), Language.getText(ev.getPlayer(), "HUB_ITEM_CHEST")));
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
	public void death(PlayerDeathEvent ev){
		ev.setDeathMessage(null);
		ev.setDroppedExp(0);
		ev.getDrops().clear();
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
}
