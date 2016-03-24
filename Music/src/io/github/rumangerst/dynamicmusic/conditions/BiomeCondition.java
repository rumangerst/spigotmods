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
public class BiomeCondition extends Condition
{
    private ArrayList<Biome> biomes = new ArrayList<>();

    public BiomeCondition(Map<String, Object> serialized)
    {
        super(serialized);
        
        List<String> biomenames = (List<String>)serialized.get("biomes");
        
        for(String b : biomenames)
        {
            Biome biome = Biome.valueOf(b);
            biomes.add(biome);
        }
    }

    @Override
    public boolean applies(Player player)
    {
        return biomes.contains(player.getLocation().getBlock().getBiome());
    }


    @Override
    public Map<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<>();
        ArrayList<String> biomelist = new ArrayList<>();
        
        for(Biome b : biomes)
        {
            biomelist.add(b.name());
        }
        
        serialized.put("biomes", biomelist);
        return serialized;
    }
    
}
