package eu.epicpvp.khub.Hub.Listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import dev.wolveringer.client.connection.PacketListener;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatus;
import eu.epicpvp.kcore.Arena.ArenaManager;
import eu.epicpvp.kcore.Arena.ArenaType;
import eu.epicpvp.kcore.Arena.GameRound;
import eu.epicpvp.kcore.Arena.Rule;
import eu.epicpvp.kcore.Arena.RulePriority;
import eu.epicpvp.kcore.Arena.BestOf.BestOf;
import eu.epicpvp.kcore.Command.Admin.CommandArena;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.Command.Commands.CommandVersusDurability;
import eu.epicpvp.kcore.Command.Commands.CommandVersusMore;
import eu.epicpvp.kcore.Disguise.DisguiseType;
import eu.epicpvp.kcore.Disguise.disguises.DisguiseBase;
import eu.epicpvp.kcore.Disguise.disguises.livings.DisguisePlayer;
import eu.epicpvp.kcore.Enum.GameCage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.Inventory.InventoryBase;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryBestOf;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBack;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonUpDownVersus;
import eu.epicpvp.kcore.Inventory.Item.Buttons.SalesPackageBase;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Packets.PacketArenaStatus;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsCreateEvent;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.UpdateAsync.UpdateAsyncType;
import eu.epicpvp.kcore.UpdateAsync.UpdaterAsync;
import eu.epicpvp.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import eu.epicpvp.kcore.UserDataConfig.UserDataConfig;
import eu.epicpvp.kcore.UserDataConfig.Events.UserDataConfigLoadEvent;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.InventorySplit;
import eu.epicpvp.kcore.Util.TabTitle;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.Versus.PlayerKit;
import eu.epicpvp.kcore.Versus.PlayerKitManager;
import eu.epicpvp.khub.kHub;
import eu.epicpvp.khub.Hub.HubManager;
import lombok.Getter;

public class HubVersusListener extends kListener{

	@Getter
	private HubManager manager;
	private HashMap<String,PacketInServerStatus> server = new HashMap<>();
	private StatsManager statsManager;
	private InventoryBase base;
	private LivingEntity creature_option;
	private LivingEntity sg_wait_list;
	private LivingEntity skywars_wait_list;
	private LivingEntity bedwars_wait_list;
	private LivingEntity versus_wait_list;
	private NameTagMessage versus_wait_list_name;
	private InventoryPageBase versus_wait_list_inv;
	private ItemStack t2;
	private ItemStack t3;
	private ItemStack t4;
	private ItemStack t5;
	private ItemStack t6;
	private HashMap<Player,Player> versus_vs = new HashMap<>();
	private HashMap<Player,Player> skywars_vs = new HashMap<>();
	private HashMap<Player,Player> bedwars_vs = new HashMap<>();
	private HashMap<Player,Player> sg_vs = new HashMap<>();
	private HashMap<Player,Player> bestof_vs = new HashMap<>();
	private Scoreboard board;
	
	private BestOf bestOf;
	
	@Getter
	private PlayerKitManager kitManager;
	private Location spawn;
	private ArenaManager versus_arenaManager;
	private ArenaManager skywars_arenaManager;
	private ArenaManager bedwars_arenaManager;
	private ArenaManager sg_arenaManager;
	private InventoryPageBase case_shop;
	private int online = 0;
	private UserDataConfig userData;
	
