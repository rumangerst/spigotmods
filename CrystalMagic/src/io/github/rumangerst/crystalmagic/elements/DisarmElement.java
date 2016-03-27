/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author ruman
 */
public class DisarmElement extends Element
{
    
    public DisarmElement(String id, String description, String... name)
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
        return (int)(level * level * 7);
    }
    
        
    private void dropPlayerArmor(Player player)
    {
        PlayerInventory inv = player.getInventory();
        
        ItemStack dropped = null;
        
        if(inv.getHelmet() != null && inv.getHelmet().getType() != Material.AIR)
        {
            dropped = inv.getHelmet();
            inv.setHelmet(null);
        }
        else if(inv.getBoots() != null && inv.getBoots().getType() != Material.AIR)
        {
            dropped = inv.getBoots();
            inv.setBoots(null);
        }
        else if(inv.getLeggings()!= null && inv.getLeggings().getType() != Material.AIR)
        {
            dropped = inv.getBoots();
            inv.setLeggings(null);
        }
        else if(inv.getChestplate()!= null && inv.getChestplate().getType() != Material.AIR)
        {
            dropped = inv.getBoots();
            inv.setChestplate(null);
        }
        
        if(dropped != null)
        {
            player.getWorld().dropItemNaturally(player.getLocation(), dropped);
        }
    }
    
    private void dropPlayerHand(Player player)
    {
        PlayerInventory inv = player.getInventory();
        
        ItemStack dropped = null;
        
        if(inv.getItemInOffHand()!= null && inv.getItemInOffHand().getType() != Material.AIR)
        {
            dropped = inv.getItemInOffHand();
            inv.setItemInOffHand(null);
        }
        else if(inv.getItemInMainHand()!= null && inv.getItemInMainHand().getType() != Material.AIR)
        {
            dropped = inv.getItemInMainHand();
            inv.setItemInMainHand(null);
        }
        
        if(dropped != null)
        {
            player.getWorld().dropItemNaturally(player.getLocation(), dropped);
        }
    }
    
    private void dropAnyItem(Player player)
    {
        PlayerInventory inv = player.getInventory();
        
        ItemStack dropped = null;
        
        int adder = CrystalMagicPlugin.RANDOM.nextInt(inv.getSize());
        
        for(int i = 0; i < inv.getSize(); ++i)
        {
            int j = (i + adder) % inv.getSize();
            
            dropped = inv.getItem(j);
            
            if(dropped != null && dropped.getType() != Material.AIR)
            {
                inv.clear(j);
                break;
            }
        }
        
        if(dropped != null && dropped.getType() != Material.AIR)
        {
            player.getWorld().dropItemNaturally(player.getLocation(), dropped);
        }
    }
    
    private void execute(LivingEntity target, int level)
    {
        if(target instanceof Player)
        {
            Player player = (Player)target;
            
            for(int lv = 1; lv <= level; ++lv)
            {
                if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.5)
                    dropPlayerArmor(player);
                if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.3)
                    dropPlayerHand(player);
            }
            
            int items_to_throw = CrystalMagicPlugin.RANDOM.nextInt(level) * 2;
            
            for(int i = 0; i < items_to_throw; ++i)
            {
                dropAnyItem(player);
            }
        }
        else
        {
            if(level > 0)
            {
                target.getEquipment().setBoots(null);
                
                if(level > 1)
                {
                    target.getEquipment().setLeggings(null);
                    target.getEquipment().setItemInOffHand(null);
                    
                    if(level > 2)
                    {
                        target.getEquipment().setHelmet(null);
                        target.getEquipment().setItemInMainHand(null);
                        
                        if(level > 3)
                        {
                            target.getEquipment().setChestplate(null);
                        }
                    }
                }
            }
        }
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
