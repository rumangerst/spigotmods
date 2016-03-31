/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import io.github.rumangerst.customitems.recipes.impl.CustomItemFurnaceRecipeImpl;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import io.github.rumangerst.customitems.recipes.impl.CustomItemRecipeImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 *
 * @author ruman
 */
public class CustomItemsEvents implements Listener
{
    CustomItemsPlugin plugin;
    CustomItemsAPI api;
    
    public CustomItemsEvents(CustomItemsPlugin plugin, CustomItemsAPI api)
    {
        this.plugin = plugin;
        this.api = api;
    }
    
    @EventHandler
    public void onPlayerCraft(PrepareItemCraftEvent event)
    {        
        if(event.getRecipe() instanceof CustomItemRecipeImpl)
        {
            CustomItemRecipeImpl recipe = (CustomItemFurnaceRecipeImpl)event.getRecipe();
            
            if(!recipe.hasCorrectCustomItems(event.getInventory().getMatrix()))
            {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
        
        ItemStack result = event.getRecipe().getResult();
        if(api.applyOverrideItem(result))
        {
            event.getInventory().setResult(result);
        }
    }
    
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event)
    {
        if(event.isCancelled())
            return;
        
        // Vanilla override
        api.applyOverrideItem(event.getItem().getItemStack());
        
        // Stack size override
        api.applyStackSizeRestriction(event.getItem().getItemStack(), event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerInventoryAction(InventoryClickEvent event)
    {
        if(event.isCancelled())
            return;
        
        // Vanilla override
        api.applyOverrideItem(event.getCurrentItem());
        
        if(event.getWhoClicked() instanceof Player)
        {        
            // Stack size override
            api.applyStackSizeRestriction(event.getCurrentItem(), (Player)event.getWhoClicked());
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.isCancelled())
            return;
        
        if(event.hasItem())
        {
            // Vanilla override
            api.applyOverrideItem(event.getItem());
            
            // Stack size override
            api.applyStackSizeRestriction(event.getItem(), event.getPlayer());
        }
    }
}
