/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

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
    
    public AnyItem(Material material, MaterialData data)
    {
        this(material,data.getData());
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
    
    public boolean isOf(CustomItemsAPI api, ItemStack stack)
    {
        if(stack == null)
            return false;
        
        if(isVanilla())
        {
            return stack.getType() == vanillamaterial && stack.getData().getData() == vanilladata;
        }
        else
        {
            return api.getCustomItem(customitem).isOf(stack);
        }
    }
    
    @Override
    public String toString()
    {
        if(isVanilla())
            return vanillamaterial + ":" + vanilladata;
        else
            return customitem;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof AnyItem)
        {
            AnyItem oi = (AnyItem)o;
            
            if(oi.isVanilla() != isVanilla())
                return false;
            if(oi.isVanilla())
            {
                return oi.vanillamaterial == vanillamaterial && oi.vanilladata == vanilladata;
            }
            else
            {
                return customitem.equals(oi.customitem);
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.customitem);
        hash = 13 * hash + Objects.hashCode(this.vanillamaterial);
        hash = 13 * hash + this.vanilladata;
        return hash;
    }
}
