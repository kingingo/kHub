package me.kingingo.khub.Lobby;

public class Lobby {

	String name;
	String bg;
	String ip;
	
    public Lobby(String name,String bg,String ip) {
    	this.name=name;
    	this.bg=bg;
    	this.ip=ip;
	}
    
    public String getName(){
    	return this.name;
    }
    
    public String getBG(){
    	return this.bg;
    }
    
    public String getIP(){
    	return ip;
    }
    
}

