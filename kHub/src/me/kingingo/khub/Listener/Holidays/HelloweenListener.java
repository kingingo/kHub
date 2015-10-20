package me.kingingo.khub.Listener.Holidays;

import lombok.Getter;
import me.kingingo.kcore.Disguise.Events.DisguisePlayerLoadEvent;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class HelloweenListener extends kListener{

	@Getter
	private HubManager manager;
	
	public HelloweenListener(HubManager manager) {
		super(manager.getInstance(),"HelloweenListener");
		this.manager=manager;
	}
	
	@EventHandler
	public void load(DisguisePlayerLoadEvent ev){
		
	}
	
//	kPacketPlayOutEntityEquipment packet;
//	@EventHandler
//	public void Head(PlayerJoinEvent ev){
//		if(packet==null){
//			this.packet=new kPacketPlayOutEntityEquipment(ev.getPlayer().getEntityId(), 1, UtilItem.RenameItem(new ItemStack(Material.PUMPKIN), "§6§lHelloween Pumpkin"));
//		}
//		
//		for(Player player : UtilServer.getPlayers()){
//			if(player.getUniqueId()==ev.getPlayer().getUniqueId())continue;
//			this.packet.setEntityID(player.getEntityId());
//			UtilPlayer.sendPacket(ev.getPlayer(), packet);
//		}
//	}
	
	@EventHandler
	public void rdm(UpdateEvent ev){
		if(ev.getType()==UpdateType.MIN_08){
				Bukkit.getWorld("world").strikeLightningEffect(Bukkit.getWorld("world").getSpawnLocation().add(UtilMath.r(30), 0, UtilMath.r(30)));
		}
	}

}

