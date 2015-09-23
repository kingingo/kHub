package me.kingingo.khub.Lobby;

import lombok.Getter;

public class Lobby {

	@Getter
	String name;
	@Getter
	String bg;
	@Getter
	int place;
	@Getter
	String ip;
	
    public Lobby(String name,String bg,String ip,int place) {
    	this.name=name;
    	this.place=place;
    	this.bg=bg;
    	this.ip=ip;
	}
    
}

