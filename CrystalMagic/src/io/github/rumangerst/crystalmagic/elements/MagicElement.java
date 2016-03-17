/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.customitems.CustomItemsAPI;
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
        switch(level)
        {
            case 1:
                return 10;
            case 2:
                return 30;
            case 3:
                return 80;
            case 4:
                return 180;
            default:
                return 1000;
        }
    }
    
    @Override
    public void execute(Entity caster, int level)
    {      
        int radius = level * 2;
        double strength = 0.15;
        
        switch(level)
        {
            case 1:
                strength = 0.15;
                break;
            case 2:
                strength = 0.3;
                break;
            case 3:
                strength = 0.7;
                break;
            case 4:
                strength = 1.0;
                break;
        }
        
        for(Entity e : caster.getNearbyEntities(radius, radius, radius))
        {
            if(e instanceof LivingEntity)
            {
                LivingEntity p = (LivingEntity)e;
                
                Vector velocity = caster.getVelocity();
                velocity.setY(-velocity.getY());                
                
                velocity = velocity.multiply(strength);                
                p.setVelocity(velocity);
            }
        }
    }
    
}
