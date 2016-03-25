/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.ArrayList;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 *
 * @author ruman
 */
public class DetoxElement extends Element
{
    
    public DetoxElement(String id, String description, String... name)
    {
        super(id, description, name);
    }
    
    @Override
    public boolean canEnchant()
    {
        return false;
    }
    
    @Override
    public void enchant(ItemStack stack, int level)
    {
        
    }
    
    @Override
    public boolean canExecute()
    {
        return true;
    }
    
    @Override
    public int getManaCost(int level)
    {
        return (int)(level * level * 5.0);
    }
    
    private void execute(LivingEntity target, int level)
    {
        if(level > 0)
        {
            target.removePotionEffect(PotionEffectType.SPEED);
            target.removePotionEffect(PotionEffectType.SLOW);
            
            if(level > 1)
            {
                target.removePotionEffect(PotionEffectType.SATURATION);
                target.removePotionEffect(PotionEffectType.HUNGER);
                target.removePotionEffect(PotionEffectType.LEVITATION);
                
                if(level > 2)
                {
                    target.removePotionEffect(PotionEffectType.REGENERATION);
                    target.removePotionEffect(PotionEffectType.POISON);
                    target.removePotionEffect(PotionEffectType.GLOWING);
                    
                    if(level > 3)
                    {
                        ArrayList<PotionEffect> e = new ArrayList<>(target.getActivePotionEffects());
                        
                        for(PotionEffect i : e)
                        {
                            target.removePotionEffect(i.getType());
                        }
                    }
                }
            }
        }

        target.getWorld().playEffect(target.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
    }
    
    @Override
    public void execute(Entity caster, int level)
    {      
        if(caster instanceof Player)
        {
            execute((Player)caster, level);
        }
        else
        {
            int radius = level;

            for(Entity e : caster.getNearbyEntities(radius, radius, radius))
            {
                if(e instanceof LivingEntity)
                {
                    LivingEntity p = (LivingEntity)e;
                    execute(p, level);
                }
            }
        }
        //caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, new PotionData(PotionType.SLOWNESS).getType().getEffectType().);
    }
    
}
