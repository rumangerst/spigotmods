/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.smithyoverhaul.items;

import io.github.rumangerst.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 *
 * @author ruman
 */
public class UnfinishedSword extends CustomItem
{
    
    public UnfinishedSword(String id, Material type, String name)
    {
        super(id, type, (byte)0, name);
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {
            Player player = (Player)event.getDamager();
            
            if(isOf(player.getItemInHand()))
            {
                player.setItemInHand(null);
            }
        }
    }
}
