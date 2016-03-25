/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author ruman
 */
public class MusicManager implements Listener, Runnable
{    
    private DynamicMusicPlugin plugin;
    private StreamManager[] streams = new StreamManager[4];
    private int active_stream_count = 4;
    
    private HashMap<Player, MusicStopTask> stopTasks = new HashMap<>();
    
    public MusicManager(DynamicMusicPlugin plugin)
    {
        this.plugin = plugin;
        
        for(int i = 0; i < 4; ++i)
        {
            streams[i] = new StreamManager(plugin, i);
        }
    }
    
    public void stop(Player player)
    {
        /*for(StreamManager str : streams)
        {
            str.stop(player);
        }*/
        
        if(canStop())
        {
            MusicStopTask stop_task = stopTasks.getOrDefault(player, null);
            
            if(stop_task == null)
            {
                int iterations = plugin.getConfig().getInt("StoppingMusic.Iterations");
                String sound = plugin.getConfig().getString("StoppingMusic.SoundName");
                float volume = (float) plugin.getConfig().getDouble("StoppingMusic.SoundVolume");
                
                stop_task = new MusicStopTask(plugin, player, sound, volume, iterations);
                stop_task.runTaskTimer(plugin, 5, 5);
                
                stopTasks.put(player, stop_task);
            }
        }
    }
    
    public boolean canStop()
    {
        return plugin.getConfig().getBoolean("StoppingMusic.Enable");
    }
    
    public void sendInfo(Player player)
    {
        if(!plugin.preferencesConfiguration.getBoolean(player.getUniqueId().toString() + ".enable_music", true))
        {
            player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "No music enabled. Run /music enable to enable music" + ChatColor.RED + "♬");
        }
        else
        {
            for(StreamManager str : streams)
            {
                Song music = str.getCurrentSong(player);
                Style style = str.getCurrentStyle(player);
                
                if(music == null)
                {
                    player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "Stream " + str.getStreamId() + ": No music playing" + ChatColor.RED + "♬");
                }
                else
                {
                     player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "Stream " + str.getStreamId() + ": " + music.getName() + " in style " + style.getName() + ChatColor.RED + "♬");
                }
            }
        }
    }
        
    public void updateActiveStreamCount()
    {
        active_stream_count = 0;
        
        for(Style style : plugin.api.styles)
        {
            active_stream_count = Math.max(active_stream_count, style.getSongBucketCount());
        }
        
        active_stream_count = Math.min(4, active_stream_count);
    }

    @Override
    public void run()
    {
        for(Player player : plugin.getServer().getOnlinePlayers())
        {
            MusicStopTask stop_task = stopTasks.getOrDefault(player, null);
            
            if(stop_task != null)
            {
                if(stop_task.finished())
                {
                    // If a task is finished, remove player from the streams and disable stop task
                    for(int i = 0; i < active_stream_count; ++i)
                    {
                        streams[i].removePlayer(player);
                    }
                    
                    stopTasks.put(player, null);
                    stop_task = null;
                    
                    //DynamicMusicPlugin.LOGGER.info("Music stop task finished");
                }
            }
            
            if(stop_task == null)            
            {
                //DynamicMusicPlugin.LOGGER.info("updating streams");
                for(int i = 0; i < active_stream_count; ++i)
                {
                    streams[i].update(player);
                }
            
            }
        }
    }
    
    
    
    @EventHandler
    public void updateOnLogin(PlayerLoginEvent event)
    {
        for(StreamManager str : streams)
        {
            str.removePlayer(event.getPlayer());
        }
    }
    
    @EventHandler
    public void updateOnRespawn(PlayerRespawnEvent event)
    {
        for(StreamManager str : streams)
        {
            str.removePlayer(event.getPlayer());
        }
    }
    
    @EventHandler
    public void updateOnTeleport(PlayerTeleportEvent event)
    {
        // Teleporting to another world stops the music
        // Clear and play now
        if(event.getFrom().getWorld() != event.getTo().getWorld())
        {
            for(StreamManager str : streams)
            {
                str.removePlayer(event.getPlayer());
            }
        }
    }
    
    
}
