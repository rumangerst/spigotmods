/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.playsoundtester;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class PlaysoundItem extends CustomItem
{
    private Sound sound;
    
    public PlaysoundItem(Sound sound)
    {
        super("playsound." + sound.name().toLowerCase().replace('_', '.'), Material.NOTE_BLOCK, "Play sound: " + sound.name().toLowerCase().replace('_', '.'));
        this.sound = sound;
    }
    
    public void play(HumanEntity player, ItemStack stack)
    {
        float pitch = NBTAPI.getFloat(stack, "playsoundtester/pitch", 1);
        player.getWorld().playSound(player.getLocation(), sound, 1, pitch);
        
        player.sendMessage("Playing sound " + sound.name().toLowerCase().replace('_', '.') + " with pitch " + pitch);
    }
    
    @EventHandler
    public void playOnClick(InventoryClickEvent event)
    {
        if(isOf(event.getCurrentItem()))
        {
            play(event.getWhoClicked(), event.getCurrentItem());
        }
    }
    
    @EventHandler
    public void modifyPitch(PlayerInteractEvent event)
    {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        
        if(isOf(stack))
        {
            float pitch = NBTAPI.getFloat(stack, "playsoundtester/pitch", 1);
            
            if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                pitch += 0.1;
            }
            else
            {
                pitch -= 0.1;
            }
            
            pitch = Math.min(2, Math.max(0, pitch));
            NBTAPI.setFloat(stack, "playsoundtester/pitch", pitch);
            
            play(event.getPlayer(), stack);
        }
    }
    
    @EventHandler
    public void preventPlacing(BlockPlaceEvent event)
    {
        if(isOf(event.getItemInHand()))
        {
            event.setCancelled(true);
        }
    }
}
