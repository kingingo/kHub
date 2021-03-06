package eu.epicpvp.khub.Hub.Listener.Holidays;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import eu.epicpvp.kcore.Disguise.DisguiseType;
import eu.epicpvp.kcore.Disguise.Events.DisguiseCreateEvent;
import eu.epicpvp.kcore.Disguise.Events.DisguisePlayerLoadEvent;
import eu.epicpvp.kcore.Disguise.disguises.livings.DisguiseWolf;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.khub.Hub.HubManager;
import lombok.Getter;

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
		if(ev.getPlayer().hasPermission(PermissionType.TEAM_MESSAGE.getPermissionToString())){
			if(ev.getPlayer().hasPermission(PermissionType.ALL_PERMISSION.getPermissionToString())){
				ev.setType( DisguiseType.WITHER );
			}else{
				ev.setType( DisguiseType.WITHER_SKELETON );
			}
		}else{
			ev.setType( types[UtilMath.randomInteger(types.length)] );
		}
	}
	
	@EventHandler
	public void rdm(UpdateEvent ev){
		if(ev.getType()==UpdateType.SLOWEST){
				Bukkit.getWorld("world").strikeLightningEffect(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.randomInteger(30), 0, UtilMath.randomInteger(30)));
		}
	}

}

