package me.kingingo.khub.Pet.Pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.khub.Pet.RideEntity.RideSheep;
import me.kingingokhub.Pet.AnvilGUI;
import me.kingingokhub.Pet.PetInterface;
import me.kingingokhub.Pet.PetManager;
import me.kingingokhub.Pet.RideInterface;
import me.kingingokhub.Pet.AnvilGUI.AnvilClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetSheep implements Listener,PetInterface {

	private HashMap<Player,RideInterface> sheeps = new HashMap<>();
	private RideSheep e=null;
	private Inventory SelectInv=null;
	private Inventory BuyInv=null;
	private Inventory OptionInv=null;
	private Inventory WoolInv=null;
	private Inventory CointInv=null;
	private List<String> desc=null;
	public float walkspeed = 0.35F;
	private int taskID=-1;
	private PetManager manager;
	
    public PetSheep(PetManager manager) {
    	this.manager=manager;
    	Bukkit.getPluginManager().registerEvents(this,manager.getManager().getInstance());
    	Description();
	}
    
    @Override
	public void remove(Player p) {
		sh=(Sheep)sheeps.get(p).getBukkitEntity();
		sh.remove();
	}
    
    Color c;
    boolean up;
    int[] rgbs;
    int r;
    int b;
    int g;
    
    public Entity getEntity(Player p){
    	if(sheeps.containsKey(p))return sheeps.get(p).getBukkitEntity();
    	return null;
    }
    
    @Override
	public Permission getPermission() {
		return Permission.PET_SHEEP;
	}
    
    @Override
	public HashMap<Player, RideInterface> getList() {
		return sheeps;
	}
    
    //Sheep-/-WHITE-/-FALSE/TRUE-/-NAME
    @Override
	public void Save(Player p) {
		if(sheeps.containsKey(p)){
			sh = (Sheep)sheeps.get(p).getBukkitEntity();
			manager.UpdateTable(p.getName(), getName()+"-/-"+getStringColor(sh.getColor())+"-/-"+sh.isAdult()+"-/-"+sh.getCustomName());
		}
	}
    
    public void AddPlayer(Player p){
    	
    	CraftWorld cw = (CraftWorld)p.getWorld();
		e = new RideSheep(cw.getHandle());
		e.setLocation(p.getLocation().getX(), p.getLocation().getY(),p.getLocation().getZ(),p.getLocation().getYaw(),p.getLocation().getPitch());
		cw.getHandle().addEntity(e,SpawnReason.CUSTOM);
    	((Sheep)e.getBukkitEntity()).setCustomNameVisible(true);
    	sheeps.put(p, e);
    }
    
    public List<String> Description(){
    	if(desc==null){
    		desc=new ArrayList<>();
    		desc.add(" ");
    		desc.add("§6Coins: §7"+getCoins());
    		desc.add(" ");
    		desc.add("§6Tokens: §7"+getTokens());
    		desc.add(" ");
    	}
    	return desc;
    }
    
    public int getTokens(){
    	return 250;
    }
    
    public int getCoins(){
    	return 50000;
    }
    
    public Inventory getOptionInv(){
    	if(OptionInv == null){
    		OptionInv=Bukkit.createInventory(null, 9,"§aPet: Sheep");
    		OptionInv.setItem(0, UtilItem.RenameItem(new ItemStack(383,1,(byte)91), "§aPet: Sheep"));
    		OptionInv.setItem(1, UtilItem.Item(new ItemStack(340,1), desc, "§6Beschreibung"));
    		OptionInv.setItem(2, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		OptionInv.setItem(3, UtilItem.RenameItem(new ItemStack(Material.ANVIL,1), "§bNamen Aendern"));
    		OptionInv.setItem(4, UtilItem.RenameItem(new ItemStack(Material.BONE,1), "§bBaby/Erwachsen"));
    		OptionInv.setItem(5, UtilItem.RenameItem(new ItemStack(Material.WOOL,1), "§bFarbe"));
    		OptionInv.setItem(6, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		OptionInv.setItem(7, UtilItem.RenameItem(new ItemStack(351,1,(byte)10),  "§aAusgewaehlt"));
    		OptionInv.setItem(8, UtilItem.RenameItem(new ItemStack(330,1),  "§cZurück"));
    	}
    	return OptionInv;
    }
    
    public Inventory getBuyInv(){
    	if(BuyInv == null){
    		BuyInv=Bukkit.createInventory(null, 9,"§aPet: Sheep");
    		BuyInv.setItem(0, UtilItem.RenameItem(new ItemStack(383,1,(byte)91), "§aPet: Sheep"));
    		BuyInv.setItem(1, UtilItem.Item(new ItemStack(340,1), desc, "§6Beschreibung"));
    		BuyInv.setItem(2, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		BuyInv.setItem(3, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		BuyInv.setItem(4, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		BuyInv.setItem(5, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		BuyInv.setItem(6, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		BuyInv.setItem(7, UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,1),  "§aKaufen"));
    		BuyInv.setItem(8, UtilItem.RenameItem(new ItemStack(330,1),  "§cZurück"));
    	}
    	return BuyInv;
    }
    
    public Inventory getCointInv(){
    	if(CointInv == null){
    		CointInv=Bukkit.createInventory(null, 9,"§aPet: Sheep");
    		CointInv.setItem(0, UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT), "§aCoins §6"+getCoins()));
    		CointInv.setItem(1, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(2, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(3, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(4, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(5, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(6, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(7, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		CointInv.setItem(8, UtilItem.RenameItem(new ItemStack(Material.GOLD_NUGGET),  "§cTokens §6"+getTokens()));
    	}
    	return CointInv;
    }
    
    public Inventory getSelectInv(){
    	if(SelectInv == null){
    		SelectInv=Bukkit.createInventory(null, 9,"§aPet: Sheep");
    		SelectInv.setItem(0, UtilItem.RenameItem(new ItemStack(383,1,(byte)91), "§aPet: Sheep"));
    		SelectInv.setItem(1, UtilItem.Item(new ItemStack(340,1), desc, "§6Beschreibung"));
    		SelectInv.setItem(2, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		SelectInv.setItem(3, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		SelectInv.setItem(4, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		SelectInv.setItem(5, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		SelectInv.setItem(6, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		SelectInv.setItem(7, UtilItem.RenameItem(new ItemStack(51,1),  "§aAuswaehlen"));
    		SelectInv.setItem(8, UtilItem.RenameItem(new ItemStack(330,1),  "§cZurück"));
    	}
    	return SelectInv;
    }
    
    public Inventory getWoolInv(){
    	if(WoolInv==null){
    		WoolInv=Bukkit.createInventory(null, 18,"§6Farben: ");
    		WoolInv.setItem(0, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)15), "§0Black"));
    		WoolInv.setItem(1, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)11), "§1Blue"));
    		WoolInv.setItem(2, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)12), "§8Brown"));
    		WoolInv.setItem(3, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)9), "§bCyan"));
    		WoolInv.setItem(4, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)7), "§7Gray"));
    		WoolInv.setItem(5, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)13), "§2Green"));
    		WoolInv.setItem(6, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)3), "§bLight Blue"));
    		WoolInv.setItem(7, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)5), "§aLime"));
    		WoolInv.setItem(8, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)2), "§dMagneta"));
    		WoolInv.setItem(9, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)1), "§6Orange"));
    		WoolInv.setItem(10, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)6), "§dPink"));
    		WoolInv.setItem(11, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)10), "§5Purple"));
    		WoolInv.setItem(12, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)14), "§cRed"));
    		WoolInv.setItem(13, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)8), "§7Silver"));
    		WoolInv.setItem(14, UtilItem.RenameItem(new ItemStack(Material.WOOL,1), "§fWhite"));
    		WoolInv.setItem(15, UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)4), "§eYellow"));
    		WoolInv.setItem(16, UtilItem.RenameItem(new ItemStack(19), "§8Zufall"));
    		WoolInv.setItem(17, UtilItem.RenameItem(new ItemStack(Material.IRON_DOOR), "§cZurück"));
    	}
    	
    	return WoolInv;
    }
    
    public DyeColor getRandomColor(){
    	
    	switch(manager.rdm.nextInt(15)){
    	case 0:
    		return DyeColor.BLACK;
    	case 1:
    		return DyeColor.BLUE;
    	case 2:
    		return DyeColor.BROWN;
    	case 3:
    		return DyeColor.CYAN;
    	case 4:
    		return DyeColor.GRAY;
    	case 5:
    		return DyeColor.GREEN;
    	case 6:
    		return DyeColor.LIGHT_BLUE;
    	case 7:
    		return DyeColor.LIME;
    	case 8:
    		return DyeColor.MAGENTA;
    	case 9:
    		return DyeColor.ORANGE;
    	case 10:
    		return DyeColor.PINK;
    	case 11:
    		return DyeColor.PURPLE;
    	case 12:
    		return DyeColor.RED;
    	case 13:
    		return DyeColor.SILVER;
    	case 14:
    		return DyeColor.WHITE;
    	case 15:
    		return DyeColor.YELLOW;
    	}
    	
    	return DyeColor.WHITE;
    }
    
    public String getStringColor(DyeColor c){
    	switch(c){
    	case BLACK:
    		return "§0Black";
    	case BLUE:
    		return "§1Blue";
    	case BROWN:
    		return "§8Brown";
    	case CYAN:
    		return "§bCyan";
    	case GRAY:
    		return "§7Gray";
    	case GREEN:
    		return "§2Green";
    	case LIGHT_BLUE:
    		return "§bLight Blue";
    	case LIME:
    		return "§aLime";
    	case MAGENTA:
    		return "§dMagneta";
    	case ORANGE:
    		return "§6Orange";
    	case PINK:
    		return "§dPink";
    	case PURPLE:
    		return "§5Purple";
    	case RED:
    		return "§cRed";
    	case SILVER:
    		return "§7Silver";
    	case WHITE:
    		return "§fWhite";
    	case YELLOW:
    		return "§eYellow";
    	}
    	
    	return "§eYellow";
    }
    
    public DyeColor getColor(String c){
    	switch(c){
    	case "§0Black":
    		return DyeColor.BLACK;
    	case "§1Blue":
    		return DyeColor.BLUE;
    	case "§8Brown":
    		return DyeColor.BROWN;
    	case "§bCyan":
    		return DyeColor.CYAN;
    	case "§7Gray":
    		return DyeColor.GRAY;
    	case "§2Green":
    		return DyeColor.GREEN;
    	case "§bLight Blue":
    		return DyeColor.LIGHT_BLUE;
    	case "§aLime":
    		return DyeColor.LIME;
    	case "§dMagneta":
    		return DyeColor.MAGENTA;
    	case "§6Orange":
    		return DyeColor.ORANGE;
    	case "§dPink":
    		return DyeColor.PINK;
    	case "§5Purple":
    		return DyeColor.PURPLE;
    	case "§cRed":
    		return DyeColor.RED;
    	case "§7Silver":
    		return DyeColor.SILVER;
    	case "§fWhite":
    		return DyeColor.WHITE;
    	case "§eYellow":
    		return DyeColor.YELLOW;
    	}
    	
    	return DyeColor.WHITE;
    }
    
    String displayname;
    Player p;
    Sheep sh;
    
    public void Join(String[] i,Player pl){
    	p=pl;
    	CraftWorld cw = (CraftWorld)p.getWorld();
		e = new RideSheep(cw.getHandle());
		e.setLocation(p.getLocation().getX(), p.getLocation().getY(),p.getLocation().getZ(),p.getLocation().getYaw(),p.getLocation().getPitch());
		cw.getHandle().addEntity(e,SpawnReason.CUSTOM);
    	
    	sheeps.put(p, e);
    	sh=(Sheep)e.getBukkitEntity();
    	sh.setCustomNameVisible(true);
    	sh.setColor(getColor(i[1]));
    	if(i[2].equalsIgnoreCase("TRUE")){
    		sh.setAdult();
    	}else{
    		sh.setBaby();
    	}
    	sh.setCustomName(i[3]);
    }
    
    @EventHandler
    public void Sit(PlayerInteractEntityEvent ev){
    	if(sheeps.containsKey(ev.getPlayer())){
    		if(sheeps.get(ev.getPlayer()).getBukkitEntity().getEntityId()==ev.getRightClicked().getEntityId()){
    			sheeps.get(ev.getPlayer()).getBukkitEntity().setPassenger(ev.getPlayer());
    		}
    	}
    }
    
    @EventHandler
    public void Inv(InventoryClickEvent ev){
    	if(!(ev.getWhoClicked() instanceof Player)){
			return;
		}
    	if(ev.getClickedInventory()==null)return;
		p = (Player)ev.getWhoClicked();
		
		if(ev.getClickedInventory().getTitle().equalsIgnoreCase("§6Farben: ")){
			displayname = ev.getCurrentItem().getItemMeta().getDisplayName();
			p.closeInventory();
			p.openInventory(manager.HauptInv);
			
			if(displayname.equalsIgnoreCase( "§8Zufall")){
//				if(disco.containsKey(p)){
//					disco.remove(p);
//				}else{
////					colorarmor.put(p, new int[] { 255, 255, 255 });
////			        upordown.put(p, Boolean.valueOf(false));
////					disco.put(p, sheeps.get(p));
//				}
				sh=(Sheep)sheeps.get(p).getBukkitEntity();
				if(!sh.isCustomNameVisible()){
					sh.setCustomName("Sheep");
					sh.setCustomNameVisible(true);
				}else{
					sh.setCustomName("jeb_");
					sh.setCustomNameVisible(false);
				}
				
				
			}
			
			if(!displayname.equalsIgnoreCase("§cZurück")){
				sh = (Sheep)sheeps.get(p).getBukkitEntity();
				sh.setColor(getColor(displayname));
	    	}
		}else if(!ev.getClickedInventory().getTitle().equalsIgnoreCase("§aPet: Sheep"))return;
		ev.setCancelled(true);
    	if(ev.getCurrentItem()==null||ev.getCurrentItem().getTypeId()==160||ev.getCurrentItem().getTypeId()==340||ev.getCurrentItem().getTypeId()==383||ev.getCurrentItem().getTypeId()==351)return;
    	
    	displayname = ev.getCurrentItem().getItemMeta().getDisplayName();
    	p.closeInventory();
    	
    	if(displayname.equalsIgnoreCase("§cZurück")){
    		p.openInventory(manager.HauptInv);
    	}else if(displayname.equalsIgnoreCase("§aAuswaehlen")){
    		for(PetInterface kp : manager.Pets){
    			if(kp.getList().containsKey(p)){
    				RideInterface e=kp.getList().get(p);
    				e.getBukkitEntity().remove();
    				kp.getList().remove(p);
    			}
    		}
    		AddPlayer(p);
    		p.sendMessage(manager.prefix+"§a Das Pet wurde Ausgewaehlt.");
    	}else if(displayname.equalsIgnoreCase("§bFarbe")){
    		p.openInventory(getWoolInv());
    	}else if(displayname.equalsIgnoreCase("§bNamen Aendern")){

    		AnvilGUI gui = new AnvilGUI(p,manager.getManager().getInstance(), new AnvilGUI.AnvilClickEventHandler(){

				@Override
				public void onAnvilClick(AnvilClickEvent event) {
					if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){
				         event.getName();
						 p.sendMessage(manager.prefix+"§a Das Pet wurde umbenannt zu §e"+event.getName());
						 sh =(Sheep) sheeps.get(p).getBukkitEntity();
						 sh.setCustomName(event.getName().replace("&", "§"));
					}
				}});
    		
				 ItemStack renamed = UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Pet Name");
				 gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, renamed);
				 gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));

				 gui.open();
    		
    	}else if(displayname.equalsIgnoreCase("§bBaby/Erwachsen")){
    		if(sheeps.containsKey(p)){
    			sh = (Sheep) sheeps.get(p).getBukkitEntity();
    			if(sh.isAdult()){
    				sh.setBaby();
    				p.sendMessage(manager.prefix+"§aDas Pet ist nun ein Baby.");
    			}else{
    				sh.setAdult();
    				p.sendMessage(manager.prefix+"§aDas Pet ist nun ein Erwachsener.");
    			}
    		}
    	}else if(displayname.equalsIgnoreCase("§aKaufen")){
    		p.openInventory(getCointInv());
    	}else if(displayname.equalsIgnoreCase("§cTokens §6"+getTokens())){
    		
    		if(manager.getManager().getTokens().delTokens(p, false, getTokens())){
    			manager.getManager().getPManager().addPermission(p, getPermission());
    			
    			for(PetInterface kp : manager.Pets){
        			if(kp.getList().containsKey(p)){
        				RideInterface e=kp.getList().get(p);
        				e.getBukkitEntity().remove();
        				kp.getList().remove(p);
        			}
        		}
        		AddPlayer(p);
    			p.sendMessage(manager.getPrefix()+"§aDu hast Erfolgreich das Pet gekauft und Ausgewaehlt.");
    		}else{
    			p.sendMessage(manager.getPrefix()+"§c Du hast nicht genug Tokens");
    		}
    	}else if(displayname.equalsIgnoreCase("§aCoins §6"+getCoins())){
    		if(manager.getManager().getCoins().delCoins(p, false,getCoins())){
    			manager.getManager().getPManager().addPermission(p, getPermission());
    			
    			for(PetInterface kp : manager.Pets){
        			if(kp.getList().containsKey(p)){
        				RideInterface e=kp.getList().get(p);
        				e.getBukkitEntity().remove();
        				kp.getList().remove(p);
        			}
        		}
        		AddPlayer(p);
    			p.sendMessage(manager.getPrefix()+"§aDu hast Erfolgreich das Pet gekauft und Ausgewaehlt.");
    		}else{
    			p.sendMessage(manager.getPrefix()+"§c Du hast nicht genug Coins!");
    		}
    	}
    	
    	
    }

	@Override
	public ItemStack getIcon() {
		return UtilItem.Item(new ItemStack(383,1,(byte)91), desc, "§a"+getName());
	}

	@Override
	public String getName() {
		return "Sheep";
	}
    
}
