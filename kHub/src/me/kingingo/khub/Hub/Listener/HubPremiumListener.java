package me.kingingo.khub.Hub.Listener;

import java.io.File;

import me.kingingo.kcore.Permission.Event.GroupLoadedEvent;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.event.EventHandler;

public class HubPremiumListener extends HubListener{

//	private ParticleManager particleManager;
	
	public HubPremiumListener(final HubManager manager) {
		super(manager,true);
//		this.particleManager=new ParticleManager(manager.getInstance());
		
//		this.particleManager.addParticle(new ParticlePicture("fly", UtilParticle.FLAME, getParticlePictures()[0]));
	}	
	
	public static File[] getParticlePictures(){
		File folder = new File("particles");
		if(!folder.exists())folder.mkdirs();
		return folder.listFiles();
	}
//	
//	@EventHandler
//	public void join(PlayerLoadPermissionEvent ev){
//		this.particleManager.addPlayer(ev.getPlayer(), "fly");
//	}
	
}