/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic.conditions;

import io.github.rumangerst.dynamicmusic.Condition;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class TimeCondition extends Condition
{
    private int from;
    private int to;

    public TimeCondition(Map<String, Object> serialized)
    {
        super(serialized);
      
        from = Integer.parseInt(serialized.get("from").toString());
        to = Integer.parseInt(serialized.get("to").toString());
    }

    @Override
    public boolean applies(Player player)
    {
        long time = player.getWorld().getTime();        
        return time >= from && time <= to;
    }


    @Override
    public Map<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<>();
        
        serialized.put("from", from);
        serialized.put("to", to);
        
        return serialized;
    }
    
    public static String documentation()
    {
        return "Applies if the world's time is between given period.\n\n"
                + "=Parameters=\n"
                + "from: 0-24000\n"
                + "to: 0-24000\n\n"
                + "Note that you can invert conditions with NOT(X) operation";
    }
}
