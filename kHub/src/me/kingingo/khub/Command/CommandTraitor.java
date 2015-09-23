package me.kingingo.khub.Command;

import java.sql.ResultSet;
import java.util.UUID;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.khub.kHub;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandTraitor implements CommandExecutor{

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "paesse", alias = {"tadd"}, sender = Sender.EVERYONE)
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		if(cs instanceof ConsoleCommandSender){	
			if(args.length == 2){
				String player = args[0];
				UUID uuid;
				
				if(UtilPlayer.isOnline(player)){
					uuid=UtilPlayer.getRealUUID(Bukkit.getPlayer(player));
				}else{
					uuid = UtilPlayer.getUUID(player, kHub.mysql);
				}
				
				int t = Integer.valueOf(args[1]);
				addTraitor(uuid,t);
				System.out.println("[KLB] " + player + " hat nun " + t);
			}
		}	
		return false;
	}
	
	public Integer getTraitor(UUID uuid){
		int d = 0;
		
		try{
			
			ResultSet rs =kHub.mysql.Query("SELECT paesse FROM users_TTT WHERE uuid='" + uuid + "'");
			
			while(rs.next()){
				d = rs.getInt(1);
			}
 			
			rs.close();
		}catch (Exception err){	
			System.err.println(err);
		}
		
		return d;
	}
	
	public void delTraitor(UUID uuid,Integer coins){
		int c = getTraitor(uuid);
		int co=c-coins;
		kHub.mysql.Update("UPDATE `users_TTT` SET paesse='"+co+"' WHERE uuid='"+uuid+"'");
	}
	
	public void addTraitor(UUID uuid,Integer coins){
		int c = getTraitor(uuid);
		int co=c+coins;
		kHub.mysql.Update("UPDATE `users_TTT` SET paesse='"+co+"' WHERE uuid='"+uuid+"'");
	}

}
