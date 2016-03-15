/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import io.github.rumangerst.crystalmagic.patterns.ModusClosed;
import io.github.rumangerst.crystalmagic.patterns.ModusFilter;
import io.github.rumangerst.crystalmagic.patterns.ModusOpen;
import io.github.rumangerst.crystalmagic.patterns.SealBalanceSmall;
import io.github.rumangerst.crystalmagic.patterns.SealOrderSmall;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicTable implements Listener
{
    CrystalMagicPlugin plugin;
    
    private ModusClosed modus_closed;
    private ModusOpen modus_open;
    private ModusFilter modus_filter;
    
    private SealOrderSmall seal_order_small;
    private SealBalanceSmall seal_balance_small;
    
    private HashMap<Location, Inventory> inventories = new HashMap<>();
    
    public static String INVENTORY_NAME_BALANCE_SMALL = "Magie: Gleichgewicht (Klein)";
    public static String INVENTORY_NAME_ORDER_SMALL = "Magie: Ordnung (Klein)";
    public static String INVENTORY_NAME_BALANCE_BIG = "Magie: Gleichgewicht";
    public static String INVENTORY_NAME_ORDER_BIG = "Magie: Ordnung";
    
    public MagicTable(CrystalMagicPlugin plugin)
    {
        this.plugin = plugin;
        
        modus_closed = new ModusClosed();
        modus_filter = new ModusFilter();
        modus_open = new ModusOpen();
        
        seal_order_small = new SealOrderSmall();
        seal_balance_small = new SealBalanceSmall();
    }
    
    @EventHandler
    public void hookIntoWorkbench(PlayerInteractEvent event)
    {
        if(!event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WORKBENCH)
        {
            if(tryOpenTableInventory(event.getPlayer(), event.getClickedBlock().getLocation()))
            {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Drops all inventories of magic tables to the ground
     */
    public void disable()
    {
        for(Location loc : inventories.keySet())
        {
            Inventory inv = inventories.get(loc);
            
            for(ItemStack stack : inv)
            {
                if(stack != null)
                    loc.getWorld().dropItem(loc, stack);
            }
        }
        
        inventories.clear();
    }
    
    private boolean tryOpenTableInventory(Player player, Location center)
    {
        Inventory existing = inventories.getOrDefault(center, null);
        String invname = "";
        
        Location new_center = center.add(0, -1, 0);
        
        if(seal_order_small.test(new_center))
        {
            invname = INVENTORY_NAME_ORDER_SMALL;
        }
        else if(seal_balance_small.test(new_center))
        {
            invname = INVENTORY_NAME_BALANCE_SMALL;
        }
        
        if(invname.isEmpty() && existing != null)
        {
            // Drop if invalid
            for(ItemStack stack : existing)
            {
                if(stack != null)
                    center.getWorld().dropItem(center, stack);
            }
            
            inventories.remove(center);
        }
        else if(!invname.isEmpty())
        {
            // If the type changed, drop the items and create a new inventory
            if(existing != null && !existing.getTitle().equals(invname))
            {
                // Drop if invalid
                for(ItemStack stack : existing)
                {
                    center.getWorld().dropItem(center, stack);
                }
                
                existing = Bukkit.createInventory(null, 9, invname);
            }
            
            if(existing == null)
            {
                existing = Bukkit.createInventory(null, 9, invname);
                inventories.put(center, existing);
            }
            
            player.getWorld().playSound(new_center, Sound.ENTITY_ELDER_GUARDIAN_HURT, 1, 0.2f);
            player.openInventory(existing);
            
            return true;
        }
        
        return false;
    }
}
