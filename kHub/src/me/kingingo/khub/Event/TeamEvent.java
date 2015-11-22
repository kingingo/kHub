package me.kingingo.khub.Event;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.Team;

import org.bukkit.entity.Player;

public abstract class TeamEvent extends Event{

	@Getter
	private HashMap<Player,Team> teamList = new HashMap<>();
	
	public TeamEvent(EventManager eventManager, String eventName) {
		super(eventManager, eventName);
	}

}
