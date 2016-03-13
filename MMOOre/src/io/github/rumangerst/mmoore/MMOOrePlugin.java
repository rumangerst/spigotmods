/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.mmoore;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class MMOOrePlugin extends JavaPlugin implements Listener
{
    private static double SIGMA = 0.3;
    
    private Random random = new Random();
    
    private HashMap<Material, Long> ore_timeout = new HashMap<>();
    private HashMap<Material, LootTable> loot = new HashMap<>();
    private HashMap<Location, Long> mined_ore = new HashMap<>();
    
    public MMOOrePlugin()
    {
        createLoot();
    }
    
    private void addOre(Material material, long timeout, LootTable loottable)
    {
        loot.put(material, loottable);
        ore_timeout.put(material, timeout);
    }
    
    private void createLoot()
    {
        addOre(Material.COAL_ORE, 1000L * 60 * 10, new LootTable(true));
        addOre(Material.IRON_ORE, 1000L * 60 * 15, new LootTable(true));
        addOre(Material.GOLD_ORE, 1000L * 60 * 20, new LootTable(true));
        addOre(Material.DIAMOND_ORE, 1000L * 60 * 20, new LootTable(true));
        addOre(Material.EMERALD_ORE, 1000L * 60 * 20, new LootTable(true));
        addOre(Material.LAPIS_ORE, 1000L * 60 * 15, new LootTable(true));
        addOre(Material.REDSTONE_ORE, 1000L * 60 * 20, new LootTable(true));
    }
    
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable()
    {
        
    }
    
    @EventHandler
    public void onInterceptPlacingOres(BlockPlaceEvent event)
    {
        if(!event.isCancelled() && event.getBlock() != null && event.getPlayer().getGameMode() == GameMode.SURVIVAL)
        {
            if(ore_timeout.containsKey(event.getBlock().getType()))
            {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInterceptOres(BlockBreakEvent event)
    {
        if(!event.isCancelled() && event.getBlock() != null && event.getPlayer().getGameMode() == GameMode.SURVIVAL)
        {
            long current = System.currentTimeMillis();
            long timeout = mined_ore.getOrDefault(event.getBlock().getLocation(), current);
            
            if(current < timeout)
            {
                BountifulAPI.sendActionBar(event.getPlayer(), "Da ist nichts mehr zu holen!");
                
                event.setCancelled(true);
                return;
            }
            
            LootTable table = loot.getOrDefault(event.getBlock().getType(), null);
            
            if(table != null)
            {
                long timeouttime = ore_timeout.get(event.getBlock().getType());
                
                timeouttime = (long)(timeouttime + (0.5 - random.nextDouble()) * SIGMA);
                
                table.dice(event);
                mined_ore.put(event.getBlock().getLocation(), current + timeouttime);
                event.setCancelled(true);
            }
        }
    }
}
