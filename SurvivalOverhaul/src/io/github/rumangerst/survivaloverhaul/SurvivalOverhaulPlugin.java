/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.survivaloverhaul;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class SurvivalOverhaulPlugin extends JavaPlugin
{
    public FileConfiguration data_storage;
    public SurvivalWater water;
    public SurvivalTemparature temparature;
    private SurvivalUpdater updater;
    
    public SurvivalOverhaulPlugin()
    {
        water = new SurvivalWater(this);
        temparature = new SurvivalTemparature(this);
        updater = new SurvivalUpdater(this, water, temparature);
        data_storage = getConfig();        
    }
    
    @Override
    public void onEnable()
    {
        try
        {            
            data_storage.load("survivaloverhaul.yml");
        }
        catch (IOException | InvalidConfigurationException ex)
        {
            Logger.getLogger(SurvivalOverhaulPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        water.registerWater();
        
        getServer().getPluginManager().registerEvents(water, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, updater, secondsToTicks(10), secondsToTicks(5));
    }
    
    @Override
    public void onDisable()
    {
        try
        {
            data_storage.save("survivaloverhaul.yml");
        }
        catch (IOException ex)
        {
            Logger.getLogger(SurvivalOverhaulPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addSnowMeltingRecipe()
    {
        ItemStack dst = new ItemStack(Material.POTION);
        
        
    }
    
    public void sendSurvivalInformationTo(Player player)
    {
        double water = this.water.getPlayerWaterLevel(player);
        double temparature = this.temparature.getPlayerTemparature(player);        
        
        String message = "";        
        message += ChatColor.AQUA + "Durst " + chatColorFromPercentage(water) + textProgressBar('|', ' ', 10, water);
        message += ChatColor.DARK_PURPLE + " Temparatur " + SurvivalTemparature.chatColorFromTemparature(temparature) + (int)temparature + "°C";
        
        BountifulAPI.sendActionBar(player, message, secondsToTicks(8));
    }
    
    public static ChatColor chatColorFromPercentage(double value)
    {
        value *= 0.6;
        
        if(value < 0.1)
            return ChatColor.DARK_RED;
        else if(value < 0.2)
            return ChatColor.RED;
        else if(value < 0.3)
            return ChatColor.GOLD;
        else if(value < 0.4)
            return ChatColor.YELLOW;
        else if(value < 0.5)
            return ChatColor.GREEN;
        else
            return ChatColor.DARK_GREEN;
    }
    
    public static String textProgressBar(char filled, char unfilled, int length, double value)
    {
        String t = "";        
        int v = (int)(length * value);
        
        for(int i = 0; i < v; ++i)
            t += filled;
        for(int i = 0; i < length - v; ++i)
            t += unfilled;
        
        return t;
    }
    
    public static int secondsToTicks(double durationSeconds)
    {
        return (int) (durationSeconds * 1000.0 / 50.0);
    }
}
