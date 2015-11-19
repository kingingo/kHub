package me.kingingo.khub.Hub.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Arena.ArenaManager;
import me.kingingo.kcore.Arena.ArenaType;
import me.kingingo.kcore.Arena.GameRound;
import me.kingingo.kcore.Arena.Rule;
import me.kingingo.kcore.Arena.RulePriority;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Command.Commands.CommandVersusDurability;
import me.kingingo.kcore.Command.Commands.CommandVersusMore;
import me.kingingo.kcore.Disguise.DisguiseType;
import me.kingingo.kcore.Disguise.disguises.DisguiseBase;
import me.kingingo.kcore.Disguise.disguises.livings.DisguisePlayer;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Hologram.nametags.NameTagType;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Inventory.InventoryCopy;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonBase;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonCopy;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonUpDownVersus;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Packets.ARENA_STATUS;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.UpdateAsync.UpdateAsyncType;
import me.kingingo.kcore.UpdateAsync.UpdaterAsync;
import me.kingingo.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDebug;
import me.kingingo.kcore.Util.UtilEnt;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Versus.PlayerKit;
import me.kingingo.kcore.Versus.PlayerKitManager;
import me.kingingo.khub.kHub;
import me.kingingo.khub.Hub.HubManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
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
	private ArrayList<SERVER_STATUS> server = new ArrayList<>();
	private StatsManager statsManager;
	private InventoryBase base;
	private Creature creature_option;
	private Creature wait_list;
	private NameTagMessage wait_list_name;
	private InventoryPageBase wait_list_inv;
	private ItemStack t2;
	private ItemStack t3;
	private ItemStack t4;
	private ItemStack t5;
	private ItemStack t6;
	private HashMap<Player,Player> vs = new HashMap<>();
	@Getter
	private PlayerKitManager kitManager;
	private ArrayList<Player> creative = new ArrayList<>();
	private Location spawn;
	private ArenaManager arenaManager;
	
	public HubVersusListener(final HubManager manager) {
		super(manager.getInstance(),"VersusListener");
		new UpdaterAsync(manager.getInstance());
		this.manager=manager;
		Bukkit.getWorld("world").setAutoSave(false);
		this.manager.getCmdHandler().register(CommandVersusDurability.class, new CommandVersusDurability());
		this.manager.getCmdHandler().register(CommandVersusMore.class, new CommandVersusMore());
		this.kitManager=new PlayerKitManager(manager.getMysql(), GameType.Versus);
		this.statsManager=new StatsManager(manager.getInstance(), manager.getMysql(), GameType.Versus);
		UtilTime.setTimeManager(manager.getPermissionManager());
		this.spawn=CommandLocations.getLocation("spawn");
		this.arenaManager=new ArenaManager(manager.getPacketManager(),statsManager, UpdateAsyncType.SLOW);
		this.base=new InventoryBase(manager.getInstance());
		
		this.arenaManager.addRule(new Rule(){

			@Override
			public boolean onRule(Player owner, Player player,ArenaType type, ARENA_STATUS arena,Object obj) {
				if(type==ArenaType._TEAMx2){
					return true;
				}else{
					if(arena.getMin_team() <= statsManager.getInt(Stats.TEAM_MIN, player)){
						if(arena.getMax_team() >= statsManager.getInt(Stats.TEAM_MAX, player)){
							return true;
						}
					}
				}
				if(UtilDebug.isDebug())UtilDebug.debug("UpdateAsyncEvent", new String[]{"Rule: Min Max","FALSE",arena.getMin_team() +"<="+ statsManager.getInt(Stats.TEAM_MIN, player),arena.getMax_team() +">="+ statsManager.getInt(Stats.TEAM_MAX, player)});
				return false;
			}
			
		}, RulePriority.HIGHEST);
		
		this.arenaManager.addRule(new Rule(){

			@Override
			public boolean onRule(Player owner, Player player,ArenaType type, ARENA_STATUS arena,Object obj) {
				if(statsManager.getString(Stats.KIT_RANDOM, player).equalsIgnoreCase("true")){
					return true;
				}
				if(UtilDebug.isDebug())UtilDebug.debug("UpdateAsyncEvent", new String[]{"Rule: RANDOM","FALSE"});
				return false;
			}
			
		}, RulePriority.HIGHEST);
		
		this.base.setMain(new InventoryCopy(InventorySize._27.getSize(), "§bVersus"));
		((InventoryCopy)this.base.getMain()).setCreate_new_inv(true);
		this.base.getMain().addButton(new ButtonUpDownVersus(this.base.getMain(), UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(short)3), "§bMin. Team"), 10, statsManager, Stats.TEAM_MIN,1,3));
		this.base.getMain().addButton(new ButtonUpDownVersus(this.base.getMain(), UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(short)3), "§6Max. Team"), 12, statsManager, Stats.TEAM_MAX,1,3));
		this.base.getMain().addButton(14, new ButtonCopy(new Click(){

			@Override
			public void onClick(Player p, ActionType a, Object o) {
				if(o instanceof InventoryPageBase){
					if(statsManager.getString(Stats.KIT_RANDOM, p).equalsIgnoreCase("true")){
						((InventoryPageBase)o).setItem(14, UtilItem.RenameItem(new ItemStack(Material.EMERALD), "§7Kit zufall: §aan"));
						statsManager.setString(p, "true", Stats.KIT_RANDOM);
					}else{
						((InventoryPageBase)o).setItem(14, UtilItem.RenameItem(new ItemStack(Material.REDSTONE), "§7Kit zufall: §caus"));
						statsManager.setString(p, "false", Stats.KIT_RANDOM);
					}
				}
			}
			
		}, new Click(){

			@Override
			public void onClick(Player p, ActionType a, Object o) {
				if(!statsManager.getString(Stats.KIT_RANDOM, p).equalsIgnoreCase("true")){
					p.getOpenInventory().setItem(14, UtilItem.RenameItem(new ItemStack(Material.EMERALD), "§7Kit zufall: §aan"));
					statsManager.setString(p, "true", Stats.KIT_RANDOM);
				}else{
					p.getOpenInventory().setItem(14, UtilItem.RenameItem(new ItemStack(Material.REDSTONE), "§7Kit zufall: §caus"));
					statsManager.setString(p, "false", Stats.KIT_RANDOM);
				}
				
			}
			
		}, new ItemStack(Material.BEDROCK)));
		this.base.getMain().setItem(16, UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "§7Kit Slots §4§lCOMING SOON!"));
		this.base.getMain().fill(Material.STAINED_GLASS_PANE, 7);
		
		if(wait_list==null){
			this.wait_list=manager.getPetManager().AddPetWithOutOwner("§c§lVersus", true, EntityType.ZOMBIE, CommandLocations.getLocation("Versus"));
			
			((Zombie)this.wait_list).getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			((Zombie)this.wait_list).getEquipment().setItemInHand(new ItemStack(Material.BOW));
			this.wait_list.setCustomName("");
			UtilEnt.setNoAI(this.wait_list, true);
			DisguiseBase dbase = DisguiseType.newDisguise(wait_list, DisguiseType.PLAYER, new Object[]{" "});
			((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("EpicPvPMC"));
			manager.getDisguiseManager().disguise(dbase);
			this.wait_list_inv=new InventoryPageBase(InventorySize._18, "§6§lVersus");
			this.t2= UtilItem.Item(new ItemStack(Material.DIAMOND_SWORD,0,(short)0), new String[]{}, "§61vs1");
			this.wait_list_inv.addButton(4, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(arenaManager.addPlayer(p, ArenaType._TEAMx2))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t2));
			this.t3=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 3");
			this.wait_list_inv.addButton(10, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(arenaManager.addPlayer(p, ArenaType._TEAMx3))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			},t3));
			this.t4=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 4");
			this.wait_list_inv.addButton(12, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(arenaManager.addPlayer(p, ArenaType._TEAMx4))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t4));
			this.t5=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 5");
			this.wait_list_inv.addButton(14, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(arenaManager.addPlayer(p, ArenaType._TEAMx5))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t5));
			this.t6=  UtilItem.Item(new ItemStack(Material.IRON_SWORD,0,(short)0), new String[]{}, "§cFFA for 6");
			this.wait_list_inv.addButton(16, new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					if(arenaManager.addPlayer(p, ArenaType._TEAMx6))
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "VERSUS_ADDED"));
				}
				
			}, t6));
			this.wait_list_inv.fill(Material.STAINED_GLASS_PANE, 7);
			this.base.addPage(this.wait_list_inv);
			
			this.creature_option=manager.getPetManager().AddPetWithOutOwner("§b§lEinstellungen", true, EntityType.ZOMBIE, CommandLocations.getLocation("Optionen"));
			NameTagMessage m;
			m=new NameTagMessage(NameTagType.SERVER, creature_option.getLocation().add(0, 2, 0), creature_option.getCustomName());
			m.send();
			creature_option.setCustomName("");
			UtilEnt.setNoAI(creature_option, true);
			
			dbase = DisguiseType.newDisguise(creature_option, DisguiseType.PLAYER, new Object[]{"  "});
			((DisguisePlayer)dbase).loadSkin(manager.getInstance(),UtilPlayer.getOnlineUUID("_rorschach"));
			manager.getDisguiseManager().disguise(dbase);
		}
	}
	
	@EventHandler
	public void save(PlayerQuitEvent ev){
		statsManager.SaveAllPlayerData(ev.getPlayer());
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
			ev.getPlayer().setVelocity(ev.getPlayer().getLocation().getDirection().multiply(2D).setY(0.7));
		}else if(ev.getNewGameMode() != GameMode.CREATIVE){
			if(creative.contains(ev.getPlayer())){
				getKitManager().updateKit(UtilPlayer.getRealUUID(ev.getPlayer()), statsManager.getInt(Stats.KIT_ID, ev.getPlayer()), ev.getPlayer().getInventory());
				
				ev.getPlayer().getInventory().clear();
				ev.getPlayer().getInventory().setArmorContents(null);
				
				ev.getPlayer().getInventory().setItem(0, UtilItem.RenameItem(new ItemStack(Material.CHEST), Language.getText(ev.getPlayer(), "HUB_ITEM_CHEST")));
				ev.getPlayer().getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§azum 1vs1 herrausfordern"));
				creative.remove(ev.getPlayer());
				ev.getPlayer().setVelocity(UtilLocation.calculateVector(ev.getPlayer().getLocation(), this.wait_list.getLocation()).multiply(2D).setY(0.7));
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
	
	int i=0;
	@EventHandler
	public void name(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_3){
			i=0;
			for(SERVER_STATUS a : this.server)i=i+a.getOnline();
			
			i=UtilServer.getPlayers().size()+i;
			if(this.wait_list_name!=null)this.wait_list_name.remove();
			this.wait_list_name = new NameTagMessage(NameTagType.PACKET, this.wait_list.getLocation().add(0, 2, 0),new String[]{"§c§lVERSUS","§aOnline §l"+i});
			this.wait_list_name.send();
		}
	}
	
	@EventHandler
	public void Inv(UpdateAsyncEvent ev){
		if(ev.getType()==UpdateAsyncType.SEC){
			if(this.arenaManager.getWait_list().get(ArenaType._TEAMx2).size()==0){
				this.wait_list_inv.setItem(4, t2);
			}else{
				this.wait_list_inv.getItem(4).setAmount(this.arenaManager.getWait_list().get(ArenaType._TEAMx2).size());
			}
			if(this.arenaManager.getWait_list().get(ArenaType._TEAMx3).size()==0){
				this.wait_list_inv.setItem(10, t3);
			}else{
				this.wait_list_inv.getItem(10).setAmount(this.arenaManager.getWait_list().get(ArenaType._TEAMx3).size());
			}
			if(this.arenaManager.getWait_list().get(ArenaType._TEAMx4).size()==0){
				this.wait_list_inv.setItem(12, t4);
			}else{
				this.wait_list_inv.getItem(12).setAmount(this.arenaManager.getWait_list().get(ArenaType._TEAMx4).size());
			}
			if(this.arenaManager.getWait_list().get(ArenaType._TEAMx5).size()==0){
				this.wait_list_inv.setItem(14, t5);
			}else{
				this.wait_list_inv.getItem(14).setAmount(this.arenaManager.getWait_list().get(ArenaType._TEAMx5).size());
			}
			if(this.arenaManager.getWait_list().get(ArenaType._TEAMx6).size()==0){
				this.wait_list_inv.setItem(16, t6);
			}else{
				this.wait_list_inv.getItem(16).setAmount(this.arenaManager.getWait_list().get(ArenaType._TEAMx6).size());
			}
			
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(creative.contains(ev.getPlayer())){
			ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
		
		statsManager.SaveAllPlayerData(ev.getPlayer());
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
		ev.getPlayer().teleport(spawn);
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
								//SEND
								this.arenaManager.delRound(((Player)ev.getEntity()));
								this.arenaManager.delRound(((Player)ev.getDamager()));
								this.arenaManager.addRound(new GameRound(((Player)ev.getEntity()), new Player[]{((Player)ev.getEntity()),((Player)ev.getDamager())}, ArenaType._TEAMx2));
								
								vs.remove(((Player)ev.getEntity()));
								vs.remove(((Player)ev.getDamager()));
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
			((InventoryCopy)this.base.getMain()).open(ev.getPlayer(), base);
		}else if(ev.getRightClicked().getEntityId()==this.wait_list.getEntityId()){
			ev.setCancelled(true);
			ev.getPlayer().openInventory(this.wait_list_inv);
			
			for(DisguiseBase b : manager.getDisguiseManager().getDisguise().values()){
				if(b instanceof DisguisePlayer){
					UtilPlayer.sendPacket(ev.getPlayer(), ((DisguisePlayer)b).removeFromTablist());
				}
			}
		}
	}
}
