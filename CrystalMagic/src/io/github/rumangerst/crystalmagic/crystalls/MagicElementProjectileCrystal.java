/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.spells.Spell;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author ruman
 */
public class MagicElementProjectileCrystal extends MagicElementCrystal
{
    private CrystalMagicPlugin plugin;    
    
    public MagicElementProjectileCrystal(String id, String name, CrystalMagicPlugin plugin)
    {
        super(id, name);
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileHit(ExplosionPrimeEvent event)
    {
        if(event.getEntity() instanceof Fireball)
        {
            CrystalMagicPlugin.LOGGER.info("fireball found");
            event.setRadius(0);
            event.setCancelled(true);
            
            event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());            
        }
    }
    
    @EventHandler
    public void onProjectileHitEntity(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Fireball)
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event)
    {
        //Usable by left and right click
        if(((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && 
                isOf(event.getPlayer().getInventory().getItemInMainHand())) ||
                ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
                isOf(event.getPlayer().getInventory().getItemInOffHand())))
        {
            Player player = event.getPlayer();
            Location spawnAt = player.getEyeLocation().toVector().add(player.getEyeLocation().getDirection()).toLocation(player.getWorld());

            Fireball entity = (Fireball)player.getWorld().spawnEntity(spawnAt, EntityType.FIREBALL);
            entity.setDirection(player.getEyeLocation().getDirection());
            
            NBTAPI.setMetadata(entity, "magic/element", "any", plugin);
        }
    }
    
}
