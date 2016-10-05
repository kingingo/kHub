package eu.epicpvp.khub.Hub.Listener;

import eu.epicpvp.dataserver.protocoll.packets.PacketInStatsEdit.Action;
import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kcore.Listener.VoteListener.VoteListener;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.khub.Hub.HubManager;

public class HubPremiumListener extends HubListener {

	public HubPremiumListener(final HubManager manager) {
		super(manager, true);

		new VoteListener(manager.getInstance(), false, new Callback<String>() {

			@Override
			public void call(String playerName, Throwable ex) {
				LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(playerName);

				loadedplayer.changeGems(Action.ADD, 5);
				loadedplayer.changeCoins(Action.ADD, 100);
				UtilServer.getClient().broadcastMessage(null, TranslationHandler.getText("PREFIX") + "§b" + playerName + " hat gevotet und §a5 Gems + 100 Coins §berhalten§l! §7>>§5§l /Vote");
			}
		});
	}
}
