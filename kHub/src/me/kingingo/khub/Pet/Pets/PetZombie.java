package me.kingingo.khub.Pet.Pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.khub.Pet.RideEntity.RideCow;
import me.kingingo.khub.Pet.RideEntity.RideZombie;
import me.kingingokhub.Pet.AnvilGUI;
import me.kingingokhub.Pet.PetInterface;
import me.kingingokhub.Pet.PetManager;
import me.kingingokhub.Pet.RideInterface;
import me.kingingokhub.Pet.AnvilGUI.AnvilClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetZombie implements Listener,PetInterface {

	private HashMap<Player,RideInterface> sheeps = new HashMap<>();
	private RideZombie e =null;
	private Inventory SelectInv=null;
	private Inventory BuyInv=null;
	private Inventory OptionInv=null;
	private Inventory CointInv=null;
	private List<String> desc=null;
	private PetManager manager;
	
    public PetZombie(PetManager manager) {
    	this.manager=manager;
    	Bukkit.getPluginManager().registerEvents(this,manager.getManager().getInstance());
    	Description();
	}
    
    public Entity getEntity(Player p){
    	if(sheeps.containsKey(p))return sheeps.get(p).getBukkitEntity();
    	return null;
    }
    
    @Override
	public Permission getPermission() {
		return Permission.PET_ZOMBIE;
	}
    
    @Override
	public HashMap<Player,RideInterface> getList() {
		return sheeps;
	}
    
    @Override
	public void remove(Player p) {
		sh=(Zombie)sheeps.get(p).getBukkitEntity();
		sh.remove();
	}
    
    //Sheep-/-FALSE/TRUE-/-FALSE/TRUE-/-NAME
    @Override
	public void Save(Player p) {
		if(sheeps.containsKey(p)){
			sh = (Zombie)sheeps.get(p).getBukkitEntity();
			manager.UpdateTable(p.getName(), getName()+"-/-"+getPass(p)+"-/-"+!sh.isBaby()+"-/-"+sh.getCustomName());
		}
	}

    public boolean getPass(Player ps){
    	p=ps;
    	sh=(Zombie)sheeps.get(p).getBukkitEntity();
    	
    	if(sh.getPassenger()==null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    public void AddPlayer(Player p){
    	
    	CraftWorld cw = (CraftWorld)p.getWorld();
    	final RideZombie rp = new RideZombie(cw.getHandle());
		rp.setLocation(p.getLocation().getX(), p.getLocation().getY(),p.getLocation().getZ(),p.getLocation().getYaw(),p.getLocation().getPitch());
		cw.getHandle().addEntity(rp,SpawnReason.CUSTOM);
    	
    	sheeps.put(p, rp);
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
    	return 100;
    }
    
    public int getCoins(){
    	return 25000;
    }
    
    public Inventory getOptionInv(){
    	if(OptionInv == null){
    		OptionInv=Bukkit.createInventory(null, 9,"§aPet: Zombie");
    		OptionInv.setItem(0, UtilItem.RenameItem(new ItemStack(383,1,(byte)54), "§aPet: Zombie"));
    		OptionInv.setItem(1, UtilItem.Item(new ItemStack(340,1), desc, "§6Beschreibung"));
    		OptionInv.setItem(2, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		OptionInv.setItem(3, UtilItem.RenameItem(new ItemStack(Material.ANVIL,1), "§bNamen Aendern"));
    		OptionInv.setItem(4, UtilItem.RenameItem(new ItemStack(Material.BONE,1), "§bBaby/Erwachsen"));
    		OptionInv.setItem(5, UtilItem.RenameItem(new ItemStack(397,1), "§bSklett"));
    		OptionInv.setItem(6, UtilItem.RenameItem(new ItemStack(160,1,(byte)8), " "));
    		OptionInv.setItem(7, UtilItem.RenameItem(new ItemStack(351,1,(byte)10),  "§aAusgewaehlt"));
    		OptionInv.setItem(8, UtilItem.RenameItem(new ItemStack(330,1),  "§cZurück"));
    	}
    	return OptionInv;
    }
    
    public Inventory getBuyInv(){
    	if(BuyInv == null){
    		BuyInv=Bukkit.createInventory(null, 9,"§aPet: Zombie");
    		BuyInv.setItem(0, UtilItem.RenameItem(new ItemStack(383,1,(byte)54), "§aPet: Zombie"));
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
    		CointInv=Bukkit.createInventory(null, 9,"§aPet: Zombie");
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
    		SelectInv=Bukkit.createInventory(null, 9,"§aPet: Zombie");
    		SelectInv.setItem(0, UtilItem.RenameItem(new ItemStack(383,1,(byte)54), "§aPet: Zombie"));
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
    
    String displayname;
    Player p;
    Zombie sh;
    
    public void Join(String[] i,Player pl){
    	p=pl;
    	CraftWorld cw = (CraftWorld)p.getWorld();
		e = new RideZombie(cw.getHandle());
		e.setLocation(p.getLocation().getX(), p.getLocation().getY(),p.getLocation().getZ(),p.getLocation().getYaw(),p.getLocation().getPitch());
		cw.getHandle().addEntity(e,SpawnReason.CUSTOM);
    	
    	sheeps.put(p, e);
    	sh=(Zombie)e.getBukkitEntity();
    	if(i[2].equalsIgnoreCase("TRUE")){
    		sh.setBaby(false);
    	}else{
    		sh.setBaby(true);
    	}
    	sh.setCustomName(i[3]);
    	if(i[1].equalsIgnoreCase("TRUE")){
    		Entity e=sh.getWorld().spawnEntity(sh.getLocation(), EntityType.SKELETON);
    		((Skeleton)e).setCustomName(sh.getCustomName());
    		sh.setPassenger(e);
    	}
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
		
		if(!ev.getClickedInventory().getTitle().equalsIgnoreCase("§aPet: Zombie"))return;
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
    	}else if(displayname.equalsIgnoreCase("§bSklett")){
    		sh=(Zombie)sheeps.get(p).getBukkitEntity();
    		if(sh.getPassenger()!=null){
    			sh.getPassenger().remove();
    			sh.setPassenger(null);
    		}else{
    			Entity e=sh.getWorld().spawnEntity(sh.getLocation(), EntityType.SKELETON);
    			((Skeleton)e).setCustomName(sh.getCustomName());
        		sh.setPassenger(e);
    		}
    	}else if(displayname.equalsIgnoreCase("§bNamen Aendern")){

    		AnvilGUI gui = new AnvilGUI(p, manager.getManager().getInstance(),new AnvilGUI.AnvilClickEventHandler(){

				@Override
				public void onAnvilClick(AnvilClickEvent event) {
					if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){
				         event.getName();
						 p.sendMessage(manager.prefix+"§a Das Pet wurde umbenannt zu §e"+event.getName());
						 sh =(Zombie) sheeps.get(p).getBukkitEntity();
						 sh.setCustomName(event.getName().replace("&", "§"));
					}
				}});
    		
				 ItemStack renamed = UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Pet Name");
				 gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, renamed);
				 gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));

				 gui.open();
    		
    	}else if(displayname.equalsIgnoreCase("§bBaby/Erwachsen")){
    		if(sheeps.containsKey(p)){
    			sh = (Zombie) sheeps.get(p).getBukkitEntity();
    			if(!sh.isBaby()){
    				sh.setBaby(true);
    				p.sendMessage(manager.prefix+"§aDas Pet ist nun ein Baby.");
    			}else{
    				sh.setBaby(false);
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
		return UtilItem.Item(new ItemStack(383,1,(byte)54), desc, "§a"+getName());
	}

	@Override
	public String getName() {
		return "Zombie";
	}
    
}
