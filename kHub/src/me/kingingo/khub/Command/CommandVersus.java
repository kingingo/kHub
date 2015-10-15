package me.kingingo.khub.Command;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.kConfig.kConfig;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandVersus implements CommandExecutor{
	
	private kConfig config;
	@Setter
	@Getter
	private static Location optionen;
	@Setter
	@Getter
	private static Location vs;
	@Setter
	@Getter
	private static Location team_3;
	@Setter
	@Getter
	private static Location team_4;
	@Setter
	@Getter
	private static Location team_5;
	@Setter
	@Getter
	private static Location team_6;
	
	public CommandVersus(JavaPlugin manager){
		this.config=new kConfig(new File("plugins"+File.separator+manager.getPlugin(manager.getClass()).getName()+File.separator+"locations.yml"));
		
		if(config.getString("Optionen")!=null&&Bukkit.getWorld(config.getString("Optionen.world"))!=null){
			if(config.isSet("Optionen")){
				optionen=config.getLocation("Optionen");
			}else{
				optionen=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
		
		if(config.getString("Vs")!=null&&Bukkit.getWorld(config.getString("Vs.world"))!=null){
			if(config.isSet("Vs")){
				vs=config.getLocation("Vs");
			}else{
				vs=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
		
		if(config.getString("Team_3")!=null&&Bukkit.getWorld(config.getString("Team_3.world"))!=null){
			if(config.isSet("Team_3")){
				team_3=config.getLocation("Team_3");
			}else{
				team_3=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
		
		if(config.getString("Team_4")!=null&&Bukkit.getWorld(config.getString("Team_4.world"))!=null){
			if(config.isSet("Team_4")){
				team_4=config.getLocation("Team_4");
			}else{
				team_4=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
		
		if(config.getString("Team_5")!=null&&Bukkit.getWorld(config.getString("Team_5.world"))!=null){
			if(config.isSet("Team_5")){
				team_5=config.getLocation("Team_5");
			}else{
				team_5=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
		
		if(config.getString("Team_6")!=null&&Bukkit.getWorld(config.getString("Team_6.world"))!=null){
			if(config.isSet("Team_6")){
				team_6=config.getLocation("Team_6");
			}else{
				team_6=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
	}

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "versus", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(p.isOp()){
			if(args[0].equalsIgnoreCase("optionen")){
				config.setLocation("Optionen", p.getLocation());
				config.save();
				setOptionen(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für Optionen wurde gesetzt!");
			}else if(args[0].equalsIgnoreCase("vs")){
				config.setLocation("Vs", p.getLocation());
				config.save();
				setVs(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für Vs wurde gesetzt!");
			}else if(args[0].equalsIgnoreCase("team3")){
				config.setLocation("Team_3", p.getLocation());
				config.save();
				setTeam_3(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für Team_3 wurde gesetzt!");
			}else if(args[0].equalsIgnoreCase("team4")){
				config.setLocation("Team_4", p.getLocation());
				config.save();
				setTeam_4(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für Team_4 wurde gesetzt!");
			}else if(args[0].equalsIgnoreCase("team5")){
				config.setLocation("Team_5", p.getLocation());
				config.save();
				setTeam_5(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für Team_5 wurde gesetzt!");
			}else if(args[0].equalsIgnoreCase("team6")){
				config.setLocation("Team_6", p.getLocation());
				config.save();
				setTeam_6(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für Team_6 wurde gesetzt!");
			}
		}
		return false;
	}
	
}
