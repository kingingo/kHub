package me.kingingo.khub.Login;

import lombok.Getter;
import me.kingingo.kcore.Command.Command;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegister implements CommandExecutor{

	public CommandRegister(LoginManager lManager) {
		this.LoginManager=lManager;
	}

	@Getter
	private LoginManager LoginManager;
	private Player p;
	private String pw;

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "register", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs,org.bukkit.command.Command cmd, String cmdLabel, String[] args) {
		if(!(cs instanceof Player))return false;
		p=(Player)cs;
		if(!getLoginManager().getRegister().contains(p))return false;
		if(args.length==0)p.sendMessage(Text.PREFIX.getText()+Text.REGISTER_MESSAGE.getText());
		else if(args.length==1){
			pw=args[0];
			getLoginManager().getRegister().remove(p);
			getLoginManager().delLogin(p.getName());
			getLoginManager().setUser(p, pw, "");
			p.sendMessage(Text.PREFIX.getText()+Text.REGISTER_ACCEPT.getText());
			return true;
		}
		return false;
	}

}
