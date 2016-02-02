package me.kingingo.khub;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.khub.Hub.HubManager;

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
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
	private kManager manager;
	@Getter
	@Setter
	private boolean blockBreak = true;
	@Getter
	@Setter
	private boolean blockPlace = true;
	@Getter
	@Setter
	private boolean blockIgnite = true;
	@Getter
	@Setter
	private boolean blockBurn = true;
	@Getter
	@Setter
	private boolean blockSpread = true;
	@Getter
	@Setter
	private boolean entityDamage = true;
	@Getter
	@Setter
	private boolean entityDamageByEntity = true;
	@Getter
	@Setter
	private boolean entityExplode = true;
	@Getter
	@Setter
	private boolean entityInteract = true;
	@Getter
	@Setter
	private boolean foodLevelChange = true;
	@Getter
	@Setter
	private boolean playerDropItem = true;
	@Getter
	private GameMode gameMode = GameMode.ADVENTURE;
	
	public Listener(kManager manager) {
		super(manager.getInstance(),"Listener");
		this.manager=manager;
		UtilString.setupBadWords(null);
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event){
	    if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)){
	    	event.setCancelled(isEntityInteract());
	    }
	}
	
	@EventHandler
	public void Sign(SignChangeEvent ev){
		if(ev.getPlayer().hasPermission(kPermission.CHAT_FARBIG.getPermissionToString())){
			ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));
		}
	}
	
	@EventHandler
	public void BlockRedstoneEventon(BlockRedstoneEvent ev){
		ev.setNewCurrent(ev.getOldCurrent());
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		ev.getPlayer().getInventory().clear();
	}
	
	@EventHandler
	public void AddBoard(PlayerSetScoreboardEvent ev){
		if(!kHub.hubType.equalsIgnoreCase("LoginHub")
				&& this.manager instanceof HubManager)UtilPlayer.setScoreboard(ev.getPlayer(), ((HubManager)this.manager).getGems() , ((HubManager)this.manager).getCoins());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().setGameMode(getGameMode());
		ev.getPlayer().getWorld().setWeatherDuration(0);
		ev.getPlayer().getWorld().setStorm(false);
		ev.getPlayer().setFoodLevel(20);
		ev.getPlayer().getInventory().setHelmet(null);
		ev.getPlayer().getInventory().setChestplate(null);
		ev.getPlayer().getInventory().setLeggings(null);
		ev.getPlayer().getInventory().setBoots(null);
		ev.getPlayer().getInventory().clear();
		if(!kHub.hubType.equalsIgnoreCase("LoginHub"))ev.getPlayer().getInventory().setItem(0, UtilItem.RenameItem(new ItemStack(Material.CHEST), Language.getText(ev.getPlayer(), "HUB_ITEM_CHEST")));
	}
	
	@EventHandler
	public void Food(FoodLevelChangeEvent ev){
		ev.setCancelled(isFoodLevelChange());
	}
	
	@EventHandler
	public void onPlayer(PlayerDropItemEvent event) {
		event.setCancelled(isPlayerDropItem());
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
	public void ex(EntityExplodeEvent ev) {
		ev.setCancelled(isEntityExplode());
	}
	
	@EventHandler
	public void Break(BlockBreakEvent ev) {
		if (ev.getPlayer().isOp())return;
		ev.setCancelled(isBlockBreak());
	}

	@EventHandler
	public void Break(BlockPlaceEvent ev) {
		if (ev.getPlayer().isOp()) return;
		ev.setCancelled(isBlockPlace());
	}

	@EventHandler
	public void Break(BlockIgniteEvent ev) {
		ev.setCancelled(isBlockIgnite());
	}

	@EventHandler
	public void Break(BlockBurnEvent ev) {
		ev.setCancelled(isBlockBurn());
	}

	@EventHandler
	public void Break(BlockSpreadEvent ev) {
		ev.setCancelled(isBlockSpread());
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		e.setCancelled(isEntityDamage());
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		e.setCancelled(isEntityDamageByEntity());
	}
	
	@EventHandler
	public void death(PlayerDeathEvent ev){
		ev.setDeathMessage(null);
		ev.setDroppedExp(0);
		ev.getDrops().clear();
	}
}
