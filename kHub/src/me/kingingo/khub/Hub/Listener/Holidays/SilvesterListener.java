package me.kingingo.khub.Hub.Listener.Holidays;

import lombok.Getter;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.TextParticle.TextParticle;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import org.bukkit.FireworkEffect.Type;

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
