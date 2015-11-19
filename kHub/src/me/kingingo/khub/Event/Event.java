package me.kingingo.khub.Event;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Listener.kListener;

public abstract class Event extends kListener{

	@Getter
	private String eventName;
	@Getter
	private EventManager eventManager;
	@Getter
	@Setter
	private GameState state;
	
	public Event(EventManager eventManager, String eventName){
		super(eventManager.getInstance(),eventName);
		this.eventName=eventName;
		this.eventManager=eventManager;
	}

	public abstract void reset();
	
	public abstract void select();
	
	public abstract void start();

	public abstract void stop();
}
