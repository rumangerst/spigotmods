/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import org.bukkit.command.CommandSender;
import org.bukkit.material.Command;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class CustomItemsPlugin extends JavaPlugin
{
    public static PluginLogger LOGGER;
    private CustomItemsAPI api;
    private CustomItemsEvents events;
    private CustomItemsGiveGUI givegui;
    
    public CustomItemsPlugin()
    {
        api = new CustomItemsAPI(this);
        events = new CustomItemsEvents(this, api);
        LOGGER = new PluginLogger(this);
        
        givegui = new CustomItemsGiveGUI(api);
    }
    
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(events, this);
        getServer().getPluginManager().registerEvents(givegui, this);
        
        getCommand("citems").setExecutor(new CustomItemsCommand(this));
        getCommand("cigive").setExecutor(givegui);
    }
    
    @Override
    public void onDisable()
    {
        
    }    
    
    public CustomItemsAPI getAPI()
    {
        return api;
    }
}
