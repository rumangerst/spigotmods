/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A custom item or a vanilla item
 * @author ruman
 */
public class AnyItem
{
    private String customitem = null;
    private Material vanillamaterial;
    private byte vanilladata;
    
    public AnyItem(String item)
    {
        if(item == null)
            throw new NullPointerException();
        
        customitem = item;
    }
    
    public AnyItem(Material material, byte data)
    {
        vanillamaterial = material;
        vanilladata = data;
    }
    
    public AnyItem(Material material)
    {
        this(material,(byte)0);
    }
    
    public boolean isVanilla()
    {
        return customitem == null;
    }
    
    public String getCustomItemID()
    {
        if(isVanilla())
            throw new UnsupportedOperationException();
        
        return customitem;
    }
    
    public Material getVanillaType()
    {
        if(!isVanilla())
            throw new UnsupportedOperationException();
        
        return vanillamaterial;
    }
    
    public byte getVanillaData()
    {
        if(!isVanilla())
            throw new UnsupportedOperationException();
        
        return vanilladata;
    }
    
    public ItemStack make(CustomItemsAPI api, int amount)
    {
        if(isVanilla())
            return new ItemStack(vanillamaterial, amount, (short)0, vanilladata);
        else
            return api.getCustomItem(customitem).make(amount);
    }
    
    @Override
    public String toString()
    {
        if(isVanilla())
            return vanillamaterial + ":" + vanilladata;
        else
            return customitem;
    }
}
