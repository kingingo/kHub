package me.kingingo.khub.Event;

import lombok.Getter;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.khub.kManager;
import me.kingingo.khub.kHub;
import me.kingingo.khub.Event.Commands.CommandEvent;

public class EventManager extends kManager{

	@Getter
	private Event event;
	@Getter
	private ServerType serverType;
	
	public EventManager(kHub instance,CommandHandler cmdHandler,MySQL mysql,PacketManager packetManager){
		super(instance,cmdHandler,mysql,packetManager);
		
		getCmdHandler().register(CommandEvent.class, new CommandEvent(this));
	}

	public void setServerType(ServerType serverType){
		this.serverType=serverType;
	}
	
	public boolean isServerType(){
		return this.serverType!=null;
	}
	
	public boolean isEvent(){
		return this.event!=null;
	}
	
	public void selectEvent(Event event){
		this.event=event;
		this.event.select();
	}
	
	public void resetEvent(){
		if(this.event!=null){
			this.event.reset();
			this.event=null;
		}
	}
	
}
