/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 *
 * @author ruman
 */
public class LevitationElement extends Element
{
    
    public LevitationElement(String id, String description, String... name)
    {
        super(id, description, name);
    }
    
    @Override
    public boolean canEnchant()
    {
        return false;
    }
    
    @Override
    public void enchant(ItemStack stack)
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
        return (int)(level * level * 5.6);
    }
    
    @Override
    public void execute(Entity caster, int level)
    {      
        double radius = level + 0.8;
        LevitationElement.playRadiusEffect(caster, level, Effect.MOBSPAWNER_FLAMES, 0);
        
        for(Entity e : caster.getNearbyEntities(radius, radius, radius))
        {
            if(e instanceof LivingEntity)
            {
                LivingEntity p = (LivingEntity)e;
                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, CustomItemsAPI.secondsToTicks(level * 5), level));
                
            }
        }  
    }
    
}
