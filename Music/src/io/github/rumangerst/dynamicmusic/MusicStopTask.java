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
public class MusicStopTask extends BukkitRunnable
{
    
    private DynamicMusicPlugin plugin;
    private Player player;
    private String music;
    private float volume;
    private int iterations;
    private boolean finished = false;

    public MusicStopTask(DynamicMusicPlugin plugin, Player player, String music, float volume, int iterations)
    {
        this.plugin = plugin;
        this.player = player;
        this.iterations = iterations;
        this.music = music;
        this.volume = volume;
    }

    @Override
    public void run()
    {
        if (player.isOnline())
        {
            if (--iterations >= 0)
            {
                //DynamicMusicPlugin.LOGGER.info("Stop task play sound " + iterations);
                player.playSound(player.getLocation(), music, volume, 1.0f);
            }
            else
            {
                finished = true;                
                cancel();
            }
        }
        else
        {
            finished = true;
            cancel();
        }
    }

    public boolean finished()
    {
        return finished;
    }
    
}
