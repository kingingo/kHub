package me.kingingo.khub.Event.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.khub.Event.EventManager;
import me.kingingo.khub.Event.SoloEvent;

public class TNTFight extends SoloEvent{
	
	String Prefix = "TNTFight"; /*Mit Mani die Farben besprechen */
	
	
	List<Player> activePlayers = new ArrayList<>();

	public TNTFight(EventManager eventManager, String eventName) {
		super(eventManager, "TNTFight");
		
	}

	public void reset() {
		
	}

	public void select() {
		Location loc = CommandLocations.getLocation("TNTFight:spawn");
		if(loc.getBlockX()!=0&&loc.getBlockZ()!=0)Bukkit.getWorld("world").setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		for(Player player : UtilServer.getPlayers())player.teleport(loc);
	}

	public void start() {

	}

	public void stop() {
		
	}
	
	public static Location getRandomLoc() {
		Random r = new Random();
		String s = CommandLocations.getLocations().get(r.nextInt(CommandLocations.getLocations().size()));
		String[] st = s.split(" ");
		return new Location(Bukkit.getServer().getWorld(st[0]), Double.parseDouble(st[1]), Double.parseDouble(st[2]), Double.parseDouble(st[3]));
	}
	
	void setUpPlayer(Player p){
		Integer i = 1;
		for (Player pl : activePlayers){
			Inventory inv = p.getInventory();
			inv.clear();
			
			
			
		}
	}
	

	
	
	/** Teamverteilung und -erstellung / Setup */
	
	static ItemStack getBow(){
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowmeta = bow.getItemMeta();
		bowmeta.setDisplayName("§cDeine Waffe!");
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		bow.setItemMeta(bowmeta);
		return bow;
	}
	
	static ItemStack getPfeil(){
		ItemStack pfeil = new ItemStack(Material.ARROW);
		ItemMeta pfeilmeta = pfeil.getItemMeta();
		pfeilmeta.setDisplayName("§6Dein explosiver Pfeil");
		pfeil.setItemMeta(pfeilmeta);
		return pfeil;
	}
	
	static ItemStack[] getArmor(String farbe){
		 ItemStack helm = new ItemStack(Material.LEATHER_HELMET);					LeatherArmorMeta helmmeta = (LeatherArmorMeta) helm.getItemMeta();
		 ItemStack brustpanzer = new ItemStack(Material.LEATHER_CHESTPLATE);		LeatherArmorMeta brustpanzermeta = (LeatherArmorMeta) brustpanzer.getItemMeta();
		 ItemStack hose = new ItemStack(Material.LEATHER_LEGGINGS);  				LeatherArmorMeta hosemeta = (LeatherArmorMeta) hose.getItemMeta();
		 ItemStack schuhe = new ItemStack(Material.LEATHER_BOOTS); 					LeatherArmorMeta schuhemeta = (LeatherArmorMeta) schuhe.getItemMeta();
		 
		 helmmeta.setColor(farbe == "Rot" ? Color.RED : farbe == "Blau" ? Color.BLUE : farbe == "Gelb" ? Color.YELLOW : farbe == "Lila" ? Color.PURPLE : farbe == "Grün" ? Color.LIME : farbe == "Orange" ? Color.ORANGE : farbe == "Weiss" ? Color.WHITE : farbe == "Schwarz" ? Color.BLACK : null);
		 brustpanzermeta.setColor(farbe == "Rot" ? Color.RED : farbe == "Blau" ? Color.BLUE : farbe == "Gelb" ? Color.YELLOW : farbe == "Lila" ? Color.PURPLE : farbe == "Grün" ? Color.LIME : farbe == "Orange" ? Color.ORANGE : farbe == "Weiss" ? Color.WHITE : farbe == "Schwarz" ? Color.BLACK : null);
		 hosemeta.setColor(farbe == "Rot" ? Color.RED : farbe == "Blau" ? Color.BLUE : farbe == "Gelb" ? Color.YELLOW : farbe == "Lila" ? Color.PURPLE : farbe == "Grün" ? Color.LIME : farbe == "Orange" ? Color.ORANGE : farbe == "Weiss" ? Color.WHITE : farbe == "Schwarz" ? Color.BLACK : null);
		 schuhemeta.setColor(farbe == "Rot" ? Color.RED : farbe == "Blau" ? Color.BLUE : farbe == "Gelb" ? Color.YELLOW : farbe == "Lila" ? Color.PURPLE : farbe == "Grün" ? Color.LIME : farbe == "Orange" ? Color.ORANGE : farbe == "Weiss" ? Color.WHITE : farbe == "Schwarz" ? Color.BLACK : null);

		return new ItemStack[]{helm, brustpanzer, hose, schuhe};
	}
	
	void getTeamColor(){
		Integer playerSize = activePlayers.size();
		//TODO Keine Zeit mehr, hier mache ich später weiter nach der Konferenz!
	}
	
	static Teams getTeamFromNumber(Integer n){
		return (n == 0 ? Teams.ROT : n == 1 ? Teams.BLAU : n == 2 ? Teams.GELB : n == 3 ? Teams.LILA : n == 4 ? Teams.GRUEN : n == 5 ? Teams.ORANGE : n == 6 ? Teams.WEISS : Teams.SCHWARZ);
	}
	
	static ItemStack bogen = new ItemStack(Material.BOW); static ItemStack pfeil = new ItemStack(Material.ARROW);
	static ItemStack helm = new ItemStack(Material.LEATHER_HELMET); static ItemStack brustpanzer = new ItemStack(Material.LEATHER_CHESTPLATE);
	static ItemStack hose = new ItemStack(Material.LEATHER_LEGGINGS); static ItemStack schuhe = new ItemStack(Material.LEATHER_BOOTS);
	
	enum Teams{
		
		ROT("Rot", 1, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		BLAU("Blau", 2, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		GELB("Gelb", 3, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		LILA("Lila", 4, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		GRUEN("Grün", 5, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		ORANGE("Orange", 6, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		WEISS("Weiss", 7, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe}),
		SCHWARZ("Schwarz", 8, new ItemStack[]{bogen, pfeil, helm, brustpanzer, hose, schuhe});
		
		ItemStack[] items;
		Integer number;
		String name;
		
		Teams(String nm, Integer nb, ItemStack[] is){
			
			name = nm;
			items = is;
			number = nb;
			
			switch (name){
			case "Rot": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Rot")[0], getArmor("Rot")[1], getArmor("Rot")[2], getArmor("Rot")[3]};
			case "Blau": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Blau")[0], getArmor("Blau")[1], getArmor("Blau")[2], getArmor("Blau")[3]};
			case "Gelb": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Gelb")[0], getArmor("Gelb")[1], getArmor("Gelb")[2], getArmor("Gelb")[3]};
			case "Lila": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Lila")[0], getArmor("Lila")[1], getArmor("Lila")[2], getArmor("Lila")[3]};
			case "Grün": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Grün")[0], getArmor("Grün")[1], getArmor("Grün")[2], getArmor("Grün")[3]};
			case "Orange": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Orange")[0], getArmor("Orange")[1], getArmor("Orange")[2], getArmor("Orange")[3]};
			case "Weiss": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Weiss")[0], getArmor("Weiss")[1], getArmor("Weiss")[2], getArmor("Weiss")[3]};
			case "Schwarz": items = new ItemStack[]{getBow(), getPfeil(), getArmor("Schwarz")[0], getArmor("Schwarz")[1], getArmor("Schwarz")[2], getArmor("Schwarz")[3]};
			}
			
			
		}
		
	}
	
}
