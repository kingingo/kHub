package dev.wolveringer.sign;

import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutLobbyServer.ServerKey;
import lombok.Getter;

@Getter
public class ServerData {
	private GameType game;
	private String serverId;
	private String serverSubId;
	private int player;
	private int maxPlayer;
	private String mots;
	
	public ServerData(GameType type,ServerKey data) {
		this.game = type;
		this.serverId = data.getServerId();
		this.serverSubId = data.getServerSubId();
		this.player = data.getPlayer();
		this.maxPlayer = data.getMaxPlayer();
		this.mots =data.getMots();
	}

	@Override
	public String toString() {
		return "ServerData [game=" + game + ", serverId=" + serverId + ", serverSubId=" + serverSubId + ", player=" + player + ", maxPlayer=" + maxPlayer + ", mots=" + mots + "]";
	}
}
