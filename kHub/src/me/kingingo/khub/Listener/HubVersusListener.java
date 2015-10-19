package me.kingingo.khub.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Hologram.nametags.NameTagType;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Inventory.InventoryChoose;
import me.kingingo.kcore.Inventory.Inventory.InventoryYesNo;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.ARENA_STATUS;
import me.kingingo.kcore.Packet.Packets.VERSUS_SETTINGS;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.UpdateAsync.UpdateAsyncType;
import me.kingingo.kcore.UpdateAsync.UpdaterAsync;
import me.kingingo.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDebug;
import me.kingingo.kcore.Util.UtilEnt;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Versus.PlayerKit;
import me.kingingo.kcore.Versus.PlayerKitManager;
import me.kingingo.kcore.Versus.VersusType;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.kHub;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class HubVersusListener extends kListener{

	@Getter
	private HubManager manager;
	private ArrayList<ARENA_STATUS> status = new ArrayList<>();
	private HashMap<GameState,ArrayList<ARENA_STATUS>> sort = new HashMap<>();
	private HashMap<VersusType,ArrayList<Player>> versus_warte_liste = new HashMap<>();
	private StatsManager statsManager;
	private InventoryBase base;
	private Creature creature_option;
	private InventoryPageBase optionen;
	private InventoryYesNo kit_random;
	private InventoryChoose kit_choose;
	private InventoryChoose team_min;
	private InventoryChoose team_max;
	private HashMap<Creature,VersusType> creatures = new HashMap<>();
	private HashMap<Player,Player> vs = new HashMap<>();
	@Getter
	private PlayerKitManager kitManager;
	private ArrayList<Player> creative = new ArrayList<>();
	
	public HubVersusListener(final HubManager manager) {
		super(manager.getInstance(),"VersusListener");
		new UpdaterAsync(manager.getInstance());
		this.manager=manager;
		this.kitManager=new PlayerKitManager(manager.getMysql(), GameType.Versus);
		this.statsManager=new StatsManager(manager.getInstance(), manager.getMysql(), GameType.Versus);
		this.base=new InventoryBase(manager.getInstance(), "§bVersus");
		UtilTime.setTimeManager(manager.getPermissionManager());
		
		for(VersusType type : VersusType.values())versus_warte_liste.put(type, new ArrayList<Player>());
		
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
		
		this.kit_choose = new InventoryChoose(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object obj) {
				player.openInventory(optionen);
				if(obj instanceof ItemStack){
					if( ((ItemStack)obj).getAmount() > 1){
						if(!player.hasPermission(kPermission.VERSUS_SLOTS.getPermissionToString())){
							player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "PREMIUM_MUST_BUYED_IN_SHOP"));
							return;
						}
					}
					
					statsManager.setInt(player, ((ItemStack)obj).getAmount(), Stats.KIT_ID);
					player.sendMessage(Language.getText(player, "PREFIX")+"§aDein Kit wurde geändert!");
				}
			}
			
		}, "Kit choose", 9, new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.ARROW,1), "§aKit 1"),UtilItem.RenameItem(new ItemStack(Material.ARROW,2), "§aKit 2"),UtilItem.RenameItem(new ItemStack(Material.ARROW,3), "§aKit 3"),UtilItem.RenameItem(new ItemStack(Material.ARROW,4), "§aKit 4")});
		
		this.optionen=new InventoryChoose(null, "§5Optionen", 9, new ItemStack[]{});
		this.optionen.addButton(1, new ButtonOpenInventory(team_min, UtilItem.Item(new ItemStack(Material.REDSTONE), new String[]{"§7Hier kannst du die ","§7Team mindest anzahl","§7setzten!"}, "§eTeam mindest anzahl")));
		this.optionen.addButton(3, new ButtonOpenInventory(team_max, UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[]{"§7Hier kannst du die ","§7Team maximal anzahl","§7setzten!"}, "§eTeam maximal anzahl")));
		this.optionen.addButton(5, new ButtonOpenInventory(this.kit_random, UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§7Einstellung nur für 1vs1"}, "Kit Random")));
		this.optionen.addButton(7, new ButtonOpenInventory(this.kit_choose, UtilItem.Item(new ItemStack(Material.DIAMOND_CHESTPLATE), new String[]{"§7Hier kannst du dein Kit Slot auswählen!"}, "§7Kit Slot")));
		this.optionen.fill(Material.STAINED_GLASS_PANE, 14);
		
		this.base.addPage(kit_choose);
		this.base.addPage(optionen);
		this.base.addPage(team_min);
		this.base.addPage(team_max);
		this.base.addPage(kit_random);
		
		if(creatures.isEmpty()){
			creatures.put(manager.getPet().AddPetWithOutOwner("§aRandom 1vs1", true, EntityType.VILLAGER, CommandLocations.getLocation("1vs1")) ,VersusType._TEAMx2);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx3.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandLocations.getLocation("Team_3")) ,VersusType._TEAMx3);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx4.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandLocations.getLocation("Team_4")) ,VersusType._TEAMx4);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx5.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandLocations.getLocation("Team_5")) ,VersusType._TEAMx5);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx6.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandLocations.getLocation("Team_6")) ,VersusType._TEAMx6);
			NameTagMessage m;
			
			for(Creature creature : creatures.keySet()){
				((Villager)creature).setProfession(Profession.BUTCHER);
				((Villager)creature).setAdult();
				m=new NameTagMessage(NameTagType.SERVER, creature.getLocation().add(0, 2, 0), creature.getCustomName());
				m.send();
				creature.setCustomName("");
				UtilEnt.setNoAI(creature, true);
			}
			
			this.creature_option=manager.getPet().AddPetWithOutOwner("§5Optionen", true, EntityType.VILLAGER, CommandLocations.getLocation("Optionen"));
			((Villager)this.creature_option).setProfession(Profession.LIBRARIAN);
			((Villager)this.creature_option).setAdult();
			m=new NameTagMessage(NameTagType.SERVER, creature_option.getLocation().add(0, 2, 0), creature_option.getCustomName());
			m.send();
			creature_option.setCustomName("");
			UtilEnt.setNoAI(creature_option, true);
		}
	}
	
	public void removeFromList(Player player){
		for(ArrayList<Player> list : versus_warte_liste.values()){
			if(list.contains(player)){
				list.remove(player);
			}
		}
	}
	
	PlayerKit k;
	@EventHandler
	public void gamemode(PlayerGameModeChangeEvent ev){
		if(ev.getNewGameMode() == GameMode.CREATIVE && !creative.contains(ev.getPlayer())){
			creative.add(ev.getPlayer());
			ev.getPlayer().getInventory().clear();
			k = getKitManager().getKit(UtilPlayer.getRealUUID(ev.getPlayer()),statsManager.getInt(Stats.KIT_ID, ev.getPlayer()));
			
			if(k!=null){
				ev.getPlayer().getInventory().setArmorContents(k.armor_content);
				ev.getPlayer().getInventory().setContents(k.content);
				ev.getPlayer().updateInventory();
			}else{
				getKitManager().addKit(ev.getPlayer().getName(),ev.getPlayer().getUniqueId(), ev.getPlayer().getInventory(), statsManager.getInt(Stats.KIT_ID, ev.getPlayer()));
			}
		}else if(ev.getNewGameMode() != GameMode.CREATIVE){
			if(creative.contains(ev.getPlayer())){
				getKitManager().updateKit(UtilPlayer.getRealUUID(ev.getPlayer()), statsManager.getInt(Stats.KIT_ID, ev.getPlayer()), ev.getPlayer().getInventory());
				
				ev.getPlayer().getInventory().clear();
				ev.getPlayer().getInventory().setArmorContents(null);
				
				ev.getPlayer().getInventory().setItem(0, UtilItem.RenameItem(new ItemStack(Material.CHEST), Language.getText(ev.getPlayer(), "HUB_ITEM_CHEST")));
				ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§azum 1vs1 herrausfordern"));
				creative.remove(ev.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void Portal(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			for(Player player : UtilServer.getPlayers()){
				if(player.getEyeLocation().getBlock().getType()==Material.PORTAL){
					UtilBG.sendToServer(player, "hub1", manager.getInstance());
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
//		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
//			ev.setCancelled(true);
//		}
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.CHEST){
				ev.getPlayer().openInventory(getManager().getShop().getMain());
			}
		}
	}
	
	@EventHandler
	public void AsyncUpdateOrder(UpdateAsyncEvent ev){
		if(ev.getType()==UpdateAsyncType.SLOWEST&&UtilServer.getPlayers().size()!=0){
			if(sort.isEmpty()){
				for(GameState s : GameState.values())sort.put(s, new ArrayList<>());
			}else{
				for(GameState s : GameState.values())sort.get(s).clear();
			}
			
			for(ARENA_STATUS s : status){
				sort.get(s.getState()).add(s);
			}
		}
	}
	
	HashMap<Team,ArrayList<Player>> list = new HashMap<>();
	ArrayList<ARENA_STATUS> rdm;
	ARENA_STATUS arena;
	int a = 0;
	int t = 0;
	Player player;
	VersusType type;
	@EventHandler
	public void WarteListe(UpdateAsyncEvent ev){
		//Führt die Überprüfung alle 3 sekunden durch & prüft ob Arenen vorhanden sind
		if(ev.getType()==UpdateAsyncType.SLOW && !UtilServer.getPlayers().isEmpty() && !status.isEmpty()){
			
			if(sort.containsKey(GameState.Laden)&&!sort.get(GameState.Laden).isEmpty()){
				if(UtilDebug.debug)Log("[LOAD] 1");
				for(ARENA_STATUS arena : sort.get(GameState.Laden)){
					if(UtilDebug.debug)Log("[LOAD] ARENA: "+arena.getArena());
					//Checkt die Team Anzahl für den gesetzten Server!
					type=(VersusType)VersusType.byInt(arena.getTeam());
					
					//Prüft ob Spieler noch auf der Warteliste sind!
					if(versus_warte_liste.containsKey(type)){
						if(UtilDebug.debug)Log("[LOAD] TYPE: "+type.name());
						if(!versus_warte_liste.get(type).isEmpty()&&arena.getOnline()>1){
							if(UtilDebug.debug)Log("[LOAD] ONLINE: "+arena.getOnline());
							//Geht die Warte liste mit den Spieler durch
							for(int i = 0 ; i<versus_warte_liste.get(type).size(); i++){
								//Prüft ob die Spieler Anzahl ob sie größer ist als die Insgesamte erlaubte Spieler anzahl vom Spieler selber
								if(arena.getOnline() >= arena.getMax_team()*arena.getTeam())break;
								if(UtilDebug.debug)Log("[LOAD] "+arena.getMax_team()+"*"+arena.getTeam());
								player=(Player)versus_warte_liste.get(type).get(i);
								//Prüft ob die Arena min Team ansprüche mit den vom Spieler vereinbar sind
								if(UtilDebug.debug)Log("[LOAD] P: "+player.getName());
								if(arena.getMin_team() <= statsManager.getInt(Stats.TEAM_MIN, player)){
									if(UtilDebug.debug)Log("[LOAD] "+arena.getMin_team()+"<="+statsManager.getInt(Stats.TEAM_MIN, player));
									//Prüft ob die Arena max Team ansprüche mit den vom Spieler vereinbar sind
									if(arena.getMax_team() >= statsManager.getInt(Stats.TEAM_MIN, player)){
										if(UtilDebug.debug)Log("[LOAD] "+arena.getMax_team()+">="+statsManager.getInt(Stats.TEAM_MAX, player));
										//Schickt den Spieler zu der Arena!
										getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(type,arena.getArena(),arena.getKit(),player,Team.SOLO,arena.getMin_team(),arena.getMax_team()) );
										versus_warte_liste.get(type).remove(player);
										UtilBG.sendToServer(player, arena.getServer(), manager.getInstance());
										arena.setOnline(arena.getOnline()+1);
									}
								}
							}
						}
					}
				}
			}
			
			if(sort.containsKey(GameState.LobbyPhase)&&!sort.get(GameState.LobbyPhase).isEmpty()){
				if(UtilDebug.debug)Log("1");
				if(rdm!=null){
					rdm.clear();
					rdm=null;
				}
				rdm=(ArrayList<ARENA_STATUS>)sort.get(GameState.LobbyPhase).clone();
				
				for(int r = 0; r<rdm.size(); r++){
					arena = rdm.get(UtilMath.r(rdm.size()));
					if(UtilDebug.debug)Log("[NEW] ARENA: "+arena.getArena()+" "+arena.getTeams());
					a=0;
					//Er geht die möhglichen Team arten für diese Arena durch und sucht Spieler!
					for(int i = arena.getTeams(); i >= 2 ; i--){
						if(UtilDebug.debug)Log("[NEW] I: "+i+" A: "+a);
						type=(VersusType)VersusType.byInt( i );
						
						//Prüft ob Spieler auf der Warte liste für den Typ sind und ob die Anzahl gleich oder höher der Team anzahl ist
						if(versus_warte_liste.containsKey(type)&&!versus_warte_liste.get(type).isEmpty()&&versus_warte_liste.get(type).size()>=i){
							
							//Setzt die min. und maximal Team Anzahl!
							if(type == VersusType._TEAMx2){
								arena.setMin_team(1);
								arena.setMax_team(1);
							}else{
								arena.setMin_team(statsManager.getInt(Stats.TEAM_MIN, versus_warte_liste.get(type).get(a)));
								arena.setMax_team(statsManager.getInt(Stats.TEAM_MAX, versus_warte_liste.get(type).get(a)));
							}
							
							//Hollt sich den ERSTEN Spieler und nimmt sein Kit!
							arena.setKit(versus_warte_liste.get(type).get(a).getName());
							if(!list.isEmpty()){
								for(int e = 0; e<list.size();e++){
									if(list.get(e)!=null&&!list.get(e).isEmpty())list.get(e).clear();
								}
								list.clear();
							}
							
							//Setzt die Team List auf
							for(Team team : type.getTeam())list.put(team, new ArrayList<Player>());
							t=0;
							boolean b = false;
							
							//Geht die Warte List nach Spielern durch
							if(type == VersusType._TEAMx2){
								list.get(Team.RED).add(versus_warte_liste.get(type).get(a));
								
								for(Player player : versus_warte_liste.get(type)){
									if(player.getUniqueId()==versus_warte_liste.get(type).get(a).getUniqueId())continue;
									
									if( statsManager.getString(Stats.KIT_RANDOM, player).equalsIgnoreCase("true") ){
										if(UtilDebug.debug)Log("[NEW] FU3 "+player.getName());
										list.get(Team.BLUE).add(player);
										break;
									}else{
										if( statsManager.getString(Stats.KIT_RANDOM, versus_warte_liste.get(type).get(a)).equalsIgnoreCase("true") ){
											if(UtilDebug.debug)Log("[NEW] FU3 "+player.getName());
											arena.setKit(player.getName());
											list.get(Team.BLUE).add(player);
											break;
										}
									}
								}
							}else{
								for(Player player : versus_warte_liste.get(type)){
									//Prüft ob die MIN Team anzahl mit den Spieler einstellungen vereinbar sind
									if(arena.getMin_team() <= statsManager.getInt(Stats.TEAM_MIN, player)){
										//Prüft ob die MAX Team anzahl mit den Spieler einstellungen vereinbar sind
										if(arena.getMax_team() >= statsManager.getInt(Stats.TEAM_MAX, player)){
											//Fügt den Spieler hinzu
											if(UtilDebug.debug)Log("[NEW] FU3 "+player.getName());
											list.get(((Team)list.keySet().toArray()[t])).add(player);
											if( (t+1)==list.size() ){
												t=0;
											}else{
												t++;
											}
										}
									}
									
									//Prüft ob die Teams voll genug sind!
									for(Team t : list.keySet()){
										if( list.get(t).size() >= arena.getMax_team() ){
											b=true;
										}else{
											b=false;
											break;
										}
									}
									
									if(b){
										break;
									}
								}
							}
							
							if(!list.isEmpty()){
								b=true;
								//Prüft ob die Teams voll genug sind!
								for(Team team : type.getTeam()){
									if(UtilDebug.debug)Log("[NEW] T: "+team.Name()+"   "+list.get(team).size()+"<"+arena.getMin_team());
									if(list.get(team).size()<arena.getMin_team()){
										b=false;
										if(UtilDebug.debug)Log("[NEW] FALSE");
										break;
									}
								}
								
								if(b){
									if(UtilDebug.debug)Log("[NEW] YEAH");
									arena.setState(GameState.Laden);
									rdm.remove(arena);
									sort.get(GameState.LobbyPhase).remove(arena);
									sort.get(GameState.Laden).add(arena);
									status.remove(arena);
									status.add(arena);
									
									//Schickt alle Spieler informationen zu den Server
									for(Team team : list.keySet()){
										for(Player player : list.get(team)){
											arena.setOnline( arena.getOnline()+1 );
											getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(type,arena.getArena(),arena.getKit(),player,team,arena.getMin_team(),arena.getMax_team()) );
										}
									}
									
									//Schickt alle Spieler los und entfernt sie von der Liste.
									for(Team team : list.keySet()){
										for(Player player : list.get(team)){
											versus_warte_liste.get(type).remove(player);
											UtilBG.sendToServer(player, arena.getServer(), manager.getInstance());
										}
									}
									break;
								}
								if(UtilDebug.debug)Log("[NEW] FU2");
							}
								
							if(versus_warte_liste.get(type).size()>a){
								if(UtilDebug.debug)Log("[NEW] FU");
								a++;
								i++;
							}else{
								if(UtilDebug.debug)Log("[NEW] FU1");
								a=0;
							}
						}else{
							if(UtilDebug.debug)Log("[NEW] "+versus_warte_liste.containsKey(type)+" "+!versus_warte_liste.get(type).isEmpty()+" "+(versus_warte_liste.get(type).size()>=i));
							if(UtilDebug.debug)Log("[NEW] FU5  "+type+"  "+versus_warte_liste.get(type).size()+">="+i);
						}
					}
					rdm.remove(arena);
				}
			}
		}	
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(creative.contains(ev.getPlayer())){
			ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
		
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
					statsManager.setString(player, "true", Stats.KIT_RANDOM);
					statsManager.setInt(player, 1, Stats.KIT_ID);
					statsManager.setInt(player, 1, Stats.TEAM_MIN);
					statsManager.setInt(player, 3, Stats.TEAM_MAX);
					statsManager.setInt(player, 200, Stats.ELO);
					statsManager.SaveAllPlayerData(player);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Join(PlayerJoinEvent ev){
		ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "WHEREIS_TEXT","Versus Hub"));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEPICPVP §7- §e"+kHub.hubType+" "+kHub.hubID, "§eShop.EpicPvP.de");
		
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		load.add(ev.getPlayer());
		ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§azum 1vs1 herrausfordern"));
	}
	
	private String s;
	@EventHandler
	public void Entity(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Player){
			if(((Player)ev.getDamager()).getItemInHand().getType()==Material.DIAMOND_SWORD){
				
					s=UtilTime.getTimeManager().check("VERSUS_SWORD", ((Player)ev.getDamager()));
					if(s!=null){
						((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "USE_BEFEHL_TIME",s));
					}else{
						UtilTime.getTimeManager().add("VERSUS_SWORD", ((Player)ev.getDamager()), TimeSpan.SECOND*10);
						if(vs.containsKey( ((Player)ev.getEntity()) )){
							if(vs.get(((Player)ev.getEntity())).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName())){
								if(!sort.get(GameState.LobbyPhase).isEmpty()){
									ARENA_STATUS arena = sort.get(GameState.LobbyPhase).get( UtilMath.r(sort.get(GameState.LobbyPhase).size()) );
										arena.setMin_team(1);
										arena.setMax_team(1);
										getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType._TEAMx2,arena.getArena(),((Player)ev.getEntity()).getName(),((Player)ev.getDamager()),VersusType._TEAMx2.getTeam()[0],1,1) );
										UtilBG.sendToServer(((Player)ev.getDamager()), arena.getServer(), manager.getInstance());
										getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType._TEAMx2,arena.getArena(),((Player)ev.getEntity()).getName(),((Player)ev.getEntity()),VersusType._TEAMx2.getTeam()[1],1,1) );
										UtilBG.sendToServer(((Player)ev.getEntity()), arena.getServer(), manager.getInstance());							
										arena.setState(GameState.InGame);
										arena.setKit(((Player)ev.getEntity()).getName());
										vs.remove(((Player)ev.getEntity()));
										vs.remove(((Player)ev.getDamager()));
								}else{
									((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_NO_FREE_ARENAS"));
									((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_NO_FREE_ARENAS"));
								}
								return;
							}
						}
						
						vs.remove(((Player)ev.getDamager()));
						vs.put(((Player)ev.getDamager()), ((Player)ev.getEntity()));
						((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_FROM_QUESTION", ((Player)ev.getDamager()).getName()));
						((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_QUESTION",((Player)ev.getEntity()).getName()));
					}
			}
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked().getEntityId()==this.creature_option.getEntityId()){
			ev.setCancelled(true);
			ev.getPlayer().openInventory(this.optionen);
		}else if(creatures.containsKey(ev.getRightClicked())){
			ev.setCancelled(true);
			if(versus_warte_liste.get(creatures.get(ev.getRightClicked())).contains(ev.getPlayer())){
				removeFromList(ev.getPlayer());
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_REMOVE"));
			}else{
				removeFromList(ev.getPlayer());
				versus_warte_liste.get(creatures.get(ev.getRightClicked())).add(ev.getPlayer());
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_ADDED"));
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
			
			for(ARENA_STATUS s : status){
				if(s.getArena().equalsIgnoreCase(packet.getArena())){
					s.Set(packet.toString());
					packet=null;
					break;
				}
			}
			
			if(packet!=null)status.add(packet);
		}
	}
	
	public class TeamMinMax{
		public int minteam;
		public int maxteam;
		private String kit_name;
	}

}
