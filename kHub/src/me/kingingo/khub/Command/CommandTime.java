package me.kingingo.khub.Command;

import java.sql.ResultSet;
import java.util.UUID;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Util.UtilNumber;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.khub.kHub;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandTime implements CommandExecutor{

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "time", sender = Sender.CONSOLE)
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		if(cs instanceof ConsoleCommandSender){	
			System.out.println("TIME: "+ (System.currentTimeMillis() > UtilNumber.toLong(args[0])));
			System.out.println("TIME: "+ (UtilTime.formatMili( ( UtilNumber.toLong(args[0])-System.currentTimeMillis() ) )));
		}	
		return false;
	}
}
