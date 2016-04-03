package eu.epicpvp.khub.Command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Util.UtilItem;

public class CommandItemName implements CommandExecutor{

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "itemname", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		
			if(args.length==0){
				p.sendMessage(Language.getText(p, "PREFIX")+"§c/itemname [Name]");
			}else{
				if(p.getItemInHand()!=null&&p.getItemInHand().getType()!=Material.AIR){
					UtilItem.RenameItem(p.getItemInHand(), args[0].replaceAll("&", "§"));
					p.sendMessage(Language.getText(p, "PREFIX")+"§aDas Item wurde umbenannt zu §e"+args[0].replaceAll("&", "§"));
				}else{
					p.sendMessage(Language.getText(p, "PREFIX")+"§cDu musst ein Item in der Hand halten.");
				}
			}
		
		return false;
	}
	
}
