/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class MusicInstance implements Runnable
{
    
    private DynamicMusicPlugin plugin;
    private Song song;
    private Player player;
    private long timeout;
    //private MusicStopTask stop_task = null;

    public MusicInstance(DynamicMusicPlugin plugin, Song song, Player player)
    {
        this.plugin = plugin;
        this.player = player;
        this.song = song;
        this.timeout = Long.MAX_VALUE;        
    }

    public Song getSong()
    {
        return song;
    }

    /*public void sendStop()
    {
    if(!isFinished() && stop_task == null)
    {
    int iterations = plugin.getConfig().getInt("StoppingMusic.Iterations");
    String sound = plugin.getConfig().getString("StoppingMusic.SoundName");
    float volume = (float) plugin.getConfig().getDouble("StoppingMusic.SoundVolume");
    DynamicMusicPlugin.LOGGER.info("Sending stop task");
    stop_task = new MusicStopTask(plugin, player, sound, volume, iterations);
    stop_task.runTaskTimer(plugin, 5, 5);
    }
    }*/
    public boolean isFinished()
    {
        /*if(stop_task != null)
        {
        return stop_task.finished();
        }*/
        return System.currentTimeMillis() > timeout;
    }

    @Override
    public void run()
    {
        this.timeout = System.currentTimeMillis() + song.getLength();
        song.play(player);
    }
    
}
