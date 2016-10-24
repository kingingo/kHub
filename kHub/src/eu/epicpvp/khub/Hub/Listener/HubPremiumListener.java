package eu.epicpvp.khub.Hub.Listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import eu.epicpvp.dataserver.protocoll.packets.PacketInStatsEdit.Action;
import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kcore.Listener.VoteListener.VoteListener;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.khub.Hub.HubManager;

public class HubPremiumListener extends HubListener {

	private ArrayList<String> votes = new ArrayList<>();
	
	public HubPremiumListener(final HubManager manager) {
		super(manager, true);

		new VoteListener(manager.getInstance(), false, new Callback<String>() {

			@Override
			public void call(String playerName, Throwable ex) {
				LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(playerName);

				loadedplayer.changeCoins(Action.ADD, 100);
				votes.add(loadedplayer.getName());
				UtilServer.getClient().sendMessage(loadedplayer.getPlayerId(), "§6Vote§8 »§f Danke für deinen Vote, §e"+loadedplayer.getName()+"§f. Wenn du deinen Key noch nicht bekommen hast, gehe auf den Server, auf den du ihn haben möchtest.");
			}
		});
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), new Runnable(){

			@Override
			public void run() {
				if(!votes.isEmpty()){
					if(votes.size()>=3){
						String players = "§e";
						for(int i = 0; i < (votes.size()-1); i++){
							players+=votes.get(i)+"§f,§e ";
						}
						players=players.substring(0, players.length()-"§f,§e".length());
						players+="§f und §e"+votes.get(votes.size()-1);
						
						UtilServer.getClient().broadcastMessage(null, "§6Vote§8 »§f Vielen Dank für euren Vote "+players+"§f!");
						UtilServer.getClient().broadcastMessage(null, "§6Vote§8 »§f Vote jetzt, für §c§l100 Coins §fund einen §c§lVote Crate Key§f! §7»&5§l /Vote");
						votes.clear();
					}
				}
			}
			
		}, 0L, 20*60*5);
	}
}
