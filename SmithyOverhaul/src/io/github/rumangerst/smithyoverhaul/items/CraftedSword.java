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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author ruman
 */
public class CraftedSword extends CustomItem
{
    
    public CraftedSword(String id, Material type, String name)
    {
        super(id, type, (byte)0, name);
    }
    
   @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);
        
        String manufacturer = NBTAPI.getString(stack, "craftedweapon/manufacturer", "Unbekannt");
        double durability = NBTAPI.getDouble(stack,"craftedweapon/durability", 0);
        double exhaustion = NBTAPI.getDouble(stack,"craftedweapon/exhaustion", 0);
        double sharpness = NBTAPI.getDouble(stack,"craftedweapon/sharpness", 0);
        double precision = NBTAPI.getDouble(stack,"craftedweapon/precision", 0);
        
        NBTAPI.setStringList(stack, "display/Lore", 
                    "Haltbarkeit: " + ratePercentage(durability),
                    "Gewichtsverteilung: " + ratePercentage(1 - exhaustion),
                    "Präzision: " + ratePercentage(precision),
                    "Schärfe: " + ratePercentage(sharpness),
                    "Geschmiedet von " + manufacturer);
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {
            Player player = (Player)event.getDamager();
            
            ItemStack stack = player.getInventory().getItemInMainHand();
            
            if(isOf(stack))
            {
                double exhaustion = NBTAPI.getDouble(stack, "craftedtool/exhaustion", 0);
                double sharpness = NBTAPI.getDouble(stack,"craftedweapon/sharpness", 0);
                
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
                
                //Modify damage via sharpness
                if(sharpness < 0.5)
                    event.setDamage(event.getDamage() * (sharpness / 0.5));
            }
        }
    }
}
