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
        return level * level * 4;
    }
    
    @Override
    public void execute(Entity caster, int level)
    {       
        float strength = 1.0f;
        
        switch(level)
        {
            case 1:
                strength = 2.0f;
                break;
            case 2:
                strength = 4.0f;
                break;
            case 3:
                strength = 6.0f;
                break;
            case 4:
                strength = 8.0f;
                break;
        }
        
        caster.getWorld().createExplosion(caster.getLocation(), strength, false);
    }
    
    
}
