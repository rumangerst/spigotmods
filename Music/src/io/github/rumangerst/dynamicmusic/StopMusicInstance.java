/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author ruman
 */
public class StopMusicInstance implements Runnable
{
    
    private DynamicMusicPlugin plugin;
    private Player player;
    private String music;
    private float volume;

    public StopMusicInstance(DynamicMusicPlugin plugin, Player player, String music, float volume)
    {
        this.plugin = plugin;
        this.player = player;
        this.music = music;
        this.volume = volume;
    }

    @Override
    public void run()
    {
        if (player.isOnline())
        {
            player.playSound(player.getLocation(), music, volume, 1.0f);
        }
    }
    
}
