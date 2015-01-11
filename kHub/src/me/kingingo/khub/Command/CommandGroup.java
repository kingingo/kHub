package me.kingingo.khub.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.khub.kHub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandGroup implements CommandExecutor{

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "group", alias = {"group","g","k"}, sender = Sender.EVERYONE, permissions = {"epicpvp.*"})
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		
		if(cs instanceof ConsoleCommandSender){
			
			if(args[0].equalsIgnoreCase("addperm")){
				
				if(args.length == 1 || args.length == 2){
					System.out.println("[kPermission] /k addperm [Player] [Permission]");	
				}
				
				if(args.length == 3){
					
					String player = args[1];
					String perm = args[2];
					kHub.pManager.addPermission(player,perm);
					System.out.println("[kPermission] " + player + " hat die " + perm + " erhalten");
				}
				
				return false;
			}
			
			if(args[0].equalsIgnoreCase("delperm")){
				
				if(args.length == 1 || args.length == 2){
					System.out.println("[kPermission] /k delperm [Player] [Permission]");	
				}
				
				if(args.length == 3){
					
					String player = args[1];
					String perm = args[2];
					kHub.pManager.removePermission(player, perm);
					System.out.println("[kPermission] " + player + " hat nun nicht mehr die Permission " + perm);
				}
				
				return false;
			}
			
			if(args[0].equalsIgnoreCase("add")){
				
				if(args.length == 1 || args.length == 2){
					System.out.println("[kPermission] /k add [Player] [Rang]");	
				}
				
				if(args.length == 3){
					
					String player = args[1];
					String rang = args[2];
					
					kHub.pManager.setGroup(player, rang);
					System.out.println("[kPermission] " + player + " hat den Rang " + rang + " erhalten");
				}
				
				return false;
			}
			
			
			return false;
		}
		
		return true;
	}


}
