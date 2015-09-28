package me.kingingo.khub.Listener;

import java.util.UUID;

import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.BROADCAST;
import me.kingingo.kcore.Packet.Packets.NOT_SAVE_COINS;
import me.kingingo.kcore.Packet.Packets.PLAYER_VOTE;
import me.kingingo.kcore.Util.Coins;
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
		 coins.addCoins(uuid, 150);
	     packetManager.SendPacket("hub", new NOT_SAVE_COINS(uuid));
		 packetManager.SendPacket("BG", new BROADCAST(Language.getText( "PREFIX")+"§6Ein Spieler hat gevotet!§a Vote jetzt auch §l/Vote"));
		 vpacket = new PLAYER_VOTE(vote.getUsername(), uuid);
		 packetManager.SendPacket("PVP", vpacket);
		 packetManager.SendPacket("SKYBLOCK", vpacket);
	 }
	
}
