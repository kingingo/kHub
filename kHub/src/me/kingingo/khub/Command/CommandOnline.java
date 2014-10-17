package me.kingingo.khub.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Server.ServerInfo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandOnline implements CommandExecutor{

	HubManager Manager;
	
	public CommandOnline(HubManager Manager){
		this.Manager=Manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "online", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd,String label, String[] args){
		Player p = (Player)cs;
		if(p.isOp()){
//			String server="§bServer(§7%§b):§7";
//			int count=0;
//			for(ServerInfo s : Manager.getServers().get(GameType.TroubleInMinecraft)){
//				server=server+s.ID+"§b,§7";
//				count++;
//			}
//			p.sendMessage(server.replaceAll("%", String.valueOf(count)));
			int i;
			for(GameType typ : Manager.getServers().keySet()){
				i=0;
				for(ServerInfo s : Manager.getServers().get(typ)){
					i=i+s.CurrentPlayers;
				}
				p.sendMessage("§bGameTyp: §7"+typ.name()+" §bSpieler:§7 "+i);
			}
		}
		
		return false;
	}

}
