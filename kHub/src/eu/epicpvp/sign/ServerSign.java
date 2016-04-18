package eu.epicpvp.sign;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.UtilBG;

public class ServerSign implements Listener {
	private static final String maxPoints = "...";
	private SignManager owner;
	private Location location;
	private ServerData server;
	private int points;
	private Sign blockData;
	private boolean animation;

	public ServerSign(Location loc, SignManager owner) {
		this.location = loc;
		this.owner = owner;
		Bukkit.getPluginManager().registerEvents(this, owner.getOwner().getManager().getInstance());
	}

	public boolean isValid() {
		return location.getBlock().getType() == Material.SIGN || location.getBlock().getType() == Material.WALL_SIGN || location.getBlock().getType() == Material.SIGN_POST;
	}

	protected Sign getSign() {
		if (!isValid())
			return null;
		if (blockData == null)
			return blockData = (Sign) location.getBlock().getState();
		return blockData;
	}

	public void setServer(ServerData server) {
		this.server = server;
		updateSign();
	}

	@EventHandler
	public void tick(UpdateEvent e) {
		if (e.getType() == UpdateType.SEC) {
			points++;
			updateSign();
		}
	}

	@EventHandler
	public void click(PlayerInteractEvent e) {
		if (server == null)
			return;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign) {
			if (e.getClickedBlock().getLocation().equals(location)) {
				e.setCancelled(true);
				if (server.getPlayer() >= server.getMaxPlayer())
					if (owner.getOwner().getManager().getPermissionManager().hasPermission(e.getPlayer(), PermissionType.JOIN_FULL_SERVER))
						return;
				UtilBG.sendToServer(e.getPlayer(), server.getServerId(), owner.getOwner().getManager().getInstance());
			}
		}
	}

	private void updateSign() {
		if (animation)
			return;
		setLines(getLines());
	}

	private void setLines(String[] lines) {
		for (int i = 0; i < 4; i++)
			getSign().setLine(i, lines[i]);
		getSign().update(false,false);
	}

	private String[] getLines() {
		String[] lines = new String[4];
		if (server == null) {
			lines[0] = "";
			lines[1] = "Lade Server" + maxPoints.substring(maxPoints.length() - (points % maxPoints.length()));
			lines[2] = "";
			lines[3] = "";
		} else {
			lines[0] = Color.BOLD + server.getGame().getShortName() + server.getServerId().split("a")[1] + (server.getServerSubId().equalsIgnoreCase("none") ? "" : " " + server.getServerSubId().replaceFirst("_", ""));
			if (server.getPlayer() >= server.getMaxPlayer()) {
				lines[1] = Color.ORANGE + Color.BOLD + "Premium";
			} else {
				lines[1] = Color.GREEN + Color.BOLD + "Join";
			}
			lines[2] = server.getMots();
			lines[3] = server.getPlayer() + "/" + server.getMaxPlayer();
		}
		return lines;
	}
}
