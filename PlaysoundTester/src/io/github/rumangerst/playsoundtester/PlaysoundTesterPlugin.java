/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.playsoundtester;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class PlaysoundTesterPlugin extends JavaPlugin
{
    public PlaysoundTesterPlugin()
    {
        
    }
    
    @Override
    public void onEnable()
    {
        CustomItemsAPI api = CustomItemsAPI.api(this);
        
        for(Sound sound : Sound.values())
        {
            api.registerItem(this, new PlaysoundItem(sound));
        }
    }
    
    @Override
    public void onDisable()
    {
        
    }
}
