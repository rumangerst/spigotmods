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
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class WorldCondition extends Condition
{
    private ArrayList<String> worlds = new ArrayList<>();

    public WorldCondition(Map<String, Object> serialized)
    {
        super(serialized);
        
        List<String> worldnames = (List<String>)serialized.get("worlds");        
        worlds = new ArrayList<>(worldnames);
    }

    @Override
    public boolean applies(Player player)
    {
        return worlds.contains(player.getLocation().getWorld().getName());
    }


    @Override
    public Map<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<>();
        
        serialized.put("worlds", worlds);
        return serialized;
    }
    
}
