package me.kingingo.khub.Pet;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import lombok.Getter;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.khub.HubManager;
import me.kingingo.khub.Pet.Pets.PetChicken;
import me.kingingo.khub.Pet.Pets.PetCow;
import me.kingingo.khub.Pet.Pets.PetIronGolem;
import me.kingingo.khub.Pet.Pets.PetPig;
import me.kingingo.khub.Pet.Pets.PetSheep;
import me.kingingo.khub.Pet.Pets.PetSpider;
import me.kingingo.khub.Pet.Pets.PetWolf;
import me.kingingo.khub.Pet.Pets.PetZombie;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.Navigation;
import net.minecraft.server.v1_7_R3.PathEntity;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetManager implements Listener{
	
	public Inventory HauptInv=Bukkit.createInventory(null, 18,"§aPet Shop");
	public ArrayList<PetInterface> Pets = new ArrayList<>();
	@Getter
	public String prefix="§a[Pet]:§r ";
	public Random rdm=new Random();
	@Getter
	public HubManager manager;
	
	public PetManager(HubManager manager){
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
		this.manager=manager;
		load();
	}
	
	public void disable(){
		CustomEntityType.unregisterEntities();
	}
	
	public void load(){
		CustomEntityType.registerEntities();
		CreateTable();
		PetSheep ps = new PetSheep(this);
		Pets.add(ps);
		PetZombie pe = new PetZombie(this);
		Pets.add(pe);
		PetWolf pw = new PetWolf(this);
		Pets.add(pw);
		PetIronGolem pig = new PetIronGolem(this);
		Pets.add(pig);
		PetCow pc = new PetCow(this);
		Pets.add(pc);
		PetSpider pz = new PetSpider(this);
		Pets.add(pz);
		PetChicken pcc = new PetChicken(this);
		Pets.add(pcc);
		PetPig pp = new PetPig(this);
		Pets.add(pp);
		for(PetInterface pi : Pets){
			HauptInv.addItem(pi.getIcon());
		}
	}
	
	double dis=0;	
	RideInterface e;
	
	private HashMap<String, Integer> _failedAttempts=new HashMap<>();
	Block targetBlock;
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST)return;
		for(PetInterface pi : Pets){
			for(Player p : pi.getList().keySet()){
				e = pi.getList().get(p);
				dis=e.getBukkitEntity().getLocation().distance(p.getLocation());
				Location petSpot = e.getBukkitEntity().getLocation();
			      Location ownerSpot = p.getLocation();
			      int xDiff = Math.abs(petSpot.getBlockX() - ownerSpot.getBlockX());
			      int yDiff = Math.abs(petSpot.getBlockY() - ownerSpot.getBlockY());
			      int zDiff = Math.abs(petSpot.getBlockZ() - ownerSpot.getBlockZ());

			      if (xDiff + yDiff + zDiff > 4)
			      {
			    		  //EntityCreature ec = ((CraftCreature)e.getBukkitEntity()).getHandle();
					       // Navigation nav = ec.getNavigation();

					        int xIndex = -1;
					        int zIndex = -1;
					        targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
					        while ((targetBlock.isEmpty()) || (targetBlock.isLiquid()))
					        {
					          if (xIndex < 2) {
					            xIndex++;
					          } else if (zIndex < 2)
					          {
					            xIndex = -1;
					            zIndex++;
					          }
					          else {
					            return;
					          }
					          targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
					        }

			        if(!this._failedAttempts.containsKey(p.getName())){
			        	ClearPetGoals(e.getBukkitEntity());
			        	this._failedAttempts.put(p.getName(), Integer.valueOf(0));
			        }
			        //nav.a(true);
			        if (((Integer)this._failedAttempts.get(p.getName())).intValue() > 3)
			        {
			          e.getBukkitEntity().teleport(p);
			          this._failedAttempts.put(p.getName(), Integer.valueOf(0));
			        }else if (/*!nav.a(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ(), 2.0D)*/ !moveEntity(e.getBukkitEntity(),targetBlock.getLocation(),2.0F)){
			          if (e.getBukkitEntity().getFallDistance() == 0.0F){
			            this._failedAttempts.put(p.getName(), Integer.valueOf(((Integer)this._failedAttempts.get(p.getName())).intValue() + 1));
			          }
			        }else{
			          this._failedAttempts.put(p.getName(), Integer.valueOf(0));
			        }
			      }
			}
		}
	}
	
	public boolean moveEntity(Entity e,Location l,float speed){
		   if(((CraftEntity)e).getHandle() instanceof EntityCreature || ((CraftEntity)e) instanceof CraftCreature || e instanceof CraftCreature){
		     EntityCreature entity = ((CraftCreature) e).getHandle();
		     Navigation nav = entity.getNavigation();
		     nav.a(true);
		     PathEntity path = nav.a(l.getX(),l.getY(),l.getZ());
		     entity.pathEntity = path;
		     return nav.a(path, speed);
		   }else if(((CraftEntity)e).getHandle() instanceof EntityInsentient){
		    EntityInsentient ei = ((EntityInsentient)((CraftEntity) e).getHandle());
		    Navigation nav = ei.getNavigation();
		    nav.a(true);
		    ei.getControllerMove().a(l.getX(),l.getY(),l.getZ(),(float)speed);
		    return nav.a(l.getX(),l.getY(),l.getZ(),(float)speed);
		   }else if(((CraftEntity)e).getHandle() instanceof EntityLiving){
		    EntityLiving entity = (EntityLiving)((CraftEntity)e).getHandle();
		    entity.move(l.getX(), l.getY(),l.getZ());
		    entity.e((float)l.getX(), (float)l.getZ());
		    return true;
		   }else{
		    System.out.println("Keiner");
		    return false;
		   }
		  }
	
	public void CreateAccount(String p){
		manager.getMysql().Update("INSERT INTO kpet (name,pet) values ('"+p.toLowerCase()+"','null');");
	}
	
	public void CreateTable(){
		manager.getMysql().Update("CREATE TABLE IF NOT EXISTS kpet(name varchar(30),pet varchar(100))");
	}
	
	public void UpdateTable(String p,String pet){
		manager.getMysql().Update("UPDATE `kpet` SET pet='"+pet+"' WHERE name='"+p.toLowerCase()+"'");
	}
	
	
	ResultSet rs;
	public void getPet(Player p){
		String petname=null;
		try{
			rs =manager.getMysql().Query("SELECT pet FROM kpet WHERE name='" + p.getName().toLowerCase() + "'");
			
			while(rs.next()){
				petname=rs.getString(1);
			}
 			
			rs.close();
		}catch (Exception err){	
			System.err.println(err);
		}
		
		if(petname!=null&&!petname.equalsIgnoreCase("null")){
			String[] split=petname.split("-/-");
			
			for(PetInterface pi : Pets){
				if(pi.getName().equalsIgnoreCase(split[0])){
					pi.Join(split,p);
					break;
				}
			}
			
		}else{
			CreateAccount(p.getName());
		}

	}
	
	@EventHandler
	public void Int(PlayerInteractEntityEvent ev){
		Player p=ev.getPlayer();
		if(p.getItemInHand()!=null&&p.getItemInHand().getType()==Material.NAME_TAG){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	  public void onBurn(EntityCombustEvent event) {
	    event.setCancelled(true);
	  }

	@EventHandler
	public void Inv(PlayerInteractEvent ev){
		if(Action.RIGHT_CLICK_BLOCK==ev.getAction()||Action.RIGHT_CLICK_AIR==ev.getAction()||Action.LEFT_CLICK_BLOCK==ev.getAction()||Action.LEFT_CLICK_AIR==ev.getAction()){
			Player p=ev.getPlayer();
			if(p.getItemInHand()!=null&&p.getItemInHand().getType()==Material.NAME_TAG&&p.getItemInHand().getItemMeta().getDisplayName()!=null&&p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§aPet Shop")){
				p.openInventory(HauptInv);
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		Player p=ev.getPlayer();
		PetInterface pet=null;
		for(PetInterface pi : Pets){
			if(pi.getList().containsKey(p)){
				pet=pi;
			}
		}
		
		if(pet!=null){
			pet.Save(p);
			pet.remove(p);
			pet.getList().remove(p);
		}
		
	}
	
	private Field _goalSelector;
	private Field _targetSelector;
	public void ClearPetGoals(Entity pet){
		
			try
		    {
		      this._goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
		      this._goalSelector.setAccessible(true);
		      this._targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
		      this._targetSelector.setAccessible(true);

		      EntityCreature creature = ((CraftCreature)pet).getHandle();

		      PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(((CraftWorld)pet.getWorld()).getHandle().methodProfiler);

		      goalSelector.a(0, new PathfinderGoalLookAtPlayer(creature, EntityHuman.class, 6.0F));
		      goalSelector.a(1, new PathfinderGoalRandomLookaround(creature));

		      this._goalSelector.set(creature, goalSelector);
		      this._targetSelector.set(creature, new PathfinderGoalSelector(((CraftWorld)pet.getWorld()).getHandle().methodProfiler));
		    }
		    catch (IllegalArgumentException e)
		    {
		      e.printStackTrace();
		    }
		    catch (IllegalAccessException e)
		    {
		      e.printStackTrace();
		    }
		    catch (NoSuchFieldException e)
		    {
		      e.printStackTrace();
		    }
		    catch (SecurityException e)
		    {
		      e.printStackTrace();
		    }	
		
	  }
	
	@EventHandler
	  public void onEntityTarget(EntityTargetEvent event)
	  {
	    if (((event.getEntity() instanceof org.bukkit.entity.Creature)))
	    {
	      event.setCancelled(true);
	    }
	  }
	
	@EventHandler
	public void Spawn(CreatureSpawnEvent ev){
		if(ev.getSpawnReason()!=SpawnReason.CUSTOM)ev.setCancelled(true);
	}
	
	@EventHandler
	public void Click(InventoryClickEvent ev){
		if(!(ev.getWhoClicked() instanceof Player)){
			return;
		}
		if(ev.getClickedInventory()==null||!ev.getClickedInventory().getTitle().equalsIgnoreCase("§aPet Shop"))return;
		ev.setCancelled(true);
		Player p = (Player)ev.getWhoClicked();
		p.closeInventory();
		for(PetInterface pi: Pets){
			if(pi.getIcon().getType()==ev.getCurrentItem().getType()&&ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(pi.getIcon().getItemMeta().getDisplayName())){
				if(manager.getPManager().hasPermission(p, pi.getPermission())){
					if(pi.getList().containsKey(p)){
						p.openInventory(pi.getOptionInv());
					}else{
						p.openInventory(pi.getSelectInv());
					}
				}else{
					p.openInventory(pi.getBuyInv());
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		Player p=ev.getPlayer();
		p.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aPet Shop"));
		getPet(p);
	}
	
	@EventHandler
	public void Damage(EntityDamageByEntityEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent e){
		e.setCancelled(true);
	}
	
}
