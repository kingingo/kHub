package me.kingingo.khub.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Inventory.InventoryChoose;
import me.kingingo.kcore.Inventory.Item.ButtonBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.ARENA_STATUS;
import me.kingingo.kcore.Packet.Packets.VERSUS_SETTINGS;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Versus.VersusKit;
import me.kingingo.kcore.Versus.VersusType;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class VersusListener extends kListener{

	@Getter
	private HubManager manager;
	private ArrayList<ARENA_STATUS> status = new ArrayList<>();
	private Villager creature=null;
	private InventoryBase base;
	private HashMap<Integer,ArrayList<Player>> versus_warte_liste = new HashMap<>();
	private StatsManager statsManager;
	private String kit;
//	private AmorColorChange color;
//	private long color_time;
	
	public VersusListener(HubManager manager) {
		super(manager.getInstance(),"VersusListener");
//		this.color=AmorColorChange.values()[UtilMath.r(AmorColorChange.values().length)];
//		this.color_time=System.currentTimeMillis()+TimeSpan.SECOND*20;
		this.manager=manager;
		this.statsManager=new StatsManager(manager.getInstance(), manager.getMysql(), GameType.Versus);
		this.base=new InventoryBase(manager.getInstance(), "§bVersus");
		VersusKit k = new VersusKit();
		k.helm=new ItemStack(Material.IRON_HELMET);
		k.chestplate=new ItemStack(Material.IRON_CHESTPLATE);
		k.leggings=new ItemStack(Material.IRON_LEGGINGS);
		k.boots=new ItemStack(Material.IRON_BOOTS);
		k.inv=new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD)};
		this.kit=UtilInv.itemStackArrayToBase64(k.toItemArray());
		
		ItemStack[] items = new ItemStack[VersusType.values().length];
		for(VersusType type : VersusType.values()){
			items[type.getTeam().length-2]=UtilItem.RenameItem(new ItemStack(Material.IRON_HELMET,type.getTeam().length), "§b"+type.getTeam().length+" Teams");
			versus_warte_liste.put(type.getTeam().length, new ArrayList<Player>());
		}
		
		this.base.setMain(new InventoryChoose(new Click(){
			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				if(obj instanceof ItemStack && ((ItemStack)obj).hasItemMeta() && ((ItemStack)obj).getItemMeta().hasDisplayName()){
					if(((ItemStack)obj).getType()==Material.IRON_HELMET&& versus_warte_liste.containsKey(((ItemStack)obj).getAmount()) ){
						player.closeInventory();
						removeFromList(player);
						versus_warte_liste.get(((ItemStack)obj).getAmount()).add(player);
						player.sendMessage(Text.PREFIX.getText()+Text.VERSUS_ADDED.getText());
					}
				}
			}
			
		}, "§bVersus", 9, items));
		this.base.getMain().setItem(8, UtilItem.RenameItem(new ItemStack(Material.MAP), "§bOptionen"));
	}
	
	public void removeFromList(Player player){
		for(ArrayList<Player> list : versus_warte_liste.values()){
			if(list.contains(player)){
				list.remove(player);
			}
		}
	}
	
	ARENA_STATUS s;
	ArrayList<Player> list = new ArrayList<>();
	@EventHandler
	public void WarteListe(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC && !status.isEmpty()){
			for(Integer i : versus_warte_liste.keySet()){
				if(versus_warte_liste.get(i).isEmpty())continue;
				if(versus_warte_liste.get(i).size()<i)continue;
				
				for(int e = 0; e<status.size(); e++){
					s=(ARENA_STATUS)status.get(e);
					if(s.getState()==GameState.LobbyPhase){
						if(s.getTeams()==i){
							list.clear();
							for(int t = 0; t<s.getTeams(); t++){
								list.add(versus_warte_liste.get(i).get(0));
								getManager().getPacketManager().SendPacket(s.getServer(), new VERSUS_SETTINGS(VersusType.byInt(i),s.getArena(), "kingingo",versus_warte_liste.get(i).get(0), byInt(t)) );
								versus_warte_liste.get(i).remove(0);
							}
							
							for(Player p : list)UtilBG.sendToServer(p, s.getServer(), getManager().getInstance());
						}
					}
				}
			}
		}else if(ev.getType()==UpdateType.SLOWER){
			for(ArrayList<Player> list : versus_warte_liste.values()){
				for(int i = 0; i<list.size(); i++)list.get(i).sendMessage(Text.PREFIX.getText()+Text.VERSUS_PLACE.getText(i+1));
			}
		}
	}
	
	public Team byInt(int i){
		switch(i){
		case 0:return Team.RED;
		case 1:return Team.BLUE;
		case 2:return Team.GREEN;
		case 3:return Team.YELLOW;
		case 4:return Team.ORANGE;
		case 5:return Team.GRAY;
		default: return null;
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		removeFromList(ev.getPlayer());
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		if(creature==null){
			creature = (Villager) manager.getPet().AddPetWithOutOwner("§bVersus", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),-98.253,82,90.739));
			creature.setAdult();
			creature.setProfession(Profession.FARMER);
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked().getEntityId()==this.creature.getEntityId()){
			if(ev.getPlayer().getName().equalsIgnoreCase("kingingohd")||ev.getPlayer().getName().equalsIgnoreCase("kingingo")){
				if(!statsManager.ExistPlayer(ev.getPlayer())){
					statsManager.setString(ev.getPlayer(), this.kit, Stats.KIT);
					statsManager.setString(ev.getPlayer(), "true", Stats.KIT_RANDOM);
					statsManager.setInt(ev.getPlayer(), 1, Stats.TEAM_MIN);
					statsManager.setInt(ev.getPlayer(), 3, Stats.TEAM_MAX);
					statsManager.SaveAllPlayerData(ev.getPlayer());
				}
				
				ev.getPlayer().openInventory(base.getMain());
			}
			ev.setCancelled(true);
		}
	}
	
	//EMPFÄNGT ALLE PACKETE UND SPEICHERT SIE AB ODER UPDATET SIE
	ARENA_STATUS packet;
	boolean find=false;
	@EventHandler
	public void Receive(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof ARENA_STATUS){
			packet = (ARENA_STATUS)ev.getPacket();
			System.out.println("PACKET: "+packet.toString());
			find=false;
			
			for(int i = 0 ; i < status.size(); i++){
				//ÜBERPRÜFT OB DIE ARENA SCHONMAL ABGESPEICHERT WURDE
				if(status.get(i).getArena().equalsIgnoreCase(packet.getArena())){
					//ÜBERSCHREIBT DAS BESTEHNDE PACKET
					status.get(i).Set(packet.toString());
					find=true;
					break;
				}
			}
			
			//SPEICHERT EINE NEUE ARENA AB!
			if(!find)status.add(packet);
		}
	}
	
