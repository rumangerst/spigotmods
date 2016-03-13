/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.smithyoverhaul.items;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author ruman
 */
public class UnfinishedTool extends CustomItem
{    
    public UnfinishedTool(String id, Material material, String name)
    {
        super(id, material, (byte)0, name);
    }
    
    @EventHandler
    public void breakEvent(BlockBreakEvent event)
    {
        if(!event.isCancelled() && isOf(event.getPlayer().getItemInHand()))
        {
            event.getPlayer().setItemInHand(null);
        }
    }
    
    @EventHandler
    public void useEvent(PlayerInteractEvent event)
    {
        if(!event.isCancelled() && isOf(event.getPlayer().getItemInHand()))
        {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, CustomItemsAPI.secondsToTicks(10), 1));
        }
    }
}
