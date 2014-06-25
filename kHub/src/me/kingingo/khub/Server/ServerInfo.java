package me.kingingo.khub.Server;

import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;

public class ServerInfo {
	  public String ID;
	  public GameState State = GameState.NONE;
	  public int CurrentPlayers = 0;
	  public int MaxPlayers = 0;
	  public String Map;
}
