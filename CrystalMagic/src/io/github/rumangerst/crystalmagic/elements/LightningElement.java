/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class LightningElement extends Element
{
    
    public LightningElement(String id, String description, String... name)
    {
        super(id, description, name);
    }
    
    @Override
    public boolean canEnchant()
    {
        return false;
    }
    
    @Override
    public void enchant(ItemStack stack, int level)
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
        return (int)(level * level * 6.0);
    }
    
    @Override
    public void execute(Entity caster, int level)
    {       
        for(int i = 1; i <= level; ++i)
        {
           Location loc;
           
           if(i == 1)
           {
               loc = caster.getLocation();
           }
           else
           {
               loc = caster.getLocation().clone();               
               loc.add(( 0.5 - CrystalMagicPlugin.RANDOM.nextDouble() ) * 2.0, 0, ( 0.5 - CrystalMagicPlugin.RANDOM.nextDouble() ) * 2.0);
           }
            
            if(level >= 1)
            {
                caster.getWorld().strikeLightning(Element.getGroundBlockLocation(loc));

                if(level >= 2)
                {
                    caster.getWorld().createExplosion(Element.getGroundBlockLocation(loc), 2.0f, false);
                }
            }
        }
    }
    
    
}
