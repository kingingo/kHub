package eu.epicpvp.khub.Hub.Listener;

import java.sql.ResultSet;
import java.util.HashMap;

import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.ServerType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.player.LanguageType;
import eu.epicpvp.kcore.Addons.AddonDoubleJump;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.DeliveryPet.DeliveryObject;
import eu.epicpvp.kcore.DeliveryPet.DeliveryPet;
import eu.epicpvp.kcore.Disguise.DisguiseType;
import eu.epicpvp.kcore.Disguise.disguises.DisguiseBase;
import eu.epicpvp.kcore.Disguise.disguises.livings.DisguisePlayer;
import eu.epicpvp.kcore.GemsShop.GemsShop;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonTeleport;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Listener.EntityClick.EntityClickListener;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.UpdateAsync.Event.UpdateAsyncEvent;
import eu.epicpvp.kcore.UpdateAsync.UpdateAsyncType;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilFile;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.kConfig.kConfig;
import eu.epicpvp.khub.Hub.HubManager;
import eu.epicpvp.khub.Hub.InvisbleManager.InvisibleManager;
import eu.epicpvp.khub.Hub.Lobby;
import eu.epicpvp.khub.kHub;
import eu.epicpvp.khub.kManager;
import eu.epicpvp.sign.SignManager;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class HubListener extends kListener {
	private static final net.md_5.bungee.api.chat.BaseComponent[] JOIN_SHOP_MESSAGE = new ComponentBuilder("")
			.append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n\n\n").color(ChatColor.GRAY).bold(true)
			.append("                    Ränge").color(ChatColor.YELLOW).bold(false)
			.append(" und ").color(ChatColor.RED)
			.append("Gems").color(ChatColor.YELLOW)
			.append(" erhälst du bei\n               dem ").color(ChatColor.RED)
			.append("Shop-Villager").color(ChatColor.YELLOW)
			.append(" in der Lobby oder...\n\n").color(ChatColor.RED)
			.append("                          ")
			.append("wenn du hier klickst").color(ChatColor.YELLOW).underlined(true)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke, um den Shop zu öffnen!").color(ChatColor.RED).create()))
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shop"))
			.append("\n\n\n").event((HoverEvent) null).event((ClickEvent) null).underlined(false)
			.append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").color(ChatColor.GRAY).bold(true)
			.create();
	@Getter
	private kManager manager;
	@Getter
	private InventoryPageBase GameInv;
	@Getter
	private HashMap<String, Sign> gungame_signs = new HashMap<>();
	@Getter
	private HashMap<Sign, String> sign_server = new HashMap<>();
	@Getter
	private InventoryPageBase LobbyInv;
	private InventoryCopy TranslationManager_inv;
	private SignManager signs;
	private Location spawn;
	@Getter
	private StatsManager timer;
	@Getter
	@Setter
	private boolean onlinestore = true;

	public HubListener(final HubManager manager) {
		this(manager, true);
	}

	public HubListener(final HubManager manager, boolean initialize) {
		super(manager.getInstance(), kHub.hubType + "Listener");
		this.manager = manager;
		this.signs = new SignManager(this);
		this.spawn = Bukkit.getWorld("world").getSpawnLocation();
		if (UtilServer.getMysteryBoxManager() != null)
			UtilServer.getMysteryBoxManager().getBlocked().add(spawn);

		Bukkit.getWorld("world").setAutoSave(false);
		if (initialize)
			initialize();

		Zombie z = (Zombie) CommandLocations.getLocation("versusc").getWorld().spawnCreature(CommandLocations.getLocation("versusc"), CreatureType.ZOMBIE);
		new NameTagMessage(NameTagType.SERVER, z.getEyeLocation().add(0, 0.2, 0), "§c§lVERSUS").send();
		z.getEquipment().setItemInHand(new ItemStack(Material.BOW));
		z.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		UtilEnt.setNoAI(z, true);
		UtilEnt.setSilent(z, true);

		DisguiseBase dbase = DisguiseType.newDisguise(z, DisguiseType.PLAYER, new Object[] { " " });
		((DisguisePlayer) dbase).loadSkin("EpicPvPMC");

		manager.getDisguiseManager().disguise(dbase);
		new EntityClickListener(manager.getInstance(), new Click() {

			@Override
			public void onClick(Player p, ActionType arg1, Object arg2) {
				UtilBG.sendToServer(p, "versus", manager.getInstance());
			}

		}, z);
		this.timer = StatsManagerRepository.getStatsManager(GameType.TIME);
	}

	public void initialize() {
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS BG_Lobby(ip varchar(30),name varchar(30),bg varchar(30), count int,place int)");
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS " + kHub.hubType.toLowerCase() + "_signs(typ varchar(30),ctyp varchar(30),world varchar(30), x double, z double, y double)");

		loadLobbys();

		new AddonDoubleJump(manager.getInstance());
		new InvisibleManager(manager.getInstance(), this);
		initializeTranslationManagerInv();
		initializeDeliveryPet();
		signs.loadSigns();
		fillGameInv();
		initializeGemsShop();
	}

	GemsShop payToWin;
	GemsShop eula;

	public void initializeGemsShop() {
		eula = new GemsShop("§d§lShop", null, ServerType.GAME);
		payToWin = new GemsShop("§d§lShop", null, ServerType.GAME, new kConfig(UtilFile.getYMLFile(UtilServer.getPermissionManager().getInstance(), "gemsshop_payToWin")), null);
		payToWin.setClick(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				if (isOnlinestore() && ((UtilServer.getPermissionManager().getPermissionPlayer(player) != null && !UtilServer.getPermissionManager().getPermissionPlayer(player).getGroups().isEmpty() && !UtilServer.getPermissionManager().getPermissionPlayer(player).getGroups().get(0).getName().equalsIgnoreCase("default")) || getTimer().getTotalInteger(player, StatsKey.SKY_TIME, StatsKey.PVP_TIME, StatsKey.GUNGAME_TIME, StatsKey.GAME_TIME) > TimeSpan.MINUTE * 30)) {

					payToWin.openInv(player);
				} else {
					eula.openInv(player);
				}
			}

		});

		payToWin.setEtype(EntityType.VILLAGER);
		payToWin.setCreature();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoinShopMessage(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(manager.getInstance(), () -> {
			if (isOnlinestore() && ((UtilServer.getPermissionManager().getPermissionPlayer(player) != null && !UtilServer.getPermissionManager().getPermissionPlayer(player).getGroups().isEmpty() && !UtilServer.getPermissionManager().getPermissionPlayer(player).getGroups().get(0).getName().equalsIgnoreCase("default")) || getTimer().getTotalInteger(player, StatsKey.SKY_TIME, StatsKey.PVP_TIME, StatsKey.GUNGAME_TIME, StatsKey.GAME_TIME) > TimeSpan.MINUTE * 30))
				player.spigot().sendMessage(JOIN_SHOP_MESSAGE);
		}, 20);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onShopCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event.getMessage().toLowerCase().startsWith("/shop")) {
			if (isOnlinestore() && ((UtilServer.getPermissionManager().getPermissionPlayer(player) != null && !UtilServer.getPermissionManager().getPermissionPlayer(player).getGroups().isEmpty() && !UtilServer.getPermissionManager().getPermissionPlayer(player).getGroups().get(0).getName().equalsIgnoreCase("default")) || getTimer().getTotalInteger(player, StatsKey.SKY_TIME, StatsKey.PVP_TIME, StatsKey.GUNGAME_TIME, StatsKey.GAME_TIME) > TimeSpan.MINUTE * 30)) {
				payToWin.openInv(player);
				event.setCancelled(true);
			} else {
				eula.openInv(player);
				event.setCancelled(true);
			}
		}
	}

	public void initializeDeliveryPet() {
		UtilServer.getDeliveryPet(new DeliveryPet(UtilInv.getBase(), null, new DeliveryObject[] { new DeliveryObject(new String[] { "", "§7Click for Vote!", "", "§ePvP Rewards:", "§7   200 Epics", "§7   1x Inventory Repair", "", "§eGame Rewards:", "§7   25 Gems", "§7   100 Coins", "", "§eSkyBlock Rewards:", "§7   200 Epics", "§7   2x Diamonds", "§7   2x Iron Ingot", "§7   2x Gold Ingot" }, PermissionType.DELIVERY_PET_VOTE, false, 28, "§aVote for ClashMC", Material.PAPER, Material.REDSTONE_BLOCK, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				p.closeInventory();
				p.sendMessage(TranslationHandler.getText(p, "PREFIX") + "§7-----------------------------------------");
				p.sendMessage(TranslationHandler.getText(p, "PREFIX") + " ");
				p.sendMessage(TranslationHandler.getText(p, "PREFIX") + "Vote Link:§a http://vote.clashmc.eu/");
				p.sendMessage(TranslationHandler.getText(p, "PREFIX") + " ");
				p.sendMessage(TranslationHandler.getText(p, "PREFIX") + "§7-----------------------------------------");
			}

		}, -1), new DeliveryObject(new String[] { "§aOnly for §eVIP§a!", "", "§ePvP Rewards:", "§7   200 Epics", "§7   10 Level", "", "§eGame Rewards:", "§7   200 Coins", "§7   2x TTT Paesse", "", "§eSkyBlock Rewards:", "§7   200 Epics", "§7   2x Diamonds", "§7   2x Iron Ingot", "§7   2x Gold Ingot" }, PermissionType.DELIVERY_PET_VIP_WEEK, true, 11, "§cRank §eVIP§c Reward", Material.getMaterial(342), Material.MINECART, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				getManager().getMoney().add(p, StatsKey.COINS, 200);
			}

		}, TimeSpan.DAY * 7), new DeliveryObject(new String[] { "§aOnly for §6ULTRA§a!", "", "§ePvP Rewards:", "§7   300 Epics", "§7   15 Level", "", "§eGame Rewards:", "§7   300 Coins", "§7   2x TTT Paesse", "", "§eSkyBlock Rewards:", "§7   300 Epics", "§7   4x Diamonds", "§7   4x Iron Ingot", "§7   4x Gold Ingot" }, PermissionType.DELIVERY_PET_ULTRA_WEEK, true, 12, "§cRank §6ULTRA§c Reward", Material.getMaterial(342), Material.MINECART, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				getManager().getMoney().add(p, StatsKey.COINS, 300);
			}

		}, TimeSpan.DAY * 7), new DeliveryObject(new String[] { "§aOnly for §aLEGEND§a!", "", "§ePvP Rewards:", "§7   400 Epics", "§7   20 Level", "", "§eGame Rewards:", "§7   400 Coins", "§7   3x TTT Paesse", "", "§eSkyBlock Rewards:", "§7   400 Epics", "§7   6x Diamonds", "§7   6x Iron Ingot", "§7   6x Gold Ingot" }, PermissionType.DELIVERY_PET_LEGEND_WEEK, true, 13, "§cRank §5LEGEND§c Reward", Material.getMaterial(342), Material.MINECART, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				getManager().getMoney().add(p, StatsKey.COINS, 400);
			}

		}, TimeSpan.DAY * 7), new DeliveryObject(new String[] { "§aOnly for §bMVP§a!", "", "§ePvP Rewards:", "§7   500 Epics", "§7   25 Level", "", "§eGame Rewards:", "§7   500 Coins", "§7   3x TTT Paesse", "", "§eSkyBlock Rewards:", "§7   500 Epics", "§7   8x Diamonds", "§7   8x Iron Ingot", "§7   8x Gold Ingot" }, PermissionType.DELIVERY_PET_MVP_WEEK, true, 14, "§cRank §3MVP§c Reward", Material.getMaterial(342), Material.MINECART, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				getManager().getMoney().add(p, StatsKey.COINS, 500);
			}

		}, TimeSpan.DAY * 7), new DeliveryObject(new String[] { "§aOnly for §bMVP§c+§a!", "", "§ePvP Rewards:", "§7   600 Epics", "§7   30 Level", "", "§eGame Rewards:", "§7   600 Coins", "§7   4x TTT Paesse", "", "§eSkyBlock Rewards:", "§7   600 Epics", "§7   10x Diamonds", "§7   10x Iron Ingot", "§7   10x Gold Ingot" }, PermissionType.DELIVERY_PET_MVPPLUS_WEEK, true, 15, "§cRank §9MVP§e+§c Reward", Material.getMaterial(342), Material.MINECART, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				getManager().getMoney().add(p, StatsKey.COINS, 600);
			}

		}, TimeSpan.DAY * 7), new DeliveryObject(new String[] { "§7/twitter [TwitterName]", "", "§ePvP Rewards:", "§7   300 Epics", "§7   15 Level", "", "§eGame Rewards:", "§7   300 Coins", "", "§eSkyBlock Rewards:", "§7   300 Epics", "§7   15 Level" }, PermissionType.DELIVERY_PET_TWITTER, false, 34, "§cTwitter Reward", Material.getMaterial(351), 4, new Click() {

			@Override
			public void onClick(Player p, ActionType a, Object obj) {
				// String s1 = getManager().getMysql().getString("SELECT twitter
				// FROM BG_TWITTER WHERE uuid='"+UtilPlayer.getRealUUID(p)+"'");
				// if(s1.equalsIgnoreCase("null")){
				// p.sendMessage(TranslationManager.getText(p,"PREFIX")+TranslationManager.getText(p,
				// "TWITTER_ACC_NOT"));
				// }else{
				// getManager().getPacketManager().SendPacket("DATA", new
				// TWIITTER_IS_PLAYER_FOLLOWER(s1, p.getName()));
				// p.sendMessage(TranslationManager.getText(p,"PREFIX")+TranslationManager.getText(p,
				// "TWITTER_CHECK"));
				// }
			}

		}, TimeSpan.DAY * 7), }, "§bThe Delivery Jockey!", EntityType.CHICKEN, CommandLocations.getLocation("DeliveryPet"), ServerType.GAME, getManager().getHologram(), getManager().getMysql()));
	}

	public void initializeTranslationManagerInv() {
		this.TranslationManager_inv = new InventoryCopy(9, "");
		this.TranslationManager_inv.setCreate_new_inv(true);
		int i = 0;

		for (LanguageType type : LanguageType.values()) {
			double percent = (type == LanguageType.ENGLISH ? 100D : TranslationHandler.getInstance().getTranslationFile(type).computeRelativeTranslatedTexts(LanguageType.ENGLISH));
			if (percent > 10) {
				int s = i;
				this.TranslationManager_inv.addButton(i, new ButtonCopy(new Click() {

					@Override
					public void onClick(Player player, ActionType t, Object object) {
						if (TranslationHandler.getLanguage(player) == type) {
							((InventoryPageBase) object).setItem(s, UtilItem.addEnchantmentGlow(((InventoryPageBase) object).getItem(s)));
						}
					}

				}, new Click() {

					@Override
					public void onClick(Player player, ActionType action, Object obj) {
						TranslationHandler.changeLanguage(player, LanguageType.valueOf(((ItemStack) obj).getItemMeta().getDisplayName().substring(2, ((ItemStack) obj).getItemMeta().getDisplayName().length())));
						player.closeInventory();
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + TranslationHandler.getText(player, "LANGUAGE_CHANGE"));
					}

				}, UtilItem.Item(new ItemStack(Material.PAPER), new String[] { "§7Translation Progress: " + color(UtilMath.trim(2, percent)) }, "§a" + type.name().toUpperCase())));
				i++;
			}
		}
		this.TranslationManager_inv.fill(Material.STAINED_GLASS_PANE, (byte) 15);

		UtilInv.getBase().addPage(this.TranslationManager_inv);
	}

	public String color(double percent) {
		if (percent < 10) {
			return "§4" + percent + "%";
		} else if (percent < 20) {
			return "§c" + percent + "%";
		} else if (percent < 50) {
			return "§6" + percent + "%";
		} else if (percent < 70) {
			return "§e" + percent + "%";
		} else if (percent < 90) {
			return "§2" + percent + "%";
		}

		return "§a" + percent + "%";
	}

	@EventHandler
	public void Portal(UpdateAsyncEvent ev) {
		if (ev.getType() == UpdateAsyncType.SEC) {
			for (Player player : UtilServer.getPlayers()) {
				if (player.getEyeLocation().getBlock().getType() == Material.PORTAL) {
					if (CommandLocations.getLocation("pvp").distance(player.getLocation()) < 10) {
						UtilBG.sendToServer(player, "pvp", getManager().getInstance());
					} else if (CommandLocations.getLocation("sky").distance(player.getLocation()) < 10) {
						UtilBG.sendToServer(player, "sky", getManager().getInstance());
					} else if (CommandLocations.getLocation("versus").distance(player.getLocation()) < 10) {
						UtilBG.sendToServer(player, "versus", getManager().getInstance());
					} else if (CommandLocations.getLocation("gg").distance(player.getLocation()) < 10) {
						UtilBG.sendToServer(player, "gungame", getManager().getInstance());
					} else if (CommandLocations.getLocation("cc").distance(player.getLocation()) < 10) {
						UtilBG.sendToServer(player, "creative", getManager().getInstance());
					}
				}
			}
		}
	}

	// UNSICHTBAR / PET SHOP / Walk Effect / FLY
	public void fillGameInv() {
		this.GameInv = new InventoryPageBase(InventorySize._54, "§8Game Menu");

		this.GameInv.addButton(4, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.MAGMA_CREAM), "§6Spawn"), Bukkit.getWorld("world").getSpawnLocation()));
		this.GameInv.addButton(11, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_AXE), "§aPvP"), CommandLocations.getLocation("pvpt")));
		this.GameInv.addButton(15, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GRASS), "§aSkyBlock"), CommandLocations.getLocation("skyblock")));
		this.GameInv.addButton(18, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GOLD_AXE), "§aGunGame"), CommandLocations.getLocation("GunGame")));
		this.GameInv.addButton(22, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_PICKAXE), "§aCreative"), CommandLocations.getLocation("creative")));
		this.GameInv.addButton(26, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BOW), "§aVersus"), CommandLocations.getLocation("vs")));
		this.GameInv.addButton(30, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "§aQuickSurvivalGames"), CommandLocations.getLocation("QuickSurvivalGames")));
		this.GameInv.addButton(32, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), "§aFalldown"), CommandLocations.getLocation("DeathGames")));
		this.GameInv.addButton(38, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.GOLD_SPADE), "§aMasterbuilders"), CommandLocations.getLocation("masterbuilders")));
		this.GameInv.addButton(39, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG, 1, (byte) 91), "§aSheepWars"), CommandLocations.getLocation("SheepWars")));
		this.GameInv.addButton(40, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.BED), "§aBedWars"), CommandLocations.getLocation("BedWars")));
		this.GameInv.addButton(41, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.EYE_OF_ENDER), "§aSkyWars §7| §6LuckyWars"), CommandLocations.getLocation("SkyWars")));
		this.GameInv.addButton(42, new ButtonTeleport(UtilItem.RenameItem(new ItemStack(Material.STICK), "§aTroubleInMinecraft"), CommandLocations.getLocation("TroubleInMinecraft")));

		this.GameInv.fill(Material.STAINED_GLASS_PANE, 7);
		UtilInv.getBase().addPage(this.GameInv);
	}

	public void loadLobbys() {
		this.LobbyInv = new InventoryPageBase(InventorySize._18, "§8Hub Selector");
		try {
			ResultSet rs = manager.getMysql().Query("SELECT `name`,`bg`,`ip`,`place` FROM BG_Lobby");
			while (rs.next()) {
				final Lobby l = new Lobby(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));

				if (l.getIp().equalsIgnoreCase(Bukkit.getServer().getIp()) && l.getPort() == Bukkit.getServer().getPort()) {
					if (l.getBg().startsWith("premiumhub")) {
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								player.closeInventory();
							}
						}, UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[] { "§6Klicke um die Premium Lobby " + l.getName().split(" ")[2] + " zu betreten " }, "§b" + l.getName())));
					} else {
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								player.closeInventory();
							}
						}, UtilItem.Item(new ItemStack(Material.GLOWSTONE_DUST), new String[] { "§6Klicke um die Lobby " + l.getName().split(" ")[1] + " zu betreten " }, "§a" + l.getName())));
					}
				} else {
					if (l.getBg().startsWith("premiumhub")) {
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								if (player.hasPermission(PermissionType.PREMIUM_LOBBY.getPermissionToString())) {
									// UtilBG.SendToBungeeCord("lobby/"+
									// l.getBg() + "/" + player.getName(),
									// player,getManager().getInstance());
									UtilBG.sendToServer(player, l.getBg(), getManager().getInstance());
								}
							}
						}, UtilItem.Item(new ItemStack(353), new String[] { "§6Klicke um die Premium Lobby " + l.getName().split(" ")[2] + " zu betreten " }, "§b" + l.getName())));
					} else {
						this.LobbyInv.addButton(l.getPlace(), new ButtonBase(new Click() {
							@Override
							public void onClick(Player player, ActionType a, Object obj) {
								// UtilBG.SendToBungeeCord("lobby/"+ l.getBg() +
								// "/" + player.getName(),
								// player,getManager().getInstance());
								UtilBG.sendToServer(player, l.getBg(), getManager().getInstance());
							}
						}, UtilItem.Item(new ItemStack(289), new String[] { "§6Klicke um die Lobby " + l.getName().split(" ")[1] + " zu betreten " }, "§a" + l.getName())));
					}
				}
			}
			rs.close();
		} catch (Exception err) {
			Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, manager.getMysql()));
		}
		this.LobbyInv.fill(Material.getMaterial(160), 1);
		UtilInv.getBase().addPage(this.LobbyInv);

	}

	@EventHandler
	public void Join(PlayerJoinEvent ev) {
		UtilServer.loopbackUntilValidDataserverConnection(() -> {
			getManager().getMoney().loadPlayer(ev.getPlayer());
		}, "money loader " + ev.getPlayer().getName(), false);
		UtilServer.loopbackUntilValidDataserverConnection(() -> {
			getTimer().loadPlayer(ev.getPlayer());
		}, "timer loader " + ev.getPlayer().getName(), false);
		UtilServer.loopbackUntilValidDataserverConnection(() -> {
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX") + TranslationHandler.getText(ev.getPlayer(), "WHEREIS_TEXT", kHub.hubID + " " + kHub.hubType));
		}, "welcome message", true);

		UtilPlayer.setTab(ev.getPlayer(), kHub.hubType + " " + kHub.hubID);
		ev.getPlayer().teleport(ev.getPlayer().getWorld().getSpawnLocation());
		ev.getPlayer().getInventory().setItem(1, UtilItem.RenameItem(new ItemStack(Material.COMPASS), TranslationHandler.getText(ev.getPlayer(), "HUB_ITEM_COMPASS")));
		if (kHub.hubType.toLowerCase().startsWith("premiumhub") || kHub.hubType.toLowerCase().startsWith("hub"))
			ev.getPlayer().getInventory().setItem(3, UtilItem.RenameItem(new ItemStack(Material.DIAMOND), "§d§lOnline-Shop"));
		ev.getPlayer().getInventory().setItem(5, UtilItem.RenameItem(new ItemStack(Material.BOOK_AND_QUILL), TranslationHandler.getText(ev.getPlayer(), "HUB_ITEM_BUCH") + " §c§lBETA"));
		ev.getPlayer().getInventory().setItem(8, UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), TranslationHandler.getText(ev.getPlayer(), "HUB_ITEM_NETHERSTAR")));
	}

	@EventHandler
	public void Inventory(InventoryMoveItemEvent ev) {
		if (ev.getSource().getHolder() instanceof Player) {
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev) {
		if ((UtilEvent.isAction(ev, ActionType.PHYSICAL) && (ev.getClickedBlock().getType() == Material.SOIL)) || (UtilEvent.isAction(ev, ActionType.BLOCK) && !ev.getPlayer().isOp())) {
			ev.setCancelled(true);
		}

		if (UtilEvent.isAction(ev, ActionType.RIGHT)) {
			if (ev.getPlayer().getItemInHand().getType() == Material.NETHER_STAR) {
				ev.getPlayer().openInventory(getLobbyInv());
				ev.setCancelled(true);
			} else if (ev.getPlayer().getItemInHand().getType() == Material.COMPASS) {
				ev.getPlayer().openInventory(getGameInv());
				ev.setCancelled(true);
			} else if (ev.getPlayer().getItemInHand().getType() == Material.CHEST) {
				ev.getPlayer().openInventory(((HubManager) getManager()).getShop());
			} else if (ev.getPlayer().getItemInHand().getType() == Material.DIAMOND_PICKAXE) {
				UtilBG.sendToServer(ev.getPlayer(), "v", getManager().getInstance());
			} else if (ev.getPlayer().getItemInHand().getType() == Material.BOOK_AND_QUILL) {
				ev.setCancelled(true);
				TranslationManager_inv.open(ev.getPlayer(), UtilInv.getBase());
			} else if (ev.getPlayer().getItemInHand().getType() == Material.FIREWORK) {
				UtilBG.sendToServer(ev.getPlayer(), "event", getManager().getInstance());
				ev.setCancelled(true);
			} else if (ev.getPlayer().getItemInHand().getType() == Material.DIAMOND) {
				ev.setCancelled(true);
				payToWin.getClick().onClick(ev.getPlayer(), ActionType.RIGHT, null);
			}
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent ev) {
		if (!ev.getPlayer().isOnGround()) {
			if (ev.getPlayer().getLocation().getY() < (spawn.getY() - 25)) {
				ev.getPlayer().teleport(spawn);
			}
		}
	}
}