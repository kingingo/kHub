package me.kingingo.khub.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandTraitor implements CommandExecutor{

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "paesse", alias = {"tadd"}, sender = Sender.EVERYONE, permissions = {"epicpvp.*"})
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		if(cs instanceof ConsoleCommandSender){	
			if(args.length == 2){
				String player = args[0];
				int t = Integer.valueOf(args[1]);
				Traitor.Exist(player);
				Traitor.addTraitor(player,t);
				System.out.println("[KLB] " + player + " hat nun " + t);
			}
		}	
		return false;
	}

}
