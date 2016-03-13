/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.survivaloverhaul;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author ruman
 */
public class SurvivalWaterPotion extends CustomItem
{  
    private SurvivalOverhaulPlugin plugin;
    public double satisfaction;
    PotionEffect[] effects;
    
    public SurvivalWaterPotion(SurvivalOverhaulPlugin plugin, String id, String name, String lore, double satisfaction, PotionEffect ... effects)
    {
        super(id, Material.POTION, (byte)0, name);
        
        this.plugin = plugin;
        this.lore = lore.split("\n");
        this.effects = effects;
        this.satisfaction = satisfaction;
    }
    
    public void applyTo(Player player)
    {
        plugin.water.hydratePlayer(player, satisfaction);
        
        for(PotionEffect e : effects)
        {
            player.addPotionEffect(e);
        }
    }
}
