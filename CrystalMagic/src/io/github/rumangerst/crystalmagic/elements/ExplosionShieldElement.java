/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.HashMap;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 *
 * @author ruman
 */
public class ExplosionShieldElement extends Element
{
    
    private HashMap<LivingEntity, Long> effect_duration = new HashMap<>();
    private HashMap<LivingEntity, Integer> effect_level = new HashMap<>();
    
    public ExplosionShieldElement(String id, String description, String... name)
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
        switch(level)
        {
            case 1:
                return 10;
            case 2:
                return 30;
            case 3:
                return 80;
            case 4:
                return 180;
            default:
                return 1000;
        }
    }
    
    @Override
    public void execute(Entity caster, int level)
    {      
        int radius = level * 2;
        
        for(Entity e : caster.getNearbyEntities(radius, radius, radius))
        {
            if(e instanceof LivingEntity)
            {
                LivingEntity p = (LivingEntity)e;
                
                effect_duration.put(p, System.currentTimeMillis() + level * 5000);
                effect_level.put(p, level);
            }
        }
    }
    
    @EventHandler
    public void catchEvent(EntityDamageEvent event)
    {
        if(event.isCancelled())
            return;
        
        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
            return;
        
        if(event.getEntity() instanceof LivingEntity)
        {
            long timeout = effect_duration.getOrDefault((LivingEntity)event.getEntity(), 0L);
            int level = effect_level.getOrDefault((LivingEntity)event.getEntity(), 0);
            
            if(level != 0 && System.currentTimeMillis() < timeout)
            {
                double mod = 1;
                
                switch(level)
                {
                    case 1:
                        mod = 0.6;
                        break;
                    case 2:
                        mod = 0.5;
                        break;
                    case 3:
                        mod = 0.4;
                        break;
                    case 4:
                        mod = 0.3;
                }
                
                event.setDamage(event.getDamage() * mod);
                event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.BLOCK_ANVIL_LAND, 0.4f, 1.5f);
            }
        }
    }
    
}
