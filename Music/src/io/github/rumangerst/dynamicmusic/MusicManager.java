/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    private int active_stream_count = 4;
    
    private HashMap<Player, Queue<Runnable>> queue = new HashMap<>();
    private HashMap<Player, Boolean> holdPlayers = new HashMap<>();
    private HashMap<Player, StreamManager[]> streamManagers = new HashMap<>();
    
    
    public MusicManager(DynamicMusicPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public StreamManager[] getOrMakeStreamManagers(Player player)
    {
        StreamManager[] m = streamManagers.getOrDefault(player, null);
        
        if(m == null)
        {
            m = new StreamManager[4];
            
            for(int i = 0; i < m.length; ++i)
            {
                m[i] = new StreamManager(plugin, player, i);
            }
            
            streamManagers.put(player, m);
            
            //DynamicMusicPlugin.LOGGER.info("Creating new style array for " + player);
        }
        
        return m;
    }
    
    private Queue<Runnable> getOrMakeQueue(Player player)
    {
        Queue<Runnable> q = queue.getOrDefault(player, null);
        
        if(q == null)
        {
            q = new ArrayDeque<>();
            queue.put(player,q);
        }
        
        return q;
    }
    
    /**
     * If any stream is currently playing
     * @param player
     * @return 
     */
    public boolean isPlaying(Player player)
    {
        StreamManager[] mgrs = streamManagers.getOrDefault(player, null);
        
        if(mgrs != null)
        {
            for(StreamManager manager : mgrs)
            {
                if(manager.isPlaying())
                    return true;
            }
        }
        
        return false;
    }
    
    /**
     * If player is currently spawning etc.
     * @param player
     * @return 
     */
    public boolean isOnHold(Player player)
    {
        return holdPlayers.getOrDefault(player, false);
    }
    
    public boolean isWorking(Player player)
    {
        return !getOrMakeQueue(player).isEmpty();
    }
    
    private void setOnHold(Player player, boolean hold)
    {
        holdPlayers.put(player, hold);
    }
    
    public void switchStyle(Player player, Style style)
    {
        //DynamicMusicPlugin.LOGGER.info("Switch-style: " + style);
        
        for(StreamManager manager : getOrMakeStreamManagers(player))
        {
            manager.switchStyle(style);
        }
    }
    
    public void enqueue(Player player, Runnable r)
    {
        getOrMakeQueue(player).add(r);
        
        //DynamicMusicPlugin.LOGGER.info("Enqueue: " + r);
    }
    
    public void updateStreamManagers(Player player)
    {
        if(isOnHold(player) || isWorking(player))
            return;
        
        for(StreamManager manager : getOrMakeStreamManagers(player))
        {
            manager.update();
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
            int iterations = plugin.getConfig().getInt("StoppingMusic.Iterations");
            String sound = plugin.getConfig().getString("StoppingMusic.SoundName");
            float volume = (float) plugin.getConfig().getDouble("StoppingMusic.SoundVolume");

            for (int i = 0; i < iterations; ++i)
            {
                enqueue(player, new StopMusicInstance(plugin, player, sound, volume));
            }
            
            enqueue(player, new StopStreamManagerInstance(plugin, player));
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
            for(StreamManager str : getOrMakeStreamManagers(player))
            {
                Song music = str.getCurrentSong();
                Style style = plugin.styleUpdater.getCurrentStyle(player);
                
                if(music == null || style == null)
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
        for (Player player : plugin.getServer().getOnlinePlayers())
        {
            if(isOnHold(player))
                continue;
            
            Queue<Runnable> q = getOrMakeQueue(player);
            
            if(!q.isEmpty())
            {
                DynamicMusicPlugin.LOGGER.info("Dequeue");
                
                q.poll().run();
            }
        }
        
//        for (Player player : plugin.getServer().getOnlinePlayers())
//        {
//            StopMusicInstance stop_task = stopTasks.getOrDefault(player, null);
//
//            if (stop_task != null)
//            {
//                if (stop_task.finished())
//                {
//                    // If a task is finished, remove player from the streams and disable stop task
//                    for (int i = 0; i < active_stream_count; ++i)
//                    {
//                        streams[i].removePlayer(player);
//                    }
//
//                    stopTasks.put(player, null);
//                    stop_task = null;
//
//                    //DynamicMusicPlugin.LOGGER.info("Music stop task finished");
//                }
//            }
//
//            if (stop_task == null)
//            {
//                //DynamicMusicPlugin.LOGGER.info("updating streams");
//                for (int i = 0; i < active_stream_count; ++i)
//                {
//                    streams[i].update(player);
//                }
//
//            }
//        }
    }
    
    public void removePlayerFromSystem(Player player)
    {
        queue.remove(player);
        streamManagers.remove(player);
        
        //DynamicMusicPlugin.LOGGER.info("|| Remove player from streaming system");
    }
    
    @EventHandler
    public void updateOnQuit(PlayerQuitEvent event)
    {
        setOnHold(event.getPlayer(), true); // Holding the player now
        removePlayerFromSystem(event.getPlayer());
    }
    
    @EventHandler
    public void updateOnLogin(PlayerLoginEvent event)
    {
        removePlayerFromSystem(event.getPlayer());
        setOnHold(event.getPlayer(), false); //Player can now be updated
    }
    
    @EventHandler
    public void updateOnRespawn(PlayerRespawnEvent event)
    {
        removePlayerFromSystem(event.getPlayer());
    }
    
    @EventHandler
    public void updateOnTeleport(PlayerTeleportEvent event)
    {
        if(event.isCancelled())
            return;
        
        // Teleporting to another world stops the music
        // Clear and play now
        if(event.getFrom().getWorld() != event.getTo().getWorld())
        {
            removePlayerFromSystem(event.getPlayer());
        }
    }
    
    
}