//	public enum AmorColorChange{
//	RAINBOW(null),
//	RANDOM_ALL(null),
//	RANDOM_ONLY(null),
//	LOAD(null);
//	
//	@Getter
//	@Setter
//	private Color[] colors;
//	private AmorColorChange(Color[] colors){
//		this.colors=colors;
//	}
//}
//
//int i = -1;
//ItemStack hand;
//@EventHandler
//public void CreatureEffect(UpdateEvent ev){
//	if(ev.getType()==UpdateType.FASTEST&&!UtilServer.getPlayers().isEmpty()){
//		if(this.creature!=null){
//			if(color_time < System.currentTimeMillis()){
//				this.color=AmorColorChange.values()[UtilMath.r(AmorColorChange.values().length)];
//				this.color_time=System.currentTimeMillis()+TimeSpan.SECOND*20;
//				if(color==AmorColorChange.LOAD){
//					creature.getEquipment().setArmorContents(null);
//				}
//				hand = this.creature.getEquipment().getItemInHand();
//				hand.setType((UtilMath.r(2)==0 ? UtilItem.rdmAxt() : UtilItem.rdmSchwert()));
//				this.creature.getEquipment().setItemInHand( hand );
//			}
//			
//			switch(color){
//			case LOAD:
//				//SETZT 2 VERSCHIEDENE FARBEN
//				if(color.getColors()==null){
//					color.setColors(new Color[2]);
//					color.getColors()[0]=me.kingingo.kcore.Util.Color.rdmColor();
//					color.getColors()[1]=color.getColors()[0];
//				}
//				
//				creature.getEquipment().setArmorContents(UtilItem.colorRunArmor(creature.getEquipment().getArmorContents(), color.getColors()));
//				break;
//			case RAINBOW:
//				creature.getEquipment().setArmorContents( UtilItem.rainbowArmor(creature.getEquipment().getArmorContents()) );
//				break;
//			case RANDOM_ALL:
//				creature.getEquipment().setArmorContents( UtilItem.setArmorColor(creature.getEquipment().getArmorContents(), me.kingingo.kcore.Util.Color.rdmColor()) );
//				break;
//			case RANDOM_ONLY:
//				if(creature.getEquipment().getArmorContents()[0]==null|| creature.getEquipment().getArmorContents()[0].getType()!=Material.LEATHER_HELMET){
//					creature.getEquipment().setArmorContents( UtilItem.setArmorColor(creature.getEquipment().getArmorContents(), me.kingingo.kcore.Util.Color.rdmColor()) );
//				}else{
//					creature.getEquipment().setHelmet( UtilItem.LSetColor(creature.getEquipment().getHelmet(), me.kingingo.kcore.Util.Color.rdmColor()) );
//					creature.getEquipment().setChestplate( UtilItem.LSetColor(creature.getEquipment().getChestplate(), me.kingingo.kcore.Util.Color.rdmColor()) );
//					creature.getEquipment().setLeggings( UtilItem.LSetColor(creature.getEquipment().getLeggings(), me.kingingo.kcore.Util.Color.rdmColor()) );
//					creature.getEquipment().setBoots( UtilItem.LSetColor(creature.getEquipment().getBoots(), me.kingingo.kcore.Util.Color.rdmColor()) );
//				}
//				break;
//			}
//		}else{
//			creature = (Villager) manager.getPet().AddPetWithOutOwner("§bVersus", true, EntityType.VILLAGER, spawn);
//			creature.setAdult();
//			creature.setProfession(Profession.FARMER);
//			creature.getEquipment().setItemInHand(new ItemStack(UtilItem.rdmAxt()));
//		}
//	}
//}

}
