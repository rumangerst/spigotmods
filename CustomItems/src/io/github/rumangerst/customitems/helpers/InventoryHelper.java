/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.helpers;

import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class InventoryHelper
{
    private InventoryHelper()
    {
        
    }
    
    public static boolean contains(CustomItemsAPI api, ItemStack[] inventory, AnyItemStack ... items)
    {
        HashSet<Integer> marked_indices = new HashSet<>();
        
        item_loop:
        for(AnyItemStack item : items)
        {
            for(int i = 0; i < inventory.length; ++i)
            {
                ItemStack stack = inventory[i];
                
                if(!marked_indices.contains(i) && item.is(api, stack))
                {
                    marked_indices.add(i);
                    continue item_loop;
                }
            }
            
            return false;
        }
        
        return true;
    }
    
    public static boolean is(CustomItemsAPI api, ItemStack[] inventory, AnyItemStack ... items)
    {
        int inv_count = 0;
        
        for(ItemStack stack : inventory)
        {
            if(stack != null)
                ++inv_count;
        }
        
        return items.length == inv_count && contains(api, inventory, items);
    }
    
    /**
     * Makes the stack maximum the amount as given by parameter and drops the rest
     * @param player
     * @param stack
     * @param amount 
     */
    public static void makeAmount(Player player, ItemStack stack, int amount)
    {
        if(stack.getAmount() > amount)
        {
            ItemStack rem = new ItemStack(stack);
            rem.setAmount(stack.getAmount() - amount);
            
            stack.setAmount(amount);
            player.getWorld().dropItem(player.getLocation(), rem);
        }
    }
}
