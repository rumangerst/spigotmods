/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import io.github.rumangerst.customitems.nbt.NBTAPI;
import io.github.rumangerst.customitems.recipes.CustomItemRecipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.rumangerst.customitems.recipes.impl.CustomItemRecipeImpl;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class CustomItemsAPI
{
    CustomItemsPlugin plugin;
    HashMap<String, CustomItem> items = new HashMap<>();
    HashMap<Material, CustomItem> item_overrides = new HashMap<>();
    ArrayList<CustomItemRecipeImpl> recipes = new ArrayList<>();
    ArrayList<CustomItemRecipe> floating_recipes = new ArrayList<>();
    HashMap<Material, String> floating_overrides = new HashMap<>();
    
    public CustomItemsAPI(CustomItemsPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public static CustomItemsAPI api(JavaPlugin plugin)
    {
        return ((CustomItemsPlugin) plugin.getServer().getPluginManager().getPlugin("CustomItems")).getAPI();
    }
    
    /**
     * Try to register all recipes that don't have necessary items, yet
     */
    private void registerFloatingRecipes()
    {
        recipetest:
        for(int i = 0; i < floating_recipes.size(); ++i)
        {
            CustomItemRecipe recipe = floating_recipes.get(i);
           
            for(String id : recipe.getCustomItemIds())
            {
                if(!items.containsKey(id))
                {
                    floating_recipes.add(recipe);                    
                    continue recipetest;
                }
            }

            recipes.add(recipe.getImplementation(this));
            
            //Evil-shit remove
            floating_recipes.remove(i);
            --i;
        }
    }
    
    private void registerFloatingOverrides()
    {
        for(Material key : new ArrayList<>(floating_overrides.keySet()))
        {
            String v = floating_overrides.get(key);            
            CustomItem i = getCustomItem(v);
            
            if(i != null)
            {
                registerOverride(key, i);
                floating_overrides.remove(key);
            }
        }
    }
    
    /**
     * Registers a recipe
     * @param recipe
     */
    public void registerRecipe(CustomItemRecipe recipe)
    {
        for(String id : recipe.getCustomItemIds())
        {
            if(!items.containsKey(id))
            {
                floating_recipes.add(recipe);
                return;
            }
        }
        
        recipes.add(recipe.getImplementation(this));
    }
    
    /**
     * Registers a item
     * @param source 
     * @param item 
     */
    public void registerItem(JavaPlugin source, CustomItem item)
    {
        // Resolve conflicts
        if(items.containsKey(item.id))
        {
            String oldid = item.id;
            
            item.id = source.getName() + ":" + item.id;
            int counter = 1;
            
            while(items.containsKey(item.id))       
            {
                item.id = source.getName() + ":" + item.id + counter;
            }
            
            CustomItemsPlugin.LOGGER.log(Level.WARNING, "Custom item with id {0} already exists. Registering it as {1}", new Object[]{oldid, item.id});
        }
        
        CustomItemsPlugin.LOGGER.log(Level.INFO, "Registering item {0} of {1}", new Object[]{item.id, item.type});
        
        items.put(item.id, item);        
        registerFloatingRecipes();
        registerFloatingOverrides();
        
        //Register item as listener
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
    }
    
    /**
     * Overrides a vanilla item by a custom item
     * @param material
     * @param item 
     */
    public void registerOverride(Material material, CustomItem item)
    {
        if(item_overrides.containsKey(material))
        {
            CustomItemsPlugin.LOGGER.log(Level.WARNING, "Existing item override for {0} is overriden by {1}", new Object[]{material, item});
        }
        
        CustomItemsPlugin.LOGGER.log(Level.INFO, "Overriding item {0} with {1}", new Object[]{material, item});
        item_overrides.put(material, item);
    }
    
    public void registerOverride(Material material, String item)
    {
        if(items.containsKey(item))
        {
            registerOverride(material, getCustomItem(item));
        }
        else
        {
            floating_overrides.put(material, item);
        }
    }
    
    /**
     * Returns if item is of item described by AnyItem
     * @param stack
     * @param item
     * @return 
     */
    public boolean isOf(ItemStack stack, AnyItem item)
    {
        return item.isOf(this, stack);
    }
    
    /**
     * Returns if item stack is of custom item
     * @param stack
     * @param id
     * @return 
     */
    public boolean isOf(ItemStack stack, String id)
    {
        CustomItem item = getCustomItem(stack);
        
        if(item != null)
        {
            return item.getId().equals(id);
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Gets custom item with id or null if id is unknown
     * @param id
     * @return 
     */
    public CustomItem getCustomItem(String id)
    {
        return items.getOrDefault(id, null);
    }
    
    /**
     * Returns custom item from item stack
     * @param stack
     * @return 
     */
    public CustomItem getCustomItem(ItemStack stack)
    {
        String id =  NBTAPI.getString(stack, "customitem/id", "");
        
        if(!id.isEmpty())
            return getCustomItem(id);
        else
            return null;
    }
    
    /**
     * Returns AnyItem from item stack
     * @param stack
     * @return 
     */
    public AnyItem getAnyItem(ItemStack stack)
    {
        CustomItem item = getCustomItem(stack);
        
        if(item != null)
        {
            return new AnyItem(item.id);
        }
        else
        {
            return new AnyItem(stack.getType(), stack.getData());
        }
    }
    
    /**
     * Returns AnyItemStack from item stack. Will not copy metadata!
     * @param stack 
     * @return  
     */
    public AnyItemStack getAnyItemStack(ItemStack stack)
    {
        return new AnyItemStack(getAnyItem(stack), stack.getAmount());
    }
    
    /**
     * Applies vanilla item override to a stack.
     * Won't happen if stack has a name
     * @param stack 
     */
    public void applyOverrideItem(ItemStack stack)
    {
        if(stack == null)
            return;
        
        if(item_overrides.containsKey(stack.getType()))
        {
            if(NBTAPI.getString(stack, "display/Name", "").isEmpty())
            {
                if(getCustomItem(stack) == null)
                {
                    item_overrides.get(stack.getType()).transform(stack);
                }
            }
        }
    }
    
    /**
     * Converts seconds to minecraft ticks (1 tick = 50ms)
     * @param seconds
     * @return 
     */
    public static int secondsToTicks(double seconds)
    {
        return (int)(seconds * 1000.0 / 50.0);
    }
}
