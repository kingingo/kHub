package eu.epicpvp.khub;

import dev.wolveringer.bukkit.permissions.GroupTyp;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Disguise.DisguiseManager;
import eu.epicpvp.kcore.Hologram.Hologram;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Pet.PetManager;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class kManager {
	@Getter
	private kHub instance;
	@Getter
	private MySQL mysql;
	@Getter
	private CommandHandler cmdHandler;
	private StatsManager money;
	@Getter
	private GroupTyp type;
	private PermissionManager permissionManager;
	private DisguiseManager disguiseManager;
	private PetManager petManager;
	private Hologram hologram;

	public kManager(kHub instance, CommandHandler cmdHandler, MySQL mysql) {
		this.instance = instance;
		this.cmdHandler = cmdHandler;
		this.mysql = mysql;
		if (kHub.hubType.toLowerCase().endsWith("hub"))
			this.type = GroupTyp.GAME;
		Calendar.getHoliday();
		UtilServer.getLagListener(); //Init lag listener
		new Listener(this);
	}

	public PermissionManager getPermissionManager() {
		if (this.permissionManager == null)
			this.permissionManager = new PermissionManager(getInstance(), null);
		return this.permissionManager;
	}

	public Hologram getHologram() {
		if (this.hologram == null)
			this.hologram = new Hologram(getInstance());
		return this.hologram;
	}

	public PetManager getPetManager() {
		if (this.petManager == null)
			this.petManager = new PetManager(getInstance());
		return this.petManager;
	}

	public DisguiseManager getDisguiseManager() {
		if (this.disguiseManager == null)
			this.disguiseManager = new DisguiseManager(getInstance());
		return this.disguiseManager;
	}

	public StatsManager getMoney() {
		if (this.money == null)
			this.money = StatsManagerRepository.getStatsManager(GameType.Money);
		return this.money;
	}

	public void DebugLog(long time, int zeile, String c) {
		System.err.println("[DebugMode]: Class: " + c);
		System.err.println("[DebugMode]: Zeile: " + zeile);
	}

	public void DebugLog(String m) {
		System.err.println("[DebugMode]: " + m);
	}
}
