/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 * This class watches over the current music style of the player
 * and starts music if needed
 * Updated slowly
 * @author ruman
 */
public class MusicStyleUpdater implements Runnable
{
    private DynamicMusicPlugin plugin;
    
    private HashMap<Player, Style> currentStyle = new HashMap<>();
    
    public MusicStyleUpdater(DynamicMusicPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public Style selectMatchingStyle(Player player)
    {
        Style matching_style = null;
        
        for(Style style : plugin.api.styles)
        {
            if(style.applies(player))
            {
                matching_style = style;
                break;
            }
        }
        
        return matching_style;
    }
    
    public boolean playerEnabledMusic(Player player)
    {
        return plugin.preferencesConfiguration.getBoolean(player.getUniqueId().toString() + ".enable_music", true);
    }
    
    private void updatePlayer(Player player)
    {
        if(playerEnabledMusic(player))
        {
            Style current = currentStyle.getOrDefault(player, null);
            Style matching = selectMatchingStyle(player);
            
            if(current != matching)
            {
                //Tell about the style change
                plugin.musicManager.switchStyle(player, matching);
                
                //Send a stop
                //On next update, the streams will update according to style
                plugin.musicManager.stop(player);
                
                currentStyle.put(player, matching);
            }
            
            plugin.musicManager.updateStreamManagers(player);
        }
    }
    
    public Style getCurrentStyle(Player player)
    {
        return currentStyle.getOrDefault(player, null);
    }

    @Override
    public void run()
    {
        for(Player player : plugin.getServer().getOnlinePlayers())
        {
            updatePlayer(player);
        }
    }
}
