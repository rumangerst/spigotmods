/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.elements;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.Hashtable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
public class InversionElement extends Element
{
    private EntityType[] spawn_level1;
    private EntityType[] spawn_level2;
    private EntityType[] spawn_level3;
    private EntityType[] spawn_level4;
    
    public InversionElement(String id, String description, String... name)
    {
        super(id, description, name);
        
        spawn_level1 = new EntityType[] { EntityType.COW, EntityType.CHICKEN, EntityType.SHEEP, EntityType.MUSHROOM_COW, EntityType.RABBIT, EntityType.PIG, EntityType.BAT };
        spawn_level2 = new EntityType[] { EntityType.SNOWMAN, EntityType.HORSE, EntityType.ZOMBIE, EntityType.WOLF };
        spawn_level3 = new EntityType[] { EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SILVERFISH, EntityType.SLIME, EntityType.CREEPER, EntityType.IRON_GOLEM };
        spawn_level4 = new EntityType[] { EntityType.BLAZE, EntityType.WITCH, EntityType.CAVE_SPIDER, EntityType.GHAST, EntityType.CREEPER, EntityType.IRON_GOLEM };
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
        return (int)(level * level * 7.0);
    }
    
    @Override
    public void execute(Entity caster, int level)
    { 
            Location loc = caster.getLocation();
            //loc.add((0.5 - CrystalMagicPlugin.RANDOM.nextDouble()) * 2.0, 0, (0.5 - CrystalMagicPlugin.RANDOM.nextDouble()) * 2.0);
            
            switch(level)
            {
                case 1:
                    loc.getWorld().spawnEntity(loc, spawn_level1[CrystalMagicPlugin.RANDOM.nextInt(spawn_level1.length)]);
                    break;
                case 2:
                    loc.getWorld().spawnEntity(loc, spawn_level2[CrystalMagicPlugin.RANDOM.nextInt(spawn_level2.length)]);
                    break;
                case 3:
                    loc.getWorld().spawnEntity(loc, spawn_level3[CrystalMagicPlugin.RANDOM.nextInt(spawn_level3.length)]);
                    break;
                case 4:
                    loc.getWorld().spawnEntity(loc, spawn_level4[CrystalMagicPlugin.RANDOM.nextInt(spawn_level4.length)]);
                    break;
            }
            
            caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 0.5f, 2.0f);
        
    }
    
}
