package eu.epicpvp.khub.Hub.Listener.Holidays;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import eu.epicpvp.khub.Hub.HubManager;
import lombok.Getter;
import eu.epicpvp.kcore.Disguise.DisguiseType;
import eu.epicpvp.kcore.Disguise.Events.DisguiseCreateEvent;
import eu.epicpvp.kcore.Disguise.Events.DisguisePlayerLoadEvent;
import eu.epicpvp.kcore.Disguise.disguises.livings.DisguiseWolf;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.kPermission;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilMath;

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

