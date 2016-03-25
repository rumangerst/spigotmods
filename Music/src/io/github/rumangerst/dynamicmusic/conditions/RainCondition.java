/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic.conditions;

import io.github.rumangerst.dynamicmusic.Condition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class RainCondition extends Condition
{

    public RainCondition(Map<String, Object> serialized)
    {
        super(serialized);
    }

    @Override
    public boolean applies(Player player)
    {
        World w = player.getWorld();
        
        return w.hasStorm();
    }


    @Override
    public Map<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<>();        
        return serialized;
    }
    
    public static String documentation()
    {
        return "Applies if it rains in player's world.\n\n"
                + "=Parameters=\n"
                + "No parameters";
    }
    
}
