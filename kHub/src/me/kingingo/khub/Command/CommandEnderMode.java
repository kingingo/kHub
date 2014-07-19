package me.kingingo.khub.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.khub.HubManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandEnderMode implements CommandExecutor{
	
	private HubManager m;
	
	public CommandEnderMode(HubManager m){
		this.m=m;
	}

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "endermode", alias = {"endermode","eg"}, sender = Sender.EVERYONE, permissions = {"epicpvp.*"})
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		if(cs instanceof ConsoleCommandSender){	
			if(args[0].equalsIgnoreCase("add")){
				
				if(args.length == 3){
					String player = args[1];
					int t = Integer.valueOf(args[2]);
					m.getTokens().Exist(player);
					m.getTokens().addTokens(player,true, t);
					
					System.out.println("[KLB] " + player + " hat nun " + t);
				}
				
				return false;
			}
			
			
			return false;
		}
		return false;
	}

}
