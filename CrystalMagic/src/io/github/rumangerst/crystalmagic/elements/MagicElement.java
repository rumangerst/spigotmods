/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author ruman
 */
public class MagicElement extends Element
{
    
    public MagicElement(String id, String description, String... name)
    {
        super(id, description, name);
    }
    
    @Override
    public boolean canEnchant()
    {
        return false;
    }
    
    @Override
    public void enchant(ItemStack stack)
    {
        
    }
    
    @Override
    public boolean canExecute()
    {
        return true;
    }
    
    @Override
    public int getManaCost(int level)
    {
        return (int)(level * level * 4.6);
    }
    
    @Override
    public void execute(Entity caster, int level)
    {      
        double radius = level + 0.8;
        Element.playRadiusEffect(caster, level, Effect.MAGIC_CRIT, 0);
        
        double strength = 0.15;
        
        switch(level)
        {
            case 1:
                strength = 1.0;
                break;
            case 2:
                strength = 2.0;
                break;
            case 3:
                strength = 3.5;
                break;
            case 4:
                strength = 4.8;
                break;
        }
        
        for(Entity e : caster.getNearbyEntities(radius, radius, radius))
        {
            if(e instanceof LivingEntity && e != caster)
            {
                LivingEntity p = (LivingEntity)e;
                
                /*Vector velocity = caster.getVelocity();
                velocity.setY(-velocity.getY());  
                
                p.getLocation().getDirection().
                
                velocity = velocity.multiply(strength);                
                p.setVelocity(velocity);*/
                
                Vector vel = new Vector(p.getLocation().getX() - caster.getLocation().getX(), 0, p.getLocation().getZ() - caster.getLocation().getZ());
                
                if(caster instanceof LivingEntity)
                {
                    vel.setY(-caster.getLocation().getDirection().getY());
                }
                else
                {
                    vel.setY(-caster.getVelocity().getY());
                }
                
                vel = vel.normalize().multiply(strength);
                e.setVelocity(vel);
            }
        }
    }
    
}
