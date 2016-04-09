package eu.epicpvp.khub.Hub.Listener.Holidays;


import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Calendar.Calendar.CalendarType;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.TextParticle.TextParticle;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.khub.Hub.HubManager;
import lombok.Getter;

public class SilvesterListener extends kListener{

	@Getter
	private Location spawn;
	
	public SilvesterListener(HubManager instance) {
		super(instance.getInstance(), "SilvesterListener");
		this.spawn=Bukkit.getWorld("world").getSpawnLocation();
		if(Calendar.isFixHolidayDate(CalendarType.SILVESTER)){
			TextParticle text = new TextParticle(instance.getInstance(), "HAPPY NEW YEAR!", spawn.clone().add(0, 4, 7), UtilParticle.RED_DUST);
			text.setSize( 0.1F);
		}
	}
	
	Type[] types = new Type[]{Type.BALL,Type.BALL_LARGE,Type.BURST};
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		UtilFirework.start(getSpawn().clone().add(UtilMath.RandomInt(10, -10), UtilMath.RandomInt(10,8), UtilMath.RandomInt(10, -10)), null, types[UtilMath.r(types.length)]);
	}
	
}
