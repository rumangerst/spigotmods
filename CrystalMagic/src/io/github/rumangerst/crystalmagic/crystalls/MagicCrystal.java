/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicCrystal extends MagicGem
{
    
    public MagicCrystal(String id, String name, CrystalMagicPlugin plugin)
    {
        super(id, Material.NETHER_STAR, (byte)0, name, plugin);
    }
    
    @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);
        
        String name;
        
        if(getElements(stack).isEmpty())
        {
            name = "Magischer Kristall";
        }
        else
        {
            if(isProjectile(stack))
            {
                name = "Magischer Projektilkristall";
            }
            else
            {
                name = "Magischer Elementkristall";
            }
            
            if (isInstant(stack))
            {
                name += " (Speicher)";
            }
            else
            {
                name += " (Konduktor)";
            }
        }
        
        NBTAPI.setString(stack, "display/Name", name);
        
    }
    
    public boolean isInstant(ItemStack stack)
    {
        return NBTAPI.getBool(stack, "crystalmagic/magiccrystal/instant", false);
    }
    
    public void setInstant(ItemStack stack, boolean instant)
    {
        NBTAPI.setBool(stack, "crystalmagic/magiccrystal/instant", instant);
        transform(stack);
    }
    
    public boolean isProjectile(ItemStack stack)
    {
        return NBTAPI.getBool(stack, "crystalmagic/magiccrystal/projectile", false);
    }
    
    public void setProjectile(ItemStack stack, boolean projectile)
    {
        NBTAPI.setBool(stack, "crystalmagic/magiccrystal/projectile", projectile);
        transform(stack);
    }
}
