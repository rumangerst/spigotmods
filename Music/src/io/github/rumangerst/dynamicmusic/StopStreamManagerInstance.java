/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import org.bukkit.entity.Player;

/**
 * Informs the stream managers that their music has stopped
 * @author ruman
 */
public class StopStreamManagerInstance implements Runnable
{
    DynamicMusicPlugin plugin;
    Player player;
    
    public StopStreamManagerInstance(DynamicMusicPlugin plugin, Player player)
    {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run()
    {
        for(StreamManager m : plugin.musicManager.getOrMakeStreamManagers(player))
        {
            m.informStopped();
        }
        //DynamicMusicPlugin.LOGGER.info("|| Inform stop to streaming managers");
    }
    
}
