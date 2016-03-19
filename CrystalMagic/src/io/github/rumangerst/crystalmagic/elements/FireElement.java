/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class FireElement extends Element
{
    
    public FireElement(String id, String description, String... name)
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
        return (int)(level * level * 6.0);
    }
    
    @Override
    public void execute(Entity caster, int level)
    {      
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.2f);
        
        for (int x = caster.getLocation().getBlockX() - level - 1; x <= caster.getLocation().getBlockX() + level - 1; ++x)
        {
            for (int z = caster.getLocation().getBlockZ() - level - 1; z <= caster.getLocation().getBlockZ() + level - 1; ++z)
            {
                Location loc = new Location(caster.getWorld(), x, caster.getLocation().getY(), z);
                loc = Element.getGroundBlockLocation(loc).add(0, 1, 0);

                if (loc.getBlock().getType() == Material.AIR)
                {
                    loc.getBlock().setType(Material.FIRE);
                }
            }
        }        
    }
    
}
