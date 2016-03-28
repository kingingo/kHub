package eu.epicpvp.khub.Hub.Listener;

import java.io.File;

import eu.epicpvp.khub.Hub.HubManager;

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