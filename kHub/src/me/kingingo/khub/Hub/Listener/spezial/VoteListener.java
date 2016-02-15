package me.kingingo.khub.Hub.Listener.spezial;

import java.util.UUID;

import lombok.Getter;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Packets.BROADCAST;
import me.kingingo.kcore.Packet.Packets.PLAYER_VOTE;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.kManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener extends kListener{

	@Getter
	private kManager manager;
	
	public VoteListener(kManager manager){
		super(manager.getInstance(),"VoteListener");
		this.manager=manager;
	}
	
	Vote vote;
	UUID uuid;
	PLAYER_VOTE vpacket;
	@EventHandler(priority=EventPriority.NORMAL)
	public void onVotifierEvent(VotifierEvent event) {
		 vote = event.getVote();
		 uuid = UtilPlayer.getUUID(vote.getUsername(), getManager().getMysql());
		 
		 if(UtilServer.getDeliveryPet()!=null){
			 if(UtilPlayer.isOnline(vote.getUsername())){
				 UtilServer.createDeliveryPet(null).deliveryUSE(Bukkit.getPlayer(vote.getUsername()), "§aVote for EpicPvP",true);
			 }else{
				 UtilServer.createDeliveryPet(null).deliveryUSE(vote.getUsername(), uuid, "§aVote for EpicPvP");
			 }
		 }
		 
		 getManager().getGems().giveGems(getManager().getPacketManager(), vote.getUsername(), 5);
		 getManager().getCoins().giveCoins(getManager().getPacketManager(), vote.getUsername(), 100);
		 
		 getManager().getPacketManager().SendPacket("BG", new BROADCAST(Language.getText( "PREFIX")+"§b"+vote.getUsername()+" hat gevotet und §a5 Gems + 100 Coins §berhalten§l! §7>>§5§l /Vote"));
		 vpacket = new PLAYER_VOTE(vote.getUsername(), uuid);
		 getManager().getPacketManager().SendPacket("PVP", vpacket);
		 getManager().getPacketManager().SendPacket("SKYBLOCK", vpacket);
	}
}
