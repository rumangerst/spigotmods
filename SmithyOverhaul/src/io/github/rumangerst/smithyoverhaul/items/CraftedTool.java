/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.smithyoverhaul.items;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import static io.github.rumangerst.smithyoverhaul.TieredSmity.ratePercentage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author ruman
 */
public class CraftedTool extends CustomItem
{
    
    public CraftedTool(String id, Material type, String name)
    {
        super(id, type, (byte)0, name);
    }
    
    @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);
        
        String manufacturer = NBTAPI.getString(stack, "craftedtool/manufacturer", "Unbekannt");
        double durability = NBTAPI.getDouble(stack,"craftedtool/durability", 0);
        double exhaustion = NBTAPI.getDouble(stack,"craftedtool/exhaustion", 0);
        double efficiency = NBTAPI.getDouble(stack,"craftedtool/efficiency", 0);
        double precision = NBTAPI.getDouble(stack,"craftedtool/precision", 0);
        
        NBTAPI.setStringList(stack, "display/Lore", 
                    "Haltbarkeit: " + ratePercentage(durability),
                    "Gewichtsverteilung: " + ratePercentage(1 - exhaustion),
                    "Präzision: " + ratePercentage(precision),
                    "Form: " + ratePercentage(efficiency),
                    "Geschmiedet von " + manufacturer);
    }
    
    @EventHandler
    public void applyEffects(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK && isOf(event.getPlayer().getItemInHand()))
        {
            Player player = event.getPlayer();
            
            ItemStack stack = player.getItemInHand();
            
            double exhaustion = NBTAPI.getDouble(stack, "craftedtool/exhaustion", 0);
            double efficiency = NBTAPI.getDouble(stack, "craftedtool/efficiency", 0);
            
            if(exhaustion > 0.9)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, CustomItemsAPI.secondsToTicks(10), 2));
            else if(exhaustion > 0.8)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, CustomItemsAPI.secondsToTicks(5), 2));
            else if(exhaustion > 0.7)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, CustomItemsAPI.secondsToTicks(7), 1));
            else if(exhaustion > 0.6)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, CustomItemsAPI.secondsToTicks(5), 1));
            else if(exhaustion > 0.5)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, CustomItemsAPI.secondsToTicks(2), 1));
            
            if(efficiency < 0.1)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, CustomItemsAPI.secondsToTicks(7), 2));
            else if(efficiency < 0.2)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, CustomItemsAPI.secondsToTicks(5), 2));
            else if(efficiency < 0.3)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, CustomItemsAPI.secondsToTicks(3), 1));
            else if(efficiency < 0.4)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, CustomItemsAPI.secondsToTicks(2), 1));
            else if(efficiency < 0.5)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, CustomItemsAPI.secondsToTicks(1), 1));
        }
    }
}
