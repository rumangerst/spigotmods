/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.spells;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.HashMap;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            //CrystalMagicPlugin.LOGGER.info("Executing spell " + spell + " for player " + player);
            try
            {
                spell.execute();
            }
            catch (Exception ex)
            {
                CrystalMagicPlugin.LOGGER.severe(ex.getMessage());
            }
            
            spells.remove(player);
        }
    }
    
    public void spell(Player player, Spell spell)
    {
        //CrystalMagicPlugin.LOGGER.info("Player " + player.getName() + " starts spell " + spell);
        
        if(isUsingSpell(player))
        {
            cancelSpell(player);
        }
        
        spells.put(player, spell);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, CustomItemsAPI.secondsToTicks(2), 2));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.4f, 0.4f);
    }
    
    public boolean isUsingSpell(Player player)
    {
        return spells.containsKey(player);
    }
    
    public int getMana(Player player)
    {
        return Math.max(0, plugin.data_storage.getInt(player.getUniqueId().toString() + ".mana", 0));
    }
    
    public int getMaxMana(Player player)
    {
        return plugin.data_storage.getInt(player.getUniqueId().toString() + ".max_mana", 10);
    }
    
    public int getManaRegeneration(Player player)
    {
        return 1;
    }
    
    public void setMana(Player player, int mana)
    {
        int max_mana = getMaxMana(player);
        plugin.data_storage.set(player.getUniqueId().toString() + ".mana", Math.min(max_mana, Math.max(0, mana)));
        
        sendInformation(player);
    }
    
    public void sendInformation(Player player)
    {
        String data = textProgressBar('✦', '✧', 10, (double)getMana(player) / (double)getMaxMana(player));        
        
        Spell spell = spells.getOrDefault(player, null);
        
        if(spell != null)
        {
            data += " - " + spell.getStatus();
        }
        
        BountifulAPI.sendActionBar(player, data);
    }
    
    public static String textProgressBar(char filled, char unfilled, int length, double value)
    {
        String t = "";        
        int v = (int)(length * value);
        
        for(int i = 0; i < v; ++i)
            t += filled;
        for(int i = 0; i < length - v; ++i)
            t += unfilled;
        
        return t;
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
        for(Player player : plugin.getServer().getOnlinePlayers())
        {
            Spell spell = spells.getOrDefault(player, null);
            
            if(!player.isOnline())
            {
                spells.remove(player);
                continue;
            }
            
            if(spell != null)
            {
                if(modifyMana(player, -spell.manaCost()) == 0 || spell.load())
                {
                    executeSpellNow(player);
                }
                else
                {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, CustomItemsAPI.secondsToTicks(2), 2));
                }
                
                sendInformation(player);
            }
            else
            {
                int old_mana = getMana(player);
                int new_mana = Math.min(getMaxMana(player), old_mana + getManaRegeneration(player));
                
                if(old_mana != new_mana)
                {
                    if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.2)
                        player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
                    
                    setMana(player, getMana(player) + getManaRegeneration(player));
                }
            }
        }
    }
}
