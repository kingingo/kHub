package me.kingingo.khub.Hub.Listener.Holidays;

import lombok.Getter;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.SkinData;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilSkin;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class BirthdayListener extends kListener{

	@Getter
	private HubManager manager;
	private SkinData kingingo;
	private SkinData t3ker;
	private int r=0;
	
	public BirthdayListener(HubManager manager){
		super(manager.getInstance(),"BirthdayListener");
		this.manager=manager;
		
		if(Calendar.holiday==CalendarType.GEBURSTAG){
			this.kingingo=UtilSkin.loadSkin(manager.getInstance(), UtilPlayer.getOnlineUUID("kingingo"));
			this.t3ker=UtilSkin.loadSkin(manager.getInstance(), UtilPlayer.getOnlineUUID("t3ker"));
		}else if(Calendar.holiday==CalendarType.GEBURSTAG_MANUEL){
			this.kingingo=UtilSkin.loadSkin(manager.getInstance(), UtilPlayer.getOnlineUUID("ManiiLP"));
			this.t3ker=this.kingingo;
		}
		
		if(Calendar.isFixHolidayDate(CalendarType.GEBURSTAG))Log("Happy Birthday Felix und Moritz!");
		else if(Calendar.isFixHolidayDate(CalendarType.GEBURSTAG_MANUEL))Log("Happy Birthday Manuel!");
	}
	
	@EventHandler
	public void rdm(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_2){
			UtilFirework.start(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.RandomInt(30, -30), UtilMath.r(20), UtilMath.RandomInt(30, -30)), null,null);
			UtilFirework.start(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.RandomInt(30, -30), UtilMath.r(20), UtilMath.RandomInt(30, -30)), null,null);
			UtilFirework.start(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.RandomInt(30, -30), UtilMath.r(20), UtilMath.RandomInt(30, -30)), null,null);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onLoginEvent(PlayerLoginEvent ev){
		if(this.kingingo!=null&&r==0){
			UtilSkin.setSkinPlayer(ev.getPlayer(), this.kingingo);
			r=1;
		}else if(this.t3ker!=null){
			UtilSkin.setSkinPlayer(ev.getPlayer(), this.t3ker);
			r=0;
		}
	}
}
