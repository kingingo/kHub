package me.kingingo.khub.Hub;

import lombok.Getter;

public class Lobby {

	@Getter
	private String name;
	@Getter
	private String bg;
	@Getter
	private int place;
	@Getter
	private String ip;
	@Getter
	private int port;
	
    public Lobby(String name,String bg,String ip,int place) {
    	this.name=name;
    	this.place=place;
    	this.bg=bg;
    	this.ip=ip.split(":")[0];
    	this.port=Integer.valueOf(ip.split(":")[1]);
	}
    
}

