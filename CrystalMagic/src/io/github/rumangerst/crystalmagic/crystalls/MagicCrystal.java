/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import com.google.common.collect.Lists;
import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.crystalmagic.spells.MagicCrystalConductSpell;
import io.github.rumangerst.crystalmagic.spells.MagicCrystalLoadSpell;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

/**
 *
 * @author ruman
 */
public class MagicCrystal extends MagicGem
{
    
    public MagicCrystal(String id, String name, CrystalMagicPlugin plugin)
    {
        super(id, Material.NETHER_STAR, (byte)0, name, plugin);
    }
    
    @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);
        
        String name;
        
        if(getElements(stack).isEmpty())
        {
            name = "Magischer Kristall";
        }
        else
        {
            if(isProjectile(stack))
            {
                name = "Magischer Projektilkristall";
            }
            else
            {
                name = "Magischer Elementkristall";
            }
            
            if (isInstant(stack))
            {
                name += " (Speicher)";
            }
            else
            {
                name += " (Konduktor)";
            }
        }
        
        HashMap<String, Integer> element_data = getElements(stack);
        ArrayList<String> element_ids = new ArrayList<>(element_data.keySet());
        
        if(!element_ids.isEmpty())
        {
            Collections.sort(element_ids);            
            ArrayList<String> elem_names = new ArrayList<>();            
            
            for(String eid : element_ids)
            {
                Element e = (Element)CustomItemsAPI.api(plugin).getCustomItem(eid);
                String n = e.getNameForLevel(element_data.get(eid));
                
                elem_names.add(n.replace("Element: ", ""));
            }
            
            name += " [" + String.join(", ", elem_names) + "]";
        }
        
        NBTAPI.setString(stack, "display/Name", name);
    }
    
    @Override
    protected String makeLore(ItemStack stack)
    {
        if(isInstant(stack))
        {
            int focus = getFocus(stack);
            int maxfocus = getMaxFocus(stack);
            
            return focus + "/" + maxfocus + " Fokus\n" + super.makeLore(stack);
        }
        else
        {
            return super.makeLore(stack);
        }
    }
    
    public boolean isInstant(ItemStack stack)
    {
        return NBTAPI.getBool(stack, "crystalmagic/magiccrystal/instant", false);
    }
    
    public void setInstant(ItemStack stack, boolean instant)
    {
        NBTAPI.setBool(stack, "crystalmagic/magiccrystal/instant", instant);
        transform(stack);
    }
    
    public boolean isProjectile(ItemStack stack)
    {
        return NBTAPI.getBool(stack, "crystalmagic/magiccrystal/projectile", false);
    }
    
    public void setProjectile(ItemStack stack, boolean projectile)
    {
        NBTAPI.setBool(stack, "crystalmagic/magiccrystal/projectile", projectile);
        transform(stack);
    }
    
    public int getFocus(ItemStack stack)
    {
        return NBTAPI.getInt(stack, "crystalmagic/magiccrystal/focus", 0);
    }
    
    public void setFocus(ItemStack stack, int focus)
    {
        NBTAPI.setInt(stack, "crystalmagic/magiccrystal/focus", Math.max(0, Math.min(getMaxFocus(stack), focus)));
        transform(stack);
    }
    
    public int getMaxFocus(ItemStack stack)
    {
        int level = getLevel(stack);
        
        return level * level * 20;
    }
    
    private void spawnMagicProjectile(Player player, HashMap<Element, Integer> levels)
    {        
        Location spawnAt = player.getEyeLocation().toVector().add(player.getEyeLocation().getDirection().multiply(2)).toLocation(player.getWorld());
                        
        /*Fireball entity = (Fireball) player.getWorld().spawnEntity(spawnAt, EntityType.FIREBALL);
        entity.setDirection(player.getEyeLocation().getDirection());
        
        entity.setMetadata("crystalmagic_projectile", new FixedMetadataValue(plugin, levels));*/
        
        int weight = 0;
        
        for(int lv : levels.values())
        {
            if(lv >  weight)
                weight = lv;
        }
        
        //Projectile proj;
        //Fireball entity;
        double vel;
        
        Fireball entity = (Fireball) player.getWorld().spawnEntity(spawnAt, EntityType.FIREBALL);
        entity.setDirection(player.getEyeLocation().getDirection());
        
        if(weight <= 1)
        {
            //proj = player.launchProjectile(Fireball.class);
            //entity = (Fireball)proj;
            vel = 2.5;
            
            player.getWorld().playSound(spawnAt, Sound.ENTITY_FIREWORK_LAUNCH, 1.0f, 0.2f);
        }
        else if(weight == 2)
        {
            //proj = player.launchProjectile(Fireball.class);
            //entity = (Fireball)proj;
            vel = 2.0;
            
            player.getWorld().playSound(spawnAt, Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.2f);
        }
        else if(weight == 3)
        {
            //proj = player.launchProjectile(Fireball.class);
            //entity = (Fireball)proj;
            vel = 1.5;
            
            player.getWorld().playSound(spawnAt, Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.2f);
        }
        else
        {
            //proj = player.launchProjectile(LargeFireball.class);
            //entity = (Fireball)proj;
            vel = 1;
            
            player.getWorld().playSound(spawnAt, Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 2.0f);
        }
        
        entity.setDirection(entity.getDirection().multiply(vel));        
        entity.setMetadata("crystalmagic_projectile", new FixedMetadataValue(plugin, levels));
    }
    
    @EventHandler
    public void triggerMagicProjectile(ExplosionPrimeEvent event)
    {
        EntityType type = event.getEntityType();        
        if(type == EntityType.FIREBALL)
        {
            MetadataValue value = NBTAPI.getMetadata(event.getEntity(), "crystalmagic_projectile", plugin);
            
            if(value != null)
            {
                event.setRadius(0);
                event.setCancelled(true);         
                
                HashMap<Element, Integer> element_levels = (HashMap<Element, Integer>)value.value();
                executeSpell(event.getEntity(), element_levels);
            }
        }
    }
    
    @EventHandler
    public void preventMagicProjectileDamage(EntityDamageByEntityEvent event)
    {
        EntityType type = event.getEntityType();        
        if(type == EntityType.FIREBALL)
        {
            MetadataValue value = NBTAPI.getMetadata(event.getEntity(), "crystalmagic_projectile", plugin);
            
            if(value != null)
            {
                event.setDamage(0);
            }
        }
    }
    
    private void executeSpell(Entity caster, HashMap<Element, Integer> element_levels)
    {
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 2.0f, 0.2f);
        
        for (Element e : element_levels.keySet())
        {
            if (e.canExecute())
            {
                e.execute(caster, element_levels.get(e));
            }
        }
    }
    
    /**
     * 
     * @param player
     * @param stack
     * @param mana
     * @return remaining mana
     */
    public int executeSpell(Player player, ItemStack stack, int mana)
    {
        CustomItemsAPI api = CustomItemsAPI.api(plugin);

        HashMap<String, Integer> elements = getElements(stack);
        HashMap<Element, Integer> element_levels = new HashMap<>();

        for (String elementid : elements.keySet())
        {
            Element elem = (Element) api.getCustomItem(elementid);
            int level = elem.getLevelFromMana(mana);
            
            //CrystalMagicPlugin.LOGGER.info("execute spell: " + elem + " lv " + level);

            if (level > 0)
            {
                mana -= elem.getManaCost(level);
                element_levels.put(elem, level);
            }
        }
        
        if(!element_levels.isEmpty())
        {
            if (isProjectile(stack))
            {
                spawnMagicProjectile(player, element_levels);
            }
            else
            {
                executeSpell(player, element_levels);
            }
        }
        else
        {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_DEATH, 1.0f, 2.0f);
        }
        
        return mana;
    }
    
    private void spellLoad(Player player, ItemStack stack)
    {
        MagicCrystalLoadSpell spell = new MagicCrystalLoadSpell(plugin, player, stack, this);
        plugin.spellhandler.spell(player, spell);
    }
    
    private void spellCast(Player player, ItemStack stack)
    {        
        if(isInstant(stack))
        {
            plugin.spellhandler.executeSpellNow(player);
            setFocus(stack, executeSpell(player, stack, getFocus(stack)));
        }
        else
        {
            MagicCrystalConductSpell spell = new MagicCrystalConductSpell(plugin, player, stack, this);
            plugin.spellhandler.spell(player, spell);
        }
    }
    
    @EventHandler
    public void onUse(PlayerInteractEvent event)
    {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        
        if(isOfOnly(stack, 1))
        {
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                if(isInstant(stack))
                {
                    if(plugin.spellhandler.isUsingSpell(event.getPlayer()))
                        plugin.spellhandler.executeSpellNow(event.getPlayer());
                    else
                        spellLoad(event.getPlayer(), stack);
                }
            }
            else
            {
                if (plugin.spellhandler.isUsingSpell(event.getPlayer()))
                {
                    plugin.spellhandler.executeSpellNow(event.getPlayer());
                }
                else
                {
                    spellCast(event.getPlayer(), stack);
                }                
            }
        }
    }
    
    @EventHandler
    public void preventBreaking(BlockBreakEvent event)
    {
        if(!event.isCancelled() && event.getPlayer().getGameMode() == GameMode.CREATIVE && isOf(event.getPlayer().getInventory().getItemInMainHand()))
        {
            event.setCancelled(true);
        }
    }
}
