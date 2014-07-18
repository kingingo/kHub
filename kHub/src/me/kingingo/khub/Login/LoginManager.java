package me.kingingo.khub.Login;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.khub.HubManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginManager extends kListener{

	@Getter
	private HubManager Manager;
	@Getter
	private HashMap<Player,String> Login = new HashMap<>();
	@Getter
	private ArrayList<Player> Register = new ArrayList<>();
	
	public LoginManager(HubManager Manager){
		super(Manager.getInstance(),"LoginManager");
		this.Manager=Manager;
		getManager().getCmd().register(CommandLogin.class, new CommandLogin(this));
		getManager().getCmd().register(CommandRegister.class, new CommandRegister(this));
	}
	
	public void setUser(Player p,String pw, String ip){
		getManager().getMysql().Update("INSERT INTO list_users (name, money,password, ip, clanname, kills, deaths,offizier) VALUES ('" + p.getName().toLowerCase() + "','0', '" + pw + "', '" + ip + "', 'default', '0', '0','false');");
	}
	
	public void delLogin(String p){
		getManager().getMysql().Update("DELETE FROM list_login WHERE player='" + p.toLowerCase() + "'");
	}
	
	public boolean isRegestriert(Player p){
		boolean user = false;
		
		try{
			
			ResultSet rs = getManager().getMysql().Query("SELECT password FROM list_users WHERE name = '" + p.getName().toLowerCase() + "'");
			
			while(rs.next()){
				user = Boolean.valueOf(true);
			}
 			
			rs.close();
		}catch (Exception err){
			System.err.println(err);
		}
		
		return user;
	}
	
	public String getPW(Player p){
		String pw = "";
		
		try{
			
			ResultSet rs = getManager().getMysql().Query("SELECT password FROM list_users WHERE name = '" + p.getName().toLowerCase() + "'");
			
			while(rs.next()){
				pw = rs.getString(1);
			}
 			
			rs.close();
		}catch (Exception err){
			System.err.println(err);
		}
		
		return pw;
	}
	
	public boolean isLogin(Player p){
		boolean b = false;
		try {
			ResultSet rs = getManager().getMysql().Query("SELECT player FROM list_login WHERE player='" + p.getName().toLowerCase() + "'");

			while (rs.next()) {
				b = Boolean.valueOf(true);
			}

			rs.close();
		} catch (Exception err) {
			System.err.println(err);
		}
		return b;
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		if(isLogin(ev.getPlayer())){
			if(!isRegestriert(ev.getPlayer())){
				Register.add(ev.getPlayer());
				ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.REGISTER_MESSAGE.getText());
			}else{
				Login.put(ev.getPlayer(), getPW(ev.getPlayer()));
				ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.LOGIN_MESSAGE.getText());
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(Login.containsKey(ev.getPlayer())){
			Login.remove(ev.getPlayer());
		}else if(Register.contains(ev.getPlayer())){
			Register.remove(ev.getPlayer());
		}
	}
	
	@EventHandler
	public void Chat(PlayerChatEvent ev){
		if(Login.containsKey(ev.getPlayer()))ev.setCancelled(true);
		if(Register.contains(ev.getPlayer()))ev.setCancelled(true);
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(Login.containsKey(ev.getPlayer()))ev.setCancelled(true);
		if(Register.contains(ev.getPlayer()))ev.setCancelled(true);
	}
	
	Location from;
	Location to;
	double x;
	double z;
	@EventHandler
	public void Move(PlayerMoveEvent ev){
		if(Login.containsKey(ev.getPlayer())||Register.contains(ev.getPlayer())){
			from = ev.getFrom();
			to = ev.getTo();
			x = Math.floor(from.getX());
			z = Math.floor(from.getZ());
			if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z){
			    x+=.5;
			    z+=.5;
			    ev.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
			}	
		}
	}
	
}
