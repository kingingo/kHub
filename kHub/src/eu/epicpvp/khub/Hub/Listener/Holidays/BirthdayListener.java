package eu.epicpvp.khub.Hub.Listener.Holidays;

import eu.epicpvp.datenserver.definitions.skin.Skin;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Calendar.Calendar.CalendarType;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilSkin;
import eu.epicpvp.khub.Hub.HubManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class BirthdayListener extends kListener{

	@Getter
	private HubManager manager;
	private Skin kingingo;
	private Skin t3ker;
	private int r=0;

	public BirthdayListener(HubManager manager){
		super(manager.getInstance(),"BirthdayListener");
		this.manager=manager;

//		if(Calendar.holiday==CalendarType.GEBURSTAG){
//			this.kingingo=UtilServer.getClient().getSkin("kingingo").getSync();
//			this.t3ker=UtilServer.getClient().getSkin("t3ker").getSync();
//		}else if(Calendar.holiday==CalendarType.GEBURSTAG_MANUEL){
//			this.kingingo=UtilServer.getClient().getSkin("ManiiLP").getSync();
//			this.t3ker=this.kingingo;
//		}

		if(Calendar.isFixHolidayDate(CalendarType.GEBURSTAG))logMessage("Happy Birthday Felix und Moritz!");
		else if(Calendar.isFixHolidayDate(CalendarType.GEBURSTAG_MANUEL))logMessage("Happy Birthday Manuel!");
	}

	@EventHandler
	public void rdm(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_2){
			UtilFirework.start(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.RandomInt(30, -30), UtilMath.randomInteger(20), UtilMath.RandomInt(30, -30)), null,null);
			UtilFirework.start(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.RandomInt(30, -30), UtilMath.randomInteger(20), UtilMath.RandomInt(30, -30)), null,null);
			UtilFirework.start(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.RandomInt(30, -30), UtilMath.randomInteger(20), UtilMath.RandomInt(30, -30)), null,null);
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
