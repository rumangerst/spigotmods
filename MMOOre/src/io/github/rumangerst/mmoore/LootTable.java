/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.mmoore;

import java.util.Hashtable;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;


/**
 *
 * @author ruman
 */
public class LootTable
{
    public boolean default_loot;
    public Loot[] loot;
    
    public LootTable(boolean default_loot, Loot ... loot)
    {
        this.default_loot = default_loot;
        this.loot = loot;
    }
    
    public void dice(BlockBreakEvent event)
    {
        ItemStack[] extra = null;
        
        for(Loot l : loot)
        {
            extra = l.dice();
            
            if(extra != null)
                break;
        }
        
        if(extra != null)
        {
            for(ItemStack it : extra)
            {
                event.getBlock().getWorld().dropItemNaturally(event.getPlayer().getLocation(), it);
            }
        }
        else if(default_loot)
        {
            //Drop the stuff if nothing else was dropped
            for(ItemStack stack : event.getBlock().getDrops(event.getPlayer().getItemInHand()))
            {
                event.getBlock().getWorld().dropItemNaturally(event.getPlayer().getLocation(), stack);
            }
        }
    }
}
