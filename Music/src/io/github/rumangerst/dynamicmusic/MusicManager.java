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
    private HashMap<Player, MusicInstance> currentMusic = new HashMap<>();
    private HashMap<Player, Style> currentStyle = new HashMap<>();
    
    private HashMap<Player, ArrayList<Song>> already_played_songs = new HashMap<>();
    
    public MusicManager(DynamicMusicPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public boolean canStop()
    {
        return plugin.getConfig().getBoolean("StoppingMusic.Enable");
    }
    
    public boolean stop(Player player)
    {
        if(canStop())
        {
            MusicInstance m = currentMusic.getOrDefault(player, null);
            
            if(m != null)
            {
                m.sendStop();
            }
        }
        
        return false;
    }
    
//    /**
//     * Tries to stop playback of current music for a player
//     * @param player
//     * @return true if signal was sent, otherwise false
//     */
//    public boolean stop(Player player)
//    {
//        if(canStop())
//        {
////            String sound = plugin.getConfig().getString("StoppingMusic.SoundName");
////            float volume = (float)plugin.getConfig().getDouble("StoppingMusic.SoundVolume");
////            int iterations = plugin.getConfig().getInt("StoppingMusic.Iterations");
////            
////            for(int i = 0; i < iterations; ++i)
////            {
////                player.playSound(player.getLocation(), sound, volume, 1.0f);
////            }
//            
////            CraftPlayer p = (CraftPlayer)player;
////            CraftWorld w = (CraftWorld)p.getWorld();
////            
////            WorldSettings.EnumGamemode md = WorldSettings.EnumGamemode.NOT_SET;
////            
////            if(p.getGameMode() == GameMode.CREATIVE)
////                md =WorldSettings.EnumGamemode.CREATIVE;
////            else if (p.getGameMode() == GameMode.SURVIVAL)
////                md = WorldSettings.EnumGamemode.SURVIVAL;
////            
////            p.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(-1, w.getHandle().getDifficulty(), w.getHandle().L(), md));
//            
//            return true;
//        }
//        
//        return false;
//    }
    
    /**
     * Plays next song if enabled and current song finished or style changes.
     * Will only play next song if already playing song can be stopped.
     * @param player 
     */
    public void tryPlayNextSong(Player player)
    {
        if(!plugin.preferencesConfiguration.getBoolean(player.getUniqueId().toString() + ".enable_music", true))
            return;
        
        MusicInstance music = currentMusic.getOrDefault(player, null);
        boolean can_stop = canStop();
        
        // Only look for a new style if we can stop
        Style style = can_stop ? currentStyle.getOrDefault(player, null) : null;
        Style newstyle = can_stop ? selectMatchingStyle(player) : null;
        
        if(can_stop)
        {
            if(style != newstyle)
            {
                if(music != null)
                {
                    music.sendStop();
                }
            }
        }
            
        // If no music is playing or the style changed
        if(music == null || music.isFinished())
        {
            //If styles have not been selected, select them now
            if(!can_stop)
            {
                newstyle = selectMatchingStyle(player);
            }            
           
            Song newsong = selectNextSong(player, newstyle);
            
            if(newsong == null)
            {
                currentMusic.put(player, null);
                currentStyle.put(player, null);
                return;
            }
            
            // Introduce song now
            currentMusic.put(player, new MusicInstance(plugin, newsong, player));
            currentStyle.put(player, newstyle);
        }
    }
    
//    /**
//     * Forces to play a matching music for the player. May cause multiple songs playing at once
//     * @param player 
//     */
//    public void forcePlayMatchingMusic(Player player)
//    {        
//        currentMusic.remove(player);
//        
//        
//        
//        DynamicMusicPlugin.LOGGER.info("try selectsong");
//        
//        Song matching = selectNextSong(player);
//        
//        if(matching != null)
//        {            
//            ArrayList<Song> played = already_played_songs.get(player);
//            played.remove(matching);
//            played.add(matching);
//            
//            currentMusic.put(player, new MusicInstance(matching, player));
//        }
//    }    
    
    public void sendInfo(Player player)
    {
        if(plugin.preferencesConfiguration.getBoolean(player.getUniqueId().toString() + ".enable_music", true))
        {
            player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "No music enabled. Run /music enable to enable music" + ChatColor.RED + "♬");
        }
        else
        {
            MusicInstance music = currentMusic.getOrDefault(player, null);
            Style style = currentStyle.getOrDefault(player, null);
            
            if(music == null)
            {
                player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "No music playing" + ChatColor.RED + "♬");
            }
            else
            {
                player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "Playing: " + music.song.getName() + ChatColor.RED + "♬");
                player.sendMessage(ChatColor.RED + "♬" + ChatColor.WHITE + "In style: " + style.getName() + ChatColor.RED + "♬");
            }
        }
    }
    
    private Style selectMatchingStyle(Player player)
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
    
    private Song selectNextSong(Player player, Style matching_style)
    {
        if(!already_played_songs.containsKey(player))
            already_played_songs.put(player, new ArrayList<>());
        
        ArrayList<Song> played = already_played_songs.get(player);
        
        if(matching_style != null)
        {            
            List<Song> available_songs = matching_style.getSongInstances(plugin.api);
            
            if(available_songs.isEmpty())
                return null;
            
            ArrayList<Song> best_songs = new ArrayList<>();
            
            /*
            Try to play songs that haven't played yet
            */
            
            for(Song s : available_songs)
            {
                if(!played.contains(s))
                {
                    best_songs.add(s);
                }
            }
            
            if(!best_songs.isEmpty())
            {
                return best_songs.get(DynamicMusicPlugin.RANDOM.nextInt(best_songs.size()));
            }
            
            /*
            If all songs have been played, select a random song randomly
            "Old" songs have a higher probability to be picked
            */
            
            for(int i = 0; i < available_songs.size(); ++i)
            {
                double t = (double)i / available_songs.size();
                
                if(DynamicMusicPlugin.RANDOM.nextDouble() > t )
                {
                    return available_songs.get(i);
                }
            }
            
            /*
            If nothing was picked, pick any
            */
            return available_songs.get(DynamicMusicPlugin.RANDOM.nextInt(available_songs.size()));                
        }
        
        return null;
    }

    @Override
    public void run()
    {
        for(Player player : plugin.getServer().getOnlinePlayers())
        {
//            MusicInstance music = currentMusic.getOrDefault(player, null);
//            
//            // If no music found or the music is finished, play a matching song
//            if(music == null || music.isFinished())
//            {
//                forcePlayMatchingMusic(player);
//            }
            
            tryPlayNextSong(player);
            
        }
    }
    
    private void removePlayer(Player player)
    {
        currentMusic.remove(player);
        currentStyle.remove(player);
    }
    
    @EventHandler
    public void updateOnLogin(PlayerLoginEvent event)
    {
        //forcePlayMatchingMusic(event.getPlayer());
        removePlayer(event.getPlayer());
    }
    
    @EventHandler
    public void updateOnRespawn(PlayerRespawnEvent event)
    {
        // Respawning stops the music
        // Clear and play now
        removePlayer(event.getPlayer());
    }
    
    @EventHandler
    public void updateOnTeleport(PlayerTeleportEvent event)
    {
        // Teleporting to another world stops the music
        // Clear and play now
        if(event.getFrom().getWorld() != event.getTo().getWorld())
        {
            //forcePlayMatchingMusic(event.getPlayer());
            removePlayer(event.getPlayer());
        }
    }
    
    public static class MusicStopTask extends BukkitRunnable
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
            if(player.isOnline())
            {
                if(--iterations >= 0)
                {
                    DynamicMusicPlugin.LOGGER.info("Stop task play sound " + iterations);
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
    
    public static class MusicInstance
    {
        private DynamicMusicPlugin plugin;
        private Song song;
        private Player player;
        private long timeout;
        private MusicStopTask stop_task = null;
        
        public MusicInstance(DynamicMusicPlugin plugin, Song song, Player player)
        {
            this.plugin = plugin;
            this.player = player;
            this.song = song;
            this.timeout = System.currentTimeMillis() + song.getLength();
            
            song.play(player);
        }
        
        public void sendStop()
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
        }
        
        public boolean isFinished()
        {
            if(stop_task != null)
            {
                return stop_task.finished();
            }
            
            return System.currentTimeMillis() > timeout;
        }
    }
}
