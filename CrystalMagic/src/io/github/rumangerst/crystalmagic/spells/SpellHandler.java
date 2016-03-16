/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.spells;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
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
    private HashMap<Player, Spell> spells = new HashMap<>();
    
    public SpellHandler(CrystalMagicPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    private void cancelSpell(Player player)
    {
        if(isUsingSpell(player))
        {
            spells.remove(player);
        }
    }
    
    public void executeSpellNow(Player player)
    {
        Spell spell = spells.getOrDefault(player, null);
        
        if(spell != null)
        {
            spell.execute();
            spells.remove(player);
        }
    }
    
    public void spell(Player player, Spell spell)
    {
        CrystalMagicPlugin.LOGGER.info("Player " + player.getName() + " starts spell " + spell);
        
        if(isUsingSpell(player))
        {
            cancelSpell(player);
        }
        
        spells.put(player, spell);
    }
    
    public boolean isUsingSpell(Player player)
    {
        return spells.containsKey(player);
    }
    
    public int getMana(Player player)
    {
        return Math.max(0, plugin.data_storage.getInt(player.getUniqueId().toString() + ".mana", 0));
    }
    
    public void setMana(Player player, int mana)
    {
        int max_mana = plugin.data_storage.getInt(player.getUniqueId().toString() + ".max_mana", 10);
        plugin.data_storage.set(player.getUniqueId().toString() + ".mana", Math.min(max_mana, Math.max(0, mana)));
        
        player.sendMessage("Mana:" + mana);
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
           setMana(player, new_mana);
           
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
           
           setMana(player, new_mana);
           
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
            Spell spell = spells.get(player);
            
            if(modifyMana(player, -spell.manaCost()) == 0 || spell.load())
            {
                executeSpellNow(player);
            }
        }
    }
}
