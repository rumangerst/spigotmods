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
public class HeightCondition extends Condition
{
    private int height;
    private int operator;

    public HeightCondition(Map<String, Object> serialized)
    {
        super(serialized);
        
        String s = serialized.get("height").toString();
        
        switch(s.charAt(0))
        {
            case '=':
                operator = 0;
                break;
            case '<':
                operator = -1;
                break;
            case '>':
                operator = 1;
                break;
        }
        
        height = Integer.parseInt(s.substring(1));
    }

    @Override
    public boolean applies(Player player)
    {
        switch (operator)
        {
            case -1:
                return player.getLocation().getY() < height;
            case 1:
                return player.getLocation().getY() > height;
            default:
                return (int) player.getLocation().getY() == height;
        }            
    }


    @Override
    public Map<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<>();
        
        String op = "=";
        
        switch(operator)
        {
            case 0:
                op = "=";
                break;
            case -1:
                op = "<";
                break;
            case 1:
                op = ">";
                break;
        }
        
        serialized.put("height", op + height);
        
        return serialized;
    }
    
}
