package helpertools.entities;

import helpertools.Common_Registry;
import helpertools.ConfigurationFactory;
import helpertools.Helpertoolscore;
import helpertools.util.Bomb_Helper;

import java.util.Random;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BombProjectile_Entity extends EntityThrowable{
	
   public int Bomb_Type;

   //all else spawn
   public BombProjectile_Entity(World world) {
       super(world);
       this.Bomb_Type = 0;
   }
   
   //spawn from a player
   public BombProjectile_Entity(World world, EntityPlayer player, int type) {
       super(world,player);       
       this.Bomb_Type = type;  
   }
   
   //stock drop spawn for non entity use
   public BombProjectile_Entity(World world, double d, double e, double f, int type) {
       super(world);       
       this.setPosition(d, e, f);
       this.Bomb_Type = type;  
      
   }
   //spawns with a designated projectory
   public BombProjectile_Entity(World world, double d, double e, double f, int type,
		   double x, double y, double z, float f1, float f2) {
       super(world);       
       this.setPosition(d, e, f);
       this.Bomb_Type = type;  
       
       this.setThrowableHeading(x, y, z, f1, f2);
   }
   
   
   @Override
   public void writeEntityToNBT(NBTTagCompound tag)
   {
       super.writeEntityToNBT(tag);

       tag.setInteger("Bomb_Type", Bomb_Type);
   }
   
   @Override
   public void readEntityFromNBT(NBTTagCompound tag)
   {
       super.readEntityFromNBT(tag);

       Bomb_Type = tag.getInteger("Bomb_Type");
   }
   
   
   @Override
   protected void entityInit() {
	   super.entityInit();
	   this.dataWatcher.addObject(22, 0);
   }
   
   public int read_type(){return this.dataWatcher.getWatchableObjectInt(22);}
   
   //spawns particle effects if enabled
   public void onUpdate()
   {
	   
	   if(!this.worldObj.isRemote){ 
		   DataWatcher dw = this.getDataWatcher();	       
	       dw.updateObject(22, Bomb_Type);
	   }
	   
	   int i;
       super.onUpdate();
       for (i = 0; i < 1; ++i)
       {
    	   this.worldObj.spawnParticle("smoke", this.posX + this.motionX * (double)i / 4.0D, this.posY + .8+ this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, 0, 0 , 0);
           this.worldObj.spawnParticle("cloud", this.posX + this.motionX * (double)i / 4.0D, this.posY + .8+ this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, 0, .1 , 0);
       }   
   }
   
   
   
   
   
   
   //stable references
   Block dirtblock = Blocks.dirt;
   Block pblock = Common_Registry.LooseDirtBlock;
   
   //type transformer
   public Block p_block(int type){
	Block block = Blocks.dirt;
	
	if(type == 0){block = Common_Registry.LooseDirtBlock;}
	if(type == 1){block = Blocks.sand;}	   
	if(type == 2){block = Blocks.gravel;}	  
	   return block;
	   	   
   }   
   public Block d_block(int type){
		Block block = Blocks.dirt;	
		
		if(type == 0){block = Blocks.dirt;}	
		else{block = p_block(type);}
		   return block;
		   	   
	   }
   
   
   
   //=================================================================================================//
   
   @Override
  protected void onImpact(MovingObjectPosition mop) {	   
	   int sideHit = mop.sideHit;
	   pblock = p_block(Bomb_Type);
	   dirtblock = d_block(Bomb_Type);
	   
	   //equipment support or creative amplification
	   int amp = 20;
	   
	   int X = (int) this.posX;
	   int Y = (int) this.posY;
	   int Z = (int) this.posZ;
	   
	   if (mop.entityHit != null ){return;}
	   
	   if(this.worldObj.isRemote){ 
		      int j4 = mop.blockY-1;   
		      if(sideHit == 0){	 j4 = j4-2;}
		      Bomb_Helper.particlecloud(worldObj, mop.blockX-1, j4, mop.blockZ-2);	
		      if(Bomb_Type == 3){Bomb_Helper.particlecloud2(worldObj, mop.blockX-1, j4, mop.blockZ-2);}
	   }
	   //serverside code
	   if(!this.worldObj.isRemote){
		   
      
      this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)1.9, false);
      
      if(Bomb_Type <= 2){Bomb_Helper.simple_generate(worldObj, pblock, dirtblock, X, Y-1, Z, sideHit);}
      if(Bomb_Type == 3){
    	  Bomb_Helper.sphere_miracle_bomb(worldObj, 4+amp, X, Y, Z);
    	  Bomb_Helper.sphere_miracle_bomb(worldObj, 2+amp, X, Y, Z);
    	  Bomb_Helper.sphere_miracle_bomb(worldObj, 1+amp, X, Y, Z);}
      if(Bomb_Type == 4){
    	  Bomb_Helper.sphere_frost_bomb(worldObj, 4+amp, X, Y, Z);
    	  Bomb_Helper.sphere_frost_bomb(worldObj, 2+amp, X, Y, Z);
    	  Bomb_Helper.sphere_frost_bomb(worldObj, 1+amp, X, Y, Z);
    	  }
      if(Bomb_Type == 5){
    	  Bomb_Helper.sphere_desert_bomb(worldObj, 6+amp, X, Y, Z);
    	  Bomb_Helper.sphere_desert_bomb(worldObj, 3+amp, X, Y, Z);
    	  Bomb_Helper.sphere_desert_bomb(worldObj, 2+amp, X, Y, Z);
    	  }
      
      //if(Bomb_Type == 3){Bomb_Helper.sphere_miracle_bomb(worldObj, 4, X, Y, Z);}
      
      //Ensures the entity itself is deleted once its objective is reached
      //otherwise it will slide along the ground for a while
      this.setDead();
	   }
   }
   
  
}