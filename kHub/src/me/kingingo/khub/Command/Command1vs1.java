package me.kingingo.khub.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Packet.Packets.ARENA_STATUS;
import me.kingingo.kcore.Packet.Packets.VERSUS_SETTINGS;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Versus.VersusType;
import me.kingingo.khub.Listener.HubVersusListener;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command1vs1 implements CommandExecutor{

	private HubVersusListener listener;
	
	public Command1vs1(HubVersusListener listener){
		this.listener=listener;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "1vs1", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player player = (Player)cs;
//			if(args.length==0){
//				player.sendMessage(Language.getText(player, "PREFIX")+"/1vs1 [Name]");
//			}else{
//				if(UtilPlayer.isOnline(args[0])){
//					if(listener.getVs().containsKey(player)&&listener.getVs().get(player).getName().equalsIgnoreCase(args[0])){
//						for(ARENA_STATUS arena : listener.getStatus().keySet()){
//							if(arena.getState() == GameState.LobbyPhase&&arena.getOnline()<=0){
//								listener.getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType.withTeamAnzahl(arena.getTeams()),arena.getArena(),player.getName(),player,VersusType._TEAMx2.getTeam()[0]) );
//								UtilBG.sendToServer(player, arena.getServer(), listener.getManager().getInstance());
//								listener.getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType.withTeamAnzahl(arena.getTeams()),arena.getArena(),player.getName(),Bukkit.getPlayer(args[0]),VersusType._TEAMx2.getTeam()[1]) );
//								UtilBG.sendToServer(Bukkit.getPlayer(args[0]), arena.getServer(), listener.getManager().getInstance());
//								listener.getStatus().get(arena).maxteam=1;
//								listener.getStatus().get(arena).minteam=1;
//								listener.getStatus().get(arena).kit_name=player.getName();
//								listener.getVs().remove(player);
//							}
//						}
//					}
//				}else{
//					player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "PLAYER_IS_OFFLINE",args[0]));
//				}
//			}
		return false;
	}

}