	public HubVersusListener(final HubManager manager) {
		super(manager.getInstance(),"VersusListener"); 
		new UpdaterAsync(manager.getInstance());
		this.manager=manager;
		this.userData=new UserDataConfig(manager.getInstance());
		this.bestOf=new BestOf(manager.getInstance());
		this.board=Bukkit.getScoreboardManager().getNewScoreboard();
		//CASE SHOP
		this.case_shop = new InventoryPageBase(InventorySize._45, "§7Cage-Shop:");
		
		int slot = 10;
		for(GameCage gcase : GameCage.values()){
			this.case_shop.addButton(slot, new SalesPackageBase(new Click(){
				
				@Override
				public void onClick(Player player, ActionType action, Object obj) {
					if(player.hasPermission(gcase.getPermission().getPermissionToString())||player.hasPermission(PermissionType.ALL_PERMISSION.getPermissionToString())){
						GameCage.saveGameCase(player, gcase, manager.getMysql());
						player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "GAMECAGE_CHANGE"));
						player.closeInventory();
					}
				}
				
			}, gcase.getGround()));
			slot=InventorySplit.getSlotBorder(slot);
		}
		
		this.case_shop.fill(Material.STAINED_GLASS_PANE, (byte)7);
		this.manager.getShop().getMain().addButton(4, new ButtonOpenInventory(this.case_shop, UtilItem.Item(new ItemStack(Material.STAINED_GLASS,1,(byte)11), new String[]{"§bKlick mich um in den Shop zu gelangen."}, "§7CageShop")));
		this.manager.getShop().addPage(this.case_shop);
		//CASE SHOP ^^
		
		Bukkit.getWorld("world").setAutoSave(false);
		this.manager.getCmdHandler().register(CommandVersusDurability.class, new CommandVersusDurability());
		this.manager.getCmdHandler().register(CommandVersusMore.class, new CommandVersusMore());
		this.kitManager=new PlayerKitManager(manager.getMysql(), GameType.Versus);
		this.kitManager.setAsync(true);
		this.statsManager=new StatsManager(manager.getInstance(),UtilServer.getClient(),GameType.Versus);
		UtilTime.setTimeManager(manager.getPermissionManager());
		this.spawn=CommandLocations.getLocation("spawn");
		
		CommandArena arena = new CommandArena();
		
		this.bedwars_arenaManager=new ArenaManager(UtilServer.getClient(),statsManager,GameType.BedWars1vs1, UpdateAsyncType.SEC_2);
		this.skywars_arenaManager=new ArenaManager(UtilServer.getClient(),statsManager,GameType.SkyWars1vs1, UpdateAsyncType.SEC_2);
		this.sg_arenaManager=new ArenaManager(UtilServer.getClient(),statsManager,GameType.SurvivalGames1vs1, UpdateAsyncType.SEC_2);
		this.bestOf.addGame(bedwars_arenaManager);
		this.bestOf.addGame(skywars_arenaManager);
		this.bestOf.addGame(sg_arenaManager);
		this.base=new InventoryBase(manager.getInstance());

		this.versus_arenaManager=new ArenaManager(UtilServer.getClient(),statsManager,GameType.Versus, UpdateAsyncType.SEC_2);
		this.bestOf.addGame(versus_arenaManager);
		arena.getList().add(this.bedwars_arenaManager);
		arena.getList().add(sg_arenaManager);
		arena.getList().add(skywars_arenaManager);
		arena.getList().add(versus_arenaManager);
		getManager().getCmdHandler().register(CommandArena.class, arena);
		this.versus_arenaManager.addRule(new Rule(){

			@Override
			public boolean onRule(Player owner, Player player,ArenaType type, PacketArenaStatus arena,Object obj) {
				if(type==ArenaType._TEAMx2){
					return true;
				}else{
					if(arena.getMin_team() <= statsManager.getInt(StatsKey.TEAM_MIN, player)){
						if(arena.getMax_team() >= statsManager.getInt(StatsKey.TEAM_MAX, player)){
							return true;
						}
					}
				}
				return false;
			}
			
		}, RulePriority.HIGHEST);
		
		this.versus_arenaManager.addRule(new Rule(){

			@Override
			public boolean onRule(Player owner, Player player,ArenaType type, PacketArenaStatus arena,Object obj) {
				if(statsManager.getString(StatsKey.KIT_RANDOM, player).equalsIgnoreCase("true")){
					return true;
				}
				return false;
			}
			
		}, RulePriority.HIGHEST);
		
		if(versus_wait_list==null){
			this.versus_wait_list=manager.getPetManager().AddPetWithOutOwner("§c§lVersus", true, EntityType.ZOMBIE, CommandLocations.getLocation("Versus"));
			UtilScoreboard.addTeam(this.board, "versus", "§c").setCanSeeFriendlyInvisibles(false);
			((Zombie)this.versus_wait_list).getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			((Zombie)this.versus_wait_list).getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
			NameTagMessage m;
			m=new NameTagMessage(NameTagType.SERVER, versus_wait_list.getLocation().add(0, 2, 0), versus_wait_list.getCustomName());
			m.send();
			this.versus_wait_list.setCustomName("");
			UtilEnt.setNoAI(this.versus_wait_list, true);
			UtilEnt.setSilent(this.versus_wait_list, true);
			DisguiseBase dbase = DisguiseType.newDisguise(versus_wait_list, DisguiseType.PLAYER, new Object[]{" "});
			((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("EpicPvPMC"));
			manager.getDisguiseManager().disguise(dbase);
			this.versus_wait_list_inv=new InventoryPageBase(InventorySize._18, "§6§lVersus");
			this.t2= UtilItem.Item(new ItemStack(Material.DIAMOND_SWORD,0,(short)0), new String[]{}, "§61vs1");
			this.versus_wait_list_inv.addButton(4, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(versus_arenaManager.addPlayer(p, ArenaType._TEAMx2))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t2));
			this.t3=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 3");
			this.versus_wait_list_inv.addButton(10, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(versus_arenaManager.addPlayer(p, ArenaType._TEAMx3))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			},t3));
			this.t4=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 4");
			this.versus_wait_list_inv.addButton(12, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(versus_arenaManager.addPlayer(p, ArenaType._TEAMx4))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t4));
			this.t5=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 5");
			this.versus_wait_list_inv.addButton(14, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(versus_arenaManager.addPlayer(p, ArenaType._TEAMx5))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t5));
			this.t6=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 6");
			this.versus_wait_list_inv.addButton(16, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(versus_arenaManager.addPlayer(p, ArenaType._TEAMx6))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t6));
			this.versus_wait_list_inv.fill(Material.STAINED_GLASS_PANE, 7);
			this.base.addPage(this.versus_wait_list_inv);
		}
		
		if(skywars_wait_list==null){
			this.skywars_wait_list=manager.getPetManager().AddPetWithOutOwner("§c§lSkyWars", true, EntityType.ZOMBIE, CommandLocations.getLocation("SkyWars"));

			UtilScoreboard.addTeam(this.board, "skywars", "§b").setCanSeeFriendlyInvisibles(false);
			((Zombie)this.skywars_wait_list).getEquipment().setItemInHand(new ItemStack(Material.IRON_AXE));
			NameTagMessage m;
			m=new NameTagMessage(NameTagType.SERVER, skywars_wait_list.getLocation().add(0, 2, 0), skywars_wait_list.getCustomName());
			m.send();
			
			this.skywars_wait_list.setCustomName("");
			UtilEnt.setSilent(this.skywars_wait_list, true);
			UtilEnt.setNoAI(this.skywars_wait_list, true);
			DisguiseBase dbase = DisguiseType.newDisguise(skywars_wait_list, DisguiseType.PLAYER, new Object[]{" "});
			((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("julle139"));
			manager.getDisguiseManager().disguise(dbase);
		}
		
		if(sg_wait_list==null){
			this.sg_wait_list=manager.getPetManager().AddPetWithOutOwner("§c§lSurvivalGames", true, EntityType.ZOMBIE, CommandLocations.getLocation("SurvivalGames"));

			UtilScoreboard.addTeam(this.board, "survivalgames", "§a").setCanSeeFriendlyInvisibles(false);
			((Zombie)this.sg_wait_list).getEquipment().setItemInHand(new ItemStack(Material.BOW));
			NameTagMessage m;
			m=new NameTagMessage(NameTagType.SERVER, sg_wait_list.getLocation().add(0, 2, 0), sg_wait_list.getCustomName());
			m.send();
			this.sg_wait_list.setCustomName("");
			UtilEnt.setSilent(this.sg_wait_list, true);
			UtilEnt.setNoAI(this.sg_wait_list, true);
			DisguiseBase dbase = DisguiseType.newDisguise(sg_wait_list, DisguiseType.PLAYER, new Object[]{" "});
			((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("akmund47"));
			manager.getDisguiseManager().disguise(dbase);
		}
		
		if(bedwars_wait_list==null){
			this.bedwars_wait_list=manager.getPetManager().AddPetWithOutOwner("§c§lBedWars", true, EntityType.ZOMBIE, CommandLocations.getLocation("BedWars"));

			UtilScoreboard.addTeam(this.board, "bedwars", "§d").setCanSeeFriendlyInvisibles(false);
			((Zombie)this.bedwars_wait_list).getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
			NameTagMessage m;
			m=new NameTagMessage(NameTagType.SERVER, bedwars_wait_list.getLocation().add(0, 2, 0), bedwars_wait_list.getCustomName());
			m.send();
			this.bedwars_wait_list.setCustomName("");
			UtilEnt.setSilent(this.bedwars_wait_list, true);
			UtilEnt.setNoAI(this.bedwars_wait_list, true);
			DisguiseBase dbase = DisguiseType.newDisguise(bedwars_wait_list, DisguiseType.PLAYER, new Object[]{" "});
			((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("kablion"));
			manager.getDisguiseManager().disguise(dbase);
		}
		
		UtilServer.getClient().getHandle().getHandlerBoss().addListener(new PacketListener() {
			
			@Override
			public void handle(Packet packet) {
				if(packet instanceof PacketInServerStatus){
					PacketInServerStatus in = (PacketInServerStatus)packet;
					
					if (in.getTyp() != GameType.Versus&&in.getTyp() != GameType.SkyWars1vs1&&in.getTyp() != GameType.BedWars1vs1&&in.getTyp() != GameType.SurvivalGames1vs1) {
				      } else if(server.containsKey(in.getServerId())){
				    	  server.remove(in.getServerId());
				    	  server.put(in.getServerId(),in);
				      }
				}
			}
		});
		
		loadSettingNPC();
	}
	
	public void loadSettingNPC(){
		this.creature_option=manager.getPetManager().AddPetWithOutOwner("§b§lEinstellungen", true, EntityType.ZOMBIE, CommandLocations.getLocation("Optionen"));
		NameTagMessage m=new NameTagMessage(NameTagType.SERVER, creature_option.getLocation().add(0, 2, 0), creature_option.getCustomName());
		m.send();
		creature_option.setCustomName("");
		UtilEnt.setSilent(this.creature_option, true);
		UtilEnt.setNoAI(this.creature_option, true);
		
		DisguiseBase dbase = DisguiseType.newDisguise(creature_option, DisguiseType.PLAYER, new Object[]{"  "});
		((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("_rorschach"));
		manager.getDisguiseManager().disguise(dbase);
		
		InventoryPageBase choose_game = new InventoryPageBase(InventorySize._9, "Versus Games:");
		this.base.setMain(choose_game);
		
		//VERSUS -
		InventoryPageBase versus_inv = new InventoryCopy(InventorySize._27.getSize(), "§bVersus");
		
		this.base.addPage(versus_inv);
		this.base.getMain().addButton(0, new ButtonBase(new Click() {
			
			@Override
			public void onClick(Player p, ActionType a, Object o) {
				((InventoryCopy)versus_inv).open(p, base);
			}
		},UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§aVersus 1vs1")));
		
		((InventoryCopy)versus_inv).setCreate_new_inv(true);
		versus_inv.addButton(new ButtonUpDownVersus(versus_inv, UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(short)3), "§bMin. Team"), 10, statsManager, StatsKey.TEAM_MIN,1,3));
		versus_inv.addButton(new ButtonUpDownVersus(versus_inv, UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(short)3), "§6Max. Team"), 12, statsManager, StatsKey.TEAM_MAX,1,3));
		versus_inv.addButton(14, new ButtonCopy(new Click(){

			@Override
			public void onClick(Player p, ActionType a, Object o) {
				if(o instanceof InventoryPageBase){
					if(statsManager.getString(StatsKey.KIT_RANDOM, p).equalsIgnoreCase("true")){
						((InventoryPageBase)o).setItem(14, UtilItem.RenameItem(new ItemStack(Material.EMERALD), "§7Kit zufall: §aan"));
						statsManager.setString(p, "true", StatsKey.KIT_RANDOM);
					}else{
						((InventoryPageBase)o).setItem(14, UtilItem.RenameItem(new ItemStack(Material.REDSTONE), "§7Kit zufall: §caus"));
						statsManager.setString(p, "false", StatsKey.KIT_RANDOM);
					}
				}
			}
			
		}, new Click(){

			@Override
			public void onClick(Player p, ActionType a, Object o) {
				if(!statsManager.getString(StatsKey.KIT_RANDOM, p).equalsIgnoreCase("true")){
					p.getOpenInventory().setItem(14, UtilItem.RenameItem(new ItemStack(Material.EMERALD), "§7Kit zufall: §aan"));
					statsManager.setString(p, "true", StatsKey.KIT_RANDOM);
				}else{
					p.getOpenInventory().setItem(14, UtilItem.RenameItem(new ItemStack(Material.REDSTONE), "§7Kit zufall: §caus"));
					statsManager.setString(p, "false", StatsKey.KIT_RANDOM);
				}
				
			}
			
		}, new ItemStack(Material.BEDROCK)));
		versus_inv.addButton(0, new ButtonBack(this.base.getMain(), UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZur§ck / Back")));
		versus_inv.setItem(16, UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "§7Kit Slots §4§lCOMING SOON!"));
		versus_inv.fill(Material.STAINED_GLASS_PANE, 7);
		//VERSUS - 
		
		//SG -
		this.base.getMain().addButton(2, new ButtonBase(new Click() {
			
			@Override
			public void onClick(Player p, ActionType a, Object o) {
//				((InventoryCopy)versus_inv).open(p, base);
			}
		},UtilItem.RenameItem(new ItemStack(Material.BOW), "§aSurvivalGames 1vs1 §4§lCOMING SOON")));
		//SG -
		
		//BW -
		this.base.getMain().addButton(4, new ButtonBase(new Click() {
			
			@Override
			public void onClick(Player p, ActionType a, Object o) {
//				((InventoryCopy)versus_inv).open(p, base);
			}
		},UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§aBedWars 1vs1 §4§lCOMING SOON")));
		//BW -
		
		//SW -
		this.base.getMain().addButton(6, new ButtonBase(new Click() {
			
			@Override
			public void onClick(Player p, ActionType a, Object o) {
//				((InventoryCopy)versus_inv).open(p, base);
			}
		},UtilItem.RenameItem(new ItemStack(Material.IRON_AXE), "§aSkyWars 1vs1 §4§lCOMING SOON")));
		//SW -
		
		//bestof -
		InventoryBestOf bestof_inv = new InventoryBestOf(InventorySize._54.getSize(), "§cBestOf Einstellungen", this.base.getMain());
		bestof_inv.setCreate_new_inv(true);
		this.base.addPage(bestof_inv);
		
		this.base.getMain().addButton(8, new ButtonBase(new Click() {
			
			@Override
			public void onClick(Player p, ActionType a, Object o) {
				((InventoryCopy)bestof_inv).open(p, base);
			}
		},UtilItem.RenameItem(new ItemStack(Material.FISHING_ROD), "§aBestOf")));
		//bestof -
		
		this.base.getMain().fill(Material.STAINED_GLASS_PANE);
	}
	
	@EventHandler
	public void save(PlayerQuitEvent ev){
		statsManager.SaveAllPlayerData(ev.getPlayer());
	}
	
	@EventHandler
	public void Inventory(InventoryMoveItemEvent  ev){
		if(ev.getSource().getHolder() instanceof Player){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void bucket(PlayerBucketEmptyEvent ev){
		if(!ev.getPlayer().isOp()){
			ev.setCancelled(true);
		}
	}
	
	PlayerKit k;
	@EventHandler
	public void gamemodeCHANGE(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			for(Player player : UtilServer.getPlayers()){
				if(UtilWorldGuard.RegionFlag(player, DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE)){
					if(player.getGameMode()!=GameMode.CREATIVE){
						player.setGameMode(GameMode.CREATIVE);
						player.getInventory().clear();
						k = getKitManager().getKit(UtilPlayer.getRealUUID(player),statsManager.getInt(StatsKey.KIT_ID, player));

						if(k!=null){
							player.getInventory().setArmorContents(k.armor_content);
							player.getInventory().setContents(k.content);
							player.updateInventory();
						}else{
							getKitManager().addKit(player.getName(),player.getUniqueId(), player.getInventory(), statsManager.getInt(StatsKey.KIT_ID, player));
						}
					}
				}else{
					if(player.getGameMode() != GameMode.ADVENTURE){
						getKitManager().updateKit(UtilPlayer.getRealUUID(player), statsManager.getInt(StatsKey.KIT_ID, player), player.getInventory());
						player.setGameMode(GameMode.ADVENTURE);

						player.setExp(0);
						player.getInventory().clear();
						for(PotionEffect t : player.getActivePotionEffects())player.removePotionEffect(t.getType());;
						player.getInventory().setArmorContents(null);
						player.getInventory().setItem(1,UtilItem.RenameItem(new ItemStack(Material.FISHING_ROD), "§aBestOf"));
						player.getInventory().setItem(0, UtilItem.RenameItem(new ItemStack(Material.CHEST), Language.getText(player, "HUB_ITEM_CHEST")));
						player.getInventory().setItem(5,UtilItem.RenameItem(new ItemStack(Material.BOW), "§aSurvivalGames 1vs1"));
						player.getInventory().setItem(6,UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§aBedWars 1vs1"));
						player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§aVersus 1vs1"));
						player.getInventory().setItem(7,UtilItem.RenameItem(new ItemStack(Material.IRON_AXE), "§aSkyWars 1vs1"));
						player.updateInventory();
					}
				}
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
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==Material.CHEST){
				ev.getPlayer().openInventory(getManager().getShop().getMain());
			}
		}
	}
	
//	@EventHandler
//	public void PacketListenerSend(PacketListenerSendEvent ev){
//		System.err.println("S: "+ev.getPacket().toString());
//	}
//
//	@EventHandler
//	public void PacketListenerReceive(PacketListenerReceiveEvent ev){
//		System.err.println("R: "+ev.getPacket().toString());
//	}
//	
//	@EventHandler
//	public void ping(PacketListenerSendEvent ev){
//		if(ev.getPacket() instanceof PacketStatusOutServerInfo){
//			kPacketStatusOutServerInfo info = new kPacketStatusOutServerInfo(ev.getPacket());
//			info.getServerPing().setPlayerSample(new ServerPingPlayerSample(this.online, this.online));
//			ev.setPacket(info.getPacket());
//			info=null;
//		}
//	}
	
	int i=0;
	@EventHandler
	public void name(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_3){
			i=0;
			for(PacketInServerStatus a : this.server.values())i=i+a.getPlayers();
			
			this.online=UtilServer.getPlayers().size()+i;
			if(this.versus_wait_list_name!=null)this.versus_wait_list_name.remove();
			this.versus_wait_list_name = new NameTagMessage(NameTagType.PACKET, CommandLocations.getLocation("online"),new String[]{"§c§lEpicPvP Versus Server","§aOnline §e§l"+this.online});
			this.versus_wait_list_name.send();
			for(Player player : UtilServer.getPlayers())player.setLevel(this.online);
		}
	}
	
	@EventHandler
	public void configCreate(UserDataConfigLoadEvent ev){
		if(!ev.getConfig().contains("BestOf.Rounds"))ev.getConfig().set("BestOf.Rounds", 6);
		
		if(!ev.getConfig().contains("BestOf.Round")){
			for(int i = 0; i<10; i++){
				ev.getConfig().set("BestOf.Round."+i, "Versus");
			}
		}
	}
	
	@EventHandler
	public void Inv(UpdateAsyncEvent ev){
		if(ev.getType()==UpdateAsyncType.SEC){
			try{
				if(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx2).size()==0){
					this.versus_wait_list_inv.setItem(4, t2);
				}else{
					if(this.versus_arenaManager.getWait_list().containsKey(ArenaType._TEAMx2)){
						this.versus_wait_list_inv.getItem(4).setAmount(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx2).size());
					}
				}
				if(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx3).size()==0){
					this.versus_wait_list_inv.setItem(10, t3);
				}else{
					if(this.versus_arenaManager.getWait_list().containsKey(ArenaType._TEAMx3)){
						this.versus_wait_list_inv.getItem(10).setAmount(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx3).size());
					}
				}
				if(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx4).size()==0){
					this.versus_wait_list_inv.setItem(12, t4);
				}else{
					if(this.versus_arenaManager.getWait_list().containsKey(ArenaType._TEAMx4)){
						this.versus_wait_list_inv.getItem(12).setAmount(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx4).size());
					}
				}
				if(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx5).size()==0){
					this.versus_wait_list_inv.setItem(14, t5);
				}else{
					if(this.versus_arenaManager.getWait_list().containsKey(ArenaType._TEAMx5)){
						this.versus_wait_list_inv.getItem(14).setAmount(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx5).size());
					}
				}
				if(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx6).size()==0){
					this.versus_wait_list_inv.setItem(16, t6);
				}else{
					if(this.versus_arenaManager.getWait_list().containsKey(ArenaType._TEAMx6)){
						this.versus_wait_list_inv.getItem(16).setAmount(this.versus_arenaManager.getWait_list().get(ArenaType._TEAMx6).size());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		statsManager.SaveAllPlayerData(ev.getPlayer());
		versus_vs.remove(ev.getPlayer());
		skywars_vs.remove(ev.getPlayer());
		bedwars_vs.remove(ev.getPlayer());
		sg_vs.remove(ev.getPlayer());
	}
	
	@EventHandler
	public void create(PlayerStatsCreateEvent ev){
		if(UtilPlayer.isOnline(ev.getPlayername())){
			Player player = Bukkit.getPlayer(ev.getPlayername());
			statsManager.setString(player, "true", StatsKey.KIT_RANDOM);
			statsManager.setInt(player, 1, StatsKey.KIT_ID);
			statsManager.setInt(player, 1, StatsKey.TEAM_MIN);
			statsManager.setInt(player, 3, StatsKey.TEAM_MAX);
			statsManager.setInt(player, 200, StatsKey.ELO);
			statsManager.SaveAllPlayerData(player);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void load(PlayerJoinEvent ev){
		ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "WHEREIS_TEXT","Versus Hub"));
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEPICPVP §7- §e"+kHub.hubType+" "+kHub.hubID, "§eShop.EpicPvP.de");
		statsManager.loadPlayer(ev.getPlayer());
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		ev.getPlayer().setExp(0);
		ev.getPlayer().teleport(spawn);
		ev.getPlayer().getInventory().setItem(1,UtilItem.RenameItem(new ItemStack(Material.FISHING_ROD), "§aBestOf"));
		ev.getPlayer().getInventory().setItem(5,UtilItem.RenameItem(new ItemStack(Material.BOW), "§aSurvivalGames 1vs1"));
		ev.getPlayer().getInventory().setItem(6,UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§aBedWars 1vs1"));
		ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§aVersus 1vs1"));
		ev.getPlayer().getInventory().setItem(7,UtilItem.RenameItem(new ItemStack(Material.IRON_AXE), "§aSkyWars 1vs1"));
	}
	
	private String s;
	@EventHandler
	public void Entity(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Player && ((Player)ev.getEntity()).getGameMode() == GameMode.ADVENTURE && ((Player)ev.getDamager()).getGameMode() == GameMode.ADVENTURE){
			if(((Player)ev.getDamager()).getItemInHand().getType()==Material.DIAMOND_SWORD){
				((Player)ev.getDamager()).getItemInHand().setDurability((short) 0);
					s=UtilTime.getTimeManager().check("VERSUS_SWORD", ((Player)ev.getDamager()));
					if(s!=null){
						((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "USE_BEFEHL_TIME",s));
					}else{
						UtilTime.getTimeManager().add("VERSUS_SWORD", ((Player)ev.getDamager()), TimeSpan.SECOND*10);
						if(versus_vs.containsKey( ((Player)ev.getEntity()) )){
							if(versus_vs.get(((Player)ev.getEntity())).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName())){
								//SEND
								this.versus_arenaManager.delRound(((Player)ev.getEntity()),true);
								this.versus_arenaManager.delRound(((Player)ev.getDamager()),true);
								this.versus_arenaManager.addRound(new GameRound(((Player)ev.getEntity()).getUniqueId(), new UUID[]{((Player)ev.getEntity()).getUniqueId(),((Player)ev.getDamager()).getUniqueId()}, ArenaType._TEAMx2));
								((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()),"PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_REQUEST"));
								((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()),"PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_REQUEST"));
								versus_vs.remove(((Player)ev.getEntity()));
								versus_vs.remove(((Player)ev.getDamager()));
								return;
							}
						}
						
						versus_vs.remove(((Player)ev.getDamager()));
						versus_vs.put(((Player)ev.getDamager()), ((Player)ev.getEntity()));
						((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_FROM_QUESTION", ((Player)ev.getDamager()).getName()));
						((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_QUESTION",((Player)ev.getEntity()).getName()));
					}
			}else if(((Player)ev.getDamager()).getItemInHand().getType()==Material.IRON_AXE){
				((Player)ev.getDamager()).getItemInHand().setDurability((short) 0);
				s=UtilTime.getTimeManager().check("SKYWARS_AXE", ((Player)ev.getDamager()));
				if(s!=null){
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "USE_BEFEHL_TIME",s));
				}else{
					UtilTime.getTimeManager().add("SKYWARS_AXE", ((Player)ev.getDamager()), TimeSpan.SECOND*10);
					if(skywars_vs.containsKey( ((Player)ev.getEntity()) )){
						if(skywars_vs.get(((Player)ev.getEntity())).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName())){
							//SEND
							this.skywars_arenaManager.delRound(((Player)ev.getEntity()),true);
							this.skywars_arenaManager.delRound(((Player)ev.getDamager()),true);
							this.skywars_arenaManager.addRound(new GameRound(((Player)ev.getEntity()).getUniqueId(), new UUID[]{((Player)ev.getEntity()).getUniqueId(),((Player)ev.getDamager()).getUniqueId()}, ArenaType._TEAMx2));
							((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()),"PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_SKYWARS_1VS1_REQUEST"));
							((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()),"PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_SKYWARS_1VS1_REQUEST"));
							skywars_vs.remove(((Player)ev.getEntity()));
							skywars_vs.remove(((Player)ev.getDamager()));
							return;
						}
					}
					
					skywars_vs.remove(((Player)ev.getDamager()));
					skywars_vs.put(((Player)ev.getDamager()), ((Player)ev.getEntity()));
					((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_FROM_QUESTION", ((Player)ev.getDamager()).getName()));
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_QUESTION",((Player)ev.getEntity()).getName()));
				}
			}else if(((Player)ev.getDamager()).getItemInHand().getType()==Material.GOLD_SWORD){
				((Player)ev.getDamager()).getItemInHand().setDurability((short) 0);
				s=UtilTime.getTimeManager().check("BEDWARS_AXE", ((Player)ev.getDamager()));
				if(s!=null){
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "USE_BEFEHL_TIME",s));
				}else{
					UtilTime.getTimeManager().add("BEDWARS_AXE", ((Player)ev.getDamager()), TimeSpan.SECOND*10);
					if(bedwars_vs.containsKey( ((Player)ev.getEntity()) )){
						if(bedwars_vs.get(((Player)ev.getEntity())).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName())){
							//SEND
							this.bedwars_arenaManager.delRound(((Player)ev.getEntity()),true);
							this.bedwars_arenaManager.delRound(((Player)ev.getDamager()),true);
							this.bedwars_arenaManager.addRound(new GameRound(((Player)ev.getEntity()).getUniqueId(), new UUID[]{((Player)ev.getEntity()).getUniqueId(),((Player)ev.getDamager()).getUniqueId()}, ArenaType._TEAMx2));
							((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()),"PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_BEDWARS_1VS1_REQUEST"));
							((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()),"PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_BEDWARS_1VS1_REQUEST"));
							bedwars_vs.remove(((Player)ev.getEntity()));
							bedwars_vs.remove(((Player)ev.getDamager()));
							return;
						}
					}
					
					bedwars_vs.remove(((Player)ev.getDamager()));
					bedwars_vs.put(((Player)ev.getDamager()), ((Player)ev.getEntity()));
					((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_FROM_QUESTION", ((Player)ev.getDamager()).getName()));
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_QUESTION",((Player)ev.getEntity()).getName()));
				}
			}else if(((Player)ev.getDamager()).getItemInHand().getType()==Material.BOW){
				((Player)ev.getDamager()).getItemInHand().setDurability((short) 0);
				s=UtilTime.getTimeManager().check("SG_BOW", ((Player)ev.getDamager()));
				if(s!=null){
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "USE_BEFEHL_TIME",s));
				}else{
					UtilTime.getTimeManager().add("SG_BOW", ((Player)ev.getDamager()), TimeSpan.SECOND*10);
					if(sg_vs.containsKey( ((Player)ev.getEntity()) )){
						if(sg_vs.get(((Player)ev.getEntity())).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName())){
							//SEND
							this.sg_arenaManager.delRound(((Player)ev.getEntity()),true);
							this.sg_arenaManager.delRound(((Player)ev.getDamager()),true);
							this.sg_arenaManager.addRound(new GameRound(((Player)ev.getEntity()).getUniqueId(), new UUID[]{((Player)ev.getEntity()).getUniqueId(),((Player)ev.getDamager()).getUniqueId()}, ArenaType._TEAMx2));
							((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()),"PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_SG_1VS1_REQUEST"));
							((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()),"PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_SG_1VS1_REQUEST"));
							sg_vs.remove(((Player)ev.getEntity()));
							sg_vs.remove(((Player)ev.getDamager()));
							return;
						}
					}
					
					sg_vs.remove(((Player)ev.getDamager()));
					sg_vs.put(((Player)ev.getDamager()), ((Player)ev.getEntity()));
					((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_FROM_QUESTION", ((Player)ev.getDamager()).getName()));
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_QUESTION",((Player)ev.getEntity()).getName()));
				}
			}else if(((Player)ev.getDamager()).getItemInHand().getType()==Material.FISHING_ROD){
				((Player)ev.getDamager()).getItemInHand().setDurability((short) 0);
				s=UtilTime.getTimeManager().check("bestof_FISH", ((Player)ev.getDamager()));
				if(s!=null){
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "USE_BEFEHL_TIME",s));
				}else{
					UtilTime.getTimeManager().add("bestof_FISH", ((Player)ev.getDamager()), TimeSpan.SECOND*10);
					if(bestof_vs.containsKey( ((Player)ev.getEntity()) )){
						if(bestof_vs.get(((Player)ev.getEntity())).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName())){
							bestof_vs.remove(((Player)ev.getEntity()));
							bestof_vs.remove(((Player)ev.getDamager()));
							bestOf.createBestOf(((Player)ev.getEntity()), ((Player)ev.getDamager()));
							return;
						}
					}
					
					bestof_vs.remove(((Player)ev.getDamager()));
					bestof_vs.put(((Player)ev.getDamager()), ((Player)ev.getEntity()));
					((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX")+Language.getText(((Player)ev.getEntity()), "HUB_VERSUS_1VS1_FROM_QUESTION", ((Player)ev.getDamager()).getName()));
					((Player)ev.getDamager()).sendMessage(Language.getText(((Player)ev.getDamager()), "PREFIX")+Language.getText(((Player)ev.getDamager()), "HUB_VERSUS_1VS1_QUESTION",((Player)ev.getEntity()).getName()));
				}
			}
		}
	}

	 Player fishplayer;
	 Player caught;
	 @EventHandler
	    public void onPlayerHitFishingRodEventThingyName (PlayerFishEvent event) {
		 	if(event.getState() == State.CAUGHT_ENTITY){
		        if (event.getCaught() instanceof Player) {
			        fishplayer = event.getPlayer();
		            if (fishplayer.getItemInHand().getType() == Material.FISHING_ROD) {
		            	caught = (Player) event.getCaught();
		            	fishplayer.getItemInHand().setDurability((short) 0);
						s=UtilTime.getTimeManager().check("bestof_FISH", fishplayer);
						if(s!=null){
							fishplayer.sendMessage(Language.getText(fishplayer, "PREFIX")+Language.getText(fishplayer, "USE_BEFEHL_TIME",s));
						}else{
							UtilTime.getTimeManager().add("bestof_FISH", fishplayer, TimeSpan.SECOND*10);
							if(bestof_vs.containsKey( caught )){
								if(bestof_vs.get(caught).getName().equalsIgnoreCase(fishplayer.getName())){
									bestof_vs.remove(caught);
									bestof_vs.remove(fishplayer);
									bestOf.createBestOf(caught, fishplayer);
									return;
								}
							}
							
							bestof_vs.remove(fishplayer);
							bestof_vs.put(fishplayer, caught);
							caught.sendMessage(Language.getText(caught, "PREFIX")+Language.getText(caught, "HUB_VERSUS_1VS1_FROM_QUESTION", fishplayer.getName()));
							fishplayer.sendMessage(Language.getText(fishplayer, "PREFIX")+Language.getText(fishplayer, "HUB_VERSUS_1VS1_QUESTION",caught.getName()));
						}
		            }
		        }
		 	}
	    }
	
	@EventHandler
	public void Interact(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked().getEntityId()==this.creature_option.getEntityId()){
			ev.setCancelled(true);
			ev.getPlayer().openInventory(this.base.getMain());
		}else if(ev.getRightClicked().getEntityId()==this.versus_wait_list.getEntityId()){
			ev.setCancelled(true);
			ev.getPlayer().openInventory(this.versus_wait_list_inv);
		}else if(ev.getRightClicked().getEntityId()==this.skywars_wait_list.getEntityId()){
			ev.setCancelled(true);
			if(skywars_arenaManager.addPlayer(ev.getPlayer(), ArenaType._TEAMx2)){
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_ADDED"));
			}else{
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_REMOVE"));
			}
		}else if(ev.getRightClicked().getEntityId()==this.bedwars_wait_list.getEntityId()){
			ev.setCancelled(true);
			if(bedwars_arenaManager.addPlayer(ev.getPlayer(), ArenaType._TEAMx2)){
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_ADDED"));
			}else{
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_REMOVE"));
			}
		}else if(ev.getRightClicked().getEntityId()==this.sg_wait_list.getEntityId()){
			ev.setCancelled(true);
			if(sg_arenaManager.addPlayer(ev.getPlayer(), ArenaType._TEAMx2)){
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_ADDED"));
			}else{
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VERSUS_REMOVE"));
			}
		}
	}
}
