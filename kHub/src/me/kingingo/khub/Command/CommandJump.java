package me.kingingo.khub.Command;

import java.util.HashMap;

import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilVector;
import me.kingingo.khub.kHub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

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
			}else{
				break;
			}
		}
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "setjump", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(p.isOp()){
			hub.getConfig().set("Config.Jump."+list.size()+".fromX", p.getLocation().getBlockX());
			hub.getConfig().set("Config.Jump."+list.size()+".fromZ", p.getLocation().getBlockZ());
			hub.getConfig().set("Config.Jump."+list.size()+".fromY", p.getLocation().getBlockY()-1);
			hub.saveConfig();
			player=p;
			p.sendMessage(Text.PREFIX.getText()+"§aDie Jump Platte "+list.size()+" wurde gesetzt!");
		}
		return false;
	}

	public Vector calculate(Location from , Location to){
		return UtilLocation.calculateVector(from, to);
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST)return;
		for(Player player : UtilServer.getPlayers()){
			if(list.containsKey(player.getLocation())){
				player.setVelocity(list.get(player.getLocation()));
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
			list.put(new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromX"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromY"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromZ")),  calculate( new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromX"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromY"),hub.getConfig().getInt("Config.Jump."+list.size()+".fromZ")), new Location(Bukkit.getWorld("world"),hub.getConfig().getInt("Config.Jump."+list.size()+".toX"),hub.getConfig().getInt("Config.Jump."+list.size()+".toY"),hub.getConfig().getInt("Config.Jump."+list.size()+".toZ")) ));
			ev.getPlayer().sendMessage(Text.PREFIX.getText()+"§cDie Jump Platte "+list.size()+" wurde gesetzt!");
		}
	}

}
