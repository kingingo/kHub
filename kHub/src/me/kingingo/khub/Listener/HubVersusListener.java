package me.kingingo.khub.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
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
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonBase;
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
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEnt;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Versus.VersusType;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Command.CommandVersus;

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
	private HashMap<VersusType,HashMap<GameState,ArrayList<ARENA_STATUS>>> status = new HashMap<>();
	private HashMap<VersusType,ArrayList<Player>> versus_warte_liste = new HashMap<>();
	private StatsManager statsManager;
	private InventoryBase base;
	private Creature creature_option;
	private InventoryPageBase optionen;
	private InventoryYesNo kit_random;
	private InventoryChoose team_min;
	private InventoryChoose team_max;
	private HashMap<Creature,VersusType> creatures = new HashMap<>();
	private HashMap<Player,Player> vs = new HashMap<>();
	
	public HubVersusListener(final HubManager manager) {
		super(manager.getInstance(),"VersusListener");
		this.manager=manager;
		manager.getCmd().register(CommandVersus.class, new CommandVersus(manager.getInstance()));
		
		UtilTime.setTimeManager(manager.getPermissionManager());
		this.statsManager=new StatsManager(manager.getInstance(), manager.getMysql(), GameType.Versus);
		this.base=new InventoryBase(manager.getInstance(), "§bVersus");
		
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
		
		this.base.addPage(optionen);
		this.base.addPage(team_min);
		this.base.addPage(team_max);
		this.base.addPage(kit_random);
		
		if(creatures.isEmpty()){
			creatures.put(manager.getPet().AddPetWithOutOwner("§aRandom 1vs1", true, EntityType.VILLAGER, CommandVersus.getVs()) ,VersusType._TEAMx2);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx3.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandVersus.getTeam_3()) ,VersusType._TEAMx3);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx4.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandVersus.getTeam_4()) ,VersusType._TEAMx4);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx5.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandVersus.getTeam_5()) ,VersusType._TEAMx5);
			creatures.put(manager.getPet().AddPetWithOutOwner("§b"+VersusType._TEAMx6.getTeam().length+"x Teams", true, EntityType.VILLAGER, CommandVersus.getTeam_6()) ,VersusType._TEAMx6);
			NameTagMessage m;
			
			for(Creature creature : creatures.keySet()){
				((Villager)creature).setProfession(Profession.BUTCHER);
				((Villager)creature).setAdult();
				m=new NameTagMessage(NameTagType.SERVER, creature.getLocation().add(0, 2, 0), creature.getCustomName());
				m.send();
				creature.setCustomName("");
				UtilEnt.setNoAI(creature, true);
			}
			
			this.creature_option=manager.getPet().AddPetWithOutOwner("§5Optionen", true, EntityType.VILLAGER, CommandVersus.getOptionen());
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
	
	@EventHandler
	public void gamemode(PlayerGameModeChangeEvent ev){
		if(ev.getNewGameMode() == GameMode.CREATIVE){
			
		}else{
			ev.getPlayer().getInventory().clear();
			ev.getPlayer().getInventory().setItem(0, UtilItem.RenameItem(new ItemStack(Material.CHEST), Language.getText(ev.getPlayer(), "HUB_ITEM_CHEST")));
			ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§azum 1vs1 herrausfordern"));
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
	ARENA_STATUS arena;
	Player player;
	boolean b=false;
	VersusType type;
	int a=0;
	int t=0;
	@EventHandler
	public void WarteListe(UpdateEvent ev){
		//Führt die Überprüfung alle 3 sekunden durch & prüft ob Arenen vorhanden sind
		if(ev.getType()==UpdateType.SEC_3 && !status.isEmpty()){
			//Geht Alle Arenen durch
			for(VersusType type : status.keySet()){
				//PRÜFT OB MOMENTAN SPIELER DA SIND!
				if(versus_warte_liste.containsKey(type)){
					
					//PRÜFT OB SERVER NOCH NICHT GESTARTET SIND UND WO NOCH PLATZ IST!
					if(status.get(type).containsKey(GameState.Laden) && !status.get(type).get(GameState.Laden).isEmpty()){
						
						for(ARENA_STATUS arena : status.get(type).get(GameState.Laden)){
							if(arena.getOnline()>1){
								//Geht die Warte liste mit den Spieler durch
								for(int i = 0 ; i<versus_warte_liste.get(type).size(); i++){
									//Prüft ob die Spieler Anzahl ob sie größer ist als die Insgesamte erlaubte Spieler anzahl vom Spieler selber
									if(arena.getOnline() >= arena.getMax_team()*arena.getTeams())break;
									player=(Player)versus_warte_liste.get(type).get(i);
									//Prüft ob die Arena min Team ansprüche mit den vom Spieler vereinbar sind
									if(arena.getMin_team() <= statsManager.getInt(Stats.TEAM_MIN, player)){
										//Prüft ob die Arena max Team ansprüche mit den vom Spieler vereinbar sind
										if(arena.getMax_team() >= statsManager.getInt(Stats.TEAM_MIN, player)){
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
						
					}else{
						if(status.get(type).containsKey(GameState.LobbyPhase)){
								arena=(ARENA_STATUS)status.get(type).get(GameState.LobbyPhase).get(UtilMath.r(status.get(type).get(GameState.LobbyPhase).size()));
								
								//Prüft ob genug Spieler für die Arena verfügbar sind und ob die Arena LEER ist
								if(versus_warte_liste.get(type).size()>=arena.getTeams()&&arena.getOnline()<=0){
									//Setzt die Team MAX u. MIN anzahl fest
									if(VersusType.byInt(arena.getTeams()) == VersusType._TEAMx2){
										arena.setMin_team(1);
										arena.setMax_team(1);
									}else{
										arena.setMin_team(statsManager.getInt(Stats.TEAM_MIN, versus_warte_liste.get(type).get(a)));
										arena.setMax_team(statsManager.getInt(Stats.TEAM_MAX, versus_warte_liste.get(type).get(a)));
									}
									
									//Hollt sich den ERSTEN Spieler und nimmt sein Kit!
									arena.setKit(versus_warte_liste.get(type).get(a).getName());
									if(!list.isEmpty()){
										for(int i = 0; i<list.size();i++){
											if(list.get(i)!=null&&!list.get(i).isEmpty())list.get(i).clear();
										}
										list.clear();
									}
									
									//Setzt die Team List auf
									for(Team team : type.getTeam())list.put(team, new ArrayList<Player>());
									t=0;
									boolean b = false;
									
									//Geht die Warte List nach Spielern durch
									for(Player player : versus_warte_liste.get(type)){
										//Prüft ob die MIN Team anzahl mit den Spieler einstellungen vereinbar sind
										if(arena.getMin_team() <= statsManager.getInt(Stats.TEAM_MIN, player)){
											//Prüft ob die MAX Team anzahl mit den Spieler einstellungen vereinbar sind
											if(arena.getMax_team() >= statsManager.getInt(Stats.TEAM_MIN, player)){
												//Fügt den Spieler hinzu
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
									
									if(!list.isEmpty()){
										b=true;
										//Prüft ob die Teams voll genug sind!
										for(Team team : type.getTeam()){
											if(list.get(team).size()<arena.getMin_team()){
												b=false;
												break;
											}
										}
										
										if(b){
											arena.setState(GameState.Laden);
											
											if(!status.get(type).containsKey(GameState.Laden))status.get(type).put(GameState.Laden, new ArrayList<>());
											status.get(type).get(GameState.Laden).add(arena);
											status.get(type).get(GameState.LobbyPhase).remove(arena);
											//Schickt alle Spieler informationen zu den Server
											for(Team team : list.keySet()){
												for(Player player : list.get(team)){
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
					statsManager.setString(player, "true", Stats.KIT_RANDOM);
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
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEPICPVP §7- §eVersus Lobby "+manager.getId(), "§eShop.EpicPvP.de");
		
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		load.add(ev.getPlayer());
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
								
								if(!status.get(VersusType._TEAMx2).get(GameState.LobbyPhase).isEmpty()){
									ARENA_STATUS arena = status.get(VersusType._TEAMx2).get(GameState.LobbyPhase).get( UtilMath.r(status.get(VersusType._TEAMx2).get(GameState.LobbyPhase).size()) );
								
									arena.setMin_team(1);
									arena.setMax_team(1);
									getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType.withTeamAnzahl(arena.getTeams()),arena.getArena(),((Player)ev.getEntity()).getName(),((Player)ev.getDamager()),VersusType._TEAMx2.getTeam()[0],1,1) );
									UtilBG.sendToServer(((Player)ev.getDamager()), arena.getServer(), manager.getInstance());
									getManager().getPacketManager().SendPacket(arena.getServer(), new VERSUS_SETTINGS(VersusType.withTeamAnzahl(arena.getTeams()),arena.getArena(),((Player)ev.getEntity()).getName(),((Player)ev.getEntity()),VersusType._TEAMx2.getTeam()[1],1,1) );
									UtilBG.sendToServer(((Player)ev.getEntity()), arena.getServer(), manager.getInstance());							
									arena.setState(GameState.InGame);
									arena.setKit(((Player)ev.getEntity()).getName());
									vs.remove(((Player)ev.getEntity()));
									vs.remove(((Player)ev.getDamager()));
									
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
			
			if(!status.containsKey(VersusType.withTeamAnzahl( packet.getTeams() )))status.put(VersusType.withTeamAnzahl( packet.getTeams() ), new HashMap<>());
			if(!status.get(VersusType.withTeamAnzahl( packet.getTeams() )).containsKey(packet.getState()))status.get(VersusType.withTeamAnzahl( packet.getTeams() )).put(packet.getState(), new ArrayList<>());
			
			for(VersusType type : VersusType.values()){
				if(status.containsKey(type)){
					for(GameState state : GameState.values()){
						if(status.get(type).containsKey(state)){
							for(int i = 0; i<status.get(type).get(state).size(); i++){
								if(status.get( type ).get(state).get(i).getArena().equalsIgnoreCase(packet.getArena())){
									status.get( type ).get(state).remove(i);
								}
							}
						}
					}
				}
			}
			
			status.get(VersusType.withTeamAnzahl( packet.getTeams() )).get(packet.getState()).add(packet);
		}
	}
	
	public class TeamMinMax{
		public int minteam;
		public int maxteam;
		private String kit_name;
	}

}
