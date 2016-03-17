/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import java.util.Objects;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class AnyItemStack
{
    private AnyItem type;
    private int amount;
    
    public AnyItemStack(AnyItem type, int amount)
    {
        if(amount < 0 || type == null)
            throw new IllegalArgumentException();
        
        this.type = type;
        this.amount = amount;
    }
    
    public AnyItem getType()
    {
        return type;
    }
    
    public int getAmount()
    {
        return amount;
    }
    
    public void setType(AnyItem type)
    {
        if(type == null)
            throw new IllegalArgumentException();
        
        this.type = type;
    }
    
    public void setAmount(int amount)
    {
        if(amount < 0)
            throw new IllegalArgumentException();
        
        this.amount = amount;
    }
    
    public ItemStack make(CustomItemsAPI api)
    {
        return type.make(api, amount);
    }
    
    public boolean is(CustomItemsAPI api, ItemStack stack)
    {
        return stack != null && stack.getAmount() == amount && type.isOf(api, stack);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof AnyItemStack)
        {
            AnyItemStack os = (AnyItemStack)o;
            
            return os.amount == amount && os.type.equals(type);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + this.amount;
        return hash;
    }
}
