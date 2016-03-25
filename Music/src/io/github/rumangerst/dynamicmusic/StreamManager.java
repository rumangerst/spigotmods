/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * Manages a music stream
 * @author ruman
 */
public class StreamManager
{
    private DynamicMusicPlugin plugin;
    private int stream_id;
    private HashMap<Player, MusicInstance> currentMusic = new HashMap<>();
    private HashMap<Player, Style> currentStyle = new HashMap<>();
    
    private HashMap<Player, ArrayList<Song>> already_played_songs = new HashMap<>();
    
    public StreamManager(DynamicMusicPlugin plugin, int stream_id)
    {
        this.plugin = plugin;
        this.stream_id = stream_id;
    }
    
    /*public boolean stop(Player player)
    {
        if(plugin.musicManager.canStop())
        {
            MusicManager.MusicInstance m = currentMusic.getOrDefault(player, null);
            
            if(m != null)
            {
                m.sendStop();
            }
        }
        
        return false;
    }*/
    
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
        boolean can_stop = plugin.musicManager.canStop();
        
        // Only look for a new style if we can stop
        Style style = can_stop ? currentStyle.getOrDefault(player, null) : null;
        Style newstyle = can_stop ? selectMatchingStyle(player) : null;
        
        if(can_stop)
        {
            if(style != null && style != newstyle)
            {
                if(music != null)
                {
                    //music.sendStop();
                    plugin.musicManager.stop(player);
                    return;
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
            
            if(!already_played_songs.containsKey(player))
                already_played_songs.put(player, new ArrayList<>());
            
            ArrayList<Song> played = already_played_songs.get(player);
            played.add(newsong);
        }
    }
    
    public Song selectNextSong(Player player, Style matching_style)
    {
        //Style must support bucket count
        if(matching_style == null || matching_style.getSongBucketCount() <= stream_id)
            return null;
        
        if(!already_played_songs.containsKey(player))
            already_played_songs.put(player, new ArrayList<>());

        ArrayList<Song> played = already_played_songs.get(player);

        List<Song> available_songs = matching_style.getSongInstances(plugin.api, stream_id);

        if (available_songs.isEmpty())
        {
            return null;
        }

        ArrayList<Song> best_songs = new ArrayList<>();

        /*
            Try to play songs that haven't played yet
         */
        for (Song s : available_songs)
        {
            if (!played.contains(s))
            {
                best_songs.add(s);
            }
        }

        if (!best_songs.isEmpty())
        {
            return best_songs.get(DynamicMusicPlugin.RANDOM.nextInt(best_songs.size()));
        }

        /*
            If all songs have been played, select a random song randomly
            "Old" songs have a higher probability to be picked
         */
        for (int i = 0; i < available_songs.size(); ++i)
        {
            double t = (double) i / available_songs.size();

            if (DynamicMusicPlugin.RANDOM.nextDouble() > t)
            {
                return available_songs.get(i);
            }
        }

        /*
            If nothing was picked, pick any
         */
        return available_songs.get(DynamicMusicPlugin.RANDOM.nextInt(available_songs.size()));
    }
    
    public void update(Player player)
    {
        tryPlayNextSong(player);
    }
    
    public void removePlayer(Player player)
    {
        currentMusic.remove(player);
        currentStyle.remove(player);
    }
    
    public Song getCurrentSong(Player player)
    {
        MusicInstance inst = currentMusic.getOrDefault(player, null);
        
        if(inst != null)
            return inst.getSong();
        return null;
    }
    
    public Style getCurrentStyle(Player player)
    {
        return currentStyle.getOrDefault(player, null);
    }
    
    public int getStreamId()
    {
        return stream_id;
    }
}
