/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public abstract class Condition implements ConfigurationSerializable
{
    private char label;
    
    public Condition(Map<String, Object> serialized)
    {
        label = serialized.get("label").toString().charAt(0);
    }
    
    public char getLabel()
    {
        return label;
    }
    
    /**
     * If the condition applies to the current situation of the plaer
     * @param player
     * @return 
     */
    public abstract boolean applies(Player player);
}
