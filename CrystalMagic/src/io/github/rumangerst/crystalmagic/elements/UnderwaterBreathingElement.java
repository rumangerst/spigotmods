/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Effect;
import org.bukkit.enchantments.Enchantment;
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
public class UnderwaterBreathingElement extends Element
{
    
    public UnderwaterBreathingElement(String id, String description, String... name)
    {
        super(id, description, name);
    }
    
    @Override
    public boolean canEnchant()
    {
        return true;
    }
    
    @Override
    public void enchant(ItemStack stack, int level)
    {
        if(level > 0)
        {
            stack.addUnsafeEnchantment(Enchantment.OXYGEN, level);
        }
    }
    
    @Override
    public boolean canExecute()
    {
        return true;
    }
    
    @Override
    public int getManaCost(int level)
    {
        return (int)(level * level * 3.5);
    }
    
    private void execute(LivingEntity target, int level)
    {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, CustomItemsAPI.secondsToTicks(level * 5), level));
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
            double radius = level + 0.8;
            Element.playRadiusEffect(caster, level, Effect.SPELL, 0);

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
