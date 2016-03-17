/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.helpers;

import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    /**
     * Returns true if given inventory contains given item stacks. Accepts duplicate item stacks as input.
     * @param api
     * @param inventory
     * @param items
     * @return 
     */
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
    
    
    /**
     * Returns true if the given inventory matches exactly given item stacks. Accepts duplicate item stacks as input.
     * @param api
     * @param inventory
     * @param items
     * @return 
     */
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
     * Extracts a map of anyItem to a list of ItemStacks of this AnyItem
     * @param api
     * @param inventory
     * @return 
     */
    public static HashMap<AnyItem, List<ItemStack>> extractAnyItemStacks(CustomItemsAPI api, ItemStack[] inventory)
    {
        HashMap<AnyItem, List<ItemStack>> result = new HashMap<>();
        
        for(ItemStack stack : inventory)
        {
            if(stack != null)
            {
                AnyItem ai = api.getAnyItem(stack);
                
                if(!result.containsKey(ai))
                    result.put(ai, new ArrayList<>());
                
                result.get(ai).add(stack);
            }
        }
        
        return result;
    }
    
    /**
     * Returns true if the list of item stacks contains only one stack of given amount
     * @param stacks
     * @param amount
     * @return 
     */
    public static boolean isOneStackOf(Collection<ItemStack> stacks, int amount)
    {
        return stacks.size() == 1 && stacks.iterator().next().getAmount() == amount;
    }
    
    /**
     * If the list of item stacks contains only one stack of given amount, return the stack. otherwise return null.
     * @param stacks
     * @param amount
     * @return 
     */
    public static ItemStack getOneStackOf(Collection<ItemStack> stacks, int amount)
    {
        if(stacks.size() == 1)
        {
            ItemStack stack = stacks.iterator().next();
            
            if(stack.getAmount() == amount)
                return stack;
        }
        
        return null;
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
