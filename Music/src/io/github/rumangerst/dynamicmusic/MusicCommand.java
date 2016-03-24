/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class MusicCommand implements CommandExecutor
{
    private DynamicMusicPlugin plugin;
    
    public MusicCommand(DynamicMusicPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(strings.length == 1)
        {
            switch(strings[0].toLowerCase())
            {
                case "enable":
                    
                    if(cs instanceof Player)
                    {
                        Player player = (Player)cs;
                        plugin.preferencesConfiguration.set(player.getUniqueId().toString() + ".enable_music", true);
                        
                        return true;
                    }
                    
                    break;
                case "disable":
                    
                    if(cs instanceof Player)
                    {
                        Player player = (Player)cs;
                        plugin.preferencesConfiguration.set(player.getUniqueId().toString() + ".enable_music", false);
                        
                        player.sendMessage("Music will stop after playback of current song.");
                        
                        plugin.musicManager.stop(player);
                        
                        return true;
                    }
                    
                    break;
                    
                case "info":
                    
                    if(cs instanceof Player)
                    {
                        Player player = (Player)cs;
                        plugin.musicManager.sendInfo(player);
                        
                        return true;
                    }
                    
                    break; 
                   
                case "reload":
                    
                    if(cs.isOp())
                    {
                        plugin.loadPluginConfiguration();
                        plugin.loadMusicConfiguration();
                        cs.sendMessage("Music configuration reloaded.");
                        
                        return true;
                    }
                    
                    break;
            }
        }
        
        return false;
    }
    
}
