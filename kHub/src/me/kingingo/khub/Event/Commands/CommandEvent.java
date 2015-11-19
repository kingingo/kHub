package me.kingingo.khub.Event.Commands;

import lombok.Getter;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.Language.Language;
import me.kingingo.khub.Event.EventManager;
import me.kingingo.khub.Event.Events.DropEvent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEvent implements CommandExecutor{
	
	@Getter
	private EventManager eventManager;
	
	public CommandEvent(EventManager eventManager){
		this.eventManager=eventManager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "event",alias={"ev"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Player player = (Player)sender;
		
		if(player.isOp()){
			if(args.length==0){
				player.sendMessage(Language.getText(player, "PREFIX")+"/event settype [PVP/SKYBLOCK/GAME]");
				player.sendMessage(Language.getText(player, "PREFIX")+"/event setevent [EventName]");
				player.sendMessage(Language.getText(player, "PREFIX")+"/event start");
				player.sendMessage(Language.getText(player, "PREFIX")+"/event stop");
				player.sendMessage(Language.getText(player, "PREFIX")+"/event reset");
			}else{
				if(args[0].equalsIgnoreCase("settype")){
					if(args.length==2){
						ServerType type = ServerType.get(args[1]);
						
						if(type!=null){
							getEventManager().setServerType(type);
							player.sendMessage(Language.getText(player, "PREFIX")+"§cDer ServerType wurde gesetzt!");
						}else{
							player.sendMessage(Language.getText(player, "PREFIX")+"§cServerType nicht gefunden!");
						}
					}else{
						player.sendMessage(Language.getText(player, "PREFIX")+"/event settype [PVP/SKYBLOCK/GAME]");
					}
				}else if(args[0].equalsIgnoreCase("setevent")){
					if(args.length==2){
						getEventManager().resetEvent();
						
						switch(args[1]){
						case "DropEvent":
							getEventManager().selectEvent(new DropEvent(getEventManager()));
							break;
						}
						
						if(getEventManager().getEvent()!=null)player.sendMessage(Language.getText(player, "PREFIX")+"§aDas Event wurde erstellt!");
					}else{
						player.sendMessage(Language.getText(player, "PREFIX")+"/event setevent [EventName]");
					}
				}else if(args[0].equalsIgnoreCase("start")){
					if(getEventManager().isEvent()){
						getEventManager().getEvent().start();
						if(getEventManager().getEvent()!=null)player.sendMessage(Language.getText(player, "PREFIX")+"§aSTART!");
					}
				}else if(args[0].equalsIgnoreCase("stop")){
					if(getEventManager().isEvent()){
						getEventManager().getEvent().stop();
						if(getEventManager().getEvent()!=null)player.sendMessage(Language.getText(player, "PREFIX")+"§cSTOP!");
					}
				}else if(args[0].equalsIgnoreCase("reset")){
					if(getEventManager().isEvent()){
						getEventManager().resetEvent();
						if(getEventManager().getEvent()!=null)player.sendMessage(Language.getText(player, "PREFIX")+"§cRESET!");
					}
				}
			}
		}
		return true;
	}
}