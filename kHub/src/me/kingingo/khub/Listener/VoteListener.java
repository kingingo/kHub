package me.kingingo.khub.Listener;

import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.BROADCAST;
import me.kingingo.kcore.Packet.Packets.NOT_SAVE_COINS;
import me.kingingo.kcore.Util.Coins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener extends kListener{

	private Vote vote;
	private Coins coins;
	private PacketManager packetManager;
	
	public VoteListener(JavaPlugin instance,Coins coins,PacketManager packetManager){
		super(instance,"VoteListener");
		this.coins=coins;
		this.packetManager=packetManager;
	}
	
	 @EventHandler(priority=EventPriority.NORMAL)
	 public void onVotifierEvent(VotifierEvent event) {
		 vote = event.getVote();
	     coins.addCoins(vote.getUsername(), 120);
	     packetManager.SendPacket("hub", new NOT_SAVE_COINS(vote.getUsername().toLowerCase()));
		 packetManager.SendPacket("BG", new BROADCAST(Text.PREFIX.getText()+"§6Der Spieler §b"+vote.getUsername()+"§6 hat fuer§b 120 Coins§6 gevotet!§a§l /Vote"));
	 }
	
}
