/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class Song implements ConfigurationSerializable
{
    private String soundid;
    private String name;
    private int length;
    
    public Song(Map<String, Object> serialized)
    {
        soundid = serialized.get("soundid").toString();
        name = serialized.get("name").toString();
        
        String l = serialized.get("duration").toString();
        
        if(l.contains(":"))
        {
            String[] cell = l.split(":");
            
            if(cell.length != 2)
                throw new IllegalArgumentException("Duration must be in format X where X is milliseconds or Y:Z where Y is minutes, Z is seconds");
            
            length = Integer.parseInt(cell[0]) * 60 * 1000 + Integer.parseInt(cell[1]) * 1000;
        }
        else
        {
            length = Integer.parseInt(l);
        }
    }
    
    /**
     * 
     * @param soundid The internal minecraft id
     * @param name The human readable name
     * @param length The length of the song in milliseconds
     */
    public Song(String soundid, String name, int length)
    {
        this.soundid = soundid;
        this.name = name;
        this.length = length;
    }
    
    public Song(String soundid, String name, int minutes, int seconds)
    {
        this(soundid, name, minutes * 60 * 1000 + seconds * 1000);
    }
    
    /**
     * Plays the song to the player
     * @param player 
     */
    public void play(Player player)
    {
        Location loc = new Location(player.getLocation().getWorld(), 0, 100000, 0);
        player.playSound(loc, soundid, Float.MAX_VALUE, 1);
        
        //DynamicMusicPlugin.LOGGER.info("Playing music " + soundid + " for " + player.getDisplayName());
    }
    
    /**
     * Get playsound id
     * @return 
     */
    public String getSoundId()
    {
        return soundid;
    }
    
    /**
     * Get the name
     * @return 
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Get the length of the song in milliseconds
     * @return 
     */
    public int getLength()
    {
        return length;
    }

    @Override
    public Map<String, Object> serialize()
    {
        HashMap<String, Object> d = new HashMap<>();
        d.put("soundid", soundid);
        d.put("name", name);
        d.put("duration", length);
        
        return d;
    }
}
