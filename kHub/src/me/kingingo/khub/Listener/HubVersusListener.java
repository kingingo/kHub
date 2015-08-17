package me.kingingo.khub.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Inventory.InventoryChoose;
import me.kingingo.kcore.Inventory.Inventory.InventoryYesNo;
import me.kingingo.kcore.Inventory.Item.ButtonBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Kit.InventorySetting.KitSettingInventorys;
import me.kingingo.kcore.Language.Language;
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
	private HashMap<ARENA_STATUS,TeamMinMax> status = new HashMap<>();
	private HashMap<VersusType,ArrayList<Player>> versus_warte_liste = new HashMap<>();
	private StatsManager statsManager;
	private String kit;
	private InventoryBase base;
	private Creature creature_option;
	private InventoryPageBase optionen;
	private InventoryYesNo kit_random;
	private InventoryChoose team_min;
	private InventoryChoose team_max;
	private KitSettingInventorys kits;
	private HashMap<Creature,VersusType> creatures = new HashMap<>();
	private HashMap<Player,Player> vs = new HashMap<>();
	
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
		this.kits=new KitSettingInventorys(manager.getInstance(), statsManager);
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
					player.closeInventory();
					if(kits.getInventorys().containsKey(player)){
						kits.addKitInventory(player);
					}else{
						try {
							kits.addKitInventory(player, new VersusKit().fromItemArray( UtilInv.itemStackArrayFromBase64(statsManager.getString(Stats.KIT, player))));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		},UtilItem.Item(new ItemStack(Material.DIAMOND_CHESTPLATE), new String[]{"§7Hier kannst du","§7dein Kit einstellen."}, "§eKit einstellen")));
		this.base.addPage(optionen);
		
		this.team_max = new InventoryChoose(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				if(obj instanceof ItemStack){
					if( ((ItemStack)obj).getAmount() < statsManager.getInt(Stats.TEAM_MIN, player)){
						player.sendMessage(Language.getText(player, "PREFIX")+"Die Team maximal anzahl kann kleiner als die mindest anzahl sein!");
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
						player.sendMessage(Language.getText(player, "PREFIX")+"Die Team mindest anzahl kann nicht größer als die maximal anzahl sein!");
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
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
			ev.setCancelled(true);
		}
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.CHEST){
				ev.getPlayer().openInventory(getManager().getShop().getMain());
			}
		}
	}
	
	HashMap<Team,ArrayList<Player>> list = new HashMap<>();
	int minteam;
	int maxteam;
	String kit_name;
	Player player;
	boolean b=false;
	VersusType type;
	int a=0;
	int t=0;
	@EventHandler
	public void WarteListe(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_3 && !status.isEmpty()){
			for(ARENA_STATUS arena : status.keySet()){
				if(arena.getState()==GameState.LobbyPhase){
					type=VersusType.withTeamAnzahl( arena.getTeams() );
					if(versus_warte_liste.containsKey(type)){
						
						if(!versus_warte_liste.get(type).isEmpty()){
							if(arena.getOnline()>1){
								maxteam=status.get(arena).maxteam;
								minteam=status.get(arena).minteam;
								kit_name=status.get(arena).kit_name;
								
								for(int i = 0 ; i<versus_warte_liste.get(type).size(); i++){
									if(arena.getOnline() >= maxteam*arena.getTeams())break;
									player=(Player)versus_warte_liste.get(type).get(i);
									if(minteam <= statsManager.getInt(Stats.TEAM_MIN, player)){
										if(maxteam >= statsManager.getInt(Stats.TEAM_MIN, player)){
											getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(type,arena.getArena(),kit_name,player,Team.SOLO) );
											versus_warte_liste.get(type).remove(player);
											UtilBG.sendToServer(player, arena.getServer(), manager.getInstance());
											arena.setOnline(arena.getOnline()+1);
										}
									}
								}
							}else{
								if(versus_warte_liste.get(type).size()>=arena.getTeams()){
									minteam=statsManager.getInt(Stats.TEAM_MIN, versus_warte_liste.get(type).get(a));
									maxteam=statsManager.getInt(Stats.TEAM_MAX, versus_warte_liste.get(type).get(a));
									kit_name=versus_warte_liste.get(type).get(a).getName();
									if(!list.isEmpty()){
										list.clear();
									}
										
									for(Team team : type.getTeam())list.put(team, new ArrayList<Player>());
									t=0;
									for(Player player : versus_warte_liste.get(type)){
										if(list.get(((Team)list.keySet().toArray()[t])).size()==maxteam&&t==list.size())break;
										if(minteam <= statsManager.getInt(Stats.TEAM_MIN, player)){
											if(maxteam >= statsManager.getInt(Stats.TEAM_MIN, player)){
												list.get(((Team)list.keySet().toArray()[t])).add(player);
												t++;
											}
										}
									}
									
									if(!list.isEmpty()){
										b=true;
										for(Team team : type.getTeam()){
											if(list.get(team).size()<minteam){
												b=false;
												break;
											}
										}
										
										if(b){
											status.get(arena).maxteam=maxteam;
											status.get(arena).minteam=minteam;
											status.get(arena).kit_name=kit_name;
											for(Team team : list.keySet()){
												for(Player player : list.get(team)){
													getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(type,arena.getArena(),kit_name,player,team) );
												}
											}
											
											for(Team team : list.keySet()){
												for(Player player : list.get(team)){
													versus_warte_liste.get(type).remove(player);
													UtilBG.sendToServer(player, arena.getServer(), manager.getInstance());
												}
											}
										}
									}else{
										a++;
									}
								}
							}
						}
					}
				}
			}
		}	
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		removeFromList(ev.getPlayer());
		statsManager.SaveAllPlayerData(player);
		vs.remove(ev.getPlayer());
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
		ev.getPlayer().sendMessage(Language.getText(player, "PREFIX")+Language.getText(ev.getPlayer(), "WHEREIS_TEXT","Versus Hub"));
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
			
			this.creature_option=manager.getPet().AddPetWithOutOwner("§5Optionen", true, EntityType.VILLAGER, new Location(Bukkit.getWorld("world"),231.491,70,181.7));
			((Villager)this.creature_option).setProfession(Profession.LIBRARIAN);
			((Villager)this.creature_option).setAdult();
		}
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§azum 1vs1 herrausfordern"));
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
			ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_ADDED"));
		}else if(ev.getPlayer().getItemInHand()!=null&&ev.getPlayer().getItemInHand().getType()==Material.DIAMOND_SWORD){
			if(ev.getRightClicked() instanceof Player){
				if(vs.containsKey( ((Player)ev.getRightClicked()) )){
					if(vs.get(((Player)ev.getRightClicked())).getName().equalsIgnoreCase(ev.getPlayer().getName())){
						for(ARENA_STATUS arena : status.keySet()){
							if(arena.getState() == GameState.LobbyPhase&&arena.getOnline()<=0){
								getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType.withTeamAnzahl(arena.getTeams()),arena.getArena(),((Player)ev.getRightClicked()).getName(),ev.getPlayer(),VersusType._TEAMx2.getTeam()[0]) );
								UtilBG.sendToServer(ev.getPlayer(), arena.getServer(), manager.getInstance());
								getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType.withTeamAnzahl(arena.getTeams()),arena.getArena(),((Player)ev.getRightClicked()).getName(),((Player)ev.getRightClicked()),VersusType._TEAMx2.getTeam()[1]) );
								UtilBG.sendToServer(((Player)ev.getRightClicked()), arena.getServer(), manager.getInstance());
								status.get(arena).maxteam=1;
								status.get(arena).minteam=1;
								status.get(arena).kit_name=((Player)ev.getRightClicked()).getName();
								vs.remove(((Player)ev.getRightClicked()));
							}
						}
						return;
					}
				}
				vs.remove(ev.getPlayer());
				vs.put(ev.getPlayer(), ((Player)ev.getRightClicked()));
			}
		}
	}
	
	//EMPFÄNGT ALLE PACKETE UND SPEICHERT SIE AB ODER UPDATET SIE
	ARENA_STATUS packet;
	boolean find=false;
	@EventHandler
	public void Receive(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof ARENA_STATUS){
			packet = (ARENA_STATUS)ev.getPacket();
			find=false;
			
			for(int i = 0 ; i < status.size(); i++){
				//ÜBERPRÜFT OB DIE ARENA SCHONMAL ABGESPEICHERT WURDE
				if(((ARENA_STATUS)status.keySet().toArray()[i]).getArena().equalsIgnoreCase(packet.getArena())){
					//ÜBERSCHREIBT DAS BESTEHNDE PACKET
					((ARENA_STATUS)status.keySet().toArray()[i]).Set(packet.toString());
					find=true;
					break;
				}
			}
			//SPEICHERT EINE NEUE ARENA AB!
			if(!find)status.put(packet,new TeamMinMax());
		}
	}
	
	public class TeamMinMax{
		public int minteam;
		public int maxteam;
		private String kit_name;
	}

}
