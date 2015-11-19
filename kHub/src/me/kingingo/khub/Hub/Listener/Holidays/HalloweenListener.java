package me.kingingo.khub.Hub.Listener.Holidays;

import lombok.Getter;
import me.kingingo.kcore.Disguise.DisguiseType;
import me.kingingo.kcore.Disguise.Events.DisguiseCreateEvent;
import me.kingingo.kcore.Disguise.Events.DisguisePlayerLoadEvent;
import me.kingingo.kcore.Disguise.disguises.livings.DisguiseWolf;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class HalloweenListener extends kListener{

	@Getter
	private HubManager manager;
	private DisguiseType[] types;
	
	public HalloweenListener(HubManager manager) {
		super(manager.getInstance(),"HalloweenListener");
		this.manager=manager;
		this.types=new DisguiseType[]{DisguiseType.ZOMBIE,DisguiseType.SKELETON,DisguiseType.SPIDER,DisguiseType.BLAZE,DisguiseType.CAVE_SPIDER,DisguiseType.PIG_ZOMBIE,DisguiseType.ENDERMAN,DisguiseType.WOLF,DisguiseType.WITCH};
	}

	@EventHandler
	public void create(DisguiseCreateEvent ev){
		if(ev.getBase() instanceof DisguiseWolf){
			((DisguiseWolf)ev.getBase()).setAngry(true);
		}
	}
	
	@EventHandler
	public void load(DisguisePlayerLoadEvent ev){
		if(ev.getPlayer().hasPermission(kPermission.TEAM_MESSAGE.getPermissionToString())){
			if(ev.getPlayer().hasPermission(kPermission.ALL_PERMISSION.getPermissionToString())){
				ev.setType( DisguiseType.WITHER );
			}else{
				ev.setType( DisguiseType.WITHER_SKELETON );
			}
		}else{
			ev.setType( types[UtilMath.r(types.length)] );
		}
	}
	
	@EventHandler
	public void rdm(UpdateEvent ev){
		if(ev.getType()==UpdateType.SLOWEST){
				Bukkit.getWorld("world").strikeLightningEffect(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.r(30), 0, UtilMath.r(30)));
		}
	}

}

