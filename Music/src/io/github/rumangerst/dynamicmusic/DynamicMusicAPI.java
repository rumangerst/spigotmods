/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import io.github.rumangerst.dynamicmusic.conditions.BiomeCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class DynamicMusicAPI
{
    private DynamicMusicPlugin plugin;
    HashMap<String, Song> songs = new HashMap<>();
    ArrayList<Style> styles = new ArrayList<>();
    private HashMap<String, Style> style_map = new HashMap<>();
    //private HashMap<String, Condition> conditions = new HashMap<>();    
    
    public DynamicMusicAPI(DynamicMusicPlugin plugin)
    {
        this.plugin = plugin;
        registerVanillaSongs();
    }
        
    private void registerVanillaSongs()
    {
        /*registerSong(new Song("record.cat", "C418 - Cat", 3, 7));
        registerSong(new Song("record.blocks", "C418 - Blocks", 5, 43));
        registerSong(new Song("record.chirp", "C418 - Chirp", 3, 7));
        registerSong(new Song("record.far", "C418 - Far", 3, 12));
        registerSong(new Song("record.mall", "C418 - Mall", 3, 18));
        registerSong(new Song("record.mellohi", "C418 - Mellohi", 1, 38));
        registerSong(new Song("record.stal", "C418 - Stal", 2, 32));
        registerSong(new Song("record.strad", "C418 - Strad", 3, 9));
        registerSong(new Song("record.wait", "C418 - Wait", 3, 54));
        registerSong(new Song("record.ward", "C418 - Ward", 4, 10));*/
    }    
    
    public void registerSong(Song song)
    {
        DynamicMusicPlugin.LOGGER.info("Registering song " + song.getName() + " with id " + song.getSoundId());
        
        songs.put(song.getSoundId(), song);
    }
    
    public void registerStyle(Style style)
    {
        registerStyle(style, -1);
    }
    
    public void registerStyle(Style style, int priority)
    {
        Style existing = style_map.getOrDefault(style.getName(), null);
        
        if(existing != null)
        {
            styles.remove(existing);
        }
        
        if(priority < 0)
            styles.add(style);
        else
            styles.add(priority, style);
        style_map.put(style.getName(), style);
        
        DynamicMusicPlugin.LOGGER.info("Registering style " + style.getName() + " at priority " + styles.indexOf(style));
    }
    
    public Song getSongFromId(String id)
    {
        return songs.getOrDefault(id, null);
    }
    
    public Song getSongFromName(String id)
    {
        for(Song s : songs.values())
        {
            if(s.getName().equals(id))
                return s;
        }
        
        return null;
    }
    
    public <T extends ConfigurationSerializable> void registerConditionType(Class<T> cl, String alias)
    {
        DynamicMusicPlugin.LOGGER.info("Registering condition type " + cl.getCanonicalName() + " as " + alias);
        ConfigurationSerialization.registerClass(cl, alias);
    }
}
