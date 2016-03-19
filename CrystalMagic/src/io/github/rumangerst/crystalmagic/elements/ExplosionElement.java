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
public class ExplosionElement extends Element
{
    
    public ExplosionElement(String id, String description, String... name)
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
        caster.getWorld().createExplosion(caster.getLocation(), level, false);
    }
    
    
}
