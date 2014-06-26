package me.kingingo.khub.Login;

import lombok.Getter;
import me.kingingo.kcore.Command.Command;
import me.kingingo.kcore.Enum.Text;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLogin extends Command{

	@Getter
	private LoginManager LoginManager;
	private Player p;
	private String pw;
	
	public CommandLogin(String command, String label,LoginManager LoginManager) {
		super(command, label);
		this.LoginManager=LoginManager;
	}

	@Override
	public boolean onCommand(CommandSender cs,org.bukkit.command.Command cmd, String cmdLabel, String[] args) {
		if(!(cs instanceof Player))return false;
		p=(Player)cs;
		if(!getLoginManager().getLogin().containsKey(p))return false;
		if(args.length==0)p.sendMessage(Text.PREFIX.getText()+Text.LOGIN_MESSAGE.getText());
		else if(args.length==1){
			pw=args[0];
			if(pw.equalsIgnoreCase(getLoginManager().getLogin().get(p))){
				getLoginManager().getLogin().remove(p);
				getLoginManager().delLogin(p.getName());
				p.sendMessage(Text.PREFIX.getText()+Text.LOGIN_ACCEPT.getText());
				return true;
			}else{
				p.sendMessage(Text.PREFIX.getText()+Text.LOGIN_DENY.getText());
			}
		}
		return false;
	}

}
