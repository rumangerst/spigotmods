/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author ruman
 */
public class SpellHandler implements Listener, Runnable
{
    private CrystalMagicPlugin plugin;
    private HashMap<Player, Spell> spells;
    private HashMap<Player, Long> timeouts;
    
    public SpellHandler(CrystalMagicPlugin plugin)
    {
        
    }
    
    public boolean isUsingSpell(Player player)
    {
        return spells.containsKey(player);
    }
    
    public int getMana(Player player)
    {
        return plugin.data_storage.getInt(player.getUniqueId().toString() + ".mana", 0);
    }
    
    /**
     * 
     * @param player
     * @param delta
     * @return the actual delta that could be achieved
     */
    public int modifyMana(Player player, int delta)
    {
       if(delta > 0)
       {
           int max_mana = plugin.data_storage.getInt(player.getUniqueId().toString() + ".max_mana", 10);
           int current_mana = getMana(player);
           
           int new_mana = Math.min(max_mana, delta + current_mana);
           plugin.data_storage.set(player.getUniqueId().toString() + ".mana", new_mana);
           
           return new_mana - current_mana;
       }
       else if(delta < 0)
       {
           int current_mana = getMana(player);           
           int new_mana = current_mana + delta;
           
           if(new_mana < 0)
           {
               player.damage(-new_mana);
               new_mana = 0;
           }
           
           plugin.data_storage.set(player.getUniqueId().toString() + ".mana", new_mana);
           
           if(player.getHealth() <= 0)
           {
               return 0; //failed
           }
           else
           {
               return delta;
           }
       }
       
       return 0;
    }

    @Override
    public void run()
    {
        for(Player player : spells.keySet())
        {
            // To prevent preloading of spells, pull mana each second
            modifyMana(player, -1);
        }
    }
}
