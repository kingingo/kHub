package eu.epicpvp.khub.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;

public class CommandBroadcast implements CommandExecutor{

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "broadcast", sender = Sender.CONSOLE)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
			String m = "";
			for(int i = 0; i < args.length; i++) {
                m =m + args[i] + " ";
            }
			Bukkit.broadcastMessage(m.replaceAll("&", "§"));
		return false;
	}

}
