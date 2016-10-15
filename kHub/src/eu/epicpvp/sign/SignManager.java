package eu.epicpvp.sign;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import eu.epicpvp.dataserver.protocoll.packets.PacketOutLobbyServer.GameServers;
import eu.epicpvp.dataserver.protocoll.packets.PacketOutLobbyServer.ServerKey;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.khub.kHub;
import eu.epicpvp.khub.Hub.Listener.HubListener;
import eu.epicpvp.thread.ThreadFactory;
import eu.epicpvp.thread.ThreadRunner;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SignManager implements Listener{
	@AllArgsConstructor
	@Getter
	private static class ServerIdentifier {
		private GameType type;
		private String subtype;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ServerIdentifier other = (ServerIdentifier) obj;
			if (subtype == null) {
				if (other.subtype != null)
					return false;
			} else if (!subtype.equals(other.subtype))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
	}
	@Getter
	private HubListener owner;
	private ThreadRunner updater;

	@SuppressWarnings("serial")
	private HashMap<ServerIdentifier, ArrayList<ServerSign>> signs = new HashMap<ServerIdentifier, ArrayList<ServerSign>>(){
		public ArrayList<ServerSign> get(Object key) {
			ArrayList<ServerSign> out = super.get(key);
			if(out == null)
				super.put((ServerIdentifier) key, out = new ArrayList<>());
			return out;
		};
	};
	public SignManager(HubListener owner) {
		this.owner = owner;
		this.updater = ThreadFactory.getFactory().createThread(new SignUpdateThread(this));
		Bukkit.getPluginManager().registerEvents(this, owner.getPlugin());
	}

	public void loadSigns() {
		try {
			ResultSet rs = owner.getManager().getMysql().Query("SELECT `typ`,`ctyp`,`x`,`y`,`z` FROM " + kHub.hubType.toLowerCase() + "_signs");
			while (rs.next()) {
				try {
					if (GameType.get(rs.getString(1)) == null) {
						System.out.println(rs.getString(1) + " == NULL");
						continue;
					}
					try {
						Location loc = new Location(Bukkit.getWorld("world"), rs.getInt(3), rs.getInt(4), rs.getInt(5));
						if(UtilServer.getMysteryBoxManager()!=null)UtilServer.getMysteryBoxManager().getBlocked().add(loc);

						signs.get(new ServerIdentifier(GameType.get(rs.getString(1)), rs.getString(2))).add(new ServerSign(loc,this));
					} catch (ClassCastException e) {
						System.err.println("[kHub] Sign nicht gefunden ...");
					}
				} catch (IllegalArgumentException e) {
					System.out.println("NOT FOUND: " + rs.getString(1));
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updater.start();
	}
	@SuppressWarnings("serial")
	protected void updateSigns(GameServers[] server){

		HashMap<ServerIdentifier, Iterator<ServerSign>> ssigns = new HashMap<ServerIdentifier, Iterator<ServerSign>>(){
			@Override
			public Iterator<ServerSign> get(Object key) {
				Iterator<ServerSign> out = super.get(key);
				if(out == null)
					super.put((ServerIdentifier) key, out = new ArrayList().iterator());
				return out;
			}
		};
		for(ServerIdentifier i : signs.keySet())
			ssigns.put(i, signs.get(i).iterator());
		for(GameServers game : server)
			for(ServerKey s : game.getServers()){
				Iterator<ServerSign> si = ssigns.get(new ServerIdentifier(game.getGame(), s.getServerSubId()));
				if(si.hasNext())
					si.next().setServer(new ServerData(game.getGame(), s));
			}
		for(ServerIdentifier i : ssigns.keySet()){
			Iterator<ServerSign> si = ssigns.get(i);
			while (si.hasNext()) {
				si.next().setServer(null); //Free sign
			}
		}
	}

	protected GameType[] buildGamesIndex(){
		ArrayList<GameType> types = new ArrayList<>();
		for(ServerIdentifier i : signs.keySet())
			if(!types.contains(i.getType()))
				types.add(i.getType());
		return  types.toArray(new GameType[0]);
	}

	@EventHandler
	public void onSign(SignChangeEvent ev) {
		Player p = ev.getPlayer();
		if (p.isOp()) {
			String sign = ev.getLine(0);
			ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));

			if (sign.equalsIgnoreCase("[S]") && p.isOp()) {
				String typ = ev.getLine(1);
				String ctyp = ev.getLine(2);
				if(GameType.get(typ) == null){
					ev.setCancelled(true);
					ev.getBlock().setType(Material.AIR);
					ev.getPlayer().sendMessage("§cGametype not found!");
					return;
				}
				signs.get(new ServerIdentifier(GameType.get(typ), ctyp)).add(new ServerSign(ev.getBlock().getLocation(), this));
				owner.getManager().getMysql().Update("INSERT INTO "+kHub.hubType.toLowerCase()+"_signs (typ,cTyp,world, x, z, y) VALUES ('"+ typ+ "', '"+ctyp+"','"+ p.getLocation().getWorld().getName()+ "','"+ ev.getBlock().getX()+ "','"+ ev.getBlock().getZ()+ "','" + ev.getBlock().getY() + "')");
			}
		}
	}

	protected void removeSign(ServerSign sign){
		for(ServerIdentifier i : signs.keySet())
			signs.get(i).remove(sign);
		owner.getManager().getMysql().Update("DELETE FROM `"+kHub.hubType.toLowerCase()+"_signs` WHERE world='"+sign.getSign().getLocation().getWorld()+"' AND x='"+sign.getSign().getLocation().getBlockX()+"' AND y='"+sign.getSign().getLocation().getBlockY()+"' AND z='"+sign.getSign().getLocation().getBlockZ()+"'");
	}
}
