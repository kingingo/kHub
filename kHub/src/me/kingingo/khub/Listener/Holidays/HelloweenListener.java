package me.kingingo.khub.Listener.Holidays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.kingingo.kcore.Disguise.Events.DisguisePlayerLoadEvent;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.PacketAPI.Packets.kPacketPlayOutEntityEquipment;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.HubManager;

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
