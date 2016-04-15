package eu.epicpvp.khub.Command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.khub.kHub;

public class CommandJump extends kListener implements CommandExecutor {

	private kHub hub;
	private HashMap<Location,Vector> list = new HashMap<>();
	private Player player;
	
	public CommandJump(kHub hub){
		super(hub,"Jump");
		this.hub=hub;
		for(int i = 0; i<50; i++){
			if(hub.getConfig().contains("Config.Jump."+i+".fromX")){
				list.put(new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+i+".fromX"),hub.getConfig().getInt("Config.Jump."+i+".fromY"),hub.getConfig().getInt("Config.Jump."+i+".fromZ")),  calculate( new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+i+".fromX"),hub.getConfig().getInt("Config.Jump."+i+".fromY"),hub.getConfig().getInt("Config.Jump."+i+".fromZ")), new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+i+".toX"),hub.getConfig().getInt("Config.Jump."+i+".toY"),hub.getConfig().getInt("Config.Jump."+i+".toZ")) ));
				logMessage("Load Jump-"+i);
			}else{
				break;
			}
		}
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "setjump", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(p.isOp()){
			if(p.getLocation().add(0,-1,0).getBlock().getType()==Material.GOLD_PLATE){
				p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"?cDu musst auf einer Gold Platte stehen!");
				return false;
			}
			hub.getConfig().set("Config.Jump."+list.size()+".fromX", p.getLocation().getBlockX());
			hub.getConfig().set("Config.Jump."+list.size()+".fromZ", p.getLocation().getBlockZ());
			hub.getConfig().set("Config.Jump."+list.size()+".fromY", p.getLocation().getBlockY()-1);
			hub.saveConfig();
			player=p;
			p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"?aDie Jump Platte "+list.size()+" wurde gesetzt!");
		}
		return false;
	}

	public Vector calculate(Location from , Location to){
		return UtilLocation.calculateVector(from, to.add(0,15,0)).multiply(10);
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FASTER)return;
		for(Player player : UtilServer.getPlayers()){
			for(Location loc : list.keySet()){
				if(loc.getBlockX()==player.getLocation().getBlockX()&&loc.getBlockY()==player.getLocation().getBlockY()-1&&loc.getBlockZ()==player.getLocation().getBlockZ()){
					player.setVelocity(list.get(loc));
				}
			}
		}
	}
	
	@EventHandler
	public void Sneak(PlayerToggleSneakEvent ev){
		if(player!=null&&player==ev.getPlayer()){
			hub.getConfig().set("Config.Jump."+list.size()+".toX", ev.getPlayer().getLocation().getBlockX());
			hub.getConfig().set("Config.Jump."+list.size()+".toZ", ev.getPlayer().getLocation().getBlockZ());
			hub.getConfig().set("Config.Jump."+list.size()+".toY", ev.getPlayer().getLocation().getBlockY()-1);
			hub.saveConfig();
			ev.getPlayer().sendMessage(TranslationHandler.getText(player, "PREFIX")+"?cDie Jump Platte "+list.size()+" wurde gesetzt!");
			list.put(new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromX"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromY"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromZ")),  calculate( new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromX"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromY"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromZ")), new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+list.size()+".toX"),hub.getConfig().getInt("Config.Jump."+list.size()+".toY"),hub.getConfig().getInt("Config.Jump."+list.size()+".toZ")) ));
			player=null;
		}
	}

}