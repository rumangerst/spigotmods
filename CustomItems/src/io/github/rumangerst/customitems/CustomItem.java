/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import io.github.rumangerst.customitems.nbt.NBTAPI;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * A basic custom item without any effects. Extend this class to define your own items.
 * Implements listener and will automatically added to the event system.
 * @author ruman
 */
public class CustomItem implements Listener
{
    String id;
    protected Material type;
    protected byte data;    
    protected String name;
    protected String[] lore = new String[0];
    
    public CustomItem(String id, Material type, byte data, String name)
    {
        if(id.isEmpty() || id.contains(" "))
            throw new IllegalArgumentException("Illegal ID for custom item!");
        
        this.id = id;
        this.type = type;
        this.data = data;
        this.name = name;
    }
    
    public CustomItem(String id, Material type, MaterialData data, String name)
    {
        this(id, type, data.getData(), name);
    }
    
    public CustomItem(String id, Material type, String name)
    {
        this(id, type, (byte)0, name);
    }
    
    public String getId()
    {
        return id;
    }
    
    public Material getType()
    {
        return type;
    }
    
    public byte getTypeData()
    {
        return data;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String[] getLore()
    {
        return lore;
    }
    
    public CustomItem setLore(String ... lore)
    {
        this.lore = lore;
        return this;
    }
    
    /**
     * Transforms a given stack into a stack of this item
     * @param stack 
     */
    public void transform(ItemStack stack)
    {
        stack.setType(type);
        stack.setData(new MaterialData(type, data));
        NBTAPI.setString(stack, "customitem/id", id);
        
        NBTAPI.setString(stack, "display/Name", name);
        NBTAPI.setStringList(stack, "display/Lore", lore);
    }
    
    /**
     * Creates a stack of this item
     * @param amount
     * @return 
     */
    public ItemStack make(int amount)
    {
        ItemStack stack = new ItemStack(type, amount, (short)0, data);
        transform(stack);
        
        return stack;
    }
    
    /**
     * Checks if a stack's custom type is this item. Returns false if stack is null.
     * @param stack
     * @return 
     */
    public boolean isOf(ItemStack stack)
    {
        if(stack == null)
            return false;
        
        return NBTAPI.getString(stack, "customitem/id", "").equals(id);
    }
    
    /**
     * Checks if the stack is this custom item and if the stack size is exactly 1
     * @param stack
     * @return 
     */
    public boolean isOfOnly(ItemStack stack)
    {
        return isOfOnly(stack, 1);
    }
    
    /**
     * Checks if the stack is this custom item and if the stack size is exactly 1
     * @param stack
     * @param amount
     * @return 
     */
    public boolean isOfOnly(ItemStack stack, int amount)
    {
        return stack != null && stack.getAmount() == amount && isOf(stack);
    }
    
    @Override
    public String toString()
    {
        return "[CI]" + id + "[" + type + ":" + data + "]";
    }
}
