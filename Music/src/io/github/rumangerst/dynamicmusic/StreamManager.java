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
    private Player player;
    private int stream_id;
    MusicInstance currentMusic = null;
    ArrayList<Song> already_played_songs = new ArrayList<>();
    private Style currentStyle = null;
    
    public StreamManager(DynamicMusicPlugin plugin, Player player, int stream_id)
    {
        this.plugin = plugin;
        this.player = player;
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
    
    
    
    public boolean isPlaying()
    {
        if(currentMusic != null)
            return !currentMusic.isFinished();
        else
            return false;
    }
    
    public void switchStyle(Style style)
    {
        currentStyle = style;
    }
    
    public void informStopped()
    {
        currentMusic = null;
    }
    
    /**
     * Plays next song if enabled and current song finished or style changes.
     * Will only play next song if already playing song can be stopped.
     * @param player 
     */
    public void update()
    {        
        //DynamicMusicPlugin.LOGGER.info(">> Begin update music " + player + " sid " + stream_id);
                
        //DynamicMusicPlugin.LOGGER.info(">>> style " + currentStyle);
        //DynamicMusicPlugin.LOGGER.info(">>> m " + currentMusic);
        
        //if(currentMusic != null)
        //{
        //    DynamicMusicPlugin.LOGGER.info(">>> mf " + currentMusic.isFinished());
        //    DynamicMusicPlugin.LOGGER.info(">>> ms " + currentMusic.getSong().getName());
        //}
            
        // If no music is playing or the style changed
        if(currentStyle != null && (currentMusic == null || currentMusic.isFinished()))
        {
            Song newsong = selectNextSong(currentStyle);
            
            if(newsong == null)
            {
                currentMusic = null;
                //DynamicMusicPlugin.LOGGER.info("<< End update music null");
                return;
            }
            
            // Introduce song now
            MusicInstance m = new MusicInstance(plugin, newsong, player);            
            currentMusic = m;
           
            already_played_songs.remove(newsong);
            already_played_songs.add(newsong);
            
            // Activate the instance
            plugin.musicManager.enqueue(player, m);
            
            //DynamicMusicPlugin.LOGGER.info("<< End update music eq sid " + stream_id);
        }
    }
    
    public Song selectNextSong(Style matching_style)
    {
        //Style must support bucket count
        if(matching_style == null || matching_style.getSongBucketCount() <= stream_id)
            return null;
       
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
            if (!already_played_songs.contains(s))
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
            double t = (double) (i + 1)  / available_songs.size();

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
    
    public Song getCurrentSong()
    {
        if(currentMusic != null)
            return currentMusic.getSong();
        return null;
    }
    
    public int getStreamId()
    {
        return stream_id;
    }
}
