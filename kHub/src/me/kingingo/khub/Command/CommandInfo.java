package me.kingingo.khub.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Server.ServerInfo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfo implements CommandExecutor{

	HubManager Manager;
	
	public CommandInfo(HubManager Manager){
		this.Manager=Manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "serverinfo", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		Player p = (Player)cs;
		if(p.isOp()){
			if(args.length==1){
				GameType type = null;
				for(GameType typ : GameType.values()){
					if(args[0].equalsIgnoreCase(typ.name())){
						type=typ;
						break;
					}
				}
				if(type==null){
					p.sendMessage("§cDer TYP konnte nicht gefunden werden!");
					return false;
				}
				String server="§bServer(§7%§b):§7";
				int count=0;
				for(ServerInfo s : Manager.getServers().get(type)){
					server=server+s.ID+"§b,§7";
					count++;
				}
				p.sendMessage(server.replaceAll("%", String.valueOf(count)));
			}
		}
		
		return false;
	}

}
