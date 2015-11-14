package me.kingingo.khub.Listener;

import java.io.File;

import io.netty.channel.rxtx.RxtxChannelConfig.Paritybit;
import me.kingingo.kcore.ParticleManager.ParticleManager;
import me.kingingo.kcore.ParticleManager.Particle.ParticlePicture;
import me.kingingo.kcore.Permission.Event.GroupLoadedEvent;
import me.kingingo.kcore.Permission.Event.PlayerLoadPermissionEvent;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.khub.HubManager;

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
	
	@EventHandler
	public void groupload(GroupLoadedEvent ev){
		if(ev.getGroup().equalsIgnoreCase("default")){
			ev.getManager().getGroups().get(ev.getGroup()).setPrefix("§e");
		}
	}
	
}