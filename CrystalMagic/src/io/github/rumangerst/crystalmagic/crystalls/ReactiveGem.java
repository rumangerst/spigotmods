/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicWorld;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author ruman
 */
public class ReactiveGem extends MagicGem
{
    
    public ReactiveGem(String id, Material type, byte data, String name, CrystalMagicPlugin plugin)
    {
        super(id, type, data, name, plugin);        
    }
    
    
    private Element diceElement(Location loc)
    {
        /*
        Fire: Deep in earth
        Water: Mountain biome, heights
        Life: Forest biome, plains biome
        Magic: Nexuses
        Inversion: Nexuses
        */
        
        Element found = null;
        String biomename = loc.getBlock().getBiome().name().toLowerCase();
        
        
        if(loc.getY() < 20)
        {
            if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.2)
            {
                found = (Element)CustomItemsAPI.api(plugin).getCustomItem("magicfireelement");
            }
        }
        else if(loc.getY() > 100)
        {
            if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.2)
            {
                found = (Element)CustomItemsAPI.api(plugin).getCustomItem("magicwaterelement");
            }
        }
        else if(biomename.contains("forest"))
        {
            if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.2)
            {
                found = (Element)CustomItemsAPI.api(plugin).getCustomItem("magiclifeelement");
            }
        }
        else if(biomename.equals("plains"))
        {
            if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.08)
            {
                found = (Element)CustomItemsAPI.api(plugin).getCustomItem("magiclifeelement");
            }
        }
        else if(MagicWorld.distanceToNexus(loc) < 20)
        {
            if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.1)
            {
                found = (Element)CustomItemsAPI.api(plugin).getCustomItem("magicmagicelement");
            }
            else if(CrystalMagicPlugin.RANDOM.nextDouble() < 0.03)
            {
                found = (Element)CustomItemsAPI.api(plugin).getCustomItem("magicinversionelement");
            }
        }
        
        return found;
    }
    
    protected void fill(Player player)
    {
        
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
    }
    
    public void prepareFill(Player player)
    {
        if(MagicWorld.distanceToNexus(player.getLocation()) < 100)
        {
            fill(player);            
        }
        else
        {
            player.sendMessage("Der Stein reagiert nicht. Seine Kraft ist zu ungeordnet, um ohne einen Nexus Magie zu ziehen!");
        }
    }
    
    @EventHandler
    public void fillGem(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            if(isOf(event.getItem()))
            {
                Player player = event.getPlayer();
                prepareFill(player);
            }
        }
    }
}
