package me.kingingo.khub.Listener;

import java.util.UUID;

import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.BROADCAST;
import me.kingingo.kcore.Packet.Packets.PLAYER_VOTE;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Gems;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener extends kListener{

	private Vote vote;
	private Gems gems;
	private Coins coins;
	private PacketManager packetManager;
	private MySQL mysql;
	
	public VoteListener(Gems gems,Coins coins,PacketManager packetManager){
		super(packetManager.getInstance(),"VoteListener");
		this.coins=coins;
		this.gems=gems;
		this.mysql=gems.getMysql();
		this.packetManager=packetManager;
	}
	
	 UUID uuid;
	 PLAYER_VOTE vpacket;
	 @EventHandler(priority=EventPriority.NORMAL)
	 public void onVotifierEvent(VotifierEvent event) {
		 vote = event.getVote();
		 uuid = UtilPlayer.getUUID(vote.getUsername(), mysql);
		 
		 if(UtilServer.getDeliveryPet()!=null){
			 if(UtilPlayer.isOnline(vote.getUsername())){
				 UtilServer.createDeliveryPet(null).deliveryUSE(Bukkit.getPlayer(vote.getUsername()), Material.PAPER,true);
			 }else{
				 UtilServer.createDeliveryPet(null).deliveryUSE(vote.getUsername(), uuid, Material.PAPER);
			 }
		 }
		 gems.giveGems(packetManager, vote.getUsername(), 25);
		 coins.giveCoins(packetManager, vote.getUsername(), 100);
		 
		 packetManager.SendPacket("BG", new BROADCAST(Language.getText( "PREFIX")+"§b"+vote.getUsername()+" hat gevotet und §a25 Gems + 100 Coins §berhalten§l! §7>>§5§l /Vote"));
		 vpacket = new PLAYER_VOTE(vote.getUsername(), uuid);
		 packetManager.SendPacket("PVP", vpacket);
		 packetManager.SendPacket("SKYBLOCK", vpacket);
	 }
	
}
