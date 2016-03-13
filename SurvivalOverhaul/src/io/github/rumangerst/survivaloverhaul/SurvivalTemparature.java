/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.survivaloverhaul;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author ruman
 */
public class SurvivalTemparature
{
    private SurvivalOverhaulPlugin plugin;
    
    public SurvivalTemparature(SurvivalOverhaulPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public double getPlayerTargetTemparature(Player player)
    {
        double biometemparature;
        double heighttemparature;
        double daynighttemparature;
        double firetemparature;
        double weathertemparature = 0;
        
        
        //Weather decreases temp always by 10°C
        if(player.getWorld().hasStorm())
            weathertemparature = -10;
        
        // Calculate the temparature modified by the height        
        if(player.getLocation().getY() < 64)
            heighttemparature = -Math.pow( (player.getLocation().getY() - 64.0) / 20.0, 3.0 );
        else
            heighttemparature = -Math.pow( (player.getLocation().getY() - 64.0) / 12.0, 3.0 );
        
        daynighttemparature = Math.max(0, Math.min(1, player.getWorld().getTime() / 24000.0));
        daynighttemparature = (Math.sin(daynighttemparature * 2.0 * Math.PI) - 0.7) * 10; //maximum is 6000 ticks, minimum is 18000 ticks        
        
        //Load temparatures from block
        Block playerblock = player.getLocation().getBlock();
        biometemparature = playerblock.getTemperature(); // Biome temparature from 0 - 2
        firetemparature = playerblock.getLightFromBlocks() / 15.0; //Stays light this. Light always positive
        
        biometemparature = Math.min(Math.pow(biometemparature - 0.7, 3) * 150, 25); // Ice mountains will -50, Extreme hills -18
        firetemparature = 15 * firetemparature;
        
        // Modify temparatures for caves
        daynighttemparature *= playerblock.getLightFromSky() / 15.0; //Caves etc do not affect daynight temparature
        biometemparature *= playerblock.getLightFromSky() / 15.0; 
        
        double temp = 25 + heighttemparature + biometemparature + firetemparature + daynighttemparature + weathertemparature;        
        temp = Math.max(-50, Math.min(50, temp));
        
        return temp;
    }
    
    public void applyTargetTemparature(Player player)
    {
        double target = getPlayerTargetTemparature(player);
        double current = getPlayerTemparature(player);
        double newtemparature;      
        
        double cold_modifier = 1;
        double heat_modifier;
        
        // Add general armor modifiers
        if(player.getInventory().getBoots() != null)
            cold_modifier *= 0.9;
        if(player.getInventory().getChestplate() != null)
            cold_modifier *= 0.5;
        if(player.getInventory().getLeggings() != null)
            cold_modifier *= 0.6;
        if(player.getInventory().getHelmet() != null)
            cold_modifier *= 0.95;
        
        heat_modifier = Math.min(1, cold_modifier + 0.5);
        
        if(target < current)
            newtemparature = current - cold_modifier * 0.15 * (current - target);
        else
            newtemparature = current + heat_modifier * 0.3 * (target - current);
        
        //player.sendMessage(target + ": " + current + " -> " + newtemparature);
        
        setPlayerTemparature(player, newtemparature);
        
        if(current > 0 && newtemparature < 0 || current < 0 && newtemparature > 0 || (int)(current / 5) != (int)(newtemparature / 5))
        {
            plugin.sendSurvivalInformationTo(player);
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        setPlayerTemparature(event.getPlayer(), 25);        
    }
    
    public double getPlayerTemparature(Player player)
    {
        return plugin.data_storage.getDouble(player.getUniqueId().toString() + ".temparature", 25);
    }
    
    public double setPlayerTemparature(Player player, double value)
    {
        plugin.data_storage.set(player.getUniqueId().toString() + ".temparature", value);
        
        return value;
    }
    
    public void levelPlayerTemperatureTo(Player player, double temperature, double strength)
    {
        double current = getPlayerTemparature(player);        
        setPlayerTemparature(player, current + (temperature - current) * strength);
    }
    
    public void applyTemparatureEffects(Player player)
    {
        double temparature = getPlayerTemparature(player);
        
        if(temparature < 0)
        {
            double damagestrength = Math.min(3, -temparature  / 10.0 + 0.5);
            int hungerstrength = (int)Math.max(1, Math.min(3, -temparature / 10.0));
            int slownessstrength = (int)Math.max(0, Math.min(3, (-temparature - 10.0) / 10.0));
            int blindnessstrength = (int)Math.max(0, Math.min(3, (-temparature - 15.0) / 10.0));
       
            player.damage(damagestrength);
            plugin.sendSurvivalInformationTo(player);
            
            if(hungerstrength != 0)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,hungerstrength, SurvivalOverhaulPlugin.secondsToTicks(8)));
            if(slownessstrength != 0)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,slownessstrength, SurvivalOverhaulPlugin.secondsToTicks(8)));
            if(blindnessstrength != 0)
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,blindnessstrength, SurvivalOverhaulPlugin.secondsToTicks(8)));
        }
        else if(temparature < 10)
        {
            int hungerstrength = (int)Math.max(1, Math.min(3, -(temparature - 10) / 10));
            if(hungerstrength != 0)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,hungerstrength, SurvivalOverhaulPlugin.secondsToTicks(8)));
        }
        else
        {
            double watersatisfaction = -Math.max(0, Math.min(0.1, Math.pow((temparature - 25.0) / 100.0, 2)));
            plugin.water.hydratePlayer(player, watersatisfaction);
        }
    }
        
    public static ChatColor chatColorFromTemparature(double value)
    {
        if(value < -10)           
            return ChatColor.DARK_RED;
        else if(value < 0)
            return ChatColor.RED;
        else if(value < 5)
            return ChatColor.GOLD;
        else if(value < 0)
            return ChatColor.YELLOW;
        else if(value < 20)
            return ChatColor.GREEN;
        else if(value < 27)
            return ChatColor.DARK_GREEN;
        else if(value < 30)
            return ChatColor.YELLOW;
        else if(value < 35)
            return ChatColor.GOLD;
        else if(value < 40)
            return ChatColor.RED;
        else
        {
            return ChatColor.DARK_RED;
        }
    }
    
}
