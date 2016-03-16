/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class Element extends CustomItem
{
    private String[] names;
    
    public Element(String id, String ... name)
    {
        super(id, Material.BLAZE_POWDER, (byte)0, name[0]);
        names = name;
    }
    
    @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);
        
        NBTAPI.setString(stack, "display/Name", "Element: " + names[0]);
    }
    
    public boolean canEnchant()
    {
        return false;
    }
    
    public boolean canExecute()
    {
        return false;
    }
    
    /**
     * Enchants an itemstack
     * @param stack 
     */
    public void enchant(ItemStack stack)
    {
        
    }
    
    /**
     * Executes the spell around/at caster. Caster can be a any entity
     * @param caster 
     * @param level 
     */
    public void execute(Entity caster, int level)
    {
        
    }
}