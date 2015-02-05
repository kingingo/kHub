package me.kingingo.khub.Listener;

import java.util.UUID;

import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.BROADCAST;
import me.kingingo.kcore.Packet.Packets.NOT_SAVE_COINS;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener extends kListener{

	private Vote vote;
	private Coins coins;
	private PacketManager packetManager;
	private MySQL mysql;
	
	public VoteListener(MySQL sql,Coins coins,PacketManager packetManager){
		super(packetManager.getInstance(),"VoteListener");
		this.coins=coins;
		this.mysql=sql;
		this.packetManager=packetManager;
	}
	
		UUID uuid;
	 @EventHandler(priority=EventPriority.NORMAL)
	 public void onVotifierEvent(VotifierEvent event) {
		 vote = event.getVote();
		 uuid = UtilPlayer.getUUID(vote.getUsername(), mysql);
	     coins.addCoins(uuid, 120);
	     packetManager.SendPacket("hub", new NOT_SAVE_COINS(uuid));
		 packetManager.SendPacket("BG", new BROADCAST(Text.PREFIX.getText()+"§6Der Spieler §b"+vote.getUsername()+"§6 hat fuer§b 120 Coins§6 gevotet!§a§l /Vote"));
	 }
	
}
