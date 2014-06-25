package me.kingingo.khub.Pet.RideEntity;
import java.lang.reflect.Field;

import me.kingingo.khub.Pet.RideInterface;
import net.minecraft.server.v1_7_R3.EntityCow;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.World;

public class RideCow extends EntityCow implements RideInterface{
public float walkspeed = 0.35F;

	public RideCow(World world) {
		super(world);
	}
	
	@Override
	public void e(float sideMot, float forMot) {
	    if (this.passenger == null || !(this.passenger instanceof EntityHuman)) {
	        super.e(sideMot, forMot);
	        this.W = 0.5F;    // Make sure the entity can walk over half slabs, instead of jumping
	        return;
	    }
	    
	    EntityHuman human = (EntityHuman) this.passenger;
	 
	    this.lastYaw = this.yaw = this.passenger.yaw;
	    this.pitch = this.passenger.pitch * 0.5F;
	 
	    this.b(this.yaw, this.pitch);
	    this.aO = this.aM = this.yaw;
	 
	    sideMot = ((EntityLiving) this.passenger).bd * 0.5F;
	    forMot = ((EntityLiving) this.passenger).be;
	 
	    if (forMot <= 0.0F) {
	        forMot *= 0.25F;   
	    }
	    sideMot *= 0.75F;    
	 
	    float speed = walkspeed;   
	    this.i(speed);   
	    super.e(sideMot, forMot);    
	 try{
	    Field jump = null;
	    jump = EntityLiving.class.getDeclaredField("bc");
	    jump.setAccessible(true);
	 
	    if (jump != null && this.onGround) {   
	        try {
	            if (jump.getBoolean(this.passenger)) {
	                double jumpHeight = 0.5D;
	                this.motY = jumpHeight;    
	            }
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }
	  } catch (Exception error){
		  error.printStackTrace();
	  }
	}
	
}
