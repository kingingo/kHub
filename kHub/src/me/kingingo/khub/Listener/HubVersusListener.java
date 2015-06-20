package me.kingingo.khub.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Inventory.InventoryChoose;
import me.kingingo.kcore.Inventory.Inventory.InventoryYesNo;
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
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Versus.VersusKit;
import me.kingingo.kcore.Versus.VersusType;
import me.kingingo.khub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class HubVersusListener extends kListener{

	@Getter
	private HubManager manager;
	private ArrayList<ARENA_STATUS> status = new ArrayList<>();
	private HashMap<VersusType,ArrayList<Player>> versus_warte_liste = new HashMap<>();
	private TreeMap<String,Integer> elo_sort_liste = new TreeMap<>();
	private ArrayList<Player> elo_liste = new ArrayList<>();
	private StatsManager statsManager;
	private String kit;
	private InventoryBase base;
	private Creature creature_elo;
	private Creature creature_option;
	private InventoryPageBase optionen;
	private InventoryYesNo kit_random;
	private InventoryChoose team_min;
	private InventoryChoose team_max;
	private HashMap<Creature,VersusType> creatures = new HashMap<>();
	
	public HubVersusListener(HubManager manager) {
		super(manager.getInstance(),"VersusListener");
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
		for(VersusType type : VersusType.values())versus_warte_liste.put(type, new ArrayList<Player>());
		
		this.optionen=new InventoryChoose(null, "§5Optionen", 9, new ItemStack[]{});
		
		this.optionen.addButton(1, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if( ((ItemStack)obj).getType()==Material.REDSTONE ){
					player.openInventory(team_min);
				}
			}
			
		}, UtilItem.Item(new ItemStack(Material.REDSTONE), new String[]{"§7Hier kannst du die ","§7Team mindest anzahl","§7setzten!"}, "§eTeam mindest anzahl")));
		
		this.optionen.addButton(3, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if( ((ItemStack)obj).getType()==Material.GLOWSTONE_DUST ){
					player.openInventory(team_max);
				}
			}
			
		}, UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[]{"§7Hier kannst du die ","§7Team maximal anzahl","§7setzten!"}, "§eTeam maximal anzahl")));
		
		this.optionen.addButton(5, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if( ((ItemStack)obj).getType()==Material.IRON_SWORD ){
					player.openInventory(kit_random);
				}
			}
			
		}, UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§7Einstellung nur für 1vs1"}, "Kit Random")));
	
		this.optionen.addButton(7, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if( ((ItemStack)obj).getType()==Material.DIAMOND_CHESTPLATE ){
					
				}
			}
			
		},UtilItem.Item(new ItemStack(Material.DIAMOND_CHESTPLATE), new String[]{"§7Hier kannst du","§7dein Kit einstellen."}, "§eKit einstellen")));
		this.base.addPage(optionen);
		
		this.team_max = new InventoryChoose(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if(obj instanceof ItemStack){
					if( ((ItemStack)obj).getAmount() < statsManager.getInt(Stats.TEAM_MIN, player)){
						player.sendMessage(Text.PREFIX.getText()+"Die Team maximal anzahl kann kleiner als die mindest anzahl sein!");
						player.closeInventory();
					}else{
						statsManager.setInt(player, ((ItemStack)obj).getAmount(), Stats.TEAM_MAX);
						player.openInventory(optionen);
					}
				}
			}
			
		}, "Team maximal: ", 9, new ItemStack[]{ UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(short)3), " "),UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,2,(short)3), " "),UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,3,(short)3), " ") });
		this.base.addPage(team_max);
		
		this.team_min = new InventoryChoose(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if(obj instanceof ItemStack){
					if( ((ItemStack)obj).getAmount() > statsManager.getInt(Stats.TEAM_MAX, player)){
						player.sendMessage(Text.PREFIX.getText()+"Die Team mindest anzahl kann nicht größer als die maximal anzahl sein!");
						player.closeInventory();
					}else{
						statsManager.setInt(player, ((ItemStack)obj).getAmount(), Stats.TEAM_MIN);
						player.openInventory(optionen);
					}
				}
			}
			
		}, "Team mindestens: ", 9, new ItemStack[]{ UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(short)3), " "),UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,2,(short)3), " "),UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,3,(short)3), " ") });
		this.base.addPage(team_min);
		
		this.kit_random= new InventoryYesNo("Kit Random", new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				statsManager.setString(player, "true", Stats.KIT_RANDOM);
				player.openInventory(optionen);
			}
			
		}, new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				statsManager.setString(player, "false", Stats.KIT_RANDOM);
				player.openInventory(optionen);
			}
			
		});
		this.base.addPage(kit_random);
	}
	
	public void removeFromList(Player player){
		for(ArrayList<Player> list : versus_warte_liste.values()){
			if(list.contains(player)){
				list.remove(player);
			}
		}
		
		elo_liste.remove(player);
		elo_sort_liste.remove(player.getName());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
			ev.setCancelled(true);
		}
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.BONE){
				ev.getPlayer().openInventory(getManager().getShop().getMain());
			}
		}
	}
	
	ARENA_STATUS s;
	ArrayList<Player> list = new ArrayList<>();
	int minteam;
	int maxteam;
	Player player;
	int pi = 0;
	@EventHandler
	public void WarteListe(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC && !status.isEmpty()){
			for(VersusType i : versus_warte_liste.keySet()){
				if(versus_warte_liste.get(i).isEmpty())continue;
				if(versus_warte_liste.get(i).isEmpty())continue;
				
				for(int e = 0; e<status.size(); e++){
					s=(ARENA_STATUS)status.get(e);
					if(s.getState()==GameState.LobbyPhase&&!s.getMap().equalsIgnoreCase("")){
						if(s.getTeams()==i.getTeam().length){
							list.clear();
							player=versus_warte_liste.get(i).get(0);
							versus_warte_liste.remove(player);
							minteam=statsManager.getInt(Stats.TEAM_MIN, player);
							maxteam=statsManager.getInt(Stats.TEAM_MAX, player);
							
							for(int t = 0; t<s.getTeams(); t++){
								if(minteam <= statsManager.getInt(Stats.TEAM_MIN, versus_warte_liste.get(i).get(pi))){
									if(maxteam >= statsManager.getInt(Stats.TEAM_MAX, versus_warte_liste.get(i).get(pi))){
										getManager().getPacketManager().SendPacket(s.getServer(), new VERSUS_SETTINGS(i,s.getArena(), player.getName(),versus_warte_liste.get(i).get(pi), byInt(t)) );
										list.add(versus_warte_liste.get(i).get(pi));
										versus_warte_liste.remove(pi);
										continue;
									}
								}
								pi++;
							}
							pi=0;
							for(Player p : list)UtilBG.sendToServer(p, s.getServer(), getManager().getInstance());
						}
					}
				}
			}
			
			for(ARENA_STATUS s : status){
				if(s.getTeams()==2){
					if(s.getMap().equalsIgnoreCase("")){
						if(s.getState()==GameState.LobbyPhase){
							getManager().getPacketManager().SendPacket(s.getServer(), new VERSUS_SETTINGS(VersusType._TEAMx2, s.getArena(), "null", elo_liste.get(0), Team.RED));
							
							for(String p : elo_sort_liste.keySet()){
								if(pi==1){
									player=Bukkit.getPlayer(p);
									getManager().getPacketManager().SendPacket(s.getServer(), new VERSUS_SETTINGS(VersusType._TEAMx2, s.getArena(), "null", player, Team.BLUE));
									pi=0;
									break;
								}
								if(elo_liste.get(0).getName().equalsIgnoreCase(p)){
									pi=1;
								}
							}
							
							elo_sort_liste.remove(elo_liste.get(0));
							elo_sort_liste.remove(player);
							
							UtilBG.sendToServer(player, s.getServer(), manager.getInstance());
							UtilBG.sendToServer(elo_liste.get(0), s.getServer(), manager.getInstance());
							elo_liste.remove(0);
							elo_liste.remove(player);
						}
					}
				}
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
	
	ArrayList<Player> load = new ArrayList<>();
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_3){
			for(Player player : load){
				if(!statsManager.ExistPlayer(player)){
					statsManager.setString(player, this.kit, Stats.KIT);
					statsManager.setString(player, "true", Stats.KIT_RANDOM);
					statsManager.setInt(player, 1, Stats.TEAM_MIN);
					statsManager.setInt(player, 3, Stats.TEAM_MAX);
					statsManager.setInt(player, 200, Stats.ELO);
					statsManager.SaveAllPlayerData(player);
				}
			}
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.WHEREIS_TEXT.getText(manager.getId()+" Hub"));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEPICPVP §7- §eVersus Lobby "+manager.getId(), "§eShop.EpicPvP.de");
		
		if(creatures.isEmpty()){
			creatures.put(manager.getPet().AddPetWithOutOwner("§aRandom 1vs1", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),259.434,71.5,192.585)) ,VersusType._TEAMx2);
			
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx3.getTeam().length+"x Teams", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),238.524,71.5,202.596)) ,VersusType._TEAMx3);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx4.getTeam().length+"x Teams", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),241.518,71.5,205.421)) ,VersusType._TEAMx4);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx5.getTeam().length+"x Teams", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),249.709,71.5,205.210)) ,VersusType._TEAMx5);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx6.getTeam().length+"x Teams", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),252.516,71.5,202.543)) ,VersusType._TEAMx6);
			
			for(Creature creature : creatures.keySet()){
				((Villager)creature).setProfession(Profession.BUTCHER);
				((Villager)creature).setAdult();
			}
			
			this.creature_elo=manager.getPet().AddPetWithOutOwner("§c1vs1", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),233.552,71.5,188.620));
			((Villager)this.creature_elo).setProfession(Profession.BLACKSMITH);
			((Villager)this.creature_elo).setAdult();
			
			this.creature_option=manager.getPet().AddPetWithOutOwner("§5Optionen", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),231.491,70,181.7));
			((Villager)this.creature_option).setProfession(Profession.LIBRARIAN);
			((Villager)this.creature_option).setAdult();
		}
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		ev.getPlayer().getInventory().setItem(8,new ItemStack(Material.DIAMOND_SWORD));
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		load.add(ev.getPlayer());
	}
	
	@EventHandler
	public void Interact(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked().getEntityId()==this.creature_option.getEntityId()){
			ev.setCancelled(true);
			ev.getPlayer().openInventory(this.optionen);
		}else if(creatures.containsKey(ev.getRightClicked())){
			ev.setCancelled(true);
			if(versus_warte_liste.get(creatures.get(ev.getRightClicked())).contains(ev.getPlayer()))return;
			removeFromList(ev.getPlayer());
			versus_warte_liste.get(creatures.get(ev.getRightClicked())).add(ev.getPlayer());
			ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.VERSUS_ADDED.getText());
		}else if(ev.getRightClicked().getEntityId()==this.creature_elo.getEntityId()){
			ev.setCancelled(true);
			if(elo_liste.contains(ev.getPlayer()))return;
			removeFromList(ev.getPlayer());
			elo_liste.add(ev.getPlayer());
			elo_sort_liste.put(ev.getPlayer().getName(), statsManager.getInt(Stats.ELO, ev.getPlayer()));
			ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.VERSUS_ADDED.getText());
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

}
