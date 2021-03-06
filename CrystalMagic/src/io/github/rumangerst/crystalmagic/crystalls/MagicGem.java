/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicGem extends CustomItem
{
    protected CrystalMagicPlugin plugin;
    
    public MagicGem(String id, Material type, byte data, String name, CrystalMagicPlugin plugin)
    {
        super(id, type, data, name);
        this.plugin = plugin;
    }
    
    @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);        
        NBTAPI.setStringList(stack, "display/Lore", makeLore(stack).split("\n"));
    }
    
    protected String makeLore(ItemStack stack)
    {
        String lore = "";
        
        lore += "Level " + getLevel(stack) + "\n";
        
        HashMap<String, Integer> elements = getElements(stack);
        
        if(!elements.isEmpty())
        {
            lore += "Elemente: ";
            
            int count = 0;
            
            for(String element : elements.keySet())
            {
                CustomItem elementitem = CustomItemsAPI.api(plugin).getCustomItem(element);
                
                if(elementitem != null)
                {      
                    lore += ((Element)elementitem).getNameForLevel(elements.get(element)) + " (" + elements.get(element) + ")";
                    
                    if(++count != elements.keySet().size())
                    {
                        lore += ", ";
                    }
                }
            }
            
            lore += "\n";
        }
        
        return lore;
    }
                
    public HashMap<String, Integer> getElements(ItemStack stack)
    {
        HashMap<String, Integer> data = new HashMap<>();
        
        for(String element : NBTAPI.keys(stack, "crystalmagic/elements"))
        {
            int level = NBTAPI.getInt(stack, "crystalmagic/elements/" + element, 0);
            
            if(level > 0)
                data.put(element, level);
        }
        
        return data;
    }
    
    public void putElement(ItemStack stack, String elementid, int level)
    {
        NBTAPI.setInt(stack, "crystalmagic/elements/" + elementid, level);        
        transform(stack);
    }
    
    public int getLevel(ItemStack stack)
    {
        return Math.max(1, Math.min(4, NBTAPI.getInt(stack, "crystalmagic/level", 0)));
    }
    
    public void setLevel(ItemStack stack, int value)
    {
        NBTAPI.setInt(stack, "crystalmagic/level", value);
        transform(stack);
    }
    
    public ItemStack make(int amount, int level)
    {
        ItemStack stack = make(amount);
        setLevel(stack, level);
        
        return stack;
    }
    
}
