package me.kingingo.khub.Listener;

import lombok.Getter;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.TextParticle.TextParticle;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

public class SilvesterListener extends kListener{

	@Getter
	private Location spawn;
	
	public SilvesterListener(HubManager instance) {
		super(instance.getInstance(), "SilvesterListener");
		this.spawn=Bukkit.getWorld("world").getSpawnLocation();
		TextParticle text = new TextParticle(instance.getInstance(), "Frohes neues Jahr!", spawn.clone().add(0, 4, 7), UtilParticle.RED_DUST);
		text.setSize( 0.1F);
	}
	
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		UtilFirework.start(getSpawn().clone().add(UtilMath.RandomInt(5, -5), UtilMath.RandomInt(8,5), UtilMath.RandomInt(5, -5)), null, null);
	}
	

}
